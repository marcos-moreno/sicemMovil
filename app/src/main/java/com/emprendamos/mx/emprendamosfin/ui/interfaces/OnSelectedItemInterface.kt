package com.emprendamos.mx.emprendamosfin.ui.interfaces

import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Client
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient

interface OnSelectedItemInterface {
    fun onSequenceSelected (sequence : SequencePayControl)
    fun onClientSelected (client: Client, sequence : SequencePayControlClient)
}