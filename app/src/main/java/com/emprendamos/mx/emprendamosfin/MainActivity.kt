package com.emprendamos.mx.emprendamosfin

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.emprendamos.mx.emprendamosfin.data.PreferenceHelper
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.PositionType
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Client
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Group
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Position
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.SequencePayControlClient
import com.emprendamos.mx.emprendamosfin.data.model.Types
import com.emprendamos.mx.emprendamosfin.extensions.createNewPayControl
import com.emprendamos.mx.emprendamosfin.extensions.editSequences
import com.emprendamos.mx.emprendamosfin.extensions.hideKeyboard
import com.emprendamos.mx.emprendamosfin.mvp.main.MainPresenter
import com.emprendamos.mx.emprendamosfin.mvp.main.MainView
import com.emprendamos.mx.emprendamosfin.ui.BaseLocationActivity
import com.emprendamos.mx.emprendamosfin.ui.PayRegisterFragment
import com.emprendamos.mx.emprendamosfin.ui.PaySequenceFragment
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnActionsListener
import com.emprendamos.mx.emprendamosfin.ui.interfaces.OnPaymentAddedListener
import com.emprendamos.mx.emprendamosfin.ui.widget.ConfirmDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_pay_register.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.collections.ArrayList

const val TAG = "MainActivity"

class MainActivity : BaseLocationActivity(), NavigationView.OnNavigationItemSelectedListener, OnPaymentAddedListener, OnActionsListener {
    private val prefHelper by lazy {
        PreferenceHelper(this)
    }

    override fun hideMenu(menuItem: Int) {
        toolbar.menu.findItem(menuItem).isVisible = false
    }

    override fun showMenu(menuItem: Int) {
        toolbar.menu.findItem(menuItem).isVisible = true
    }

    override fun onSequenceEnded(sequenceId: Long) {
        if (is_editable) {
            onSequenceFinishedEdit(sequenceId)
        } else
            onSequenceFinished()
    }

    private var fragmentCallback: () -> Unit = {}

    override fun onShowSequences(listener: () -> Unit) {
        fragmentCallback = listener
    }

    override fun onSetSequences(sequences: ArrayList<SequencePayControlClient>) {
        this.sequencePayControlClient = sequences
    }

    override fun onRequestClientSequences(): ArrayList<SequencePayControlClient> {
        return sequencePayControlClient
    }

    override fun onPaymentEdited(sequenceClientId: Long, realPayment: Double, refund: Double, contribution: Double, fee: Double, saving: Double, assistance: String) {
        sequencePayControlClient.forEach {
            if (it.id == sequenceClientId) {
                it.local_modification = true
                it.isEdited = true
                it.real_payment = realPayment
                it.refund = refund
                it.aport = contribution
                it.fee = fee
                it.saving = saving
                it.asist = assistance
            }
        }

        val notEdited = sequencePayControlClient.filter { !it.isEdited }
        toolbar.menu.findItem(R.id.init_save).isVisible = notEdited.isEmpty()

    }

