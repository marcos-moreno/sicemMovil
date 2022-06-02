package com.emprendamos.mx.emprendamosfin.data.database.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Update
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.PayControl

@Dao
interface PayControlDao {

    @Query("SELECT * FROM paycontrols")
    fun getAll(): List<PayControl>

    @Query("UPDATE paycontrols SET local_modification = 0")
    fun clearModified()

    @Query("SELECT Count(*) FROM paycontrols WHERE local_modification = 1")
    fun getCountModified(): Int

    @Query("SELECT * FROM paycontrols WHERE local_modification = 1 AND group_id = :groupId")
    fun getAllModified(groupId : Int): List<PayControl>

    @Query("SELECT * FROM paycontrols where id = :payControlId")
    fun getForId(payControlId : Long): PayControl

    @Query("SELECT * FROM paycontrols WHERE group_id = :groupId")
    fun getAllForGroup(groupId : Int): List<PayControl>

    @Insert(onConflict = REPLACE)
    fun insert(paycontrol: PayControl)

    @Update(onConflict = REPLACE)
    fun update(paycontrol: PayControl)

    @Insert(onConflict = REPLACE)
    fun insertAll(paycontrol: List<PayControl>)

    @Query("DELETE FROM paycontrols")
    fun clearTable()

}