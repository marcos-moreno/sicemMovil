package com.emprendamos.mx.emprendamosfin.data.database.repository.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Position

@Dao
interface PositionDao {

    @Query("SELECT * from positions")
    fun getAll(): List<Position>

    @Insert(onConflict = REPLACE)
    fun insert(position: Position)

    @Query("DELETE FROM positions")
    fun clearTable()

}

enum class PositionType(var type : Short, var description : String) {
    NOT_SPECIFIED(1.toShort(), "<NO ESPECIFICADO>"),

    SERVICE_UNAVAILABLE(2.toShort(), "Servicio deshabilitado"),

    LOG_IN(3.toShort(), "Iniciar sesión"),

    GROUP_OPEN(4.toShort(), "Abrir grupo"),

    GROUP_CLOSE(5.toShort(), "Cerrar grupo"),

    GROUP_SYNC(6.toShort(), "Sincronización de grupos"),

    SEQUENCE_EDIT(7.toShort(), "Editar registro"),

    SEQUENCE_NEW(8.toShort(), "Guardar nuevo registro"),

    SEQUENCE_SAVE(9.toShort(), "Guardar registro"),

    SEQUENCE_BEGIN(10.toShort(), "Iniciar registro");
}