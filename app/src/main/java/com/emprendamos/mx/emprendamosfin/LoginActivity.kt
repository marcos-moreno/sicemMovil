package com.emprendamos.mx.emprendamosfin

import android.os.Bundle
import com.emprendamos.mx.emprendamosfin.data.PreferenceHelper
import com.emprendamos.mx.emprendamosfin.data.database.repository.dao.PositionType
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Position
import com.emprendamos.mx.emprendamosfin.mvp.login.LoginPresenter
import com.emprendamos.mx.emprendamosfin.mvp.login.LoginView
import com.emprendamos.mx.emprendamosfin.ui.BaseLocationActivity
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*
import java.util.*

class LoginActivity : BaseLocationActivity() {

    private val presenter = LoginPresenter(LoginView(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        supportActionBar?.hide()

        presenter.setVersionCode()

        if(PreferenceHelper(this).getAdvisor() != null)
        {
            if(!PreferenceHelper(this).getLogged()) {
                text_asesor.setText(PreferenceHelper(this).getAdvisor()!!.key)
            }
            else
            {
                presenter.byPass()
            }
        }

        btn_login.setOnClickListener() {
            saveLocation(Position(null, Date().time, Date().time, PositionType.LOG_IN.type, 0.0, 0.0, 0.0, location.toString(), 0, 0, Date().time))
            presenter.login(text_asesor.text.toString(),text_password.text.toString())
        }

        btn_config.setOnClickListener {
            presenter.settings()
        }

    }
}
