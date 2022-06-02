package com.emprendamos.mx.emprendamosfin.data.database.repository.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.emprendamos.mx.emprendamosfin.extensions.DateFormat
import com.emprendamos.mx.emprendamosfin.extensions.findNodeByName
import com.emprendamos.mx.emprendamosfin.extensions.toDate
import com.emprendamos.mx.emprendamosfin.extensions.tup
import org.w3c.dom.NodeList
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "paycontrols")
data class PayControl(

        @PrimaryKey(autoGenerate = false)
        var id: Long?,

        @ColumnInfo(name = "date")
        var date: Long,

        @ColumnInfo(name = "group_id")
        var group_id: Int,

        @ColumnInfo(name = "cycle")
        var cycle: Short,

        @ColumnInfo(name = "init_date")
        var init_date: Long?,

        @ColumnInfo(name = "last_date")
        var last_date: Long?,

        @ColumnInfo(name = "advisor_name")
        var advisor_name: String,

        @ColumnInfo(name = "manager_name")
        var manager_name: String,

        @ColumnInfo(name = "coordination")
        var coordination: String,

        @ColumnInfo(name = "delay_payment")
        var delay_payment: Double,

        @ColumnInfo(name = "week1")
        var week1: Short,

        @ColumnInfo(name = "active")
        var active: String,

        @ColumnInfo(name = "secuency_paycontrol_id")
        var secuency_paycontrol_id: Long

) : PayControlCommon() {

    @Ignore
    var group: Group? = null
        set(value) {
            field = value
            group_id = value?.code ?: 0
        }

    companion object {
        val NEW_NO_SYNC: Short = -1
        val NEW_WEEK1: Short = 1
        val EDITED_WEEK1: Short = 2
    }


    @Ignore
    var sequences : ArrayList<SequencePayControl> = arrayListOf()

    constructor() : this(null, 0L, 0, 0, null, null, "", "", "", 0.0, 0, "", 0L)

    override fun xml2Obj(nodeList: NodeList) {
        super.xml2Obj(nodeList)

        date = nodeList.findNodeByName("FECHA")?.textContent?.toDate(DateFormat.dd_MM_yyyy)?.time ?: Date(0).time

        cycle = nodeList.findNodeByName("CICLO")?.textContent?.toShort() ?: 0
        active = nodeList.findNodeByName("ACTIVO")?.textContent ?: ""
        coordination = nodeList.findNodeByName("COORDINACION")?.textContent ?: ""

        last_date = nodeList.findNodeByName("FECHAFIN")?.textContent?.toDate(DateFormat.yyyy_MM_dd)?.time ?: Date(0).time
        init_date = nodeList.findNodeByName("INICIO")?.textContent?.toDate(DateFormat.yyyy_MM_dd)?.time ?: Date(0).time

        delay_payment = nodeList.findNodeByName("MORA")?.textContent?.toDouble() ?: 0.0
        advisor_name = nodeList.findNodeByName("NOMBREASESOR")?.textContent ?: ""
        week1 = nodeList.findNodeByName("SEMANA1")?.textContent?.toShort() ?: -1

        manager_name = nodeList.findNodeByName("NOMBREGERENTE")?.textContent ?: ""

    }

    override fun payControl2xml(header: Boolean): String {
        val builder = StringBuilder()

        builder.append(String.format("<FECHA>%s</FECHA>", SimpleDateFormat(DateFormat.dd_MM_yyyy.format).format(Date(date)) )).append('\n')

        builder.append(group!!.obj2xml(header))

        builder.append(String.format("<CICLO>%02d</CICLO>", cycle)).append('\n')
        builder.append(String.format("<INICIO>%s</INICIO>", SimpleDateFormat(DateFormat.dd_MM_yyyy.format).format(Date(init_date!!)) )).append('\n')
        builder.append(String.format("<FECHAFIN>%s</FECHAFIN>",SimpleDateFormat(DateFormat.dd_MM_yyyy.format).format(Date(last_date!!)) )).append('\n')
        builder.append(String.format("<NOMBREASESOR>%s</NOMBREASESOR>", advisor_name)).append('\n')
        builder.append(String.format("<NOMBREGERENTE>%s</NOMBREGERENTE>", manager_name)).append('\n')
        builder.append(String.format("<COORDINACION>%s</COORDINACION>", coordination)).append('\n')

        builder.append(String.format("<SEMANA1>%s</SEMANA1>", week1)).append('\n')
        builder.append(String.format("<ACTIVO>%s</ACTIVO>", active)).append('\n')
        builder.append(super.payControl2xml(header))

        return builder.toString()
    }

    fun getKey() : Long {
        var key = arrayOf(group_id.toLong(), cycle.toLong(), date)
        id = tup(key.size,key)
        return id!!
    }

}