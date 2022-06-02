package com.emprendamos.mx.emprendamosfin.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.emprendamos.mx.emprendamosfin.data.PreferenceHelper
import com.emprendamos.mx.emprendamosfin.data.database.repository.AppDatabase
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Position
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.experimental.launch

open class BaseLocationActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    private val UPDATE_INTERVAL: Long = 5000
    private val FASTEST_INTERVAL: Long = 5000
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null
    var location: Location? = null

    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
            } else {
                Toast.makeText(this, "¡Error al obtener los permisos!", Toast.LENGTH_LONG)
            }

            return false
        }

        return true
    }

    override fun onStart() {
        super.onStart()

        googleApiClient?.let {
            it.connect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    override fun onResume() {
        super.onResume()

        if (!checkPlayServices()) {
            Toast.makeText(this, "Necesitas instalar los Google Play Services para usar la aplicación correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()

        googleApiClient?.let {
            if (it.isConnected) {
                LocationServices.FusedLocationApi.removeLocationUpdates(it, this)
                it.disconnect()
            }
        }
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = UPDATE_INTERVAL
        locationRequest?.fastestInterval = FASTEST_INTERVAL

        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "¡Necesitas habilitar los permisos para obtener la ubicación!", Toast.LENGTH_SHORT).show()
            finish()
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)

        startLocationUpdates()
    }

    override fun onConnectionSuspended(p0: Int) { }

    override fun onConnectionFailed(p0: ConnectionResult) { }

    override fun onLocationChanged(location: Location?) {
        this.location = location
        val position = Position()
        location?.let {
            position.longitude = it.longitude
            position.latitude = it.latitude
            position.altitude = it.altitude
        }?: run {
            position.longitude = 0.0
            position.latitude = 0.0
            position.altitude = 0.0
        }

        PreferenceHelper(this).setLastLocation(position)
    }

    fun saveLocation(position : Position) {
        location?.let {
            position.latitude = it.latitude
            position.longitude = it.longitude
            position.altitude = it.altitude
            launch {
                AppDatabase.getInstance(this@BaseLocationActivity).positionDao().insert(position)
            }
        } ?: run {
            val lastPosition = PreferenceHelper(this).getLastLocation()
            position.latitude = lastPosition.latitude
            position.longitude = lastPosition.longitude
            position.altitude = lastPosition.altitude
            launch {
                AppDatabase.getInstance(this@BaseLocationActivity).positionDao().insert(position)
            }
        }
    }

}
