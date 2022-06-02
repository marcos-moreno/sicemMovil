package com.emprendamos.mx.emprendamosfin.data

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Advisor
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Position
import com.emprendamos.mx.emprendamosfin.data.services.SoapManager
import com.google.gson.Gson

class PreferenceHelper(val context: Context) {

    private val USER_PREF = "ASESOR"
    private val IS_SYNC = "IS_SYNC"
    private val IS_LOGGED = "IS_LOGGED"
    private val LOGIN_URL = "LOGIN_URL"
    private val SERVICES_URL = "SERVICES_URL"
    private val GPS_URL = "GPS_URL"
    private val POSITION_LATITUDE = "POSITION_LATITUDE"
    private val POSITION_LONGITUD = "POSITION_LONGITUD"
    private val POSITION_ALTITUDE = "POSITION_ALTITUDE"


    fun getAdvisor(): Advisor? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val us = prefs.getString(USER_PREF, "")
        val gson = Gson()
        if (us != null) {
            return gson.fromJson(us, Advisor::class.java)
        } else
            return null
    }

    fun getLogged(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val status = prefs.getBoolean(IS_LOGGED, true)
        return status
    }

    fun saveAdvisor(loginResponse: Advisor) {
        try {
            val gson = Gson()
            val u = gson.toJson(loginResponse)
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val prefEditor = prefs.edit()
            prefEditor.putString(USER_PREF, u)
            prefEditor.putBoolean(IS_LOGGED, true)
            prefEditor.commit()
        } catch (ex: Exception) {
            Log.e("PreferenceManager", ex.message)
        }
    }

    fun clearDefaults() {
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            this.edit().remove(USER_PREF).apply()
            this.edit().remove(IS_LOGGED).apply()
            this.edit().remove(IS_SYNC).apply()
        }
    }

    fun setSynced(isSync: Boolean) {
        try {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val prefEditor = prefs.edit()
            prefEditor.putBoolean(IS_SYNC, isSync)
            prefEditor.commit()
        } catch (ex: Exception) {
            Log.e("PreferenceManager", ex.message)
        }
    }

    fun setLogged(isLogged: Boolean) {
        try {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val prefEditor = prefs.edit()
            prefEditor.putBoolean(IS_LOGGED, isLogged)
            prefEditor.commit()
        } catch (ex: Exception) {
            Log.e("PreferenceManager", ex.message)
        }
    }

    fun getIsSynced() = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_SYNC, false)

    fun getLoginUrl() = PreferenceManager.getDefaultSharedPreferences(context).getString(LOGIN_URL, SoapManager.URL_LOGIN_ENDPOINT)
    fun setLoginUrl(loginUrl: String) = PreferenceManager.getDefaultSharedPreferences(context).edit().putString(LOGIN_URL, loginUrl).commit()

    fun getServicesUrl() = PreferenceManager.getDefaultSharedPreferences(context).getString(SERVICES_URL, SoapManager.URL_WS_ENDPOINT)
    fun setServicesUrl(servicesUrl: String) = PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SERVICES_URL, servicesUrl).commit()

    fun getGpsUrl() = PreferenceManager.getDefaultSharedPreferences(context).getString(GPS_URL, SoapManager.URL_GPS_ENDPOINT)
    fun setGpsUrl(gpsUrl: String) = PreferenceManager.getDefaultSharedPreferences(context).edit().putString(GPS_URL, gpsUrl).commit()

    fun getLastLocation(): Position {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val lastPosition = Position()
        lastPosition.latitude = prefs.getFloat(POSITION_LATITUDE, 0.0f).toDouble()
        lastPosition.longitude = prefs.getFloat(POSITION_LONGITUD, 0.0f).toDouble()
        lastPosition.altitude = prefs.getFloat(POSITION_ALTITUDE, 0.0f).toDouble()

        return lastPosition
    }
    fun setLastLocation(lastLocation: Position) {
        try {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val prefEditor = prefs.edit()
            prefEditor.putFloat(POSITION_LATITUDE, lastLocation.latitude.toFloat())
            prefEditor.putFloat(POSITION_LONGITUD, lastLocation.longitude.toFloat())
            prefEditor.putFloat(POSITION_ALTITUDE, lastLocation.altitude.toFloat())
            prefEditor.commit()
        } catch (ex: Exception) {
            Log.e("PreferenceManager", ex.message)
        }
    }

    fun restoreConfig() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SERVICES_URL, SoapManager.URL_WS_ENDPOINT).apply()
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(LOGIN_URL, SoapManager.URL_LOGIN_ENDPOINT).apply()
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(GPS_URL, SoapManager.URL_GPS_ENDPOINT).apply()
    }

}