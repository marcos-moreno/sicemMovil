package com.emprendamos.mx.emprendamosfin.data.model

enum class Types {
    NOT_ASIGNED('\u0000', "<NO ASIGNADO>"),

    UNKNOWN('Z', "Desconocido"),

    S('S', "Semanal"),

    P('P', "Extemporaneo");

    var identification: Char = '\u0000'

    var description: String = ""


    constructor(identification: Char, description: String) {
        this.identification = identification
        this.description = description
    }

    override fun toString(): String {
        return description
    }
}