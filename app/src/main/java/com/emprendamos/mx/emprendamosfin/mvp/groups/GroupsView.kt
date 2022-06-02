package com.emprendamos.mx.emprendamosfin.mvp.groups

import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.widget.TextView
import android.widget.Toast
import com.emprendamos.mx.emprendamosfin.GroupsActivity
import com.emprendamos.mx.emprendamosfin.LoginActivity
import com.emprendamos.mx.emprendamosfin.R
import com.emprendamos.mx.emprendamosfin.mvp.BaseView
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.app_bar_groups.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class GroupsView(activity : GroupsActivity) : BaseView<GroupsActivity>(activity)
{

    fun init() {

        val toggle = ActionBarDrawerToggle(
                activity, activity.drawer_layout, activity.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        activity.drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        activity.toolbar.navigationIcon = ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_caja_navegacion, null)
    }

    fun setAdvisorName(name : String) {
        activity.nav_view.getHeaderView(0).text_full_name.text = name
    }

    fun setLastSync(lastSync : String) {
        activity.nav_view.getHeaderView(0).text_last_sync.text = lastSync
    }

    fun finishIfEmptyData () {
        Toast.makeText(activity,"Este usuario no tiene información que mostrar",Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, LoginActivity::class.java)
        activity.startActivity(intent)
    }

}