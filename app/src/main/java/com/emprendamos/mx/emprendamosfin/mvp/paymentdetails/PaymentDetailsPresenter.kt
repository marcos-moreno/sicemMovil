package com.emprendamos.mx.emprendamosfin.mvp.paymentdetails

import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnActionsListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnPaymentAddedListener

class PaymentDetailsPresenter(
        val view: PaymentDetailsView,
        val code: Int?,
        val name: String?,
        val role: String?,
        val theorical_payment: Double,
        val real_payment: Double,
        val refund: Double,
        val contribution: Double,
        val fee: Double,
        val saving: Double,
        val asistance: String,
        val editing: Boolean,
        val type : String?,
        val sequenceClientId: Long
){
    fun init(listener: OnPaymentAddedListener?, menuListener: OnActionsListener?){
        view.init(listener, code,name,role,theorical_payment,real_payment,refund,contribution,fee,saving,asistance,editing,type,sequenceClientId, menuListener)
    }
}
