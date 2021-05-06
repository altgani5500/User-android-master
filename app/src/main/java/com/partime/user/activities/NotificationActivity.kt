package com.partime.user.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.NotificationAdapter
import com.partime.user.R
import com.partime.user.Responses.NotificationMessage
import com.partime.user.Responses.NotificationRespose
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_notification.*
import retrofit2.Call
import retrofit2.Callback

class NotificationActivity : BaseActivity(), View.OnClickListener {

    var notifications = ArrayList<NotificationMessage>()
    private var notificationAdapter: NotificationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        AppValidator.rotateBackArrow(imgBackNotiifcations, this@NotificationActivity)

        validateNet()

        swipe_refresh_notification.setOnRefreshListener {
            validateNet()

        }

        imgBackNotiifcations.setOnClickListener(this)
        btnRetryNotfications.setOnClickListener(this)
    }

    /*
    Method to validate network
     */
    private fun validateNet() {

        if (AppValidator.isInternetAvailable(this@NotificationActivity)) {

            recyclerNotifications.visibility = View.VISIBLE
            getNotification()
        } else {
            recyclerNotifications.visibility = View.GONE
            llNoNotifications.visibility = View.GONE
            llErrorNotifications.visibility = View.VISIBLE
        }
    }

    /*
    Method to the notifications
     */
    private fun getNotification() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@NotificationActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val language = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())

        val apiService = ApiService.init()
        val call: Call<NotificationRespose> = apiService.getNotifications("Bearer $authKey", language)

        call.enqueue(object : Callback<NotificationRespose> {
            override fun onResponse(
                call: Call<NotificationRespose>,
                response: retrofit2.Response<NotificationRespose>?
            ) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)
                    swipe_refresh_notification.isRefreshing = false

                    if (response.body()?.code == 200) {

                        Toast.makeText(
                            this@NotificationActivity,
                            response.body()?.success.toString(),
                            Toast.LENGTH_LONG
                        )

                        if (!notifications.isEmpty()) {
                            notifications.clear()
                        }

                        notifications.addAll(response.body()?.message as ArrayList<NotificationMessage>)

                        if (notifications.size > 0) {

                            notificationAdapter = NotificationAdapter(notifications, this@NotificationActivity)

                            llNoNotifications.visibility = View.GONE
                            llErrorNotifications.visibility = View.GONE
                            recyclerNotifications.visibility = View.VISIBLE
                            recyclerNotifications!!.layoutManager =
                                LinearLayoutManager(this@NotificationActivity, RecyclerView.VERTICAL, false)
                            recyclerNotifications!!.adapter = notificationAdapter

                        } else {

                            llNoNotifications.visibility = View.VISIBLE
                            recyclerNotifications.visibility = View.GONE
                            llErrorNotifications.visibility = View.GONE

                        }

                    } else {

                        Toast.makeText(
                            this@NotificationActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

            }

            override fun onFailure(call: Call<NotificationRespose>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)
                swipe_refresh_notification.isRefreshing = false

                recyclerNotifications.visibility = View.GONE
                llNoNotifications.visibility = View.GONE
                llErrorNotifications.visibility = View.VISIBLE

                Toast.makeText(this@NotificationActivity, R.string.no_internet, Toast.LENGTH_LONG)

            }

        })

    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackNotiifcations -> onBackPressed()

            btnRetryNotfications -> {

                llErrorNotifications.visibility = View.GONE
                validateNet()
            }
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()

        var intent = Intent(this@NotificationActivity, MessagesActivity::class.java)
        setResult(100, intent)
        finish()
    }
}
