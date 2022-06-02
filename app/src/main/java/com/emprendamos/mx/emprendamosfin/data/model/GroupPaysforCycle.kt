package com.emprendamos.mx.emprendamosfin.data.model

import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Group
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.PayControl
import java.util.Date

class GroupPaysforCycle {
    var group: Group?

    var cycle: Short

    var sequences: ArrayList<SequencePayControl>

    var isNewBottonBuild: Boolean

    constructor(group: Group?, cycle: Short) {
        this.group = group
        this.cycle = cycle
        this.sequences = arrayListOf()
        this.isNewBottonBuild = false
    }

    fun getCountofSequences(): Int {
        var count = 0
        for (sequence in sequences) {
            if ( isTemplateRegister(sequence.paycontrol!!) )
                ++count

        }

        return sequences.size - count
    }

    fun isTemplateRegister(paycontrol: PayControl) : Boolean {
        return Date(paycontrol.date).time == 631173600000L
                || Date(paycontrol.date).year == 1990
                || Date(paycontrol.date).year == 90

    }
}