package com.emprendamos.mx.emprendamosfin.data.database.repository.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.emprendamos.mx.emprendamosfin.extensions.findNodeByName
import org.w3c.dom.NodeList

@Entity(tableName = "clients")
data class Client(

        @PrimaryKey(autoGenerate = true)
        var id: Long?,

        // GCComun
        @ColumnInfo(name = "code")
        var code: Int,

        @ColumnInfo(name = "name")
        var name: String,
        // End GCComun

        @ColumnInfo(name = "group_id")
        var group_id: Long,

        @ColumnInfo(name = "role")
        var role: Char

){
    @Ignore
    var group: Group? = null
    set(value) {
        field = value
        group_id = value?.id ?: 0
    }

    @Ignore
    var sequencePayControlCliente : SequencePayControlClient? = null

    constructor():this(null,0,"",0, ' ')

    fun xml2Client(nodeList: NodeList) {

        code = nodeList.findNodeByName("CDGCL")?.textContent?.toInt()?:0
        name = nodeList.findNodeByName("CLIENTE")?.textContent?.toString()?:""
        var tmp_role = nodeList.findNodeByName("PUESTO")?.textContent?.toString()?:""


        if (!tmp_role.isNullOrBlank())
            role = tmp_role.get(0)
        else
            role = ' '
    }

    fun obj2xml() : String {
        val builder = StringBuilder()

        builder.append(String.format("<CDGCL>%06d</CDGCL>", code )).append('\n')
        builder.append(String.format("<CLIENTE>%s</CLIENTE>", name)).append('\n')

        builder.append(String.format("<PUESTO>%c</PUESTO>", role)).append('\n')

        return builder.toString()
    }
}