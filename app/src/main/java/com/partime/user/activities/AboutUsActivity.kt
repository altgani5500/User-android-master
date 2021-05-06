package com.partime.user.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.debut.countrycodepicker.CountryPicker
import com.debut.countrycodepicker.data.Country
import com.debut.countrycodepicker.listeners.CountryCallBack
import com.gems.app.utilities.AppValidator
import com.google.android.material.snackbar.Snackbar

import com.partime.user.R
import com.partime.user.Responses.AboutUsResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_about_us.*
import retrofit2.Call
import retrofit2.Callback

class AboutUsActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

//        editTextTextPersonName.setOnClickListener { view ->
//
//            CountryPicker.show(supportFragmentManager, object : CountryCallBack {
//
//                override fun onCountrySelected(country: Country) {
//
//                    editTextTextPersonName.setText("${country.name}")
//                    //Snackbar.make(fab, "Country : ${country.name} and Country Code : ${country.countryCode}", Snackbar.LENGTH_LONG).show()
//                }
//            })
//        }



        AppValidator.rotateBackArrow(imgBackAboutUs, this@AboutUsActivity)

        //check for net connection
        if (AppValidator.isInternetAvailable(this)) {

            aboutUs()

        } else {

            linearLayoutAboutUs.visibility = View.GONE
            llErrorAboutUs.visibility = View.VISIBLE

        }

        btnRetryAboutUs.setOnClickListener(this)
        imgBackAboutUs.setOnClickListener(this)
    }

    /*
    Method to get about us data
     */
    private fun aboutUs() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@AboutUsActivity)

        val apiService = ApiService.init()
        val call: Call<AboutUsResponse> =
            apiService.aboutUs(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()))

        call.enqueue(object : Callback<AboutUsResponse> {
            override fun onResponse(call: Call<AboutUsResponse>, response: retrofit2.Response<AboutUsResponse>?) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        txtUserCountAboutUs.text = response.body()?.message?.userCount.toString()
                        txtEnterPriseCountAboutUs.text = response.body()?.message?.enterpriseCount.toString()
                        txtAboutUs.text = response.body()?.message?.aboutUs

                    } else {

                        Toast.makeText(
                            this@AboutUsActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

            }

            override fun onFailure(call: Call<AboutUsResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                linearLayoutAboutUs.visibility = View.GONE
                llErrorAboutUs.visibility = View.VISIBLE
            }

        })

    }

    override fun onClick(v: View?) {

        if (v == imgBackAboutUs) {
            onBackPressed()
        }
        if (v == btnRetryAboutUs) {

            llErrorAboutUs.visibility = View.GONE

            if (AppValidator.isInternetAvailable(this)) {

                linearLayoutAboutUs.visibility = View.VISIBLE
                aboutUs()

            } else {

                linearLayoutAboutUs.visibility = View.GONE
                llErrorAboutUs.visibility = View.VISIBLE

            }
        }

    }

    //on back pressed
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(11)
        finish()
    }
}
