package com.emprendamos.mx.emprendamosfin.ui.widget

import android.content.Context
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import com.emprendamos.mx.emprendamosfin.R
import kotlinx.android.synthetic.main.dialog_progress.*

class JTProgressDialog(context: Context) : AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)
        setCancelable(false)
        progressBar.indeterminateDrawable.setColorFilter(
                ResourcesCompat.getColor(context.resources,R.color.colorPrimaryDark,null),
                android.graphics.PorterDuff.Mode.SRC_IN)

    }

    fun setMessage(message : String)
    {
        text_progress.text = message
    }
}