package com.emprendamos.mx.emprendamosfin

import android.content.Context
import android.location.Location

class LocationHelper {

    var location : Location? = null

    companion object {
        fun getInstance(context: Context) { HOLDER.instance }
    }

    object HOLDER {
        var instance = LocationHelper()
    }
}