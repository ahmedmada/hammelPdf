package org.hammel.paintpdf.colorpicker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

private const val HUNDRED = 100
private const val TAG = "FileOperations"

@SuppressWarnings("TooGenericExceptionCaught")
fun storeBitmapTemporally(bitmap: Bitmap, context: Context, imageName: String) {
    var outputStream: FileOutputStream? = null
    try {
        outputStream = context.openFileOutput(imageName, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.PNG, HUNDRED, outputStream)
        outputStream.flush()
    } catch (e: Exception) {
        when (e) {
            is FileNotFoundException -> Log.e(TAG, "FileNotFoundException: ${e.message}")
            is IOException -> Log.e(TAG, "IOException: ${e.message}")
        }
    } finally {
        outputStream?.close()
    }
}

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun loadBitmapByName(context: Context, imageName: String): Bitmap? {
    var bitmap: Bitmap? = null
    withContext(Dispatchers.IO) {
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = context.openFileInput(imageName)
            bitmap = BitmapFactory.decodeStream(fileInputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fileInputStream?.close()
        }
    }
    return bitmap
}

fun deleteBitmapFile(context: Context, imageName: String) = context.deleteFile(imageName)
