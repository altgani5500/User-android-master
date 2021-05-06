package com.parttime.user.uicomman.forgots

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.gems.app.utilities.AppValidator
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.ForgotResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import com.parttime.user.uicomman.auth.LoginActivity
import kotlinx.android.synthetic.main.forgot_email.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*


class ForgotEmailActivity : BaseActivity(), View.OnClickListener {


    var map = HashMap<String, String>()
    var forgotType: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_email)
        AppValidator.rotateBackArrow(imgBackForgotMain, this@ForgotEmailActivity)

        forgotType = intent.getStringExtra(Constants.FORGOT_TYPE)

        btnForgotSumitMail.setOnClickListener(this)
        imgBackForgotMain.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        if (v == btnForgotSumitMail) {

            if (checkValidation()) {

                if (AppValidator.isInternetAvailable(this)) {

                    if (forgotType!!.contentEquals("For username")) {

                        forgot(1)
                    } else if (forgotType!!.contentEquals("For password")) {

                        forgot(2)
                    }


                } else {

                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show()

                }
            }
        }

        if (v == imgBackForgotMain) {

            onBackPressed()
        }
    }

    private fun forgot(type: Int) {

        var progressBar = ProgressBarUtil().showProgressDialog(this@ForgotEmailActivity)


        map.put("email", edtForgotEmail.text.toString().trim())
        map.put("forgotType", type.toString())


        val apiService = ApiService.init()
        val call = apiService.forgot(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()), map)
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<ForgotResponse> {
            override fun onResponse(call: Call<ForgotResponse>, response: retrofit2.Response<ForgotResponse>?) {
                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        Toast.makeText(this@ForgotEmailActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        val intent = Intent(this@ForgotEmailActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {


                        Toast.makeText(
                            this@ForgotEmailActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }


            }

            override fun onFailure(call: Call<ForgotResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@ForgotEmailActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }


        })


    }

    private fun checkValidation(): Boolean {

        if (edtForgotEmail.text.isNullOrEmpty() || edtForgotEmail.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_email, Toast.LENGTH_LONG).show()
            return false
        } else if (!AppValidator.isValidEmail(edtForgotEmail.text.toString())) {

            Toast.makeText(this, com.partime.user.R.string.error_invalid_email, Toast.LENGTH_LONG).show()
            return false

        } else {
            return true
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    //hide keyboard on any touch on screen
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)
        return super.onTouchEvent(event)
    }
}