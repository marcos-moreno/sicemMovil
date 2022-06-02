package com.emprendamos.mx.emprendamosfin.mvp

import android.app.Activity
import com.emprendamos.mx.emprendamosfin.ui.widget.JTProgressDialog
import java.lang.ref.WeakReference

open class BaseView<T : Activity>(act : T) {

    val activity = WeakReference(act).get()!!

    val progressDialog by lazy {
        JTProgressDialog(activity)
    }

    fun showLoader(message : String = "Cargando...") {
        activity.runOnUiThread {
            progressDialog.show()
            progressDialog.setMessage(message)
        }
    }

    fun hideLoader() {
        activity.runOnUiThread {
            progressDialog.dismiss()
        }
    }
}
