package com.emprendamos.mx.emprendamosfin.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Client
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient
import com.emprendamos.mx.emprendamosfin.mvp.payedit.PayEditPresenter
import com.emprendamos.mx.emprendamosfin.mvp.payedit.PayEditView
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnActionsListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnPaymentAddedListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnSelectedItemInterface


private const val ARGUMENT_CODE = "ARGUMENT_CODE"
private const val ARGUMENT_SEQUENCE = "ARGUMENT_SEQUENCE"
private const val ARGUMENT_CICLE = "ARGUMENT_CICLE"
private const val ARGUMENT_GROUP = "ARGUMENT_GROUP"
private const val ARGUMENT_PAYCONTROL = "ARGUMENT_PAYCONTROL"
private const val ARGUMENT_DATE = "ARGUMENT_DATE"
private const val ARGUMENT_TYPE = "ARGUMENT_TYPE"
private const val ARGUMENT_OBSERVATIONS = "ARGUMENT_OBSERVATIONS"

class PayEditFragment : Fragment(), OnSelectedItemInterface {
    override fun onSequenceSelected(sequence: SequencePayControl) { }

    private var code: Int? = null
    private var cicle: Short? = null
    private var group_name: String? = null
    private var paycontrol_id: Long = 0L
    private var date: String? = null
    private var type: String? = null
    private var observations: String? = null
    private var sequence: Short = 0

    private var listener: OnPaymentAddedListener? = null
    private var menuListener: OnActionsListener? = null

    private val presenter by lazy {
        PayEditPresenter(PayEditView(activity!!), code!!,cicle!!,group_name!!, paycontrol_id, date!!, type!!, observations!!, sequence)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getInt(ARGUMENT_CODE)
            cicle = it.getShort(ARGUMENT_CICLE)
            group_name = it.getString(ARGUMENT_GROUP)
            paycontrol_id = it.getLong(ARGUMENT_PAYCONTROL)
            date = it.getString(ARGUMENT_DATE)
            type = it.getString(ARGUMENT_TYPE)
            observations = it.getString(ARGUMENT_OBSERVATIONS)
            sequence = it.getShort(ARGUMENT_SEQUENCE, 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_pay_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.init(listener,menuListener)
        presenter.onSelectedListener = this

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

    override fun onResume() {
        super.onResume()
        presenter.refreshDate()
    }


    override fun onClientSelected(client: Client, sequence: SequencePayControlClient) {
        presenter.navigateToClient(client, sequence)
    }

    companion object {
        @JvmStatic
        fun newInstance(code: Int, cicle: Short, group_name: String, paycontrol_id: Long, date: String, type: String, observations: String, sequence: Short = 0) =
                PayEditFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARGUMENT_CODE, code)
                        putShort(ARGUMENT_SEQUENCE, sequence)
                        putShort(ARGUMENT_CICLE, cicle)
                        putString(ARGUMENT_GROUP, group_name)
                        putLong(ARGUMENT_PAYCONTROL, paycontrol_id)// rest of two data
                        putString(ARGUMENT_DATE, date)
                        putString(ARGUMENT_TYPE, type)
                        putString(ARGUMENT_OBSERVATIONS, observations)
                    }
                }
    }
}
