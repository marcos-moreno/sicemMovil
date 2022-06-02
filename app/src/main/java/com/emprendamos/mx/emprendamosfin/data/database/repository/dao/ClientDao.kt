package com.emprendamos.mx.emprendamosfin.data.database.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Client

@Dao
interface ClientDao {

    @Query("SELECT * FROM clients")
    fun getAll(): List<Client>

    @Query("SELECT * FROM clients WHERE code = :clientId")
    fun getById(clientId : Long): Client

    @Query("SELECT * FROM clients WHERE group_id = :groupId")
    fun getAllForGroup(groupId : Int): List<Client>

    @Insert(onConflict = REPLACE)
    fun insert(client: Client)

    @Insert(onConflict = REPLACE)
    fun insertAll(client: List<Client>)

    @Query("DELETE FROM clients")
    fun clearTable()


}