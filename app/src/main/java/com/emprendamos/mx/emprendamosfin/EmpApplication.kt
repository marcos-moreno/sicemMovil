package com.emprendamos.mx.emprendamosfin

import android.app.Application
import com.facebook.stetho.Stetho

class EmpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}