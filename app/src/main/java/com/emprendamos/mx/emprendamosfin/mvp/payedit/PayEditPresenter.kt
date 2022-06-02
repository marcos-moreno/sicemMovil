package com.emprendamos.mx.emprendamosfin.mvp.payedit

import com.emprendamos.mx.emprendamosfin.data.database.repository.AppDatabase
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Client
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient
import com.emprendamos.mx.emprendamosfin.extensions.DateFormat
import com.emprendamos.mx.emprendamosfin.extensions.toDate
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnActionsListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnPaymentAddedListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnSelectedItemInterface
import kotlinx.coroutines.experimental.async

class PayEditPresenter(
        val view : PayEditView,
        val code: Int,
        val cicle: Short,
        val group_name: String,
        val paycontrol_id: Long,
        val date: String,
        val type: String,
        val observations: String,
        val sequence: Short = 0
) {
    private var sequencesClient: ArrayList<SequencePayControlClient> = arrayListOf()
    private var listener: OnPaymentAddedListener? = null
    var onSelectedListener : OnSelectedItemInterface? = null

    fun init(listener : OnPaymentAddedListener?, menuListener: OnActionsListener? ) {
        this.listener = listener
        view.init(code, cicle, group_name, date, type, observations, menuListener)
        getClients()
    }

    private fun getClients() {
        sequencesClient = listener!!.onRequestClientSequences()


        if (sequencesClient.size > 0)
            showClientsList()
        else {
            async {
                AppDatabase.getInstance(view.activity).sequenceDao().getAllForPayControlAndSequence(paycontrol_id, sequence).forEach {
                    AppDatabase.getInstance(view.activity).sequenceClientDao().getAllForPayControlSequence(it.id!!).forEach {
                        var c = AppDatabase.getInstance(view.activity).clientDao().getById(it.client_id!!)
                        it.client = c
                        sequencesClient.add(it)
                    }
                }
                listener?.onSetSequences(sequencesClient)
                view.activity.runOnUiThread {
                    showClientsList()
                }
            }
        }
    }

    private fun showClientsList() {
        view.showClientList(sequencesClient, onSelectedListener!!)
    }

    fun navigateToClient(client: Client, sequence: SequencePayControlClient) {
        view.navigateToClient(client, sequence)

    }

    fun refreshDate() {
        view.refreshDate()
    }
}