    private fun onSequenceFinishedEdit(sequenceId: Long) {
        launch {
            var payControl = presenter.preparePayControlForEdit(paycontrol_id, sequence!!, txtobservations.text.toString(), txtdate.text.toString(), sequenceId)

            editSequences(this@MainActivity, sequencePayControlClient, payControl) { wasSuccessful, message ->
                runOnUiThread {
                    if (wasSuccessful) {
                        saveLocation(Position(null, Date().time, Date().time, PositionType.SEQUENCE_SAVE.type, 0.0, 0.0, 0.0, location.toString(), group_id.toLong(), payControl.sequences[0].id!!, Date().time))
                        Toast.makeText(this@MainActivity, "Registros editados con exito", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

    private fun onSequenceFinished() {

        val notEdited = sequencePayControlClient.filter { !it.isEdited }

        if (notEdited.isNotEmpty()) {
            Toast.makeText(this@MainActivity, "Termina de registrar todos los pagos", Toast.LENGTH_SHORT).show()

        } else {
            launch {
                val payControl = presenter.getPayControlForNewRegister(paycontrol_id, txtdate.text.toString(), (spinner_type.selectedItem as Types).description, cicle, txtobservations.text.toString())

                createNewPayControl(this@MainActivity, payControl, sequencePayControlClient) {
                    runOnUiThread {
                        if (it) {
                            saveLocation(Position(null, Date().time, Date().time, PositionType.SEQUENCE_NEW.type, 0.0, 0.0, 0.0, location.toString(), group_id.toLong(), payControl.sequences[0].id!!, Date().time))
                            Toast.makeText(this@MainActivity, "Registros guardados con exito", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Error al guardar", Toast.LENGTH_LONG).show()
                        }

                    }
                }
            }
        }
    }

    companion object {
        val ARGUMENT_PAY_CONTROL = "ARGUMENT_PAY_CONTROL"
        val ARGUMENT_GROUP = "ARGUMENT_GROUP"
        val ARGUMENT_SEQUENCE = "ARGUMENT_SEQUENCE"
        val ARGUMENT_IS_EDIT = "ARGUMENT_IS_EDIT"

        val ARGUMENT_REGISTER_TYPE = "ARGUMENT_REGISTER_TYPE"
        val ARGUMENT_OBSERVATIONS = "ARGUMENT_OBSERVATIONS"
        val ARGUMENT_DATE = "ARGUMENT_DATE"

        val ARGUMENT_CICLE = "ARGUMENT_CICLE"
    }

    var sequencePayControlClient = ArrayList<SequencePayControlClient>()

    var paycontrol_id = 0L
    var sequence: Short? = null
    var is_editable = false
    var fecreg = 0L
    var register_type = ""
    var observations = ""
    var group_id = 0
    var cicle: Short? = null

    var group = Group()
    var clients: ArrayList<Client> = arrayListOf()

    private val presenter by lazy {
        MainPresenter(MainView(this))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sync -> {
                syncData()
            }
            R.id.action_logout -> {
                launch {
                    prefHelper.setLogged(false)
                    runOnUiThread {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this@MainActivity,"Logged out!!", Toast.LENGTH_SHORT).show()
                        finish()

                    }
                    //presenter.clearDB()
                    //runOnUiThread {
                    //    Toast.makeText(this@MainActivity, "Datos eliminados", Toast.LENGTH_LONG).show()
                    //    finishAffinity()
                    //}
                }
            }
            R.id.action_close -> {
                Toast.makeText(this@MainActivity,"¡Adios!", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.action_free -> {
                presenter.freeApp {
                    wasSuccessful ->
                    runOnUiThread {
                        if (wasSuccessful) {
                            Toast.makeText(this@MainActivity, "¡Liberado!", Toast.LENGTH_SHORT).show()
                            presenter.close()
                        } else {
                            Toast.makeText(this@MainActivity, "No es posible liberar, existen cambios por sincronizar", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        presenter.init()
        presenter.showMenuInfo()
        nav_view.setNavigationItemSelectedListener(this)

        intent.extras?.let {
            paycontrol_id = it.getLong(ARGUMENT_PAY_CONTROL)
            sequence = it.getShort(ARGUMENT_SEQUENCE, 0)
            is_editable = it.getBoolean(ARGUMENT_IS_EDIT, false)

            fecreg = it.getLong(ARGUMENT_DATE)
            register_type = it.getString(ARGUMENT_REGISTER_TYPE)
            observations = it.getString(ARGUMENT_OBSERVATIONS)

            group_id = it.getInt(ARGUMENT_GROUP)
            cicle = it.getShort(ARGUMENT_CICLE, 0)

            launch {
                group = async {
                    presenter.getGroupById(group_id.toLong())
                }.await()

                runOnUiThread {
                    if (is_editable) {
                        supportFragmentManager?.beginTransaction()?.replace(R.id.container, PaySequenceFragment.newInstance(group_id, cicle!!, group.name, paycontrol_id, SimpleDateFormat("dd/MM/yyyy").format(Date(fecreg)), register_type, observations, sequence
                                ?: 0))?.commit()
                    } else {
                        supportFragmentManager?.beginTransaction()?.replace(R.id.container, PayRegisterFragment.newInstance(group_id, cicle!!, group.name, paycontrol_id))?.commit()
                    }
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        if (sequencePayControlClient.size == 0) {
            toolbar.menu.findItem(R.id.init_save).isVisible = false
        }

        toolbar.menu.findItem(R.id.init_register).isVisible = !is_editable
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.init_register) {

            fragmentCallback()

            if (sequencePayControlClient.size == 0) {
                toolbar.menu.findItem(R.id.init_save).isVisible = true
            }

            hideKeyboard(this@MainActivity)

        } else if (id == R.id.init_save) {
            presenter.goToResume(sequencePayControlClient)
        }

        return true
    }

    override fun onBackPressed() {
        val dialog = ConfirmDialog<Unit>(this, "Cancelar", "Salir")
        dialog.setOnCancelListener {
            dialog.dismiss()
        }

        dialog.setOnAcceptListener {
            dialog.dismiss()
            super.onBackPressed()
        }

        dialog.show()

    }

    private fun syncData() {
        saveLocation(Position(null, Date().time, Date().time, PositionType.GROUP_SYNC.type, 0.0, 0.0, 0.0, location.toString(), 0,
                0, Date().time))
        presenter.syncData {
            success, message ->
            runOnUiThread {
                if (success) {
                    //Correct Sync
                    Toast.makeText(this@MainActivity,"¡Sincronizado!", Toast.LENGTH_LONG).show()
                    presenter.freeAppAfterSync {
                        wasSuccessful ->
                        runOnUiThread {
                            if (wasSuccessful) {
                                Toast.makeText(this@MainActivity, "¡Liberado!", Toast.LENGTH_SHORT).show()
                                presenter.close()
                            } else {
                                Toast.makeText(this@MainActivity, "No es posible liberar, existen cambios por sincronizar", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    //Erroneous Sync
                    Toast.makeText(this@MainActivity,"No sincronizado. Error: $message", Toast.LENGTH_LONG).show();
                }
            }
        }
        Toast.makeText(this@MainActivity, "Sincronizando, espera por favor", Toast.LENGTH_LONG).show()
    }
}
