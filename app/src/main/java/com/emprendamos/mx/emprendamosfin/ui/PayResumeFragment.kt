package com.emprendamos.mx.emprendamosfin.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.extensions.toMoney
import com.emprendamos.mx.emprendamosfin.mvp.payresume.PayResumePresenter
import com.emprendamos.mx.emprendamosfin.mvp.payresume.PayResumeView
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnPaymentAddedListener
import kotlinx.android.synthetic.main.fragment_pay_resume.*

private const val ARGUMENT_CODE = "ARGUMENT_CODE"
private const val ARGUMENT_GROUP = "ARGUMENT_GROUP"
private const val ARGUMENT_CICLE = "ARGUMENT_CICLE"
private const val ARGUMENT_DATE = "ARGUMENT_DATE"
private const val ARGUMENT_TYPE = "ARGUMENT_TYPE"
private const val ARGUMENT_OBSERVATIONS = "ARGUMENT_OBSERVATIONS"
private const val ARGUMENT_THEORICAL_PAYMENT = "ARGUMENT_THEORICAL_PAYMENT"
private const val ARGUMENT_REAL_PAYMENT = "ARGUMENT_REAL_PAYMENT"
private const val ARGUMENT_REFUND = "ARGUMENT_REFUND"
private const val ARGUMENT_APORT = "ARGUMENT_APORT"
private const val ARGUMENT_FEE = "ARGUMENT_FEE"
private const val ARGUMENT_SAVING = "ARGUMENT_SAVING"
private const val ARGUMENT_SEQUENCE_ID = "ARGUMENT_SEQUENCE_ID"


class PayResumeFragment : DialogFragment() {
    private var code: String? = null
    private var group: String? = null
    private var cicle: String? = null
    private var date: String? = null
    private var type: String? = null
    private var observations: String? = null
    private var theorical_payment = 0.0
    private var real_payment= 0.0
    private var refund = 0.0
    private var aport = 0.0
    private var fee = 0.0
    private var saving = 0.0
    private var sequenceId = 0L
    private var mView : View? = null

    private var listener: OnPaymentAddedListener? = null

    private val presenter by lazy {
        PayResumePresenter(PayResumeView(mView!!),code,group,cicle,date,type,observations,theorical_payment,real_payment,refund,aport,fee,saving, sequenceId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getString(ARGUMENT_CODE, "")
            group = it.getString(ARGUMENT_GROUP, "")
            cicle = it.getString(ARGUMENT_CICLE, "")
            date = it.getString(ARGUMENT_DATE, "")
            type = it.getString(ARGUMENT_TYPE, "")
            observations = it.getString(ARGUMENT_OBSERVATIONS, "")
            theorical_payment = it.getDouble(ARGUMENT_THEORICAL_PAYMENT)
            real_payment = it.getDouble(ARGUMENT_REAL_PAYMENT)
            refund = it.getDouble(ARGUMENT_REFUND)
            aport = it.getDouble(ARGUMENT_APORT)
            fee = it.getDouble(ARGUMENT_FEE)
            saving = it.getDouble(ARGUMENT_SAVING)
            sequenceId = it.getLong(ARGUMENT_SEQUENCE_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pay_resume, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        presenter.init(listener)
    }

     override fun onStart() {
        super.onStart()
        presenter.showDialog(dialog.window)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPaymentAddedListener) {
            listener = context
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
        fun newInstance(code : String, group : String, cicle : String, date : String, type : String, observations: String, theo_payment : Double, real_payment : Double, refund : Double, aport : Double, fee : Double, saving : Double, sequenceId : Long) =
                PayResumeFragment().apply {
                    setStyle(DialogFragment.STYLE_NO_TITLE,0)
                    arguments = Bundle().apply {
                        putString( ARGUMENT_CODE, code)
                        putString( ARGUMENT_GROUP, group)
                        putString( ARGUMENT_CICLE, cicle)
                        putString( ARGUMENT_DATE, date)
                        putString( ARGUMENT_TYPE, type)
                        putString( ARGUMENT_OBSERVATIONS, observations)
                        putDouble( ARGUMENT_THEORICAL_PAYMENT, theo_payment)
                        putDouble( ARGUMENT_REAL_PAYMENT, real_payment)
                        putDouble( ARGUMENT_REFUND, refund)
                        putDouble( ARGUMENT_APORT, aport)
                        putDouble( ARGUMENT_FEE, fee)
                        putDouble( ARGUMENT_SAVING, saving)
                        putLong( ARGUMENT_SEQUENCE_ID, sequenceId)
                    }
                }
    }
}
