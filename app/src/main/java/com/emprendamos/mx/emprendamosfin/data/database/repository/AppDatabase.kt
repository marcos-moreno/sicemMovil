package com.emprendamos.mx.emprendamosfin.data.database.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.AdvisorDao
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.ClientDao
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.GroupDao
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.PayControlDao
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.PositionDao
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.SystemStateDao
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.SequencePayControlDao
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.SequenceClientDao

import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Advisor
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Client
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Group
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.PayControl
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Position
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SystemState
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient


@Database(entities = [
    (Advisor::class),
    (Client::class),
    (Group::class),
    (PayControl::class),
    (Position::class),
    (SystemState::class),
    (SequencePayControl::class),
    (SequencePayControlClient::class)
], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun advisorDao(): AdvisorDao

    abstract fun clientDao(): ClientDao

    abstract fun groupDao(): GroupDao

    abstract fun payControlDao(): PayControlDao

    abstract fun positionDao(): PositionDao

    abstract fun systemDao(): SystemStateDao

    abstract fun sequenceDao(): SequencePayControlDao

    abstract fun sequenceClientDao(): SequenceClientDao

    companion object {

        private var sInstance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                sInstance = Room
                        .databaseBuilder(context, AppDatabase::class.java, "emprendamosfin")
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return sInstance!!
        }
    }

}

