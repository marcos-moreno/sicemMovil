package com.emprendamos.mx.emprendamosfin.mvp.paymentdetails

import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.data.model.Asistances
import com.emprendamos.mx.emprendamosfin.extensions.editableZero
import com.emprendamos.mx.emprendamosfin.extensions.getAsistancePosition
import com.emprendamos.mx.emprendamosfin.extensions.toMoney
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnActionsListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnPaymentAddedListener
import kotlinx.android.synthetic.main.fragment_payment_details.*
import java.lang.ref.WeakReference
import java.text.DecimalFormat

class PaymentDetailsView(act: FragmentActivity) {

    val activity = WeakReference(act).get()!!

    fun init(listener: OnPaymentAddedListener?, code: Int?, name: String?, role: String?, theorical_payment: Double, real_payment: Double, refund: Double, contribution: Double, fee: Double, saving: Double, asistance: String, editing: Boolean, type: String?, sequenceClientId: Long, menuListener: OnActionsListener?) {

        menuListener!!.hideMenu(R.id.init_save)
        menuListener!!.hideMenu(R.id.init_register)

        activity.txt_code.text = activity!!.getString(R.string.cFormatoCodigoGC, code)
        activity.txt_name.text = name
        activity.txt_role.text = role

        activity.tiet_theorical_payment.setText(theorical_payment.toString().toMoney())

        if (editing) {
            activity.tiet_difference.setText((theorical_payment - real_payment).toString().toMoney())
            activity.tiet_real_payment.setText(editableZero(real_payment))
            activity.tiet_return.setText(editableZero(refund))
            activity.tiet_contribution.setText(editableZero(contribution))
            activity.tiet_fee.setText(editableZero(fee))
            activity.tiet_saving.setText(editableZero(saving))
        }

        activity.tiet_real_payment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val cleanString = editable?.toString()
                if (!cleanString.isNullOrEmpty()) {
                    activity.tiet_difference.setText(DecimalFormat("0.00").format((activity.tiet_theorical_payment.text.toString().toDouble() - cleanString!!.toDouble()).toString().toDouble()))
                } else
                    activity.tiet_difference.setText(DecimalFormat("0.00").format(0))
            }
        })

        val adapter = ArrayAdapter(
                activity!!,
                R.layout.item_spinner_black,
                Asistances.values()
        )
        activity.spinner_assistance!!.adapter = adapter

        if(editing) {
            activity.spinner_assistance.setSelection( getAsistancePosition (asistance)  )
        } else {
            activity.spinner_assistance.setSelection( getAsistancePosition ("Desconocido")  )
        }

        activity.btn_save_payment.setOnClickListener {

            if (activity.tiet_real_payment.text.toString().isEmpty() ||
                    activity.tiet_return.text.toString().isEmpty() ||
                    activity.tiet_contribution.text.toString().isEmpty() ||
                    activity.tiet_fee.text.toString().isEmpty() ||
                    activity.tiet_saving.text.toString().isEmpty() )
            {
                Toast.makeText(activity!!, "Completa todos los campos para continuar", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(type!! == "S"){
                val name = (activity.spinner_assistance.selectedItem as Asistances).name
                if (name.isNullOrEmpty() || name == "Desconocido") {
                    Toast.makeText(activity!!, "El campo asistencia es requerido para el tipo Semanal", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            listener?.onPaymentEdited(
                    sequenceClientId,
                    activity.tiet_real_payment.text.toString().toDouble(),
                    activity.tiet_return.text.toString().toDouble(),
                    activity.tiet_contribution.text.toString().toDouble(),
                    activity.tiet_fee.text.toString().toDouble(),
                    activity.tiet_saving.text.toString().toDouble(),
                    (activity.spinner_assistance.selectedItem as Asistances).name
            )
            activity.supportFragmentManager.popBackStack()
        }

        activity.tiet_saving.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                activity.main_content.post {
                    activity.main_content.fullScroll(View.FOCUS_DOWN)
                }
                true
            }
            false
        }
    }
}