package com.emprendamos.mx.emprendamosfin.mvp.payregister

import android.app.DatePickerDialog
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Client
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient
import com.emprendamos.mx.emprendamosfin.data.model.Types
import com.emprendamos.mx.emprendamosfin.extensions.EMPTY_STRING
import com.emprendamos.mx.emprendamosfin.extensions.toDate
import com.emprendamos.mx.emprendamosfin.ui.PaymentDetailsFragment
import com.emprendamos.mx.emprendamosfin.ui.adapters.ClientsByGroupListAdapter
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnActionsListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnSelectedItemInterface
import kotlinx.android.synthetic.main.fragment_pay_register.*
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar

class PayRegisterView(act: FragmentActivity) {

    val activity = WeakReference(act).get()!!

    private var type: String? = ""
    private var menuListener: OnActionsListener? = null
    var date = SimpleDateFormat("dd/MM/yyyy").format(Date())

    fun init(code: Int?, cicle: Short?, group_name: String?, menuListener: OnActionsListener? ){
        this.menuListener = menuListener

        activity.txtcode.text = activity!!.getString(R.string.cFormatoCodigoGC, code)
        activity.txtgroup.text = group_name
        activity.txtcicle.text = String.format("%02d", cicle?.toLong() ?: 0)

        activity.txtdate.text = date

        activity.txtdate.setOnClickListener {
            var cal = Calendar.getInstance()
            if (date != EMPTY_STRING) {
                cal.time = date.toDate(com.emprendamos.mx.emprendamosfin.extensions.DateFormat.dd_MM_yyyy)
            }
            DatePickerDialog(activity!!, R.style.DialogTheme, DatePickerDialog.OnDateSetListener { p0, year, month, day ->
                activity.txtdate.text = "$day/${month + 1}/$year"
                date = activity.txtdate.text.toString()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).apply { }.show()
        }

        val types = arrayOf(Types.NOT_ASIGNED, Types.S, Types.P)
        val adapter = ArrayAdapter(
                activity!!,
                R.layout.item_spinner,
                types
        )

        activity.recyclerview.layoutManager = LinearLayoutManager(activity)

        adapter.setDropDownViewResource(R.layout.item_spinner_black)
        activity.spinner_type!!.adapter = adapter

        activity.back_layout.visibility = View.GONE


    }

    fun showClientList(sequences: ArrayList<SequencePayControlClient>, onSelectedListener: OnSelectedItemInterface) {
        activity.recyclerview.adapter = ClientsByGroupListAdapter(sequences, onSelectedListener)
        activity.recyclerview.adapter.notifyDataSetChanged()
        menuListener!!.hideMenu(R.id.init_register)
    }

    fun navigateToClient(client: Client, sequence: SequencePayControlClient) {
        if ((activity.spinner_type.selectedItem as Types).name == Types.NOT_ASIGNED.name) {
            Toast.makeText(activity!!, "Debe seleccionar el tipo de registro", Toast.LENGTH_LONG).show()
        }
        else {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, PaymentDetailsFragment.newInstance(client.group_id.toInt(), client.name, client.role.toString(), sequence.theorical_payment, sequence.real_payment!!, sequence.refund, sequence.aport, sequence.fee, sequence.saving, sequence.id!!, sequence.asist, (activity.spinner_type.selectedItem as Types).name, sequence.isEdited))
                .addToBackStack("PaymentRegister")
                .commit()
        }
    }

    fun refreshDate() {
        with(activity) {
            txtdate.text = date
        }
    }
}