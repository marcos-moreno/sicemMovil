package com.emprendamos.mx.emprendamosfin.data.database.repository.entities

import android.arch.persistence.room.ColumnInfo
import com.emprendamos.mx.emprendamosfin.data.database.repository.mapper.MapperManager
import com.emprendamos.mx.emprendamosfin.data.model.Types
import com.emprendamos.mx.emprendamosfin.extensions.DateFormat
import com.emprendamos.mx.emprendamosfin.extensions.findNodeByName
import com.emprendamos.mx.emprendamosfin.extensions.toDate
import org.w3c.dom.NodeList
import java.text.SimpleDateFormat
import java.util.Date

open class PayControlCommon {

    //PayControlPre
    @ColumnInfo(name = "real_payment")
    var real_payment: Double? = 0.0

    @ColumnInfo(name = "type")
    var type: String = ""

    @ColumnInfo(name = "local_modification")
    var local_modification: Boolean = false
    //End PayControl Pre

    @ColumnInfo(name = "order_date")
    var order_date: Long = 0

    @ColumnInfo(name = "theorical_payment")
    var theorical_payment: Double = 0.0

    @ColumnInfo(name = "aport")
    var aport: Double = 0.0

    @ColumnInfo(name = "refund")
    var refund: Double = 0.0

    @ColumnInfo(name = "saving")
    var saving: Double = 0.0

    @ColumnInfo(name = "fee")
    var fee: Double = 0.0

    @ColumnInfo(name = "status")
    var status: Short = 0

    open fun xml2Obj(nodeList: NodeList) {
        real_payment = MapperManager.findNodeByName(nodeList, "PAGOREAL")?.textContent?.toDouble() ?: 0.0

        var temp_type = nodeList.findNodeByName("TIPO")?.textContent ?: "Z"
        type = getType(temp_type).description

        order_date = nodeList.findNodeByName("FECHAORD")?.textContent?.toDate(DateFormat.yyyyMMdd)?.time ?: Date(0).time

        status = nodeList.findNodeByName("ESTATUS")?.textContent?.toShort() ?: 0

        theorical_payment = nodeList.findNodeByName("PAGOTEO")?.textContent?.toDouble() ?: nodeList.findNodeByName("PARCIALIDAD")?.textContent?.toDouble() ?: 0.0

        aport = nodeList.findNodeByName("APORT")?.textContent?.toDouble() ?: 0.0
        refund = nodeList.findNodeByName("DEVOLUCION")?.textContent?.toDouble() ?: 0.0
        saving = nodeList.findNodeByName("AHORRO")?.textContent?.toDouble() ?: 0.0
        fee = nodeList.findNodeByName("MULTA")?.textContent?.toDouble() ?: 0.0
    }

    open fun payControl2xml(header: Boolean): String {
        val constructor = StringBuilder()

        constructor.append(String.format("<FECHAORD>%s</FECHAORD>", SimpleDateFormat(DateFormat.yyyyMMdd.format).format(Date(order_date)) )).append('\n')
        if (!header) {
            constructor.append(String.format("<PAGOREAL>%.2f</PAGOREAL>", real_payment)).append('\n')
            constructor.append(String.format("<PAGOTEO>%.2f</PAGOTEO>", theorical_payment)).append('\n')
            constructor.append(String.format("<APORT>%.2f</APORT>", aport)).append('\n')
            constructor.append(String.format("<DEVOLUCION>%.2f</DEVOLUCION>", refund)).append('\n')
            constructor.append(String.format("<AHORRO>%.2f</AHORRO>", saving)).append('\n')
            constructor.append(String.format("<MULTA>%.2f</MULTA>", fee)).append('\n')
        }
        constructor.append(String.format("<ESTATUS>%d</ESTATUS>", status)).append('\n')

        return constructor.toString()
    }

    fun getType(temp_type : String) : Types {
        var enum_type = Types.NOT_ASIGNED
        val values = Types.values()

        for (value in values) {
            if (value.identification === temp_type.get(0)) {
                enum_type = value
                break
            }
        }

        return enum_type
    }

}
