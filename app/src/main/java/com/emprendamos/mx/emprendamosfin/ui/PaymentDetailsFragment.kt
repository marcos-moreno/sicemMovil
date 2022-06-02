package com.emprendamos.mx.emprendamosfin.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.mvp.paymentdetails.PaymentDetailsPresenter
import com.emprendamos.mx.emprendamosfin.mvp.paymentdetails.PaymentDetailsView
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnActionsListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnPaymentAddedListener

private const val ARGUMENTUMENT_CODE = "ARGUMENTUMENT_CODE"
private const val ARGUMENT_NAME = "ARGUMENT_NAME"
private const val ARGUMENT_ROLE = "ARGUMENT_ROLE"
private const val ARGUMENT_THEORICAL_PAYMENT = "ARGUMENT_THEORICAL_PAYMENT"
private const val ARGUMENT_REAL_PAYMENT = "ARGUMENT_REAL_PAYMENT"
private const val ARGUMENT_REFUND = "ARGUMENT_REFUND"
private const val ARGUMENT_CONTRIBUTION = "ARGUMENT_CONTRIBUTION"
private const val ARGUMENT_FEE = "ARGUMENT_FEE"
private const val ARGUMENT_SAVING = "ARGUMENT_SAVING"
private const val ARGUMENT_SEQUENCE_CLIENT_ID = "ARGUMENT_SEQUENCE_CLIENT_ID"
private const val ARGUMENT_EDITING = "ARGUMENT_EDITING"
private const val ARGUMENT_TYPE = "ARGUMENT_TYPE"
private const val ARGUMENT_ASISTANCE = "ARGUMENT_ASISTANCE"

class PaymentDetailsFragment : Fragment() {
    private var code: Int? = null
    private var name: String? = null
    private var role: String? = null
    private var theorical_payment = 0.0
    private var real_payment = 0.0
    private var refund = 0.0
    private var contribution = 0.0
    private var fee = 0.0
    private var saving = 0.0
    private var asistance = ""
    private var type : String? = null
    private var sequenceClientId: Long = 0
    private var editing: Boolean = false

    private var listener: OnPaymentAddedListener? = null
    private var menuListener: OnActionsListener? = null

    private val presenter by lazy {
        PaymentDetailsPresenter(PaymentDetailsView(activity!!),code,name,role,theorical_payment,real_payment,refund,contribution,fee,saving,asistance,editing,type,sequenceClientId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getInt(ARGUMENTUMENT_CODE)
            name = it.getString(ARGUMENT_NAME)
            role = it.getString(ARGUMENT_ROLE)
            theorical_payment = it.getDouble(ARGUMENT_THEORICAL_PAYMENT)
            real_payment = it.getDouble(ARGUMENT_REAL_PAYMENT)
            refund = it.getDouble(ARGUMENT_REFUND)
            contribution = it.getDouble(ARGUMENT_CONTRIBUTION)
            fee = it.getDouble(ARGUMENT_FEE)
            saving = it.getDouble(ARGUMENT_SAVING)
            sequenceClientId = it.getLong(ARGUMENT_SEQUENCE_CLIENT_ID, 0)
            asistance = it.getString(ARGUMENT_ASISTANCE, "")
            type = it.getString(ARGUMENT_TYPE, "")
            editing = it.getBoolean(ARGUMENT_EDITING, false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.init(listener, menuListener)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPaymentAddedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }


        if (context is OnActionsListener) {
            menuListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(code: Int, name: String, role: String, theorical_payment: Double, real_payment: Double, refund: Double, contribution: Double, fee: Double, saving: Double, sequenceId: Long, asistance: String, type: String?, editing: Boolean = false) =
                PaymentDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARGUMENTUMENT_CODE, code)
                        putString(ARGUMENT_NAME, name)
                        putString(ARGUMENT_ROLE, role)
                        putDouble(ARGUMENT_THEORICAL_PAYMENT, theorical_payment)
                        putDouble(ARGUMENT_REAL_PAYMENT, real_payment)
                        putDouble(ARGUMENT_REFUND, refund)
                        putDouble(ARGUMENT_CONTRIBUTION, contribution)
                        putDouble(ARGUMENT_FEE, fee)
                        putDouble(ARGUMENT_SAVING, saving)
                        putLong(ARGUMENT_SEQUENCE_CLIENT_ID, sequenceId)
                        putString(ARGUMENT_ASISTANCE, asistance)
                        putString(ARGUMENT_TYPE, type)
                        putBoolean(ARGUMENT_EDITING, editing)
                    }
                }
    }
}
