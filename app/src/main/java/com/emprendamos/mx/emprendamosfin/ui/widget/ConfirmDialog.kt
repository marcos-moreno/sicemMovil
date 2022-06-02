package com.emprendamos.mx.emprendamosfin.ui.widget

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.emprendamos.mx.emprendamosfin.R
import kotlinx.android.synthetic.main.dialog_confirm.*

class ConfirmDialog<T> : AlertDialog {

    var cancelText = ""
    var acceptText = ""
    constructor(context : Context) : super(context)
    constructor(context: Context, cancelButtonText : String, acceptButtonText : String) : super(context) {
        acceptText = acceptButtonText
        cancelText = cancelButtonText
    }

    var cancelListener : (() -> Unit)? = null
    var acceptListener : (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm)

        btn_cancel.setOnClickListener {
            cancelListener?.invoke()
        }

        btn_action.setOnClickListener {
            acceptListener?.invoke()
        }

        setCancelButtonText(cancelText)
        setAcceptButtonText(acceptText)
    }

    fun setOnCancelListener(listener : () -> Unit)
    {
        cancelListener = listener
    }

    fun setOnAcceptListener(listener : () -> Unit)
    {
        acceptListener = listener
    }

    fun setCancelButtonText(cancelButtonText : String)
    {
        btn_cancel.text = cancelButtonText
    }

    fun setAcceptButtonText(acceptButtonText : String)
    {
        btn_action.text = acceptButtonText
    }
}