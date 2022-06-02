package com.emprendamos.mx.emprendamosfin.data.database.repository.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.emprendamos.mx.emprendamosfin.data.model.Types
import com.emprendamos.mx.emprendamosfin.extensions.*
import org.w3c.dom.NodeList
import java.text.SimpleDateFormat
import java.util.Date


@Entity(tableName = "sequence_pay_controls")
data class SequencePayControl (

    @PrimaryKey(autoGenerate = true)
    var id: Long?,

    //PayControlPre
    @ColumnInfo(name = "real_payment")
    var real_payment: Double,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "local_modification")
    var local_modification: Boolean,
    //End PayControl Pre

    @ColumnInfo(name = "paycontrol_id")
    var paycontrol_id: Long?,

    @ColumnInfo(name = "sequence")
    var sequence: Short,

    @ColumnInfo(name = "fecreg")
    var fecreg: Long?,

    @ColumnInfo(name = "week")
    var week: Short,

    @ColumnInfo(name = "fecant")
    var fecant: Long,

    @ColumnInfo(name = "sincronized")
    var sincronized: Boolean?,

    @ColumnInfo(name = "observation")
    var observation: String,

    @ColumnInfo(name = "sequencepaycontrolclient_id")
    var sequencepaycontrolclient_id : Long?,

    @ColumnInfo(name = "theorical_payment")
    var theorical_payment: Double = 0.0,

    @ColumnInfo(name = "aport")
    var aport: Double = 0.0,

    @ColumnInfo(name = "refund")
    var refund: Double = 0.0,

    @ColumnInfo(name = "saving")
    var saving: Double = 0.0,

    @ColumnInfo(name = "fee")
    var fee: Double = 0.0


){
    @Ignore
    var paycontrol: PayControl? = null
    set(value) {
        field = value
        paycontrol_id = value?.id ?: 0
    }

    @Ignore
    var sequencepaycontrolclient: SequencePayControlClient? = null
        set(value) {
            field = value
            sequencepaycontrolclient_id = value?.id ?: 0
        }

    @Ignore
    var payControlSequencePayControlClient = arrayListOf<SequencePayControlClient>()

    constructor():this(
        null,
        0.0,
        "",
        false,
        null,
        0,
        0L,
        0,
        0L,
        null,
        "",
        null)

    fun getKey() : Long {
        id = tup(2,arrayOf(paycontrol_id!!.toLong(),sequence.toLong()))
        return id!!
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

    fun xml2Obj(nodeList: NodeList) {

        sequence = nodeList.findNodeByName("SECUENCIA")?.textContent?.toShort()?:0

        var temp_type = nodeList.findNodeByName("TIPO")?.textContent ?: "Z"
        type = getType(temp_type).description

        fecreg = nodeList.findNodeByName("FECREG")?.textContent?.toDate(DateFormat.dd_MM_yyyy)?.time ?: Date(0).time
        fecant = nodeList.findNodeByName("FECANT")?.textContent?.toDate(DateFormat.dd_MM_yyyy)?.time ?: Date(0).time
        week = nodeList.findNodeByName("SEMANA")?.textContent?.toShort()?:0
        observation = nodeList.findNodeByName("OBSERVACION")?.textContent?.toString()?:""

        paycontrol = PayControl()
        paycontrol?.xml2Obj(nodeList)

        sequencepaycontrolclient = SequencePayControlClient()
        sequencepaycontrolclient?.xml2Obj(nodeList)

    }

    fun obj2xml(header: Boolean): String {
        val builder = StringBuilder()
        val observation: String
        val maxchars = 256

        if (this.observation.length > maxchars)
            observation = this.observation.substring(0, maxchars)
        else
            observation = this.observation

        builder.append(paycontrol!!.payControl2xml(header))

        builder.append(String.format("<SEMANA>%02d</SEMANA>",week)).append('\n')
        builder.append(String.format("<FECANT>%s</FECANT>", SimpleDateFormat(DateFormat.dd_MM_yyyy.format).format(Date(fecant)) )).append('\n')
        builder.append(String.format("<SECUENCIA>%02d</SECUENCIA>", sequence)).append('\n')

        builder.append(String.format("<TIPO>%s</TIPO>", getTypeKey(type))).append('\n')
        builder.append(String.format("<TIPOREG>%s</TIPOREG>", type)).append('\n')

        builder.append(String.format("<PAGOREAL>%.2f</PAGOREAL>", real_payment)).append('\n')
        var delay_payment = theorical_payment - (real_payment!! + aport)
        builder.append(String.format("<MORA>%.2f</MORA>", delay_payment)).append('\n')
        builder.append(String.format("<PAGOTEO>%.2f</PAGOTEO>", theorical_payment)).append('\n')
        builder.append(String.format("<APORT>%.2f</APORT>", aport)).append('\n')
        builder.append(String.format("<DEVOLUCION>%.2f</DEVOLUCION>", refund)).append('\n')
        builder.append(String.format("<AHORRO>%.2f</AHORRO>", saving)).append('\n')
        builder.append(String.format("<MULTA>%.2f</MULTA>", fee)).append('\n')

        builder.append(String.format("<FECREG>%s</FECREG>", SimpleDateFormat(DateFormat.dd_MM_yyyy_HH_mm.format).format(Date(fecreg!!)) )).append('\n')
        builder.append(String.format("<OBSERVACION>%s</OBSERVACION>", observation)).append('\n')

        return builder.toString()
    }
}