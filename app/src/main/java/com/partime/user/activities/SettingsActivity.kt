package com.partime.user.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.gems.app.utilities.AppValidator
import com.partime.user.AccountSettingsActivity
import com.partime.user.R
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.user.uicomman.auth.LoginActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.register_dailog.*

class SettingsActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        AppValidator.rotateBackArrow(imgSettingsBack, this@SettingsActivity)
        imgSettingsBack.setOnClickListener(this)
        llAccountSettings.setOnClickListener(this)
        llChangeLanguage.setOnClickListener(this)
        llAdvanceSettings.setOnClickListener(this)
        llNotifictaionSettings.setOnClickListener(this)
        llAddNetworks.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {

            imgSettingsBack -> onBackPressed()

            llAccountSettings -> {

                if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                    registerDialog()

                } else {
                    var intent = Intent(this@SettingsActivity, AccountSettingsActivity::class.java)
                    startActivity(intent)
                }

            }
            llChangeLanguage -> {

                var intent = Intent(this@SettingsActivity, ChangeLanguageActivity::class.java)
                startActivity(intent)
            }
            llAdvanceSettings -> {
                if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                    registerDialog()

                } else {
                    var intent = Intent(this@SettingsActivity, AdvanceSettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            llNotifictaionSettings -> {

                if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                    registerDialog()

                } else {
                    var intent = Intent(this@SettingsActivity, NotificationSettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            llAddNetworks->{

                if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                    registerDialog()

                } else {
                    var intent = Intent(this@SettingsActivity, AddNetworkActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }

    /*
   Method to open register dailog
    */
    private fun registerDialog() {

        var registerDailog = Dialog(this@SettingsActivity)

        registerDailog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        registerDailog.setContentView(R.layout.register_dailog)
        registerDailog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        registerDailog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        registerDailog.btnRegisterDailog.setOnClickListener {

            val intent = Intent(this@SettingsActivity, RegisterActivity::class.java)
            startActivity(intent)
            //  finish()
            registerDailog.dismiss()
        }
        registerDailog.txtLoginHereRegisterDailog.setOnClickListener {

            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            startActivity(intent)
            registerDailog.dismiss()

        }
        registerDailog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
