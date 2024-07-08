package org.hammel.paintpdf.ui

import android.content.Context
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.ui.ToastFactory.makeText

class LayerNavigator(private val context: Context) : LayerContracts.Navigator {
    override fun showToast(id: Int, length: Int) {
        makeText(context, id, length).show()
    }
}
