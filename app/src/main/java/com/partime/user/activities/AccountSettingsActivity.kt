package com.partime.user

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.gems.app.utilities.AppValidator
import com.partime.user.activities.ChangePasswordActivity
import com.partime.user.activities.EditEmailActivity
import com.partime.user.activities.RegisterActivity
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.user.uicomman.auth.LoginActivity
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.national_id_dialog.*
import kotlinx.android.synthetic.main.register_dailog.*

class AccountSettingsActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        AppValidator.rotateBackArrow(imgBackAccSett, this@AccountSettingsActivity)

        imgBackAccSett.setOnClickListener(this)
        llEditEmail.setOnClickListener(this)
        llChangeNationalId.setOnClickListener(this)
        llChangePassword.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v) {

            llEditEmail -> {

                if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                    //register dialog....
                    registerDialog()

                } else {

                    var intent = Intent(this@AccountSettingsActivity, EditEmailActivity::class.java)
                    startActivity(intent)
                }

            }
            llChangeNationalId -> {
                if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {
                    //register dialog....
                    registerDialog()

                } else {
                    //change national id....
                    changeNationalId()
                }
            }
            llChangePassword -> {

                if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {
                    //register dialog....
                    registerDialog()

                } else {

                    var intent = Intent(this@AccountSettingsActivity, ChangePasswordActivity::class.java)
                    startActivity(intent)
                }
            }
            imgBackAccSett -> {
                onBackPressed()
            }
        }

    }

    /*
    Method to show dialog for chaging national id
     */
    private fun changeNationalId() {

        var chnageNationalIdDailog = Dialog(this@AccountSettingsActivity)

        chnageNationalIdDailog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        chnageNationalIdDailog.setContentView(R.layout.national_id_dialog)
        chnageNationalIdDailog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        chnageNationalIdDailog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        //dailog button click
        chnageNationalIdDailog.btnOkNationalDialog.setOnClickListener {

            chnageNationalIdDailog.dismiss()
        }

        chnageNationalIdDailog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /*
    Method to show register dialog
     */
    private fun registerDialog() {

        var registerDailog = Dialog(this)

        registerDailog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        registerDailog.setContentView(R.layout.register_dailog)
        registerDailog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        registerDailog.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        registerDailog.btnRegisterDailog.setOnClickListener {

            val intent = Intent(this@AccountSettingsActivity, RegisterActivity::class.java)
            startActivity(intent)
            registerDailog.dismiss()

        }
        registerDailog.txtLoginHereRegisterDailog.setOnClickListener {

            val intent = Intent(this@AccountSettingsActivity, LoginActivity::class.java)
            startActivity(intent)
            registerDailog.dismiss()

        }
        //show dailog....
        registerDailog.show()
    }
}
