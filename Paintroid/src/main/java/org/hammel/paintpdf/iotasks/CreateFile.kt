package org.hammel.paintpdf.iotasks

import android.app.Activity
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hammel.paintpdf.FileIO
import org.hammel.paintpdf.FileIO.createNewEmptyPictureFile
import java.io.File
import java.lang.NullPointerException
import java.lang.ref.WeakReference

class CreateFile(
    callback: CreateFileCallback,
    private val requestCode: Int,
    private val filename: String?,
    private val scopeIO: CoroutineScope
) {
    private val callbackRef: WeakReference<CreateFileCallback> = WeakReference(callback)

    @SuppressWarnings("TooGenericExceptionCaught")
    fun execute() {
        val callback = callbackRef.get()
        var file: File? = null
        scopeIO.launch {
            try {
                file = createNewEmptyPictureFile(filename, callback?.fileActivity)
            } catch (e: NullPointerException) {
                Log.e(TAG, "Can't create file", e)
            }
            withContext(Dispatchers.Main) {
                if (callback != null && !callback.isFinishing) {
                    callback.onCreateFilePostExecute(requestCode, file)
                }
            }
        }
    }

    interface CreateFileCallback {
        val fileActivity: Activity?
        val isFinishing: Boolean
        fun onCreateFilePostExecute(requestCode: Int, file: File?)
    }

    companion object {
        private val TAG = CreateFile::class.java.simpleName
    }
}
