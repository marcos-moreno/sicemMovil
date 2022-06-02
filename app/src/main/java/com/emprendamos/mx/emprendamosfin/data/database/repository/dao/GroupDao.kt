package com.emprendamos.mx.emprendamosfin.data.database.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Group

@Dao
interface GroupDao {

    @Query("SELECT * from groups")
    fun getAll(): List<Group>

    @Query("SELECT * from groups WHERE code = :groupId")
    fun getById(groupId: Long): Group

    @Insert(onConflict = REPLACE)
    fun insert(group: Group)

    @Insert(onConflict = REPLACE)
    fun insertAll(groups: List<Group>)

    @Query("DELETE FROM groups")
    fun clearTable()


}