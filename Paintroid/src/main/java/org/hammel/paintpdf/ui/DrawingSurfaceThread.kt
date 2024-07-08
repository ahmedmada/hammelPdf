package org.hammel.paintpdf.ui

import android.util.Log
import kotlin.jvm.Synchronized

internal class DrawingSurfaceThread(
    private val drawingSurface: DrawingSurface,
    private val threadRunnable: Runnable
) {
    private val internalThread: Thread
    private var running = false

    init {
        internalThread = Thread(InternalRunnable(), "DrawingSurfaceThread")
        internalThread.isDaemon = true
    }

    companion object {
        private val TAG = DrawingSurfaceThread::class.java.simpleName
    }

    private fun internalRun() {
        while (running) {
            threadRunnable.run()
        }
    }

    @Synchronized
    fun start() {
        Log.d(TAG, "DrawingSurfaceThread.start")
        if (running || internalThread.state == Thread.State.TERMINATED) {
            Log.d(TAG, "DrawingSurfaceThread.start returning")
            return
        }
        if (!internalThread.isAlive) {
            running = true
            internalThread.start()
        }
        drawingSurface.refreshDrawingSurface()
    }

    @Synchronized
    fun stop() {
        Log.d(TAG, "DrawingSurfaceThread.stop")
        running = false
        Log.d(TAG, "DrawingSurfaceThread.join")
        while (internalThread.isAlive) {
            try {
                internalThread.interrupt()
                internalThread.join()
                Log.d(TAG, "DrawingSurfaceThread.stopped")
            } catch (e: InterruptedException) {
                Log.e(TAG, "Interrupt while joining DrawingSurfaceThread\n", e)
            }
        }
    }

    private inner class InternalRunnable : Runnable {
        override fun run() {
            Thread.yield()
            internalRun()
        }
    }
}
