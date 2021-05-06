package com.parttime.user.uicomman.forgots

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.gems.app.utilities.AppValidator
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.uicomman.BaseActivity

import kotlinx.android.synthetic.main.activity_forgot.*

class ForgotActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        AppValidator.rotateBackArrow(imgBackForgot, this@ForgotActivity)


        llForgotPassword.setOnClickListener(this)
        llForgotUserName.setOnClickListener(this)
        imgBackForgot.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == llForgotUserName) {

            val intent = Intent(this, ForgotEmailActivity::class.java)
            intent.putExtra(Constants.FORGOT_TYPE, "For username")
            startActivity(intent)
            finish()

        }
        if (v == llForgotPassword) {

            val intent = Intent(this, ForgotEmailActivity::class.java)
            intent.putExtra(Constants.FORGOT_TYPE, "For password")
            startActivity(intent)
            finish()
        }

        if (v == imgBackForgot) {

            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}