package com.emprendamos.mx.emprendamosfin.data.database.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Advisor

@Dao
interface AdvisorDao {

    @Query("SELECT * from advisors")
    fun getAll(): List<Advisor>

    @Insert(onConflict = REPLACE)
    fun insert(advisor: Advisor)

}