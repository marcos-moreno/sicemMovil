package com.emprendamos.mx.emprendamosfin.data.database.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Update
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient

@Dao
interface SequenceClientDao {

    @Query("SELECT * from sequence_pay_controls_client")
    fun getAll(): List<SequencePayControlClient>

    @Query("SELECT * from sequence_pay_controls_client WHERE sequencePayControl_id = :sequencePayControlId")
    fun getAllForPayControlSequence(sequencePayControlId : Long): List<SequencePayControlClient>

    @Query("SELECT * from sequence_pay_controls_client WHERE sequencePayControl_id = :sequencePayControlId AND local_modification = 1")
    fun getAllForPayControlSequenceModified(sequencePayControlId : Long): List<SequencePayControlClient>

    @Insert(onConflict = REPLACE)
    fun insert(payControl: SequencePayControlClient)

    @Update(onConflict = REPLACE)
    fun update(payControl: SequencePayControlClient)

    @Insert(onConflict = REPLACE)
    fun insertAll(paycontrol: List<SequencePayControlClient>)

    @Query("DELETE FROM sequence_pay_controls_client")
    fun clearTable()

    @Query("DELETE FROM sequence_pay_controls_client where sequencePayControl_id = :id")
    fun clearForSequenceId(id: Long)

}