package com.partime.user.activities

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.gems.app.utilities.AppValidator
import com.partime.user.R
import com.partime.user.Responses.ChangePasswordResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_change_password.*
import retrofit2.Call
import retrofit2.Callback

class ChangePasswordActivity : BaseActivity(), View.OnClickListener {

    var map = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        imgBackChangePass.setOnClickListener(this)
        imgChangePasswordOk.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackChangePass -> onBackPressed()

            imgChangePasswordOk -> {

                if (validations()) {
                    changePassword()

                }
            }
        }

    }

    /*
    Method to check validations
     */

    private fun validations(): Boolean {

        if (edtChangePassOldPassword.text.isNullOrBlank() || edtChangePassOldPassword.text.isNullOrEmpty()) {

            Toast.makeText(this, R.string.no_old_passowrd, Toast.LENGTH_LONG).show()
            return false
        } else if (edtChangePassNewPassword.text.isNullOrBlank() || edtChangePassNewPassword.text.isNullOrEmpty()) {
            Toast.makeText(this, R.string.no_password, Toast.LENGTH_LONG).show()
            return false
        } else if (!AppValidator.isValidPassword(edtChangePassNewPassword.text.toString())) {
            Toast.makeText(this, com.partime.user.R.string.invalid_password, Toast.LENGTH_LONG).show()
            return false
        } else if (edtChangePassConfrimPassword.text.isNullOrBlank() || edtChangePassConfrimPassword.text.isNullOrEmpty()) {
            Toast.makeText(this, R.string.no_confrim_password, Toast.LENGTH_LONG).show()
            return false
        } else if (!edtChangePassConfrimPassword.text.toString().equals(edtChangePassNewPassword.text.toString())) {

            Toast.makeText(this, R.string.error_password_not_match, Toast.LENGTH_LONG).show()
            return false
        } else {

            return true
        }
    }

    /*
    Method to change password
     */
    private fun changePassword() {
        var progressBar = ProgressBarUtil().showProgressDialog(this@ChangePasswordActivity)

        var authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        map.put("oldPassword", edtChangePassOldPassword.text.toString())
        map.put("newPassword", edtChangePassNewPassword.text.toString())

        val apiService = ApiService.init()
        val call: Call<ChangePasswordResponse> = apiService.changeUserPassword(
            "Bearer $authkey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()),
            map
        )

        call.enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(
                call: Call<ChangePasswordResponse>,
                response: retrofit2.Response<ChangePasswordResponse>?
            ) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        Toast.makeText(
                            this@ChangePasswordActivity,
                            response.body()?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()

                    } else {

                        Toast.makeText(
                            this@ChangePasswordActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }


            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@ChangePasswordActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })


    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
    }

    //hide keyboard on any touch on screen
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)
        return super.onTouchEvent(event)
    }
}
