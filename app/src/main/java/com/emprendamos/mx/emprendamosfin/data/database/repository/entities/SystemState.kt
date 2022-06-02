package com.emprendamos.mx.emprendamosfin.data.database.repository.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "systemstates")
data class SystemState(

        @PrimaryKey(autoGenerate = true) var id: Long?,

        @ColumnInfo(name = "date")
        var date: Long

){
    constructor():this(null,0L)
}