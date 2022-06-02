package com.emprendamos.mx.emprendamosfin.mvp.login

import android.util.Log
import com.emprendamos.mx.emprendamosfin.data.PreferenceHelper
import com.emprendamos.mx.emprendamosfin.data.database.repository.entities.Advisor
import com.emprendamos.mx.emprendamosfin.data.services.SoapManager
import kotlinx.coroutines.experimental.async

class LoginPresenter(val view : LoginView) {

    private val soapManager by lazy {
        SoapManager(view.activity)
    }

    private val prefHelper by lazy {
        PreferenceHelper(view.activity)
    }

    private val advisor by lazy {
        prefHelper.getAdvisor()
    }

    fun login(userName: String, userCode: String) {
        view.showLoader()

        if (advisor != null && advisor!!.key.equals(userName, true) && advisor!!.code.equals(userCode, true)) {
            Log.e("LoginLocal", "si validó local")
            view.onLoginResult(advisor!!.name, Advisor(1,"EMPFIN",userCode, "", userName), true)
        } else {
            Log.e("LoginLocal", "no validó local")
            async {
                soapManager.doLogin(userName, userCode) {
                    if (it.isNotEmpty())
                        view.onLoginResult(it, Advisor(1, "EMPFIN", userCode, "", userName), false)
                    else
                        view.onLoginFailed()

                    view.hideLoader()
                }
            }
        }
    }

    fun byPass() {

        view.byPass()
    }

    fun settings() {
        view.showSettings()
    }

    fun setVersionCode() {
        view.setVersionCode()
    }
}