package com.emprendamos.mx.emprendamosfin.mvp.groups

import android.util.Log
import android.widget.Toast
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.data.PreferenceHelper
import com.emprendamos.mx.emprendamosfin.data.database.repository.AppDatabase
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.PayInfo
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl
import com.emprendamos.mx.emprendamosfin.data.database.repository.mapper.MapperManager
import com.emprendamos.mx.emprendamosfin.data.model.GroupPaysforCycle
import com.emprendamos.mx.emprendamosfin.data.services.SoapManager
import com.emprendamos.mx.emprendamosfin.extensions.EMPTY_STRING
import com.emprendamos.mx.emprendamosfin.extensions.toOList
import com.emprendamos.mx.emprendamosfin.ui.adapters.GroupsListAdapter
import kotlinx.android.synthetic.main.app_bar_groups.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.lang.Exception
import java.util.HashMap

class GroupsPresenter(var view: GroupsView) {

    private val soapManager by lazy {
        SoapManager(view.activity)
    }

    private val mapperManager by lazy {
        MapperManager()
    }

    private val dbManager by lazy {
        AppDatabase.getInstance(view.activity)
    }

    private val prefHelper by lazy {
        PreferenceHelper(view.activity)
    }

    private val advisor by lazy {
        prefHelper.getAdvisor()
    }

    fun loadInitialDb(payInfo: PayInfo) {
        payInfo.groups?.run {
            dbManager.groupDao().insertAll(this.toOList())

            payInfo.clients?.let {
                dbManager.clientDao().insertAll(it.toOList())
            }

            payInfo.payControls?.let {
                dbManager.payControlDao().insertAll(it.toOList())

                payInfo.sequences?.let {
                    dbManager.sequenceDao().insertAll(it.toOList())

                    payInfo.clientSequence?.let {
                        dbManager.sequenceClientDao().insertAll(it.toOList())
                    }
                }
            }
        }
    }

    fun lookForModified(): Deferred<Int> {
        return async {
            val modified = AppDatabase.getInstance(view.activity).payControlDao().getCountModified()
            modified
        }
    }

    fun clearDB() {
        dbManager.sequenceClientDao().clearTable()
        dbManager.sequenceDao().clearTable()
        dbManager.payControlDao().clearTable()
        dbManager.clientDao().clearTable()
        dbManager.groupDao().clearTable()
        prefHelper.clearDefaults()
    }

    fun init() {
        view.init()
    }

    fun resumePresenter() {
        if (!prefHelper.getIsSynced()) {
            getData(advisor!!.key!!)
        } else
            getPayInfoFromDb()
    }

    fun getData(agentKey: String) {
        view.showLoader(view.activity.getString(R.string.title_downloading_groups))
        async {
            soapManager.importGroupData(agentKey) {
                updateUI(view.activity.getString(R.string.title_writing_groups))
                if (it.isNotEmpty()) {
                    mapperManager.exportData(it) {
                        if (it == null) {
                            view.activity.runOnUiThread{
                                prefHelper.setLogged(false)
                                view.finishIfEmptyData()
                            }

                        } else {
                            var payInfo = it
                            updateUI(view.activity.getString(R.string.title_downloading_clients))
                            soapManager.importGroupAndClientData(agentKey) {
                                updateUI(view.activity.getString(R.string.title_writing_clients))
                                mapperManager.exportGroupAndClientData(it, payInfo)
                                loadInitialDb(payInfo)
                                prefHelper.setSynced(true)
                                view.activity.runOnUiThread {
                                    getPayInfoFromDb()
                                }
                                updateUI(null)
                            }
                        }
                    }
                    view.activity.runOnUiThread {
                        Log.e("Data", it)
                    }
                }
            }

        }.start()
    }

    fun updateUI(message: String?) {
        if (message == null) {
            view.hideLoader()
            return
        }
        view.showLoader(message)
    }

