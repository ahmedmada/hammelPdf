package org.hammel.paintpdf.listener

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import org.hammel.paintpdf.tools.Tool
import org.hammel.paintpdf.tools.Tool.StateChange
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.options.ToolOptionsViewController
import org.hammel.paintpdf.ui.DrawingSurface
import org.hammel.paintpdf.ui.zoomwindow.ZoomWindowController
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.math.hypot

private const val DRAWER_EDGE_SIZE = 20f
private const val CONSTANT_1 = 0.5f
private const val JITTER_DELAY_THRESHOLD: Long = 30
private const val JITTER_DISTANCE_THRESHOLD = 50f
private const val ANIMATION_THRESHOLD = 4

open class DrawingSurfaceListener(
    private val callback: DrawingSurfaceListenerCallback,
    private val displayDensity: Float
) : OnTouchListener {
    private var touchMode: TouchMode
    private var pointerDistance = 0f
    private var xMidPoint = 0f
    private var yMidPoint = 0f
    private var eventX = 0f
    private var eventY = 0f
    private val canvasTouchPoint: PointF
    private val eventTouchPoint: PointF
    private val drawerEdgeSize: Int = (DRAWER_EDGE_SIZE * displayDensity + CONSTANT_1).toInt()
    private var timerStartDraw = 0.toLong()
    private lateinit var zoomController: ZoomWindowController
    private var callZoomWindow: Boolean = true
//    private lateinit var sharedPreferences: UserPreferences

    private var recentTouchEventsData: MutableList<TouchEventData> = mutableListOf()

    private data class TouchEventData constructor(val timeStamp: Long, val xCoordinate: Float, val yCoordinate: Float)

    internal enum class TouchMode {
        DRAW, PINCH
    }

    init {
        touchMode = TouchMode.DRAW
        canvasTouchPoint = PointF()
        eventTouchPoint = PointF()
    }

    private fun newHandEvent(x: Float, y: Float) {
        eventX = x
        eventY = y
    }

    private fun calculatePointerDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return hypot(x, y)
    }

    private fun calculateMidPoint(event: MotionEvent) {
        xMidPoint = (event.getX(0) + event.getX(1)) / 2f
        yMidPoint = (event.getY(0) + event.getY(1)) / 2f
    }

    fun setZoomController(
        zoomWindowController: ZoomWindowController,
//        sharedPreferences: UserPreferences
    ) {
        zoomController = zoomWindowController
//        this.sharedPreferences = sharedPreferences
    }

    private fun handleActionMove(currentTool: Tool?, event: MotionEvent, shouldAnimate: Boolean) {
        val xOld: Float
        val yOld: Float
        if (event.pointerCount == 1) {
            currentTool ?: return
            recentTouchEventsData.add(TouchEventData(event.eventTime, event.x, event.y))
            removeObsoleteTouchEventsData(event.eventTime)
            if (currentTool.handToolMode()) {
                if (touchMode == TouchMode.PINCH) {
                    xOld = 0f
                    yOld = 0f
                    touchMode = TouchMode.DRAW
                } else {
                    xOld = eventX
                    yOld = eventY
                }
                newHandEvent(event.x, event.y)
                if (xOld > 0 && eventX != xOld || yOld > 0 && eventY != yOld) {
                    callback.translatePerspective(eventX - xOld, eventY - yOld)
                }
            } else if (touchMode != TouchMode.PINCH) {
                touchMode = TouchMode.DRAW
                currentTool.handleMove(canvasTouchPoint, shouldAnimate)
            }
            handleZoomWindowOnMove(currentTool, event)
        } else {
            if (touchMode == TouchMode.DRAW) {
                saveToolActionBeforeZoom(PointF(event.x, event.y))
                currentTool?.resetInternalState(StateChange.MOVE_CANCELED)
            }
            touchMode = TouchMode.PINCH
            val pointerDistanceOld = pointerDistance
            pointerDistance = calculatePointerDistance(event)
            if (pointerDistanceOld > 0 && pointerDistanceOld != pointerDistance) {
                val scale = pointerDistance / pointerDistanceOld
                callback.multiplyPerspectiveScale(scale)
            }
            xOld = xMidPoint
            yOld = yMidPoint
            calculateMidPoint(event)
            if (xOld > 0 && xMidPoint != xOld || yOld > 0 && yMidPoint != yOld) {
                callback.translatePerspective(xMidPoint - xOld, yMidPoint - yOld)
            }
            zoomController.dismissOnPinch()
        }
    }

    private fun handleZoomWindowOnMove(currentTool: Tool, event: MotionEvent) {
//        if (sharedPreferences.preferenceZoomWindowEnabled) {
            if (!callback.getCurrentTool()?.toolType?.name.equals(ToolType.CURSOR.name)) {
                zoomController.onMove(canvasTouchPoint, PointF(event.x, event.y))
            } else {
                zoomController.onMove(currentTool.toolPositionCoordinates(canvasTouchPoint), PointF(event.x, event.y))
            }
//        }
    }

    private fun saveToolActionBeforeZoom(point: PointF) {
        val currentTool = callback.getCurrentTool()
        if (currentTool?.toolType?.name.equals(ToolType.CURSOR.name) ||
            currentTool?.toolType?.name.equals(ToolType.LINE.name)) {
            currentTool?.handleUp(point)
            currentTool?.handleDown(point)
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val drawingSurface = view as DrawingSurface
        val currentTool = callback.getCurrentTool()
        canvasTouchPoint.x = event.x
        canvasTouchPoint.y = event.y
        eventTouchPoint.x = canvasTouchPoint.x
        eventTouchPoint.y = canvasTouchPoint.y
        callback.convertToCanvasFromSurface(canvasTouchPoint)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (eventTouchPoint.x < drawerEdgeSize || view.getWidth() - eventTouchPoint.x < drawerEdgeSize) {
                    return false
                }
                timerStartDraw = System.currentTimeMillis()
                recentTouchEventsData.add(TouchEventData(event.eventTime, event.x, event.y))
                currentTool?.handleDown(canvasTouchPoint)
                handleZoomWindowOnTouch(currentTool, event)
            }
            MotionEvent.ACTION_MOVE -> {
                var threshold = view.height / ANIMATION_THRESHOLD
                var shouldAnimate = event.y < threshold || event.y > view.height - threshold
                handleActionMove(currentTool, event, shouldAnimate)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (touchMode == TouchMode.DRAW) {
                    val drawingTime = System.currentTimeMillis() - timerStartDraw
                    removeObsoleteTouchEventsData(event.eventTime)
                    var dX = 0f
                    var dY = 0f
                    if (recentTouchEventsData.size > 1) {
                        val oldestEntry = recentTouchEventsData[0]
                        val distanceCorrectionX = event.x - oldestEntry.xCoordinate
                        val distanceCorrectionY = event.y - oldestEntry.yCoordinate
                        val distance = distanceCorrectionX * distanceCorrectionX + distanceCorrectionY * distanceCorrectionY
                        if (distance < JITTER_DISTANCE_THRESHOLD * displayDensity && distance != 0f) {
                            dX = distanceCorrectionX
                            dY = distanceCorrectionY
                        }
                    }
                    canvasTouchPoint.x = event.x - dX
                    canvasTouchPoint.y = event.y - dY
                    callback.convertToCanvasFromSurface(canvasTouchPoint)
                    currentTool?.drawTime = drawingTime
                    currentTool?.handleUp(canvasTouchPoint)
                } else {
                    currentTool?.resetInternalState(StateChange.MOVE_CANCELED)
                }
                pointerDistance = 0f
                xMidPoint = 0f
                yMidPoint = 0f
                eventX = 0f
                eventY = 0f
                touchMode = TouchMode.DRAW
                if (callZoomWindow) zoomController.dismiss()
                callback.getCurrentTool()?.handToolMode()
            }
            MotionEvent.ACTION_POINTER_UP ->
            {
                currentTool?.handleDownAnimations(canvasTouchPoint)
                currentTool?.handleUpAnimations(canvasTouchPoint)
                drawingSurface.refreshDrawingSurface()
                return false
            }
        }
        drawingSurface.refreshDrawingSurface()
        return true
    }

    private fun handleZoomWindowOnTouch(currentTool: Tool?, event: MotionEvent) {
        callZoomWindow = false
//            if (sharedPreferences.preferenceZoomWindowEnabled) {
//            if (!currentTool?.toolType?.name.equals(ToolType.CURSOR.name)) {
//                zoomController.show(canvasTouchPoint, PointF(event.x, event.y))
//            } else {
//                currentTool?.toolPositionCoordinates(canvasTouchPoint)
//                        ?.let { zoomController.show(it, PointF(event.x, event.y)) }
//            }
//            true
//        } else {
//            false
//        }
    }

    private fun removeObsoleteTouchEventsData(timeStamp: Long) {
        val obsoleteTouchEventsData: MutableList<TouchEventData> = ArrayList()
        for (touchEventData in recentTouchEventsData) {
            if (timeStamp - touchEventData.timeStamp > JITTER_DELAY_THRESHOLD) {
                obsoleteTouchEventsData.add(touchEventData)
            } else {
                break
            }
        }
        recentTouchEventsData.removeAll(obsoleteTouchEventsData)
    }

    interface DrawingSurfaceListenerCallback {
        fun getCurrentTool(): Tool?

        fun multiplyPerspectiveScale(factor: Float)

        fun translatePerspective(x: Float, y: Float)

        fun convertToCanvasFromSurface(surfacePoint: PointF)

        fun getToolOptionsViewController(): ToolOptionsViewController
    }
}
