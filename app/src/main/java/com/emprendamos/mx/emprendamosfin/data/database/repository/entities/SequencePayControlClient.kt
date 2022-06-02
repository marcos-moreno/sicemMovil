package com.emprendamos.mx.emprendamosfin.data.database.repository.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.emprendamos.mx.emprendamosfin.extensions.DateFormat
import com.emprendamos.mx.emprendamosfin.extensions.findNodeByName
import com.emprendamos.mx.emprendamosfin.extensions.getTypeKey
import com.emprendamos.mx.emprendamosfin.extensions.tup
import org.w3c.dom.NodeList
import java.text.SimpleDateFormat
import java.util.Date

@Entity(tableName = "sequence_pay_controls_client")
data class SequencePayControlClient (

        @PrimaryKey(autoGenerate = true) var id: Long?,

        @ColumnInfo(name = "sequencePayControl_id")
        var sequencepaycontrol_id: Long,

        @ColumnInfo(name = "client_id")
        var client_id: Long,
        
        @ColumnInfo(name = "total")
        var total: Double,
        
        @ColumnInfo(name = "cantentre")
        var cantentre: Double,
        
        @ColumnInfo(name = "parciality")
        var parciality: Double,
        
        @ColumnInfo(name = "asist")
        var asist: String

) : PayControlCommon() {

    @Ignore
    var isEdited = false

    @Ignore
    var client: Client? = null
    set(value) {
        field = value
        client_id = value?.code?.toLong() ?: 0
    }

    @Ignore
    var sequencePayControl: SequencePayControl? = null
        set(value) {
            field = value
            sequencepaycontrol_id = value?.id ?: 0
        }

    fun getKey() : Long {
        id = tup(2,arrayOf(sequencepaycontrol_id!!.toLong(),client_id))
        return id!!
    }

    constructor():this(
        null,
        0L,
            0,
        0.0,
        0.0,
        0.0,
        ""
        )

    override fun xml2Obj(nodeList: NodeList) {
        super.xml2Obj(nodeList)

        total = nodeList.findNodeByName("TOTAL_A_PAGAR")?.textContent?.toDouble()?:0.0
        cantentre = nodeList.findNodeByName("CANTENTRE")?.textContent?.toDouble()?:0.0
        parciality = nodeList.findNodeByName("CANTENTRE")?.textContent?.toDouble()?:0.0

        asist = nodeList.findNodeByName("ASIST")?.textContent?.toString()?:"Desconocido"

        client = Client()
        client?.xml2Client(nodeList)

        //sequencePayControl = SequencePayControl()
        //sequencePayControl?.xml2Obj(nodeList)

    }

    fun obj2xml(header: Boolean): String {
        val builder = StringBuilder()

        builder.append(String.format("<FECHA>%s</FECHA>", SimpleDateFormat(DateFormat.dd_MM_yyyy.format).format(Date(sequencePayControl!!.paycontrol!!.date)) )).append('\n')

        builder.append(sequencePayControl!!.paycontrol!!.group!!.obj2xml(header))

        builder.append(String.format("<CICLO>%02d</CICLO>", sequencePayControl!!.paycontrol!!.cycle)).append('\n')
        builder.append(String.format("<SECUENCIA>%02d</SECUENCIA>", sequencePayControl!!.sequence)).append('\n')
        builder.append(String.format("<SEMANA>%02d</SEMANA>", sequencePayControl!!.sequence)).append('\n')

        builder.append(client!!.obj2xml())

        builder.append(super.payControl2xml(header))

        builder.append(String.format("<FECANT>%s</FECANT>", SimpleDateFormat(DateFormat.dd_MM_yyyy.format).format(Date(sequencePayControl!!.fecant)) )).append('\n')

        builder.append(String.format("<TOTAL_A_PAGAR>%.2f</TOTAL_A_PAGAR>", total)).append('\n')
        builder.append(String.format("<CANTENTRE>%.2f</CANTENTRE>", cantentre)).append('\n')
        builder.append(String.format("<PARCIALIDAD>%.2f</PARCIALIDAD>", parciality)).append('\n')
        builder.append(String.format("<ASIST>%s</ASIST>", asist.replace("_"," ").replace("Mando", "Mandó"))).append('\n')

        builder.append(String.format("<TIPOREG>%s</TIPOREG>", sequencePayControl!!.paycontrol!!.type)).append('\n')
        builder.append(String.format("<TIPO>%s</TIPO>", getTypeKey(sequencePayControl!!.paycontrol!!.type))).append('\n')


        return builder.toString()
    }

}