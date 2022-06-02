package com.emprendamos.mx.emprendamosfin.mvp.main

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.ActionBarDrawerToggle
import com.emprendamos.mx.emprendamosfin.MainActivity
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.mvp.BaseView
import com.emprendamos.mx.emprendamosfin.ui.PayResumeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_pay_register.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainView(var a : MainActivity) : BaseView<MainActivity>(a) {

    fun init() {
        val toggle = ActionBarDrawerToggle(
                activity, activity.drawer_layout, activity.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        activity.drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        activity.toolbar.navigationIcon = ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_caja_navegacion, null)
    }

    fun goToResume(theoricalPaymentSum: Double, realPaymentSum: Double, refundSum: Double, aportSum: Double, feeSum: Double, savingSum: Double, sequenceId : Long) {
        PayResumeFragment.newInstance(activity.txtcode.text.toString(), activity.txtgroup.text.toString(), activity.txtcicle.text.toString(), activity.txtdate.text.toString(), "", activity.txtobservations.text.toString(), theoricalPaymentSum, realPaymentSum, refundSum, aportSum, feeSum, savingSum, sequenceId).show(activity.supportFragmentManager, "payResumeFragment")
    }

    fun setAdvisorName(name : String) {
        activity.nav_view.getHeaderView(0).text_full_name.text = name
    }

    fun setLastSync(lastSync : String) {
        activity.nav_view.getHeaderView(0).text_last_sync.text = lastSync
    }
}