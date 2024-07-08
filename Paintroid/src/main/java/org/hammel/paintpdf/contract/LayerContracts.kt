package org.hammel.paintpdf.contract

import android.graphics.Bitmap
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.StringRes
import org.hammel.paintpdf.controller.DefaultToolController
import org.hammel.paintpdf.ui.DrawingSurface
import org.hammel.paintpdf.ui.dragndrop.ListItemDragHandler
import org.hammel.paintpdf.ui.viewholder.BottomNavigationViewHolder

interface LayerContracts {
    interface Adapter {
        fun notifyDataSetChanged()

        fun getViewHolderAt(position: Int): LayerViewHolder?
    }

    interface Presenter {
        val layerCount: Int
        val presenter: Presenter

        fun onSelectedLayerInvisible()

        fun onSelectedLayerVisible()

        fun getListItemDragHandler(): ListItemDragHandler

        fun refreshLayerMenuViewHolder()

        fun disableVisibilityAndOpacityButtons()

        fun getLayerItem(position: Int): Layer?

        fun getLayerItemId(position: Int): Long

        fun addLayer()

        fun removeLayer()

        fun changeLayerOpacity(position: Int, opacityPercentage: Int)

        fun setLayerVisibility(position: Int, isVisible: Boolean)

        fun refreshDrawingSurface()

        fun setAdapter(layerAdapter: Adapter)

        fun setDrawingSurface(drawingSurface: DrawingSurface)

        fun invalidate()

        fun setDefaultToolController(defaultToolController: DefaultToolController)

        fun setBottomNavigationViewHolder(bottomNavigationViewHolder: BottomNavigationViewHolder)

        fun isShown(): Boolean

        fun onStartDragging(position: Int, view: View)

        fun onStopDragging()

        fun setLayerSelected(position: Int)

        fun getSelectedLayer(): Layer?
    }

    interface LayerViewHolder {
        val bitmap: Bitmap?
        val view: View

        fun setSelected(isSelected: Boolean)

        fun updateImageView(layer: Layer)

        fun setMergable()

        fun isSelected(): Boolean

        fun getViewLayout(): LinearLayout

        fun bindView()

        fun setLayerVisibilityCheckbox(setTo: Boolean)
    }

    interface LayerMenuViewHolder {
        fun disableAddLayerButton()

        fun enableAddLayerButton()

        fun disableRemoveLayerButton()

        fun enableRemoveLayerButton()

        fun disableLayerOpacityButton()

        fun disableLayerVisibilityButton()

        fun isShown(): Boolean
    }

    interface Layer {
        var bitmap: Bitmap
        var isVisible: Boolean
        var opacityPercentage: Int

        fun getValueForOpacityPercentage(): Int
    }

    interface Model {
        val layers: List<Layer>
        var currentLayer: Layer?
        var width: Int
        var height: Int
        val layerCount: Int

        fun reset()

        fun getLayerAt(index: Int): Layer?

        fun getLayerIndexOf(layer: Layer): Int

        fun addLayerAt(index: Int, layer: Layer): Boolean

        fun listIterator(index: Int): ListIterator<Layer>

        fun setLayerAt(position: Int, layer: Layer)

        fun removeLayerAt(position: Int): Boolean

        fun getBitmapOfAllLayers(): Bitmap?

        fun getBitmapListOfAllLayers(): List<Bitmap?>
    }

    interface Navigator {
        fun showToast(@StringRes id: Int, length: Int)
    }
}
