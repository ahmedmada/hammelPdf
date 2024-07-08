package org.hammel.paintpdf.ui

import android.content.Context
import android.net.Uri
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.hammel.paintpdf.command.serialization.CommandSerializer
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.contract.MainActivityContracts.Interactor
import org.hammel.paintpdf.iotasks.CreateFile
import org.hammel.paintpdf.iotasks.CreateFile.CreateFileCallback
import org.hammel.paintpdf.iotasks.LoadImage
import org.hammel.paintpdf.iotasks.LoadImage.LoadImageCallback
import org.hammel.paintpdf.iotasks.SaveImage
import org.hammel.paintpdf.iotasks.SaveImage.SaveImageCallback

class MainActivityInteractor(private val idlingResource: CountingIdlingResource) : Interactor {
    private val scopeIO = CoroutineScope(Dispatchers.IO)

    override fun saveCopy(
        callback: SaveImageCallback,
        requestCode: Int,
        layerModel: LayerContracts.Model,
        commandSerializer: CommandSerializer,
        uri: Uri?,
        context: Context
    ) {
        SaveImage(callback, requestCode, layerModel, commandSerializer, uri, true, context, scopeIO, idlingResource).execute()
    }

    override fun createFile(callback: CreateFileCallback, requestCode: Int, filename: String) {
        CreateFile(callback, requestCode, filename, scopeIO).execute()
    }

    override fun saveImage(
        callback: SaveImageCallback,
        requestCode: Int,
        layerModel: LayerContracts.Model,
        commandSerializer: CommandSerializer,
        uri: Uri?,
        context: Context
    ) {
        SaveImage(callback, requestCode, layerModel, commandSerializer, uri, false, context, scopeIO, idlingResource).execute()
    }

    override fun loadFile(
        callback: LoadImageCallback,
        requestCode: Int,
        uri: Uri?,
        context: Context,
        scaling: Boolean,
        commandSerializer: CommandSerializer,
    ) {
        LoadImage(callback, requestCode, uri, context, scaling, commandSerializer, scopeIO, idlingResource).execute()
    }
}