    fun getPayInfoFromDb() {
        view.showLoader(view.activity.getString(R.string.title_loading_data))
        var payInfo = PayInfo()
        async {
            try {
                payInfo.groups = HashMap()
                payInfo.run {
                    AppDatabase.getInstance(view.activity).groupDao().getAll().forEach {

                        it.clients = ArrayList(AppDatabase.getInstance(view.activity).clientDao().getAllForGroup(it.code))
                        it.payControls = ArrayList(AppDatabase.getInstance(view.activity).payControlDao().getAllForGroup(it.code))

                        it.payControls.forEach {
                            var payControl = it
                            it.sequences = ArrayList(AppDatabase.getInstance(view.activity).sequenceDao().getAllForPayControl(it.id!!))

                            it.sequences.forEach {
                                it.paycontrol = payControl
                                it.payControlSequencePayControlClient = ArrayList(AppDatabase.getInstance(view.activity).sequenceClientDao().getAllForPayControlSequence(it.id!!))
                            }
                        }
                        this.groups!![it.code!!] = (it)
                    }
                }
                var groupsList = prepareData(payInfo)
                updateUI(null)
                view.activity.runOnUiThread {
                    view.activity.exp_list_groups.setAdapter(GroupsListAdapter(view.activity, groupsList))
                }
            } catch (ex: Exception) {
                updateUI(null)
                view.activity.runOnUiThread {
                    Toast.makeText(view.activity, "Error: ${ex.message}", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    fun prepareData(payInfo: PayInfo): List<GroupPaysforCycle> {

        var groupsPerCicle = HashMap<Int, MutableMap<Short, GroupPaysforCycle>>()
        var list = ArrayList<GroupPaysforCycle>()

        payInfo.groups?.forEach { (i, group) ->
            Log.e("prepareData", "Grupo $i")

            try {
                val payCicles: MutableMap<Short, GroupPaysforCycle>

                if (groupsPerCicle.containsKey(group.code))
                    payCicles = groupsPerCicle.get(group.code)!!
                else {
                    payCicles = java.util.HashMap<Short, GroupPaysforCycle>()
                    groupsPerCicle.put(group.code, payCicles)
                }

                for (payControl in group.payControls) {
                    val cycle = payControl.cycle
                    val currentCycle: GroupPaysforCycle

                    if (payCicles.containsKey(cycle))
                        currentCycle = payCicles[cycle]!!
                    else {
                        currentCycle = GroupPaysforCycle(group, cycle)
                        payCicles[cycle] = currentCycle
                    }

                    currentCycle.sequences.addAll(payControl.sequences)

                    currentCycle.sequences.apply {
                        this.sortWith (compareBy<SequencePayControl>{it.paycontrol!!.date}.thenBy { it.sequence})
                    }
                }
            } catch (ex: Exception) {
                Log.e("prepareData", "${ex.stackTrace}")
            }

        }

        for (ciclosygrupos in groupsPerCicle.values) {
            list.addAll(ciclosygrupos.values)
        }

        list.sortBy {
            it.group!!.name
        }

        return list
    }

    fun syncData(completableListener : (Boolean, String) -> Unit = {_, _ -> }) {
        val key = advisor!!.key!!
        val company_code = advisor!!.company_code
        updateUI("Sincronizando...")
        soapManager.syncService(view.activity, advisor!!) { success, message ->
            if (success) {
                updateUI("Sincronizando posiciones...")
                soapManager.syncPositions(company_code, key, view.activity) {
                    updateUI(null)
                    completableListener(true, EMPTY_STRING)
                }
            } else {
                updateUI(null)
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

    private fun clearModified () {
        async {
            AppDatabase.getInstance(view.activity).payControlDao().clearModified()
        }
    }

    fun close() {
        view.activity.finish()
    }

    fun showMenuInfo() {
        view.setAdvisorName(advisor?.name ?: EMPTY_STRING)
        view.setLastSync("Last sync: n/a")
    }
}