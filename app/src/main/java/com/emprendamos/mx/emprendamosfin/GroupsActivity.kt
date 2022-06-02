package com.emprendamos.mx.emprendamosfin

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import android.widget.Toast
import com.emprendamos.mx.emprendamosfin.data.PreferenceHelper
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.PositionType
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Position
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControl
import com.emprendamos.mx.emprendamosfin.extensions.EMPTY_STRING
import com.emprendamos.mx.emprendamosfin.extensions.isTemplateRegister
import com.emprendamos.mx.emprendamosfin.mvp.groups.GroupsPresenter
import com.emprendamos.mx.emprendamosfin.mvp.groups.GroupsView
import com.emprendamos.mx.emprendamosfin.ui.BaseLocationActivity
import com.emprendamos.mx.emprendamosfin.ui.adapters.GroupsListAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.app_bar_groups.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.experimental.launch
import java.util.Date

class GroupsActivity : BaseLocationActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sync -> {
                syncData()
            }
            R.id.action_logout -> {
                launch {
                    prefHelper.setLogged(false)
                    runOnUiThread {
                        val intent = Intent(this@GroupsActivity, LoginActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this@GroupsActivity,"Logged out!!", Toast.LENGTH_SHORT).show()
                        finish()

                    }
                    //launch {
                    //    presenter.clearDB()
                    //    runOnUiThread {
                    //        Toast.makeText(this@GroupsActivity, "Datos eliminados", Toast.LENGTH_LONG).show()
                    //        finish()
                    //    }
                    //}
                }
            }
            R.id.action_close -> {
                Toast.makeText(this@GroupsActivity,"¡Adios!", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.action_free -> {
                presenter.freeApp {
                    wasSuccessful ->
                        runOnUiThread {
                            if (wasSuccessful) {
                                Toast.makeText(this@GroupsActivity, "¡Liberado!", Toast.LENGTH_SHORT).show()
                                presenter.close()
                            } else {
                                Toast.makeText(this@GroupsActivity, "No es posible liberar, existen cambios por sincronizar", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true
    }

    private val presenter by lazy {
        GroupsPresenter(GroupsView(this))
    }

    private val prefHelper by lazy {
        PreferenceHelper(this)
    }

    private val advisor by lazy {
        prefHelper.getAdvisor()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        presenter.init()
        presenter.showMenuInfo()
        nav_view.setNavigationItemSelectedListener(this)
        if (advisor == null) {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        presenter.resumePresenter()

        exp_list_groups.setOnChildClickListener { parent, view, groupPosition, childPosition, id ->

            var sequence = (exp_list_groups.expandableListAdapter as GroupsListAdapter).getChild(groupPosition, childPosition) as SequencePayControl

            if (childPosition == 0)
                saveLocation(Position(null, Date().time, Date().time, PositionType.SEQUENCE_BEGIN.type, 0.0, 0.0, 0.0, location.toString(), sequence.paycontrol?.group_id?.toLong()
                        ?: 0, sequence.paycontrol_id ?: 0, Date().time))
            else
                saveLocation(Position(null, Date().time, Date().time, PositionType.SEQUENCE_EDIT.type, 0.0, 0.0, 0.0, location.toString(), sequence.paycontrol?.group_id?.toLong()
                        ?: 0, sequence.paycontrol_id ?: 0, Date().time))


            var intent = Intent(this@GroupsActivity, MainActivity::class.java)
            intent.putExtra(MainActivity.ARGUMENT_PAY_CONTROL, sequence.paycontrol_id)
            intent.putExtra(MainActivity.ARGUMENT_GROUP, sequence.paycontrol?.group_id ?: 0)
            intent.putExtra(MainActivity.ARGUMENT_CICLE, sequence.paycontrol?.cycle ?: 0)
            intent.putExtra(MainActivity.ARGUMENT_SEQUENCE, sequence.sequence)
            intent.putExtra(MainActivity.ARGUMENT_DATE, sequence.paycontrol!!.date)
            intent.putExtra(MainActivity.ARGUMENT_IS_EDIT, !isTemplateRegister(sequence.paycontrol!!))
            intent.putExtra(MainActivity.ARGUMENT_REGISTER_TYPE, sequence.type)
            intent.putExtra(MainActivity.ARGUMENT_OBSERVATIONS, sequence.observation)

            startActivity(intent)

            true
        }

        exp_list_groups.setOnGroupExpandListener {
            val currentTime = Date().time
            saveLocation(
                    Position(null,
                            currentTime,
                            currentTime,
                            PositionType.GROUP_OPEN.type,
                            0.0,
                            0.0,
                            0.0,
                            location.toString(),
                            0,
                            0,
                            currentTime)
            )
        }

        exp_list_groups.setOnGroupCollapseListener {
            val currentTime = Date().time
            saveLocation(
                    Position(null,
                            currentTime,
                            currentTime,
                            PositionType.GROUP_CLOSE.type,
                            0.0,
                            0.0,
                            0.0,
                            location.toString(),
                            0,
                            0,
                            currentTime)
            )
        }
    }

    private fun syncData() {
        saveLocation(Position(null, Date().time, Date().time, PositionType.GROUP_SYNC.type, 0.0, 0.0, 0.0, location.toString(), 0,
                0, Date().time))
        presenter.syncData {
            success, message ->
            runOnUiThread {
                if (success) {
                    //Correct Sync
                    Toast.makeText(this@GroupsActivity,"¡Sincronizado!", Toast.LENGTH_LONG).show()
                    presenter.freeAppAfterSync {
                        wasSuccessful ->
                        runOnUiThread {
                            if (wasSuccessful) {
                                Toast.makeText(this@GroupsActivity, "¡Liberado!", Toast.LENGTH_SHORT).show()
                                presenter.close()
                            } else {
                                Toast.makeText(this@GroupsActivity, "No es posible liberar, existen cambios por sincronizar", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                } else {
                    //Erroneous Sync
                    Toast.makeText(this@GroupsActivity,"No sincronizado. Error: $message", Toast.LENGTH_LONG).show();
                }
            }
        }
        // Toast.makeText(this@GroupsActivity, "Sincronizando, espera por favor", Toast.LENGTH_LONG).show()
    }

}
