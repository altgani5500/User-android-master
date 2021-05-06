package com.partime.user.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.gems.app.utilities.AppValidator
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.uicomman.BaseActivity
import kotlinx.android.synthetic.main.activity_terms_conditions.*

class TermsConditionsActivity : BaseActivity(), View.OnClickListener {

    lateinit var progressBar: ProgressDialog
    var url:String="https://www.partime.org/user-t&c.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_conditions)

        if (AppValidator.isInternetAvailable(this@TermsConditionsActivity)) {
            if (!url.isNullOrEmpty()) {

                progressBar = ProgressBarUtil().showProgressDialog(this@TermsConditionsActivity)
                termsConditionsWebView.settings.javaScriptEnabled = true
                setWebView()
            }
        } else {

            llErrorTermsConditions.visibility = View.VISIBLE
            termsConditionsWebView.visibility = View.GONE
        }

        imgBackTermsConditions.setOnClickListener(this)
        btnRetryTermsConditions.setOnClickListener(this)
    }

    /*
    Method to open the web view
     */
    private fun setWebView() {


        termsConditionsWebView.webViewClient = object : WebViewClient() {

        }
        termsConditionsWebView.loadUrl(url)

        ProgressBarUtil().hideProgressDialog(progressBar)
    }


    override fun onClick(v: View?) {

        if (v == imgBackTermsConditions) {

            finish()
        }
        if (v == btnRetryTermsConditions) {

            llErrorTermsConditions.visibility = View.GONE

            if (AppValidator.isInternetAvailable(this@TermsConditionsActivity)) {

                termsConditionsWebView.visibility = View.VISIBLE

                if (!url.isNullOrEmpty()) {

                    progressBar = ProgressBarUtil().showProgressDialog(this@TermsConditionsActivity)
                    termsConditionsWebView.settings.javaScriptEnabled = true
                    setWebView()
                }
            } else {

                llErrorTermsConditions.visibility = View.VISIBLE
                termsConditionsWebView.visibility = View.GONE
            }

        }

    }
}

