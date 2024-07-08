package org.hammel.paintpdf.iotasks

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.test.espresso.idling.CountingIdlingResource
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hammel.paintpdf.FileIO
import org.hammel.paintpdf.FileIO.filename
import org.hammel.paintpdf.FileIO.getBitmapReturnValueFromUri
import org.hammel.paintpdf.FileIO.getScaledBitmapFromUri
import org.hammel.paintpdf.command.serialization.CommandSerializer

class LoadImage(
    callback: LoadImageCallback,
    private val requestCode: Int,
    private val uri: Uri?,
    context: Context,
    private val scaleImage: Boolean,
    private val commandSerializer: CommandSerializer,
    private val scopeIO: CoroutineScope,
    private val idlingResource: CountingIdlingResource
) {
    private val callbackRef: WeakReference<LoadImageCallback> = WeakReference(callback)
    private val context: WeakReference<Context> = WeakReference(context)

    private fun getMimeType(uri: Uri, resolver: ContentResolver): String? {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            return resolver.getType(uri)
        }
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        if (fileExtension.equals("catrobat-image")) {
            return "application/octet-stream"
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.US))
    }

    private fun getBitmapReturnValue(
        uri: Uri,
        resolver: ContentResolver
    ): BitmapReturnValue {
        val mimeType: String? = getMimeType(uri, resolver)
        return if (mimeType == "application/zip" || mimeType == "application/octet-stream") {
            try {
                val fileContent = commandSerializer.readFromFile(uri)
                BitmapReturnValue(fileContent.commandModel, fileContent.colorHistory)
            } catch (e: CommandSerializer.NotCatrobatImageException) {
                Log.e(TAG, "Image might be an ora file instead")
                OpenRasterFileFormatConversion.importOraFile(
                    resolver,
                    uri
                )
            }
        } else {
            if (scaleImage) {
                getScaledBitmapFromUri(resolver, uri, context.get())
            } else {
                getBitmapReturnValueFromUri(resolver, uri, context.get())
            }
        }
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    fun execute() {
        val callback = callbackRef.get()
        if (callback == null || callback.isFinishing) {
            return
        }
        callback.onLoadImagePreExecute(requestCode)

        var returnValue: BitmapReturnValue? = null
        scopeIO.launch {
            idlingResource.increment()
            if (uri == null) {
                Log.e(TAG, "Can't load image file, uri is null")
            } else {
                try {
                    val resolver = callback.contentResolver
                    filename = "image"
                    returnValue = getBitmapReturnValue(uri, resolver)
                } catch (e: IOException) {
                    Log.e(TAG, "Can't load image file", e)
                } catch (e: NullPointerException) {
                    Log.e(TAG, "Can't load image file", e)
                }
            }

            withContext(Dispatchers.Main) {
                if (!callback.isFinishing) {
                    try {
                        callback.onLoadImagePostExecute(requestCode, uri, returnValue)
                    } finally {
                        idlingResource.decrement()
                    }
                }
            }
        }
    }

    interface LoadImageCallback {
        fun onLoadImagePostExecute(requestCode: Int, uri: Uri?, result: BitmapReturnValue?)
        fun onLoadImagePreExecute(requestCode: Int)
        val contentResolver: ContentResolver
        val isFinishing: Boolean
    }

    companion object {
        private val TAG = LoadImage::class.java.simpleName
    }
}
