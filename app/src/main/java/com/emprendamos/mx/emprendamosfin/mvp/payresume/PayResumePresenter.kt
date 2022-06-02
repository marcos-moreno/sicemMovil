package com.emprendamos.mx.emprendamosfin.mvp.payresume

import android.view.View
import android.view.Window
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnPaymentAddedListener

class PayResumePresenter(
        var view: PayResumeView,
        var code: String?,
        var group: String?,
        var cicle: String?,
        var date: String?,
        var type: String?,
        var observations: String?,
        var theorical_payment: Double,
        var real_payment: Double,
        var refund: Double,
        var aport: Double,
        var fee: Double,
        var saving: Double,
        var sequenceId: Long
) {
    fun init(listener: OnPaymentAddedListener?) {
        view.init(listener,code,group,cicle,date,type,observations,theorical_payment,real_payment,refund,aport,fee,saving, sequenceId)

    }

    fun showDialog(window: Window) {
        view.showDialog(window)
    }
}