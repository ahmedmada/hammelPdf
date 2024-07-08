package org.hammel.paintpdf

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.hammel.paintpdf.command.CommandFactory
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.command.CommandManager.CommandListener
import org.hammel.paintpdf.command.implementation.AsyncCommandManager
import org.hammel.paintpdf.command.implementation.DefaultCommandFactory
import org.hammel.paintpdf.command.implementation.DefaultCommandManager
import org.hammel.paintpdf.command.implementation.LayerOpacityCommand
import org.hammel.paintpdf.command.serialization.CommandSerializer
import org.hammel.paintpdf.common.CommonFactory
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.contract.MainActivityContracts
import org.hammel.paintpdf.contract.MainActivityContracts.MainView
import org.hammel.paintpdf.controller.DefaultToolController
import org.hammel.paintpdf.iotasks.OpenRasterFileFormatConversion
import org.hammel.paintpdf.listener.DrawerLayoutListener
import org.hammel.paintpdf.listener.PresenterColorPickedListener
import org.hammel.paintpdf.model.LayerModel
import org.hammel.paintpdf.model.MainActivityModel
import org.hammel.paintpdf.presenter.LayerPresenter
import org.hammel.paintpdf.presenter.MainActivityPresenter
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolReference
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.implementation.BaseToolWithShape
import org.hammel.paintpdf.tools.implementation.ClippingTool
import org.hammel.paintpdf.tools.implementation.DefaultContextCallback
import org.hammel.paintpdf.tools.implementation.DefaultToolFactory
import org.hammel.paintpdf.tools.implementation.DefaultToolPaint
import org.hammel.paintpdf.tools.implementation.DefaultToolReference
import org.hammel.paintpdf.tools.implementation.DefaultWorkspace
import org.hammel.paintpdf.tools.implementation.LineTool
import org.hammel.paintpdf.tools.implementation.TransformTool
import org.hammel.paintpdf.ui.DrawingSurface
import org.hammel.paintpdf.ui.KeyboardListener
import org.hammel.paintpdf.ui.LayerAdapter
import org.hammel.paintpdf.ui.LayerNavigator
import org.hammel.paintpdf.ui.MainActivityInteractor
import org.hammel.paintpdf.ui.MainActivityNavigator
import org.hammel.paintpdf.ui.Perspective
import org.hammel.paintpdf.ui.dragndrop.DragAndDropListView
import org.hammel.paintpdf.ui.tools.DefaultToolOptionsViewController
import org.hammel.paintpdf.ui.viewholder.BottomBarViewHolder
import org.hammel.paintpdf.ui.viewholder.BottomNavigationViewHolder
import org.hammel.paintpdf.ui.viewholder.DrawerLayoutViewHolder
import org.hammel.paintpdf.ui.viewholder.LayerMenuViewHolder
import org.hammel.paintpdf.ui.viewholder.TopBarViewHolder
import org.hammel.paintpdf.ui.zoomwindow.DefaultZoomWindowController
import paintpdf.BuildConfig
import paintpdf.R
import java.io.File
import java.util.Locale

private const val TEMP_IMAGE_COROUTINE_DELAY_MILLI_SEC = 1000
private const val MILLI_SEC_TO_SEC = 1000
private const val TEMP_IMAGE_SAVE_INTERVAL = 60
private const val TEMP_IMAGE_IDLE_INTERVAL = 2 * TEMP_IMAGE_COROUTINE_DELAY_MILLI_SEC

class MainActivity : AppCompatActivity(), MainView, CommandListener {
    @VisibleForTesting
    lateinit var perspective: Perspective


    lateinit var layerAdapter: LayerAdapter

    lateinit var zoomWindowController: DefaultZoomWindowController

    lateinit var commandManager: CommandManager
    lateinit var toolPaint: ToolPaint
    lateinit var bottomNavigationViewHolder: BottomNavigationViewHolder
    lateinit var model: MainActivityContracts.Model

    private lateinit var commandSerializer: CommandSerializer
    private lateinit var layerPresenter: LayerPresenter

