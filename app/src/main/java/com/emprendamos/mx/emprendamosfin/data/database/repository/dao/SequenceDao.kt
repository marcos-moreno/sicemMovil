package com.emprendamos.mx.emprendamosfin.data.database.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Update
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl

@Dao
interface SequencePayControlDao {

    @Query("SELECT * from sequence_pay_controls")
    fun getAll(): List<SequencePayControl>

    @Query("SELECT * from sequence_pay_controls WHERE id= :sequenceId")
    fun getById(sequenceId : Long): SequencePayControl

    @Query("SELECT MAX(sequence) from sequence_pay_controls WHERE paycontrol_id = :payControlId")
    fun getMax(payControlId: Long): Short

    @Query("SELECT MAX(A.sequence) + 1 FROM sequence_pay_controls A INNER JOIN paycontrols B ON B.id = A.paycontrol_id WHERE B.group_id = :groupId AND fecant = :fecAnt")
    fun getNextSequence(groupId : Int, fecAnt : Long): Short

    @Query("SELECT * from sequence_pay_controls WHERE paycontrol_id = :payControlId")
    fun getAllForPayControl(payControlId : Long): List<SequencePayControl>

    @Query("SELECT * from sequence_pay_controls WHERE paycontrol_id = :payControlId AND local_modification = 1")
    fun getAllForPayControlModified(payControlId : Long): List<SequencePayControl>

    @Query("SELECT * from sequence_pay_controls WHERE paycontrol_id = :payControlId AND sequence = :sequence")
    fun getAllForPayControlAndSequence(payControlId : Long, sequence : Short): List<SequencePayControl>

    @Query("SELECT * from sequence_pay_controls WHERE paycontrol_id = :payControlId AND sequence = :sequence AND fecant = :fecAnt")
    fun getAllForPayControlAndSequenceAndDate(payControlId : Long, sequence : Short, fecAnt: Long): List<SequencePayControl>

    @Query("SELECT MAX(A.sequence) FROM sequence_pay_controls A INNER JOIN paycontrols B ON B.id = A.paycontrol_id WHERE B.group_id = :groupId AND B.date = :fecAnt")
    fun getMaxSequenceForDate(groupId: Int, fecAnt: Long): Short

    @Insert(onConflict = REPLACE)
    fun insert(paycontrol: SequencePayControl) : Long?

    @Update(onConflict = REPLACE)
    fun update(paycontrol: SequencePayControl)

    @Insert(onConflict = REPLACE)
    fun insertAll(paycontrol: List<SequencePayControl>)

    @Query("DELETE FROM sequence_pay_controls")
    fun clearTable()

    @Query("DELETE FROM sequence_pay_controls where id = :id")
    fun clearForId(id: Long)

    @Query("UPDATE sequence_pay_controls SET sincronized = :sincronized where id = :id")
    fun updateSincronized(id: Long, sincronized: Boolean)
}