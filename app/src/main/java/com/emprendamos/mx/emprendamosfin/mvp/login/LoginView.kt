package com.emprendamos.mx.emprendamosfin.mvp.login

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.emprendamos.mx.emprendamosfin.BuildConfig
import com.emprendamos.mx.emprendamosfin.GroupsActivity
import com.emprendamos.mx.emprendamosfin.LoginActivity
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.data.PreferenceHelper
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Advisor
import com.emprendamos.mx.emprendamosfin.data.database.repository.mapper.MapperManager
import com.emprendamos.mx.emprendamosfin.mvp.BaseView
import kotlinx.android.synthetic.main.content_login.*

class LoginView(act: LoginActivity) : BaseView<LoginActivity>(act) {

    private val mapperManager by lazy {
        MapperManager()
    }

    private val preferenceManager by lazy {
        PreferenceHelper(activity)
    }

    fun onLoginResult(data: String, advisor: Advisor = Advisor(), local: Boolean) {

        val userName = if (local) {
            data
        } else {
            mapperManager.exportAdvisorName(data)
        }

        if (userName.isEmpty()) {
            activity.runOnUiThread {
                Toast.makeText(activity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        } else {
            advisor.name = userName

            preferenceManager.saveAdvisor(advisor)

            activity.runOnUiThread {
                Toast.makeText(activity, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                var intent = Intent(activity, GroupsActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }

    fun onLoginFailed() {
        activity.runOnUiThread {
            Toast.makeText(activity, "Hubo un error al realizar la consulta", Toast.LENGTH_SHORT).show()
        }
    }

    fun byPass() {
        var intent = Intent(activity, GroupsActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

    fun showSettings() {
        var dialogBuilder = AlertDialog.Builder(activity)
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_settings, null)

        val dialog = dialogBuilder.create()
        val loginUrl = dialogView.findViewById<EditText>(R.id.tiet_url_login).apply {
            setText(preferenceManager.getLoginUrl())
        }

        val servicesUrl = dialogView.findViewById<EditText>(R.id.tiet_url_services).apply {
            setText(preferenceManager.getServicesUrl())
        }

        val gpsUrl = dialogView.findViewById<EditText>(R.id.tiet_url_gps).apply {
            setText(preferenceManager.getGpsUrl())
        }

        dialogView.findViewById<Button>(R.id.btn_save_config).setOnClickListener {
            if (loginUrl.text.toString().isEmpty() || gpsUrl.text.toString().isEmpty() || servicesUrl.text.toString().isEmpty()) {
                Toast.makeText(activity, activity.getString(R.string.hint_fill_allfields), Toast.LENGTH_LONG).show()
            } else {
                preferenceManager.setLoginUrl(loginUrl.text.toString())
                preferenceManager.setServicesUrl(servicesUrl.text.toString())
                preferenceManager.setGpsUrl(gpsUrl.text.toString())
                Toast.makeText(activity, activity.getString(R.string.hint_config_saved), Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }

        dialogView.findViewById<Button>(R.id.btn_restore_config).setOnClickListener {
            preferenceManager.restoreConfig()
            dialog.dismiss()
        }

        dialog.setView(dialogView)
        dialog.show()
    }

    fun setVersionCode() {
        activity.text_info.text = "Emprendamos Financiera v.${BuildConfig.VERSION_NAME}"
    }
}