package com.emprendamos.mx.emprendamosfin.mvp.main

import com.emprendamos.mx.emprendamosfin.data.PreferenceHelper
import com.emprendamos.mx.emprendamosfin.data.database.repository.AppDatabase
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.PayControl
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient
import com.emprendamos.mx.emprendamosfin.data.services.SoapManager
import com.emprendamos.mx.emprendamosfin.extensions.EMPTY_STRING
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
import java.util.*

class MainPresenter(var view : MainView) {

    val dbManager by lazy {
        AppDatabase.getInstance(view.activity)
    }

    val prefHelper by lazy {
        PreferenceHelper(view.activity)
    }

    val advisor by lazy {
        prefHelper.getAdvisor()
    }

    private val soapManager by lazy {
        SoapManager(view.activity)
    }

    fun init() {
        view.init()
    }

    fun getPayControlForNewRegister(payControlId : Long, date: String, type : String, cicle : Short?, observations: String) : PayControl {
        var payControl = dbManager.payControlDao().getForId(payControlId)
        payControl.local_modification = true
        payControl.date = SimpleDateFormat("dd/MM/yyyy").parse(date).time
        payControl.type = type
        payControl.cycle = cicle ?: 1
        payControl.getKey()
        payControl.sequences = ArrayList(dbManager.sequenceDao().getAllForPayControl(payControlId))
        payControl.sequences[0].sequence = dbManager.sequenceDao().getMaxSequenceForDate(payControl.group_id, payControl.date)
        payControl.sequences[0].paycontrol = payControl

        if (payControl.sequences[0].sequence == 0.toShort())
            payControl.sequences[0].sequence = 1
        else
            payControl.sequences[0].sequence++

        payControl.sequences[0].getKey()
        payControl.sequences[0].local_modification = true
        payControl.sequences[0].observation = observations
        payControl.sequences[0].fecant = payControl.date
        payControl.sequences[0].fecreg = Date().time
        payControl.week1 = 1

        return payControl

    }

    fun preparePayControlForEdit(payControlId: Long, sequence: Short, observations: String, date: String, sequenceId: Long) : PayControl {
        var payControl = dbManager.payControlDao().getForId(payControlId)
        payControl.local_modification = true

        var auxDate = payControl.date
        payControl.date = SimpleDateFormat("dd/MM/yyyy").parse(date).time

        payControl.sequences = ArrayList()
        payControl.sequences.add(dbManager.sequenceDao().getById(sequenceId))
        payControl.sequences[0].paycontrol = payControl

        var auxId = payControl.sequences[0].id

        if (auxDate != payControl.date) {

            payControl.getKey()
            payControl.sequences[0].paycontrol = payControl
            dbManager.payControlDao().insert(payControl)

            var maxSequence = dbManager.sequenceDao().getMaxSequenceForDate(payControl.group_id, payControl.date)

            if (maxSequence == 0.toShort()) {
                payControl.sequences[0].sequence = 1
            } else {
                payControl.sequences[0].sequence = ++maxSequence
            }

            payControl.sequences[0].getKey()
        } else {
            dbManager.payControlDao().update(payControl)
        }

        payControl.sequences[0].local_modification = true
        payControl.sequences[0].fecreg = Date().time
        payControl.sequences[0].observation = observations

        payControl.week1 = 2

        if (auxDate != payControl.date) {
            dbManager.sequenceDao().insert(payControl.sequences[0])
            if (auxId != payControl.sequences[0].id)
                deletePrevious(auxId!!)
        }
        else
            dbManager.sequenceDao().insert(payControl.sequences[0])

        return payControl
    }

    private fun deletePrevious(sequenceId: Long) {
        dbManager.sequenceClientDao().clearForSequenceId(sequenceId)
        dbManager.sequenceDao().clearForId(sequenceId)
    }

    fun getGroupById(groupId : Long) = dbManager.groupDao().getById(groupId)

    fun clearDB()
    {
        dbManager.sequenceClientDao().clearTable()
        dbManager.sequenceDao().clearTable()
        dbManager.payControlDao().clearTable()
        dbManager.clientDao().clearTable()
        dbManager.groupDao().clearTable()
        prefHelper.clearDefaults()
    }

    fun goToResume(sequencePayControlClient: ArrayList<SequencePayControlClient>) {
        val theoricalPaymentSum = sequencePayControlClient.sumByDouble { it.theorical_payment }
        val realPaymentSum = sequencePayControlClient.sumByDouble { it.real_payment ?: 0.0 }
        val refundSum = sequencePayControlClient.sumByDouble { it.refund }
        val aportSum = sequencePayControlClient.sumByDouble { it.aport }
        val feeSum = sequencePayControlClient.sumByDouble { it.fee }
        val savingSum = sequencePayControlClient.sumByDouble { it.saving }

        view.goToResume(theoricalPaymentSum, realPaymentSum, refundSum,aportSum,feeSum,savingSum, sequencePayControlClient[0].sequencepaycontrol_id)

    }

    fun showMenuInfo() {
        view.setAdvisorName(advisor?.name ?: EMPTY_STRING)
        view.setLastSync("Last sync: n/a")
    }

    fun syncData(completableListener : (Boolean, String) -> Unit = {_,_ -> }) {
        val key = advisor!!.key!!
        val company_code = advisor!!.company_code
        soapManager.syncService(view.activity, advisor!!) { success, message ->
            if (success) {
                soapManager.syncPositions(company_code, key, view.activity) {
                    completableListener(true, EMPTY_STRING)
                }
            } else {
                completableListener(success, message)
            }
        }
    }

    fun clearLocalModification () {
        launch {
            clearModified()
        }
    }

    fun freeApp(completitionListener : (Boolean) -> Unit) {
        launch {
            val modified = lookForModified().await()
            if (modified == 0) {
                clearDB()
                prefHelper.clearDefaults()
                completitionListener(true)
            } else {
                completitionListener(false)
            }
        }
    }

    fun freeAppAfterSync(completitionListener : (Boolean) -> Unit) {
        launch {
            clearDB()
            prefHelper.clearDefaults()
            completitionListener(true)
        }
    }

    fun close() {
        view.activity.finishAffinity()
    }

    private fun lookForModified(): Deferred<Int> {
        return async {
            val modified = AppDatabase.getInstance(view.activity).payControlDao().getCountModified()
            modified
        }
    }

    private fun clearModified () {
        async {
            AppDatabase.getInstance(view.activity).payControlDao().clearModified()
        }
    }

}