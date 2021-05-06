package com.partime.user.activities

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioButton
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
import kotlinx.android.synthetic.main.activity_advance_settings.*
import retrofit2.Call
import retrofit2.Callback

class AdvanceSettingsActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    var map = HashMap<String, String>()

    var isCompleteProfile = false
    var isGrades = false
    var isCertificates = false
    var isLocation = false
    var isAge = false
    var isEducationLevel = false
    var isCompanies = false
    var isGender = false
    var isCv = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advance_settings)

        AppValidator.rotateBackArrow(imgBackAdvanceSett, this@AdvanceSettingsActivity)

        //get the pred defined settings of user (if any)
        getUserSettings()

        //button clicks
        switchGrades.setOnCheckedChangeListener(this)
        switchCertificates.setOnCheckedChangeListener(this)
        switchLocation.setOnCheckedChangeListener(this)
        switchAge.setOnCheckedChangeListener(this)
        switchEducation.setOnCheckedChangeListener(this)
        switchCompaniesWorked.setOnCheckedChangeListener(this)
        switchGender.setOnCheckedChangeListener(this)
        switchCv.setOnCheckedChangeListener(this)

        radioBtnAll.setOnCheckedChangeListener(this)
        radioBtnCompanyApplied.setOnCheckedChangeListener(this)

        imgBackAdvanceSett.setOnClickListener(this)

    }
    /*
      Method to get the pre defined settings
       */

    private fun getUserSettings() {

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

                        saveUserSettings(response.body()?.message!!)

                    } else {

                        // Toast.makeText(this@HomeActivity, response.body()?.error_message.toString(), Toast.LENGTH_LONG)

                    }
                }

            }

            override fun onFailure(call: Call<GetSettingsResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)
                Toast.makeText(this@AdvanceSettingsActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
            }

        })

    }
    /*
    Method to chnage the settings of user
     */

    private fun changeSettings(key: String, value: Int, checked: Boolean, switchBtn: Switch, flag: Int) {

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
                            this@AdvanceSettingsActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        )
                        switchBtn.isChecked = !checked
                        setValues(flag, false)
                    }
                }
            }

            override fun onFailure(call: Call<ChangeUserSettingsResponse>, t: Throwable) {

                Toast.makeText(this@AdvanceSettingsActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                switchBtn.isChecked = checked

            }

        })

    }

    private fun changeRadioSettings(key: String, value: Int, checked: Boolean, switchBtn: RadioButton, flag: Int) {

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
                            this@AdvanceSettingsActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        )
                        switchBtn.isChecked = !checked
                        setValues(flag, false)
                    }
                }
            }

            override fun onFailure(call: Call<ChangeUserSettingsResponse>, t: Throwable) {


                Toast.makeText(this@AdvanceSettingsActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                switchBtn.isChecked = checked

            }

        })

    }

    /*
    Method to save the changed settings
     */

    private fun setValues(flag: Int, status: Boolean) {

        if (flag == 1) {
            isCompleteProfile = status
        } else if (flag == 2) {
            isGrades = status
        } else if (flag == 3) {
            isCertificates = status
        } else if (flag == 4) {
            isLocation = status
        } else if (flag == 5) {
            isAge = status
        } else if (flag == 6) {
            isEducationLevel = status
        } else if (flag == 7) {
            isCompanies = status
        } else if (flag == 8) {
            isGender = status
        } else if (flag == 9) {
            isCv = status
        }
    }

    /*
    Method  to set the ui as per the settings
     */

    private fun saveUserSettings(message: GetSettingsMessage) {

        if (!message.grades.isNullOrBlank() || !message.grades.isNullOrEmpty()) {

            switchGrades.isChecked = message.grades.toInt() != 0
        }
        if (!message.certificates.isNullOrBlank() || !message.certificates.isNullOrEmpty()) {
            switchCertificates.isChecked = message.certificates.toInt() != 0

        }
        if (!message.location.isNullOrBlank() || !message.location.isNullOrEmpty()) {

            switchLocation.isChecked = message.location.toInt() != 0

        }
        if (!message.age.isNullOrBlank() || !message.age.isNullOrEmpty()) {

            switchAge.isChecked = message.age.toInt() != 0

        }


        if (!message.education_level.isNullOrBlank() || !message.education_level.isNullOrEmpty()) {

            switchEducation.isChecked = message.education_level.toInt() != 0

        }
        if (!message.working_at.isNullOrBlank() || !message.working_at.isNullOrEmpty()) {

            switchCompaniesWorked.isChecked = message.working_at.toInt() != 0

        }
        if (!message.gender.isNullOrBlank() || !message.gender.isNullOrEmpty()) {

            switchGender.isChecked = message.gender.toInt() != 0

        }
        if (!message.cv.isNullOrBlank() || !message.cv.isNullOrEmpty()) {
            switchCv.isChecked = message.cv.toInt() != 0

        }

        if (!message.work_hours.isNullOrBlank() || !message.work_hours.isNullOrEmpty()) {

            if (message.work_hours.toInt() == 1) {

                radioBtnAll.isChecked = true
                radioBtnCompanyApplied.isChecked = false

            } else {

                radioBtnAll.isChecked = false
                radioBtnCompanyApplied.isChecked = true
            }
        }
        if (!message.work_hours.isNullOrEmpty() || !message.work_hours.isNullOrBlank()) {

            if (message.work_hours.toInt() == 1) {

                radioBtnAll.isChecked = true
                radioBtnCompanyApplied.isChecked = false

            } else {

                radioBtnAll.isChecked = false
                radioBtnCompanyApplied.isChecked = true
            }
        }
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackAdvanceSett -> onBackPressed()

        }
    }

    // listens the check changes in toggle buttons
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        when (buttonView) {

            switchGrades -> {

                if (isChecked && !isGrades) {

                    changeSettings("grades", 1, isChecked, switchGrades, 2)


                } else if (!isChecked && isGrades) {

                    changeSettings("grades", 0, isChecked, switchGrades, 2)

                }

            }

            switchCertificates -> {
                if (isChecked && !isCertificates) {

                    changeSettings("certificates", 1, isChecked, switchCertificates, 3)

                } else if (!isChecked && isCertificates) {
                    changeSettings("certificates", 0, isChecked, switchCertificates, 3)

                }

            }
            switchLocation -> {
                if (isChecked && !isLocation) {

                    changeSettings("location", 1, isChecked, switchLocation, 4)

                } else if (!isChecked && isLocation) {
                    changeSettings("location", 0, isChecked, switchLocation, 4)

                }
            }
            switchAge -> {
                if (isChecked && !isAge) {

                    changeSettings("age", 1, isChecked, switchAge, 5)

                } else if (!isChecked && isAge) {
                    changeSettings("age", 0, isChecked, switchAge, 5)

                }
            }
            switchEducation -> {
                if (isChecked && !isEducationLevel) {

                    changeSettings("education_level", 1, isChecked, switchEducation, 6)

                } else if (!isChecked && isEducationLevel) {
                    changeSettings("education_level", 0, isChecked, switchEducation, 6)
                }
            }
            switchCompaniesWorked -> {
                if (isChecked && !isCompanies) {

                    changeSettings("working_at", 1, isChecked, switchCompaniesWorked, 7)
                } else if (!isChecked && isCompanies) {

                    changeSettings("working_at", 0, isChecked, switchCompaniesWorked, 7)

                }
            }
            switchGender -> {

                if (isChecked && !isGender) {

                    changeSettings("gender", 1, isChecked, switchGender, 8)

                } else if (!isChecked && isGender) {


                    changeSettings("gender", 0, isChecked, switchGender, 8)
                }

            }
            switchCv -> {
                if (isChecked && !isCv) {
                    changeSettings("cv", 1, isChecked, switchCv, 9)
                } else if (!isChecked && isCv) {
                    changeSettings("cv", 0, isChecked, switchCv, 9)

                }
            }
            radioBtnAll -> {

                if (isChecked) {

                    radioBtnCompanyApplied.isChecked = false
                    changeRadioSettings("work_hours", 1, isChecked, radioBtnAll, 10)
                }

            }
            radioBtnCompanyApplied -> {

                if (isChecked) {

                    radioBtnAll.isChecked = false
                    changeRadioSettings("work_hours", 2, isChecked, radioBtnAll, 10)
                }

            }

        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
    }
}
