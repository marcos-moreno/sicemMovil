package com.emprendamos.mx.emprendamosfin.data.model

enum class Asistances {
    Asistencia,

    Retardo,

    Falta,

    Mando_Pago,

    Permiso,

    Desconocido;


    override fun toString(): String {
        var returned = this.name.replace('_', ' ')

        if (returned.contains("Mando"))
            returned = returned.replace("Mando", "Mandó")
        else if (returned.contains("Desconocido"))
            returned = "--Seleccionar--"

        return returned
    }

}