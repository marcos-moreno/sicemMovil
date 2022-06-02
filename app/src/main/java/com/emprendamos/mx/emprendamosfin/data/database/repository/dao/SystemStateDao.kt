package com.emprendamos.mx.emprendamosfin.data.database.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SystemState

@Dao
interface SystemStateDao {

    @Query("SELECT * from systemstates")
    fun getAll(): List<SystemState>

    @Insert(onConflict = REPLACE)
    fun insert(systemstate: SystemState)

}