    private lateinit var presenterMain: MainActivityContracts.Presenter
    private lateinit var drawerLayoutViewHolder: DrawerLayoutViewHolder
    private lateinit var keyboardListener: KeyboardListener
    private lateinit var appFragment: PaintroidApplicationFragment
    lateinit var defaultToolController: DefaultToolController
    private lateinit var commandFactory: CommandFactory
    private var deferredRequestPermissionsResult: Runnable? = null
    private lateinit var progressBar: ContentLoadingProgressBar

    var idlingResource = CountingIdlingResource("MainIdleResource")
    private lateinit var toolReference:ToolReference

    @Volatile
    private var lastInteractionTime = System.currentTimeMillis()

    @Volatile
    private var minuteTemporaryCopiesCounter = 0

    @Volatile
    private var userInteraction = false
    private var isTemporaryFileSavingTest = false

    private val isRunningEspressoTests: Boolean by lazy {
        try {
            Class.forName("androidx.test.espresso.Espresso")
            true
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Application is not in test mode.")
            false
        }
    }

    companion object {
        const val TAG = "MainActivity"
        private const val IS_FULLSCREEN_KEY = "isFullscreen"
        private const val IS_SAVED_KEY = "isSaved"
        private const val IS_OPENED_FROM_CATROID_KEY = "isOpenedFromCatroid"
        private const val IS_OPENED_FROM_FORMULA_EDITOR_IN_CATROID_KEY =
            "isOpenedFromFormulaEditorInCatroid"
        private const val SAVED_PICTURE_URI_KEY = "savedPictureUri"
        private const val CAMERA_IMAGE_URI_KEY = "cameraImageUri"
        private const val APP_FRAGMENT_KEY = "customActivityState"
        private const val SHARED_PREFS_NAME = "preferences"
        private const val FIRST_LAUNCH_AFTER_INSTALL = "firstLaunchAfterInstall"
    }

    override val presenter: MainActivityContracts.Presenter
        get() = presenterMain

    override val displayMetrics: DisplayMetrics
        get() = resources.displayMetrics

    override val isKeyboardShown: Boolean
        get() = keyboardListener.isSoftKeyboardVisible

    override val myContentResolver: ContentResolver
        get() = contentResolver

    override val finishing: Boolean
        get() = isFinishing

    override fun onResume() {
        super.onResume()
        deferredRequestPermissionsResult?.let { result ->
            val runnable: Runnable = result
            deferredRequestPermissionsResult = null
            runnable.run()
        }
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    private fun handleIntent(receivedIntent: Intent): Boolean {
        var receivedUri = receivedIntent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)

        receivedUri = receivedUri ?: receivedIntent.data

        val mimeType: String? = if (receivedUri?.scheme == ContentResolver.SCHEME_CONTENT) {
            myContentResolver.getType(receivedUri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(receivedUri?.toString())
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.US))
        }

