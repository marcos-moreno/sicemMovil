package com.emprendamos.mx.emprendamosfin.mvp.payedit

import android.app.DatePickerDialog
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Client
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient
import com.emprendamos.mx.emprendamosfin.data.model.Types
import com.emprendamos.mx.emprendamosfin.extensions.DateFormat
import com.emprendamos.mx.emprendamosfin.extensions.EMPTY_STRING
import com.emprendamos.mx.emprendamosfin.extensions.getTypePosition
import com.emprendamos.mx.emprendamosfin.extensions.toDate
import com.emprendamos.mx.emprendamosfin.ui.PaymentDetailsFragment
import com.emprendamos.mx.emprendamosfin.ui.adapters.ClientsByGroupListAdapter
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnActionsListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnSelectedItemInterface
import kotlinx.android.synthetic.main.fragment_pay_register.*
import java.lang.ref.WeakReference
import java.util.Calendar

class PayEditView(act: FragmentActivity) {
    val activity = WeakReference(act).get()!!
    var mDate = EMPTY_STRING
    private var menuListener: OnActionsListener? = null

    fun init(code: Int, cicle: Short, group_name: String, date: String, type: String, observations: String, menuListener: OnActionsListener? ){

        this.menuListener = menuListener
        if(mDate == EMPTY_STRING)
            mDate = date
        with(activity) {
            txtcode.text = activity!!.getString(R.string.cFormatoCodigoGC, code)
            txtgroup.text = group_name
            txtcicle.text = String.format("%02d", cicle?.toLong() ?: 0)

            txtdate.text = date

            txtdate.setOnClickListener {
                var cal = Calendar.getInstance()
                if (date != EMPTY_STRING) {
                    cal.time = date.toDate(DateFormat.dd_MM_yyyy)
                }
                DatePickerDialog(activity!!, R.style.DialogTheme, DatePickerDialog.OnDateSetListener { p0, year, month, day ->
                    txtdate.text = "$day/${month + 1}/$year"
                    mDate = txtdate.text.toString()
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).apply { }.show()
            }

            val types = arrayOf(Types.NOT_ASIGNED, Types.S, Types.P)
            val adapter = ArrayAdapter(
                    activity!!,
                    R.layout.item_spinner,
                    types
            )
            adapter.setDropDownViewResource(R.layout.item_spinner_black)
            spinner_type!!.adapter = adapter

            if (type != null) {
                spinner_type.setSelection(getTypePosition(type!!))
            } else {
                spinner_type.setSelection(0)
            }

            txtobservations.isEnabled = true
            txtobservations.text = Editable.Factory.getInstance().newEditable(observations)

            back_layout.visibility = View.GONE
            recyclerview.layoutManager = LinearLayoutManager(activity)

        }
    }

    fun showClientList(sequencesClient: ArrayList<SequencePayControlClient>, listener: OnSelectedItemInterface) {
        activity.recyclerview.adapter = ClientsByGroupListAdapter(sequencesClient, listener)
        activity.recyclerview.adapter.notifyDataSetChanged()
        menuListener!!.showMenu(R.id.init_save)
    }



    fun navigateToClient(client: Client, sequence: SequencePayControlClient) {
        if ((activity.spinner_type.selectedItem as Types).name == Types.NOT_ASIGNED.name) {
            Toast.makeText(activity!!, "Debe seleccionar el tipo de registro", Toast.LENGTH_LONG).show()
        }
        else {
            activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.container, PaymentDetailsFragment.newInstance(client.group_id.toInt(), client.name, client.role.toString(), sequence.theorical_payment, sequence.real_payment!!, sequence.refund, sequence.aport, sequence.fee, sequence.saving, sequence.id!!, sequence.asist, (activity.spinner_type.selectedItem as Types).name, editing = true))
                    .addToBackStack("PaymentEdit")
                    .commit()
        }
    }

    fun refreshDate() {
        with(activity) {
            txtdate.text = mDate
        }
    }
}