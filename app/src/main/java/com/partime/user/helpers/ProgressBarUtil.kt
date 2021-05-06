package com.partime.user.helpers

import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.partime.user.R

class ProgressBarUtil {

    var progressDialog: ProgressDialog? = null

    fun showProgressDialog(context: Context): ProgressDialog {

        if (progressDialog != null && progressDialog!!.isShowing) {

            progressDialog!!.dismiss()

        } else {
            progressDialog = ProgressDialog(context)
            val inflater = LayoutInflater.from(context)
            progressDialog!!.window.requestFeature(Window.FEATURE_NO_TITLE)
            progressDialog!!.window.setBackgroundDrawableResource(android.R.color.transparent)
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
            progressDialog!!.setContentView(inflater.inflate(R.layout.layout_dialog, null))
        }
        return progressDialog!!
    }

    fun hideProgressDialog(progressBar: ProgressDialog) {

        if (progressBar != null && progressBar.isShowing) {
            progressBar.dismiss()
        }
    }

}