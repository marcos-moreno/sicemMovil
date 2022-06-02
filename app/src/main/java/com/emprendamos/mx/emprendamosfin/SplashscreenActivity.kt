package com.emprendamos.mx.emprendamosfin

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AlphaAnimation
import android.view.animation.RotateAnimation
import android.view.animation.DecelerateInterpolator

import android.widget.Toast
import kotlinx.android.synthetic.main.activity_splashscreen.*

const val ALL_PERMISSIONS_RESULT = 1011

class SplashscreenActivity : AppCompatActivity() {

    private val permissionsToRequest: Array<String> = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun hasPermissions(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.isNotEmpty()) {
                val permissionsToRequestAgain = arrayListOf<String>()
                permissionsToRequest.forEach {
                    if (!hasPermissions(it))
                    {
                        permissionsToRequestAgain.add(it)
                    }
                }

                if (permissionsToRequestAgain.size > 0)
                    requestPermissions(permissionsToRequestAgain.toTypedArray(), ALL_PERMISSIONS_RESULT)
                else
                    rotateImage()
            }
        } else
            rotateImage()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ALL_PERMISSIONS_RESULT -> {
                var permissionsDenied = arrayListOf<String>()
                permissionsToRequest.forEach {
                    if (!hasPermissions(it))
                    {
                        permissionsDenied.add(it)
                        Toast.makeText(this@SplashscreenActivity,"Permisos no concedidos",Toast.LENGTH_SHORT).show()
                    }
                }

                if (permissionsDenied.size == 0)
                {
                    rotateImage()
                }
            }
        }
    }

    fun rotateImage() {
        var animationSet = AnimationSet(false)

        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 1000
        rotate.interpolator = DecelerateInterpolator()
        rotate.fillAfter = true

        rotate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                val i = Intent(this@SplashscreenActivity, LoginActivity::class.java)
                startActivity(i)

                finish()
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })

        var alphaAnimation = AlphaAnimation(0F, 1F)
        alphaAnimation.duration = 900
        alphaAnimation.fillAfter = true

        animationSet.addAnimation(rotate)
        animationSet.addAnimation(alphaAnimation)

        this.imgvSplash.startAnimation(animationSet)
    }

}
