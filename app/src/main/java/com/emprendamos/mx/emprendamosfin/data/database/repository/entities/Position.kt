package com.emprendamos.mx.emprendamosfin.data.database.repository.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "positions")
data class Position(

        @PrimaryKey(autoGenerate = true) var id: Long?,

        @ColumnInfo(name = "gps_date")
        var gps_date: Long?,
        
        @ColumnInfo(name = "gps_hour")
        var gps_hour: Long?,
        
        @ColumnInfo(name = "position_type")
        var position_type: Short,
        
        @ColumnInfo(name = "longitude")
        var longitude: Double,
        
        @ColumnInfo(name = "latitude")
        var latitude: Double,
        
        @ColumnInfo(name = "altitude")
        var altitude: Double,
        
        @ColumnInfo(name = "original_string")
        var original_string: String,
        
        @ColumnInfo(name = "group_id")
        var group_id: Long,
        
        @ColumnInfo(name = "secuence_paycontrol_id")
        var secuence_paycontrol_id: Long,
        
        @ColumnInfo(name = "register_hour")
        var register_hour: Long?

){
    constructor():this(
            null,
            null,
            null,
            0,
            0.0,
            0.0,
            0.0,
            "",
            0L,
            0L,
            null)
}