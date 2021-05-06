package com.partime.user.activities

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.gems.app.utilities.AppValidator
import com.partime.user.R
import com.partime.user.Responses.EditEmailResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_edit_email.*
import retrofit2.Call
import retrofit2.Callback

class EditEmailActivity : BaseActivity(), View.OnClickListener {

    var map = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_email)

        imgBackEditEmail.setOnClickListener(this)
        imgChangeEmailOk.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackEditEmail -> onBackPressed()

            imgChangeEmailOk -> {

                if (validations()) {
                    changeEmail()
                }

            }
        }

    }

    /*
    Method to check validations
     */

    private fun validations(): Boolean {

        if (edtEditEmail.text.isNullOrEmpty() || edtEditEmail.text.isNullOrBlank()) {
            Toast.makeText(this, com.partime.user.R.string.no_email, Toast.LENGTH_LONG).show()
            return false
        } else if (!AppValidator.isValidEmail(edtEditEmail.text.toString())) {

            Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_LONG).show()
            return false

        } else {
            return true
        }
    }

    /*
    Method to change email
     */
    private fun changeEmail() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@EditEmailActivity)

        var authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        map.put("email", edtEditEmail.text.toString())

        val apiService = ApiService.init()
        val call: Call<EditEmailResponse> = apiService.changeEmail(
            "Bearer $authkey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()),
            map
        )

        call.enqueue(object : Callback<EditEmailResponse> {
            override fun onResponse(
                call: Call<EditEmailResponse>,
                response: retrofit2.Response<EditEmailResponse>?
            ) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        Toast.makeText(this@EditEmailActivity, response.body()?.message.toString(), Toast.LENGTH_LONG)
                            .show()
                        finish()

                    } else {

                        Toast.makeText(
                            this@EditEmailActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }


            }

            override fun onFailure(call: Call<EditEmailResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@EditEmailActivity, R.string.no_internet, Toast.LENGTH_LONG)

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
