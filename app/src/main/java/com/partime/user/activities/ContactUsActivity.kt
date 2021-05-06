package com.partime.user.activities

import android.content.Context
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
import com.partime.user.Responses.ContactUsResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_contact_us.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class ContactUsActivity : BaseActivity(), View.OnClickListener {

    var map = HashMap<String, String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        AppValidator.rotateBackArrow(imgBackContactUs, this@ContactUsActivity)

        //get the user name and user email
        var userName = intent.getStringExtra(Constants.USER_NAME)
        var userEmail = intent.getStringExtra(Constants.USER_EMAIL)

        if (userName != null && userEmail != null) {

            //set the user name and email
            edtNameContactUs.setText(userName)
            edtNameContactUs.setSelection(edtNameContactUs.text.length)
            edtEmailContactUs.setText(userEmail)
            edtEmailContactUs.setSelection(edtEmailContactUs.text.length)

        }


        imgBackContactUs.setOnClickListener(this)
        btnSendContactUs.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        if (v == imgBackContactUs) {
            onBackPressed()
        }
        if (v == btnSendContactUs) {

            if (AppValidator.isInternetAvailable(this)) {

                if (checkValidations()) {
                    contactUs()
                }

            } else {

                Toast.makeText(this@ContactUsActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*
    Method to check validations
     */
    private fun checkValidations(): Boolean {

        if (edtNameContactUs.text.isNullOrEmpty() || edtNameContactUs.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_name, Toast.LENGTH_LONG).show()
            return false
        } else if (edtEmailContactUs.text.isNullOrEmpty() || edtEmailContactUs.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_email, Toast.LENGTH_LONG).show()
            return false
        } else if (!AppValidator.isValidEmail(edtEmailContactUs.text.toString())) {

            Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_LONG).show()
            return false

        } else if (edtProblemContactUs.text.isNullOrEmpty() || edtProblemContactUs.text.isNullOrBlank()) {
            Toast.makeText(this, com.partime.user.R.string.no_problem, Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }

    }

    /*
    Method to contact us
     */
    private fun contactUs() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@ContactUsActivity)
        var authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())


        map.put("email", edtEmailContactUs.text.toString().trim())
        map.put("name", edtNameContactUs.text.toString().trim())
        map.put("problem", edtProblemContactUs.text.toString().trim())

        val apiService = ApiService.init()
        val call: Call<ContactUsResponse> = apiService.contactUs(
            "Bearer $authkey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()),
            map
        )
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<ContactUsResponse> {
            override fun onResponse(call: Call<ContactUsResponse>, response: retrofit2.Response<ContactUsResponse>?) {
                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        Toast.makeText(this@ContactUsActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        val intent = Intent(this@ContactUsActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {

                        Toast.makeText(
                            this@ContactUsActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

            }

            override fun onFailure(call: Call<ContactUsResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@ContactUsActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }


        })


    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(11)
        finish()
    }

    //hide keyboard on any touch on screen
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
//        return true
//    }
}
