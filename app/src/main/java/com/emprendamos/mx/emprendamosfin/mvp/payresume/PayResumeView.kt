package com.emprendamos.mx.emprendamosfin.mvp.payresume

import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.emprendamos.mx.emprendamosfin.extensions.editableZero
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnPaymentAddedListener
import kotlinx.android.synthetic.main.fragment_pay_resume.view.*
import java.lang.ref.WeakReference

class PayResumeView(act : View) {

    val view = WeakReference(act).get()!!

    fun init(listener: OnPaymentAddedListener?, code: String?, group: String?, cicle: String?, date: String?, type: String?, observations: String?, theorical_payment: Double, real_payment: Double, refund: Double, aport: Double, fee: Double, saving: Double, sequenceId: Long) {
        view.txtcode.text = code
        view.txtgroup.text = group
        view.txtcicle.text = String.format("%02d", cicle?.toLong() ?: 0)
        view.txtdate.text = date ?: ""
        view.txttype.text = type
        view.txtobservations.setText(observations)
        view.txtobservations.isEnabled = false

        view.tiet_theorical_payment.setText(editableZero(theorical_payment))
        view.tiet_real_payment.setText(editableZero(real_payment))
        view.tiet_return.setText(editableZero(refund))
        view.tiet_contribution.setText(editableZero(aport))
        view.tiet_fee.setText(editableZero(fee))
        view.tiet_saving.setText(editableZero(saving))
        view.tiet_total.setText(editableZero(real_payment + aport))
        view.btn_save_payment.setOnClickListener {
            listener?.onSequenceEnded(sequenceId)
        }
    }

    fun showDialog(window: Window) {
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}