        receivedUri ?: return true
        try {
            if (mimeType.equals("application/zip") || mimeType.equals("application/octet-stream")) {
                try {
                    val fileContent = commandSerializer.readFromFile(receivedUri)
                    commandManager.loadCommandsCatrobatImage(fileContent.commandModel)
                    presenterMain.setColorHistoryAfterLoadImage(fileContent.colorHistory)
                    return false
                } catch (e: CommandSerializer.NotCatrobatImageException) {
                    Log.e(TAG, "Image might be an ora file instead")
                    OpenRasterFileFormatConversion.mainActivity = this
                    OpenRasterFileFormatConversion.importOraFile(
                        myContentResolver,
                        receivedUri
                    ).layerList?.let { layerList ->
                        commandManager.setInitialStateCommand(
                            commandFactory.createInitCommand(
                                layerList
                            )
                        )
                    }
                    val paint = Paint()
                    val coordinate = PointF(0.0f, 0.0f)
                    paint.color = Color.TRANSPARENT
                    commandManager.addCommand(commandFactory.createPointCommand(paint, coordinate))
                    commandManager.undo()
                    commandManager.reset()
                }
            } else {
                org.hammel.paintpdf.FileIO.filename = "image"
                org.hammel.paintpdf.FileIO.getBitmapFromUri(myContentResolver, receivedUri, applicationContext)
                    ?.let { receivedBitmap ->
                        commandManager.setInitialStateCommand(
                            commandFactory.createInitCommand(
                                receivedBitmap
                            )
                        )
                    }
            }
        } catch (e: Exception) {
            Log.e("Can not read", "Unable to retrieve Bitmap from Uri")
        }
        return true
    }

    private fun validateIntent(receivedIntent: Intent): Boolean {
        val receivedAction = receivedIntent.action
        val receivedType = receivedIntent.type

        return receivedAction == Intent.ACTION_EDIT || receivedAction != null && receivedType != null && (receivedAction == Intent.ACTION_SEND || receivedAction == Intent.ACTION_VIEW) && (
                receivedType.startsWith(
                    "image/"
                ) || receivedType.startsWith("application/")
                )
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.PdfPaintTheme)
        super.onCreate(savedInstanceState)
        getAppFragment()
        PaintroidApplication.cacheDir = cacheDir
        setContentView(R.layout.activity_pocketpaint_main)
        onCreateDrawingSurface()
        presenterMain.onCreateTool()
        OpenRasterFileFormatConversion.mainActivity = this

        val receivedIntent = intent
        isTemporaryFileSavingTest = intent.getBooleanExtra("isTemporaryFileSavingTest", false)
