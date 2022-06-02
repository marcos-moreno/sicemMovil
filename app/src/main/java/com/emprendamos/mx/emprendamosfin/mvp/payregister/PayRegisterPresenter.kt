package com.emprendamos.mx.emprendamosfin.mvp.payregister

import com.emprendamos.mx.emprendamosfin.data.database.repository.AppDatabase
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Client
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnActionsListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnPaymentAddedListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnSelectedItemInterface
import kotlinx.coroutines.experimental.launch

class PayRegisterPresenter(
        val view: PayRegisterView,
        val code: Int?,
        val group_name: String?,
        val cicle: Short?,
        val paycontrol_id: Long
) {
    private var listener: OnPaymentAddedListener? = null
    private var sequences: ArrayList<SequencePayControlClient> = arrayListOf()

    var onSelectedListener : OnSelectedItemInterface? = null


    fun init(listener : OnPaymentAddedListener?, menuListener: OnActionsListener? ) {
        this.listener = listener
        view.init(code, cicle, group_name, menuListener)
        getClients()
    }

    private fun getClients() {
        sequences = listener!!.onRequestClientSequences()

        if (sequences.size > 0)
            showClientsList()
        else {
            launch {
                AppDatabase.getInstance(view.activity).sequenceDao().getAllForPayControl(paycontrol_id).forEach {
                    AppDatabase.getInstance(view.activity).sequenceClientDao().getAllForPayControlSequence(it.id!!).forEach {
                        var c = AppDatabase.getInstance(view.activity).clientDao().getById(it.client_id!!)
                        it.client = c
                        sequences.add(it)
                    }
                }
                listener?.onSetSequences(sequences)
            }
        }
    }

    fun showClientsList() {
        view.showClientList(sequences, onSelectedListener!!)

    }

    fun navigateToClient(client: Client, sequence: SequencePayControlClient) {
        view.navigateToClient(client, sequence)

    }

    fun refreshDate() {
        view.refreshDate()
    }

}