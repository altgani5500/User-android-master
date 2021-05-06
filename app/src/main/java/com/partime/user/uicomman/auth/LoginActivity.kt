package com.parttime.user.uicomman.auth

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.gems.app.utilities.AppValidator
import com.partime.user.R
import com.partime.user.Responses.LoginResponse
import com.partime.user.activities.HomeActivity
import com.partime.user.activities.RegisterActivity
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.helpers.Utilities
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import com.parttime.user.uicomman.forgots.ForgotActivity
import kotlinx.android.synthetic.main.acitivity_logins.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : BaseActivity(), View.OnClickListener {

    var map = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_logins)

        if (appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()) == "ar") {

            imgBackLogin.visibility = View.GONE
        }

        edtLoginUserName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                var result = s.toString().replace(" ", "")
                if (!s.toString().equals(result)) {
                    edtLoginUserName.setText(result)
                    edtLoginUserName.setSelection(result.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }


        })

        tvForgot.setOnClickListener(this)
        txtRegisterHere.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
        imgBackLogin.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        if (v == tvForgot) {
            launchActivity(ForgotActivity::class.java)
        }
        if (v == txtRegisterHere) {

            launchActivity(RegisterActivity::class.java)
            finish()

        }
        if (v == btnLogin) {

            //hide the keypad when done
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(btnLogin.windowToken, 0)
            if (checkVlaidations())

                if (AppValidator.isInternetAvailable(this)) {

                    login()

                } else {

                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show()

                }
        }
        if (v == imgBackLogin) {

            setResult(11)
            onBackPressed()

        }
    }

    private fun login() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@LoginActivity)

        map.put("userName", edtLoginUserName.text.toString().trim())
        map.put("password", edtLoginPassword.text.toString().trim())


        val apiService = ApiService.init()
        val call = apiService.login(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()), map)
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: retrofit2.Response<LoginResponse>?) {
                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        Toast.makeText(this@LoginActivity, R.string.login_successful, Toast.LENGTH_LONG)
                        saveUserDetails(response)
                        Utilities.clearAllgoToActivity(this@LoginActivity, HomeActivity::class.java)
                        finishAffinity()

                    } else {


                        Toast.makeText(this@LoginActivity, response.body()?.error_message.toString(), Toast.LENGTH_LONG)
                            .show()

                    }

                }


            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@LoginActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    private fun checkVlaidations(): Boolean {

        if (edtLoginUserName.text.isNullOrEmpty() || edtLoginUserName.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_user_name, Toast.LENGTH_LONG).show()
            return false
        } else if (edtLoginPassword.text.isNullOrEmpty() || edtLoginPassword.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_password, Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }


    }

    override fun onStart() {
        super.onStart()
        overridePendingTransition(R.anim.right_to_left_animation, R.anim.left_to_right_animation)
    }

    override fun onDestroy() {
        super.onDestroy()
        overridePendingTransition(R.anim.right_to_left_animation, R.anim.left_to_right_animation)
    }

    private fun saveUserDetails(response: Response<LoginResponse>) {
        appPrefence.setBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString(), true)
        appPrefence.setString(
            AppPrefence.SharedPreferncesKeys.USER_ID.toString(),
            response.body()?.message?.userId.toString()
        )
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString(), response.body()?.message?.apiToken)
    }


    override fun onRestart() {
        super.onRestart()
        clearData()

    }

    private fun clearData() {

        edtLoginUserName.setText("")
        edtLoginPassword.setText("")
    }

    //hide keyboard on any touch on screen
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)
        return super.onTouchEvent(event)
    }

}
