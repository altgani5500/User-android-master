package com.partime.user.activities

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import com.gems.app.utilities.AppValidator
import com.partime.user.R
import com.partime.user.Responses.ChangeUserSettingsResponse
import com.partime.user.Responses.GetSettingsMessage
import com.partime.user.Responses.GetSettingsResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_notification_settings.*
import retrofit2.Call
import retrofit2.Callback

class NotificationSettingsActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    var isMessage = false
    var isJobUpdate = false
    var isTask = false
    var isScheduleUpdate = false

    var map = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        AppValidator.rotateBackArrow(imgBackNotificationSett, this@NotificationSettingsActivity)

        getNotificationSettings()

        switchMessageNotify.setOnCheckedChangeListener(this)
        switchJobUpdateNotify.setOnCheckedChangeListener(this)
        switchTaskNotify.setOnCheckedChangeListener(this)
        switchScheduleUpdateNotify.setOnCheckedChangeListener(this)

        imgBackNotificationSett.setOnClickListener(this)
    }

    /*
    Method to get the notification settings
     */
    private fun getNotificationSettings() {

        var progressBar = ProgressBarUtil().showProgressDialog(this)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())


        val apiService = ApiService.init()
        val call: Call<GetSettingsResponse> = apiService.getUserSetting("Bearer $authKey")

        call.enqueue(object : Callback<GetSettingsResponse> {
            override fun onResponse(
                call: Call<GetSettingsResponse>,
                response: retrofit2.Response<GetSettingsResponse>?
            ) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        saveNotificationSettings(response.body()?.message!!)

                    } else {

                        // Toast.makeText(this@HomeActivity, response.body()?.error_message.toString(), Toast.LENGTH_LONG)

                    }
                }

            }

            override fun onFailure(call: Call<GetSettingsResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)
                Toast.makeText(this@NotificationSettingsActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
            }

        })
    }

    /*
    Method to save notification settings
     */
    private fun saveNotificationSettings(message: GetSettingsMessage) {

        if (!message.message_notification.isNullOrBlank() || !message.message_notification.isNullOrEmpty()) {

            switchMessageNotify.isChecked = message.message_notification.toInt() != 0

        }
        if (!message.job_update_notification.isNullOrBlank() || !message.job_update_notification.isNullOrEmpty()) {

            switchJobUpdateNotify.isChecked = message.job_update_notification.toInt() != 0

        }
        if (!message.task_notification.isNullOrBlank() || !message.task_notification.isNullOrEmpty()) {

            switchTaskNotify.isChecked = message.task_notification.toInt() != 0

        }
        if (!message.schedule_notification.isNullOrBlank() || !message.schedule_notification.isNullOrEmpty()) {

            switchScheduleUpdateNotify.isChecked = message.schedule_notification.toInt() != 0

        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        when (buttonView) {

            switchMessageNotify -> {

                if (isChecked && !isMessage) {

                    changeSettings("message_notification", 1, isChecked, switchMessageNotify, 1)


                } else if (!isChecked && isMessage) {

                    changeSettings("message_notification", 0, isChecked, switchMessageNotify, 1)

                }
            }
            switchJobUpdateNotify -> {

                if (isChecked && !isJobUpdate) {

                    changeSettings("job_update_notification", 1, isChecked, switchJobUpdateNotify, 2)


                } else if (!isChecked && isJobUpdate) {

                    changeSettings("job_update_notification", 0, isChecked, switchJobUpdateNotify, 2)

                }
            }
            switchTaskNotify -> {

                if (isChecked && !isTask) {

                    changeSettings("task_notification", 1, isChecked, switchTaskNotify, 3)


                } else if (!isChecked && isTask) {

                    changeSettings("task_notification", 0, isChecked, switchTaskNotify, 3)

                }
            }
            switchScheduleUpdateNotify -> {

                if (isChecked && !isScheduleUpdate) {

                    changeSettings("schedule_notification", 1, isChecked, switchScheduleUpdateNotify, 4)


                } else if (!isChecked && isScheduleUpdate) {

                    changeSettings("schedule_notification", 0, isChecked, switchScheduleUpdateNotify, 4)

                }
            }
        }
    }

    private fun changeSettings(key: String, value: Int, checked: Boolean, switchBtn: Switch, flag: Int) {

        var progressBar = ProgressBarUtil().showProgressDialog(this)

        map.put("key", key)
        map.put("value", value.toString())

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        val apiService = ApiService.init()
        val call = apiService.changeUserSetting("Bearer $authKey", map)
        call.enqueue(object : Callback<ChangeUserSettingsResponse> {
            override fun onResponse(
                call: Call<ChangeUserSettingsResponse>,
                response: retrofit2.Response<ChangeUserSettingsResponse>?
            ) {

                ProgressBarUtil().hideProgressDialog(progressBar)

                if (response != null) {

                    if (response.body()?.code == 200) {

                        if (checked) {
                            switchBtn.isChecked = checked
                            setValues(flag, true)
                        } else {

                            switchBtn.isChecked = checked
                            setValues(flag, false)
                        }


                    } else {

                        Toast.makeText(
                            this@NotificationSettingsActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        )
                        switchBtn.isChecked = !checked
                        setValues(flag, false)
                    }
                }
            }

            override fun onFailure(call: Call<ChangeUserSettingsResponse>, t: Throwable) {

                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@NotificationSettingsActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                switchBtn.isChecked = checked

            }

        })

    }

    private fun setValues(flag: Int, status: Boolean) {

        if (flag == 1) {
            isMessage = status
        } else if (flag == 2) {
            isJobUpdate = status
        } else if (flag == 3) {
            isTask = status
        } else if (flag == 4) {
            isScheduleUpdate = status
        }

    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackNotificationSett -> onBackPressed()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
    }
}
