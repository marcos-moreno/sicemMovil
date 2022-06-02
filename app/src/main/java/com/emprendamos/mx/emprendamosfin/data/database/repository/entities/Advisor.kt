package com.emprendamos.mx.emprendamosfin.data.database.repository.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "advisors")
data class Advisor(

        @PrimaryKey(autoGenerate = true) var id: Long?,

        @ColumnInfo(name = "company_code")
        var company_code: String,

        @ColumnInfo(name = "code")
        var code: String,

        @ColumnInfo(name = "name")
        var name: String,

        @ColumnInfo(name = "key")
        var key: String?

){
    constructor():this(null,"","","","")

    fun obj2Xml(): String {
        throw UnsupportedOperationException("Asesor no soporta la conversión a XML")
    }
}
