package com.emprendamos.mx.emprendamosfin.data.database.repository.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.emprendamos.mx.emprendamosfin.data.database.repository.mapper.MapperManager
import org.w3c.dom.NodeList

@Entity(tableName = "groups")
data class Group(

    @PrimaryKey(autoGenerate = true)
    var id: Long?,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "code")
    var code: Int,

    @ColumnInfo(name = "client_id")
    var client_id: Long?,

    @ColumnInfo(name = "paycontrol_id")
    var paycontrol_id: Long?

){

    @Ignore
    var payControls : ArrayList<PayControl> = arrayListOf()

    @Ignore
    var clients : ArrayList<Client> = arrayListOf()

    constructor():this(null,"", 0, 0L, 0L)

    fun xml2Group(nodeList : NodeList)
    {
        var node = MapperManager.findNodeByName(nodeList, "GRUPO")

        code = MapperManager.findNodeByName(nodeList, "CDGNS")?.textContent?.toInt() ?: 0

        node?.let{
            name = node.textContent
        }
    }

    fun obj2xml(header: Boolean): String {
        val constructor = StringBuilder()

        constructor.append(String.format("<CDGNS>%06d</CDGNS>", code)).append('\n')
        if(header)
            constructor.append(String.format("<GRUPO>%s</GRUPO>", name)).append('\n')

        return constructor.toString()
    }

}