//        when {
//            validateIntent(receivedIntent) && savedInstanceState == null -> {
        if (handleIntent(receivedIntent)) {
            commandManager.reset()
        }
        model.savedPictureUri = null
        model.cameraImageUri = null
        presenterMain.initializeFromCleanState(null, null)

        commandManager.addCommandListener(this)
        lastInteractionTime = System.currentTimeMillis()
        if ((!isRunningEspressoTests || isTemporaryFileSavingTest) && !model.isOpenedFromCatroid) {
            startAutoSaveCoroutine()
        }
        presenterMain.finishInitialize()

        if (!BuildConfig.DEBUG) {
            val prefs = getSharedPreferences(SHARED_PREFS_NAME, 0)

            if (prefs.getBoolean(FIRST_LAUNCH_AFTER_INSTALL, true)) {
                prefs.edit().putBoolean(FIRST_LAUNCH_AFTER_INSTALL, false).apply()
                presenterMain.showHelpClicked()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            presenterMain.saveNewTemporaryImage()
            minuteTemporaryCopiesCounter = 0
            userInteraction = false
        }
        this.window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pocketpaint_more_options, menu)
        presenterMain.removeMoreOptionsItems(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pdfPaint_options_export -> presenterMain.saveCopyClicked(true)
            R.id.pdfPaint_options_save_image -> presenterMain.saveImageClicked()
            R.id.pdfPaint_options_save_duplicate -> presenterMain.saveCopyClicked(false)
            R.id.pdfPaint_replace_image -> presenterMain.replaceImageClicked()
            R.id.pdfPaint_add_to_current_layer -> presenterMain.addImageToCurrentLayerClicked()
            R.id.pdfPaint_options_new_image -> presenterMain.newImageClicked()
            R.id.pdfPaint_options_discard_image -> presenterMain.discardImageClicked()
            R.id.pdfPaint_options_fullscreen_mode -> {
                perspective.mainActivity = this
                presenterMain.enterHideButtonsClicked()
            }

            R.id.pdfPaint_options_rate_us -> presenterMain.rateUsClicked()
            R.id.pdfPaint_options_help -> presenterMain.showHelpClicked()
            R.id.pdfPaint_options_about -> presenterMain.showAboutClicked()
            R.id.pdfPaint_share_image_button -> presenterMain.shareImageClicked()
            R.id.pdfPaint_options_feedback -> presenterMain.sendFeedback()
            R.id.pdfPaint_zoom_window_settings ->
                presenterMain.showZoomWindowSettingsClicked(
                    UserPreferences(getPreferences(MODE_PRIVATE))
                )

            R.id.pdfPaint_advanced_settings -> presenterMain.showAdvancedSettingsClicked()
            android.R.id.home -> presenterMain.backToPocketCodeClicked()
            else -> return false
        }
        return true
    }

    private fun getAppFragment() {
        supportFragmentManager.findFragmentByTag(APP_FRAGMENT_KEY)?.let { fragment ->
            appFragment = fragment as PaintroidApplicationFragment
        }
        if (!this::appFragment.isInitialized) {
            appFragment = PaintroidApplicationFragment()
            supportFragmentManager.beginTransaction().add(appFragment, APP_FRAGMENT_KEY).commit()
        }
    }

    private fun onCreateGlobals(): LayerContracts.Model {
        val currentLayerModel = appFragment.layerModel ?: LayerModel()
        appFragment.layerModel = currentLayerModel
        var layerModel = currentLayerModel

        commandFactory = DefaultCommandFactory()

        val currentCommandManager = appFragment.commandManager
        if (currentCommandManager == null) {
            val metrics = resources.displayMetrics
            val synchronousCommandManager: CommandManager =
                DefaultCommandManager(CommonFactory(), layerModel)
            commandManager = AsyncCommandManager(synchronousCommandManager, layerModel)
            val initCommand =
                commandFactory.createInitCommand(metrics.widthPixels, metrics.heightPixels)
            commandManager.setInitialStateCommand(initCommand)
            commandManager.reset()
            appFragment.commandManager = commandManager
        } else {
            commandManager = currentCommandManager
        }

        val currentToolPaint = appFragment.toolPaint ?: DefaultToolPaint(applicationContext)
        appFragment.toolPaint = currentToolPaint
        toolPaint = currentToolPaint

        val currentTool = appFragment.currentTool ?: DefaultToolReference()
        appFragment.currentTool = currentTool
        toolReference = currentTool

        return layerModel
    }

    private fun onCreateMainView(
        layerModel: LayerContracts.Model,
        toolReference: ToolReference,
        toolOptionsViewController: DefaultToolOptionsViewController,
        idlingResource: CountingIdlingResource
    ) {
        val context: Context = this
        val drawerLayout = findViewById<DrawerLayout>(R.id.pdfPaint_drawer_layout)
        val topBarLayout = findViewById<ViewGroup>(R.id.pdfPaint_layout_top_bar)
        val bottomBarLayout = findViewById<View>(R.id.pdfPaint_main_bottom_bar)
        val bottomNavigationView = findViewById<View>(R.id.pdfPaint_main_bottom_navigation)
        drawerLayoutViewHolder = DrawerLayoutViewHolder(drawerLayout)
        val topBarViewHolder = TopBarViewHolder(topBarLayout)
        val bottomBarViewHolder = BottomBarViewHolder(bottomBarLayout)
        bottomNavigationViewHolder = BottomNavigationViewHolder(
            bottomNavigationView,
            resources.configuration.orientation,
            applicationContext
        )
        perspective = Perspective(layerModel.width, layerModel.height)
        val listener = DefaultWorkspace.Listener {
        }
        var workspace = DefaultWorkspace(
            layerModel,
            perspective,
            listener,
        )
        model = MainActivityModel()
        commandSerializer = CommandSerializer(this, commandManager, model)
        zoomWindowController = DefaultZoomWindowController(
            this,
            layerModel,
            workspace,
            toolReference,
            UserPreferences(getPreferences(MODE_PRIVATE))
        )
        defaultToolController = DefaultToolController(
            toolReference,
            toolOptionsViewController,
            DefaultToolFactory(this),
            commandManager,
            workspace,
            idlingResource,
            toolPaint,
            DefaultContextCallback(context)
        )
        val preferences = UserPreferences(getPreferences(MODE_PRIVATE))
        val navigator = MainActivityNavigator(this, toolReference)
        presenterMain = MainActivityPresenter(
            this,
            this,
            model,
            workspace,
            MainActivityNavigator(this, toolReference),
            MainActivityInteractor(idlingResource),
            topBarViewHolder,
            bottomBarViewHolder,
            drawerLayoutViewHolder,
            bottomNavigationViewHolder,
            DefaultCommandFactory(),
            commandManager,
            defaultToolController,
            preferences,
            idlingResource,
            context,
            filesDir,
            commandSerializer
        )
        org.hammel.paintpdf.FileIO.navigator = navigator
        defaultToolController.setOnColorPickedListener(PresenterColorPickedListener(presenterMain))
        keyboardListener = KeyboardListener(drawerLayout)
        setTopBarListeners(topBarViewHolder)
        setBottomBarListeners(bottomBarViewHolder)
        setBottomNavigationListeners(bottomNavigationViewHolder)
        setActionBarToolTips(topBarViewHolder, context)
        progressBar = findViewById(R.id.pdfPaint_content_loading_progress_bar)
    }

    private fun onCreateLayerMenu(layerModel: LayerContracts.Model) {
        setLayoutDirection()
        val layerLayout = findViewById<NavigationView>(R.id.pdfPaint_nav_view_layer)
        val drawerLayout = findViewById<DrawerLayout>(R.id.pdfPaint_drawer_layout)
        val layerListView = findViewById<DragAndDropListView>(R.id.pdfPaint_layer_side_nav_list)
        val layerMenuViewHolder = LayerMenuViewHolder(layerLayout)
        val layerNavigator = LayerNavigator(applicationContext)
        layerPresenter = LayerPresenter(
            layerModel, layerListView, layerMenuViewHolder,
            commandManager, DefaultCommandFactory(), layerNavigator
        )
        val layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        layerListView.layoutManager = layoutManager
        layerListView.manager = layoutManager
        layerAdapter = LayerAdapter(layerPresenter, this)
        layerListView.setLayerAdapter(layerAdapter)
        presenterMain.setLayerAdapter(layerAdapter)
        layerPresenter.setAdapter(layerAdapter)
        layerListView.setPresenter(layerPresenter)
        layerListView.adapter = layerAdapter
        layerPresenter.refreshLayerMenuViewHolder()
        layerPresenter.disableVisibilityAndOpacityButtons()
        setLayerMenuListeners(layerMenuViewHolder)
        val drawerLayoutListener = DrawerLayoutListener(this, layerPresenter)
        drawerLayout.addDrawerListener(drawerLayoutListener)
    }

    private fun setLayoutDirection() {
        var visibilityBtn =
            findViewById<ImageButton>(R.id.pdfPaint_layer_side_nav_button_visibility)
        var layerNavigationView = findViewById<NavigationView>(R.id.pdfPaint_nav_view_layer)
        if (LanguageHelper.isCurrentLanguageRTL()) {
            visibilityBtn.setBackgroundResource(R.drawable.rounded_corner_top_rtl)
            layerNavigationView.setBackgroundResource(R.drawable.layer_nav_view_background_rtl)
        } else {
            visibilityBtn.setBackgroundResource(R.drawable.rounded_corner_top_ltr)
            layerNavigationView.setBackgroundResource(R.drawable.layer_nav_view_background_ltr)
        }
    }

    var pagesList = ArrayList<DrawingSurface>()
    fun removeDrawingSurface() {
        val container: LinearLayout = findViewById(R.id.container)
        container.removeView(pagesList.get(pagesList.size - 1))
        pagesList.removeAt(pagesList.size - 1)
    }

    fun addDrawingSurface(): DrawingSurface {
        val layerModel = onCreateGlobals()

        appFragment.currentTool = toolReference
        val toolOptionsViewController = DefaultToolOptionsViewController(this, idlingResource)


        onCreateMainView(layerModel, toolReference, toolOptionsViewController, idlingResource)
        onCreateLayerMenu(layerModel)


        val container: LinearLayout = findViewById(R.id.container)
        val drawingSurface = DrawingSurface(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                400 // height in pixels
            ).apply {
                setMargins(10, 10, 10, 10)
            }
            setBackgroundColor(Color.parseColor("#00000000"))
        }

        container.addView(drawingSurface)

        drawingSurface.setArguments(
            layerModel,
            perspective,
            toolReference,
            idlingResource,
//            supportFragmentManager,
            toolOptionsViewController,
            drawerLayoutViewHolder,
            zoomWindowController,
        )
        layerPresenter.setDrawingSurface(drawingSurface)
        return drawingSurface
    }

    private fun onCreateDrawingSurface() {

        pagesList.add(addDrawingSurface())
        appFragment.perspective = perspective
        layerPresenter.setDefaultToolController(defaultToolController)
        layerPresenter.setBottomNavigationViewHolder(bottomNavigationViewHolder)
    }

    private fun setLayerMenuListeners(layerMenuViewHolder: LayerMenuViewHolder) {
        layerMenuViewHolder.layerAddButton.setOnClickListener { layerPresenter.addLayer() }
        layerMenuViewHolder.layerDeleteButton.setOnClickListener { layerPresenter.removeLayer() }
    }

    private fun setActionBarToolTips(topBar: TopBarViewHolder, context: Context) {
        TooltipCompat.setTooltipText(topBar.undoButton, context.getString(R.string.button_undo))
        TooltipCompat.setTooltipText(topBar.redoButton, context.getString(R.string.button_redo))
    }

    private fun mirrorUndoAndRedoButtonsForRtlLanguage() {
        val undoButton: ImageButton = findViewById(R.id.pdfPaint_btn_top_undo)
        val undoDrawable = ContextCompat.getDrawable(this, R.drawable.ic_pocketpaint_undo)

        val redoButton: ImageButton = findViewById(R.id.pdfPaint_btn_top_redo)
        val redoDrawable = ContextCompat.getDrawable(this, R.drawable.ic_pocketpaint_redo)

        undoDrawable?.let {
            it.isAutoMirrored = true
            undoButton.setImageDrawable(it)
        }

        redoDrawable?.let {
            it.isAutoMirrored = true
            redoButton.setImageDrawable(it)
        }
    }

    private fun setTopBarListeners(topBar: TopBarViewHolder) {
        topBar.undoButton.setOnClickListener { presenterMain.undoClicked() }
        topBar.redoButton.setOnClickListener { presenterMain.redoClicked() }
        topBar.managePages.setOnClickListener { presenterMain.managePages() }
        topBar.removePages.setOnClickListener { presenterMain.removePages() }

        if (LanguageHelper.isCurrentLanguageRTL()) {
            mirrorUndoAndRedoButtonsForRtlLanguage()
        }

        topBar.checkmarkButton.setOnClickListener {
            idlingResource.increment()
            if (toolReference.tool?.toolType?.name.equals(ToolType.TRANSFORM.name)) {
                (toolReference.tool as TransformTool).checkMarkClicked = true
                val tool = toolReference.tool as BaseToolWithShape?
                tool?.onClickOnButton()
            } else if (toolReference.tool?.toolType?.name.equals(ToolType.CLIP.name)) {
                val tool = toolReference.tool as ClippingTool?
                tool?.onClickOnButton()
            } else {
                val tool = toolReference.tool as BaseToolWithShape?
                tool?.onClickOnButton()
            }
            idlingResource.decrement()
        }
        topBar.plusButton.setOnClickListener {
            val tool = toolReference.tool as LineTool
            tool.onClickOnPlus()
        }
        LineTool.topBarViewHolder = topBar
    }

    private fun setBottomBarListeners(viewHolder: BottomBarViewHolder) {
        val toolTypes = ToolType.values()
        for (type in toolTypes) {
            val toolButton = viewHolder.layout.findViewById<View>(type.toolButtonID) ?: continue
            toolButton.setOnClickListener { presenterMain.toolClicked(type) }
        }
    }

    private fun setBottomNavigationListeners(viewHolder: BottomNavigationViewHolder) {
        viewHolder.bottomNavigationView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_tools -> presenterMain.actionToolsClicked()
                    R.id.action_current_tool -> presenterMain.actionCurrentToolClicked()
                    R.id.action_color_picker -> presenterMain.showColorPickerClicked()
                    R.id.action_layers -> presenterMain.showLayerMenuClicked()
                    else -> return@OnNavigationItemSelectedListener false
                }
                true
            }
        )
    }

    override fun initializeActionBar(isOpenedFromCatroid: Boolean) {
        val toolbar = findViewById<Toolbar>(R.id.pdfPaint_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(!isOpenedFromCatroid)
            setDisplayHomeAsUpEnabled(isOpenedFromCatroid)
            setHomeButtonEnabled(true)
            setDisplayShowHomeEnabled(false)
        }
    }

    override fun commandPostExecute() {
        if (!finishing) {
            if (commandManager.lastExecutedCommand !is LayerOpacityCommand) {
                layerPresenter.invalidate()
            }
            presenterMain.onCommandPostExecute()
        }
    }

    override fun onDestroy() {
        commandManager.removeCommandListener(this)
        presenterMain.saveNewTemporaryImage()
        if (finishing) {
            commandManager.shutdown()
            appFragment.currentTool = null
            appFragment.commandManager = null
            appFragment.layerModel = null
        }
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState) {
            putBoolean(IS_FULLSCREEN_KEY, model.isFullscreen)
            putBoolean(IS_SAVED_KEY, model.isSaved)
            putBoolean(IS_OPENED_FROM_CATROID_KEY, model.isOpenedFromCatroid)
            putBoolean(
                IS_OPENED_FROM_FORMULA_EDITOR_IN_CATROID_KEY,
                model.isOpenedFromFormulaEditorInCatroid
            )
            putParcelable(SAVED_PICTURE_URI_KEY, model.savedPictureUri)
            putParcelable(CAMERA_IMAGE_URI_KEY, model.cameraImageUri)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.isStateSaved) {
            super.onBackPressed()
        } else if (!supportFragmentManager.popBackStackImmediate()) {
            presenterMain.onBackPressed()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenterMain.handleActivityResult(requestCode, resultCode, data)
    }

    override fun superHandleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (VERSION.SDK_INT == Build.VERSION_CODES.M) {
            deferredRequestPermissionsResult = Runnable {
                presenterMain.handleRequestPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults
                )
            }
        } else {
            presenterMain.handleRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun superHandleRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun refreshDrawingSurface() {
//        drawingSurface.refreshDrawingSurface()
    }

    override fun enterHideButtons() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        }
    }

    override fun exitHideButtons() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun getUriFromFile(file: File): Uri = Uri.fromFile(file)

    override fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        if (inputMethodManager != null) {
            val rootView = window.decorView.rootView
            inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)
        }
    }

    override fun showContentLoadingProgressBar() {
        progressBar.show()
    }

    override fun hideContentLoadingProgressBar() {
        progressBar.hide()
    }

    override fun addPage() {
        pagesList.add(addDrawingSurface())
    }

    override fun removePage() {
        if (pagesList.size > 0)
            removeDrawingSurface()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        lastInteractionTime = System.currentTimeMillis()
        userInteraction = true
    }

    @Synchronized
    private fun addToMinuteTemporaryCopiesCounter(seconds: Int) {
        this.minuteTemporaryCopiesCounter += seconds
    }

    private fun startAutoSaveCoroutine() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(TEMP_IMAGE_COROUTINE_DELAY_MILLI_SEC.toLong())
                addToMinuteTemporaryCopiesCounter(TEMP_IMAGE_COROUTINE_DELAY_MILLI_SEC / MILLI_SEC_TO_SEC)
                if ((System.currentTimeMillis() - lastInteractionTime >= TEMP_IMAGE_IDLE_INTERVAL || minuteTemporaryCopiesCounter >= TEMP_IMAGE_SAVE_INTERVAL) && userInteraction) {
                    presenterMain.saveNewTemporaryImage()
                    minuteTemporaryCopiesCounter = 0
                    userInteraction = false
                }
            }
        }
    }

    fun getVersionCode(): String = runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName
    }.getOrDefault("")
}
