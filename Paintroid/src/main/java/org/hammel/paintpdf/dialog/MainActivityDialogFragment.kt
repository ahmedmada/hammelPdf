package org.hammel.paintpdf.dialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import org.hammel.paintpdf.contract.MainActivityContracts
import org.hammel.paintpdf.contract.MainActivityContracts.MainView

open class MainActivityDialogFragment : AppCompatDialogFragment() {
    lateinit var presenter: MainActivityContracts.Presenter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = requireActivity() as MainView
        presenter = activity.presenter
    }
}
