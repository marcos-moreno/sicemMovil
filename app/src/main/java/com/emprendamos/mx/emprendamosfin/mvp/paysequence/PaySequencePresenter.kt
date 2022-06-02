package com.emprendamos.mx.emprendamosfin.mvp.paysequence

import com.emprendamos.mx.emprendamosfin.data.database.repository.AppDatabase
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient
import com.emprendamos.mx.emprendamosfin.extensions.DateFormat
import com.emprendamos.mx.emprendamosfin.extensions.toDate
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnSelectedItemInterface
import kotlinx.coroutines.experimental.async

class PaySequencePresenter(
        var view: PaySequenceView,
        var code: Int?,
        var cicle: Short?,
        var group_name: String?,
        var paycontrol_id: Long,
        var date: String?, var type: String?,
        var observations: String?,
        var sequence: Short) {


    private var sequencesClient: ArrayList<SequencePayControlClient> = arrayListOf()
    private var sequences: ArrayList<SequencePayControl> = arrayListOf()

    var listener : OnSelectedItemInterface? = null

    fun init() {
        view.init(code, cicle, group_name, date, type, observations)
        getClients()
    }

    private fun getClients() {
        async {
            AppDatabase.getInstance(view.activity).sequenceDao().getAllForPayControlAndSequence(paycontrol_id, sequence).forEach {
                var sPayControl = it
                AppDatabase.getInstance(view.activity).sequenceClientDao().getAllForPayControlSequence(it.id!!).forEach {
                    var c = AppDatabase.getInstance(view.activity).clientDao().getById(it.client_id!!)
                    it.client = c
                    sequencesClient.add(it)
                    sPayControl.payControlSequencePayControlClient.add(it)
                }
                sequences.add(sPayControl)
            }
            view.activity.runOnUiThread {
                view.setGroups(sequences, date!!.toDate(DateFormat.dd_MM_yyyy).time, listener!!)
            }
        }
    }

    fun navigateToEditFragment() {
        view.navigateToFragment(code, cicle, group_name, date, type, paycontrol_id, sequence)
    }
}