package com.emprendamos.mx.emprendamosfin.data.database.repository.entities

class PayInfo {
    var groups: HashMap<Int, Group>? = null

    var payControls: HashMap<Long, PayControl>? = null

    var sequences: HashMap<Long, SequencePayControl>? = null

    var clientSequence: HashMap<Long, SequencePayControlClient>? = null

    var clients: HashMap<Int, Client>? = null

    var advisor: Advisor? = null
}