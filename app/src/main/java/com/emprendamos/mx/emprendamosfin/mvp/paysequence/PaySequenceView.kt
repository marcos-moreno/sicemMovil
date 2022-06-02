package com.emprendamos.mx.emprendamosfin.mvp.paysequence

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl
import com.emprendamos.mx.emprendamosfin.data.model.Types
import com.emprendamos.mx.emprendamosfin.ui.adapters.SequencesByGroupListAdapter
import com.emprendamos.mx.emprendamosfin.extensions.getTypePosition
import com.emprendamos.mx.emprendamosfin.ui.PayEditFragment
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnSelectedItemInterface
import kotlinx.android.synthetic.main.fragment_paysequence.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date

class PaySequenceView(act : FragmentActivity) {

    val activity = WeakReference(act).get()!!

    fun init(code: Int?, cicle: Short?, group_name: String?, date: String?, type: String?, observations: String?){
        activity.txtcode.text = activity!!.getString(R.string.cFormatoCodigoGC,code)
        activity.txtgroup.text = group_name
        activity.txtcicle.text = String.format("%02d", cicle?.toLong() ?: 0)

        activity.txtdate.text = date ?: ""

        val types = arrayOf(Types.NOT_ASIGNED, Types.S, Types.P)
        val adapter = ArrayAdapter(
                activity!!,
                R.layout.item_spinner,
                types
        )
        activity.txtobservations.isEnabled = false
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activity.spinner_type!!.adapter = adapter

        if(type != null) {
            activity.spinner_type.setSelection(getTypePosition(type!!))
        } else {
            activity.spinner_type.setSelection(0)
        }

        activity.spinner_type.isEnabled = false
        activity.txtdate.isEnabled = false

        activity.txtobservations.text = Editable.Factory.getInstance().newEditable(observations)

        activity.back_layout.visibility = View.GONE
        activity.recyclerview.layoutManager = LinearLayoutManager(activity)
    }

    fun setGroups(sequences : ArrayList<SequencePayControl>, date: Long, listener : OnSelectedItemInterface) {
        activity.recyclerview.adapter = SequencesByGroupListAdapter(sequences, date, listener)
    }

    fun navigateToFragment(code: Int?, cicle: Short?, group_name: String?, date: String?, type: String?, paycontrol_id : Long, sequence : Short) {
        activity.supportFragmentManager?.beginTransaction()?.replace(R.id.container, PayEditFragment.newInstance(code!!, cicle!!, group_name!!, paycontrol_id, date!!, activity.spinner_type.selectedItem.toString(), activity.txtobservations.text.toString(), sequence))?.commit()

    }

}