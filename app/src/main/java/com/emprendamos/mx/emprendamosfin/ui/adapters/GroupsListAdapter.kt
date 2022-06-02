package com.emprendamos.mx.emprendamosfin.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl
import com.emprendamos.mx.emprendamosfin.data.model.GroupPaysforCycle
import com.emprendamos.mx.emprendamosfin.extensions.EMPTY_STRING
import com.emprendamos.mx.emprendamosfin.extensions.isTemplateRegister
import com.emprendamos.mx.emprendamosfin.extensions.tup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Collections
import kotlin.collections.ArrayList

class GroupsListAdapter(var context : Context, var groups: List<GroupPaysforCycle>) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return groups!!.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return groups!![groupPosition].sequences.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return groups!![groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {

        var returned: SequencePayControl? = null
        val registers: ArrayList<SequencePayControl> = (groups!![groupPosition].sequences)

        /*Collections.sort(registers, Comparator<SequencePayControl> { lhs, rhs ->
            val lhsDate: Date = Date(lhs.paycontrol!!.date)
            val rhsDate: Date = Date(rhs.paycontrol!!.date)

            when {
                lhsDate.time == getDateTemplate().time -> -1
                rhsDate.time == getDateTemplate().time -> 1
                else -> lhsDate.compareTo(rhsDate)
            }
        })*/

        for ((i, control) in registers.withIndex()) {
            if (i == childPosition) {
                returned = control
                break
            }
        }

        returned?.apply {
            this.paycontrol?.group = groups!![groupPosition].group
        }

        return returned
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return tup(2, arrayOf(groupPosition.toLong(), childPosition.toLong()))
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        if (convertView == null) {
            val inflater: LayoutInflater

            inflater = LayoutInflater.from(context)

            convertView = inflater.inflate(R.layout.list_item, null)
        }

        val group = getGroup(groupPosition) as GroupPaysforCycle

        if (group != null) {
            var txtvista: TextView = convertView!!.findViewById<View>(R.id.txtEncabezado) as TextView

            txtvista.text = context!!.getString(R.string.cFormatoCodigoGC, group!!.group!!.code)

            txtvista = convertView.findViewById<View>(R.id.txtEncabezado2) as TextView
            txtvista.text = context!!.getString(
                    R.string.cCodigoNombre,
                    group!!.cycle,
                    group!!.group!!.name
            )

            txtvista = convertView.findViewById<View>(R.id.txtAdicional) as TextView

            var clients = group!!.group!!.clients.size

            if (clients == 0)
                clients = group!!.sequences[0].payControlSequencePayControlClient.size

            txtvista.text = context!!.getString(
                    R.string.cFormatoDescripcionGrupo,
                    clients,
                    group!!.sequences.size -1)
        }

        return convertView!!
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null)
        }

        val uncontrol: SequencePayControl? = getChild(groupPosition, childPosition) as SequencePayControl?
        val grupociclo: GroupPaysforCycle = getGroup(groupPosition) as GroupPaysforCycle

        if (uncontrol != null) {
            var txtvista: TextView

            if (isTemplateRegister(uncontrol.paycontrol!!)) {
                if (grupociclo.isNewBottonBuild) {
                    convertView!!.visibility = View.GONE

                    return convertView
                }

                convertView!!.setBackgroundColor(context!!.resources.getColor(R.color.colorSuccess))

                txtvista = convertView!!.findViewById<View>(R.id.txtEncabezado) as TextView
                txtvista.setText(R.string.cMASNuevoRegistro)
                txtvista.setBackgroundColor(context!!.resources.getColor(android.R.color.transparent))

                txtvista.visibility = View.VISIBLE

                txtvista = convertView!!.findViewById<View>(R.id.txtEncabezado2) as TextView
                txtvista.visibility = View.INVISIBLE
                txtvista.text = EMPTY_STRING

                txtvista = convertView!!.findViewById<View>(R.id.txtAdicional) as TextView
                txtvista.visibility = View.VISIBLE
                txtvista.text = EMPTY_STRING

            } else {
                convertView!!.setBackgroundColor(context!!.resources.getColor(android.R.color.white))

                txtvista = convertView!!.findViewById<View>(R.id.txtEncabezado) as TextView
                txtvista.visibility = View.INVISIBLE
                txtvista.text = EMPTY_STRING

                txtvista = convertView!!.findViewById<View>(R.id.txtEncabezado2) as TextView
                txtvista.visibility = View.VISIBLE
                txtvista.text = SimpleDateFormat("dd/MM/yyyy").format(Date(uncontrol.paycontrol!!.date))

                txtvista = convertView!!.findViewById<View>(R.id.txtAdicional) as TextView
                txtvista.visibility = View.VISIBLE
                txtvista.text = context!!.getString(
                        R.string.cFormatoControlPagoSecuenciaNumero,
                        uncontrol!!.sequence
                )
            }
        }

        return convertView!!
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun getDateTemplate(): Date {
        val date = Calendar.getInstance()

        date.set(1990, 0, 1, 0, 0, 0)
        date.set(Calendar.MILLISECOND, 0)
        return date.time
    }
}