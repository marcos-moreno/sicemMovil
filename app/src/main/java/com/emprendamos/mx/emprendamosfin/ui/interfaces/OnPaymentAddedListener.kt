package com.emprendamos.mx.emprendamosfin.ui.interfaces

import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient

interface OnPaymentAddedListener {
    fun onPaymentEdited(sequenceClientId : Long, realPayment : Double, refund : Double, contribution : Double, fee : Double, saving : Double, assistance : String)
    fun onRequestClientSequences() : ArrayList<SequencePayControlClient>
    fun onSetSequences(sequences : ArrayList<SequencePayControlClient>)
    fun onShowSequences(listener : () -> Unit)
    fun onSequenceEnded(sequenceId: Long = 0)
}