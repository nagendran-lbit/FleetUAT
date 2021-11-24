package com.lbit.fleetuat.ui

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.lbit.fleetuat.R
import com.lbit.payroll.Singleton.UserSession

class SplashScreen : AppCompatActivity() {

    private var mMobile: String = ""

    private var mAppUpdateManager: AppUpdateManager? = null
    private val RC_APP_UPDATE = 11
    private var appUpdateInfoTask: Task<AppUpdateInfo>? = null
    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        supportActionBar!!.hide()

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.header_color)

        mAppUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateInfoTask = mAppUpdateManager!!.getAppUpdateInfo()

        installStateUpdatedListener = InstallStateUpdatedListener { installState: InstallState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate()
            }
        }
        mAppUpdateManager!!.registerListener(installStateUpdatedListener)

        mMobile = UserSession(this@SplashScreen).getMobile()

        if (mMobile.isEmpty()) {

            val i = Intent(this@SplashScreen, LoginScreen::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
            finish()

        } else {

            UserSession(this@SplashScreen).setAttValue("")

            val i = Intent(this@SplashScreen, LandingPage::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
            finish()
        }

    }

    override fun onDestroy() {
        mAppUpdateManager!!.unregisterListener(installStateUpdatedListener)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode == RESULT_OK) {
//                Toast.makeText(this@Splashscreen, "App download started.", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this@Splashscreen, "App download canceled.", Toast.LENGTH_LONG).show();
            } else if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
//                Toast.makeText(this@Splashscreen, "App download failed.", Toast.LENGTH_LONG).show();
            }
        }
    }

    override fun onResume() {
        try {
            mAppUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    // If an in-app update is already running, resume the update.
                    try {
                        mAppUpdateManager!!.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            this,
                            RC_APP_UPDATE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    try {
                        mAppUpdateManager!!.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            this,
                            RC_APP_UPDATE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                } else {

                    /*val i = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(i)
                    overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
                    finish()*/
                }
            }

            mAppUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                //For flexible update
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onResume()
    }

    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(
            findViewById(R.id.splash),
            "New app is ready,Click to install !",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Install") { view: View? ->
            if (mAppUpdateManager != null) {
                mAppUpdateManager!!.completeUpdate()
            }
        }
        snackbar.setActionTextColor(resources.getColor(R.color.text_links))
        snackbar.show()
    }
}