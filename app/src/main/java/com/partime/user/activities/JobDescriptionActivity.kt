package com.partime.user.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.partime.user.Adapters.FragPagerAdapter
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.ViewPagerDialogAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.ApplyJobResponse
import com.partime.user.Responses.JobDetailsMessage
import com.partime.user.Responses.JobDetailsResponse
import com.partime.user.Responses.SaveJobResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.helpers.buildDeepLink
import com.partime.user.helpers.shortLinkTask
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import com.parttime.user.uicomman.auth.LoginActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_job_description.*
import kotlinx.android.synthetic.main.info_dailog.*
import kotlinx.android.synthetic.main.job_description_section_three.*
import kotlinx.android.synthetic.main.job_description_section_two.*
import kotlinx.android.synthetic.main.job_details_section_one.*
import kotlinx.android.synthetic.main.register_dailog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobDescriptionActivity : BaseActivity(), View.OnClickListener {

    lateinit var adapter: FragPagerAdapter
    lateinit var dailogAdapter: ViewPagerDialogAdapter
    var map = HashMap<String, String>()
    var userId = 0
    var jobId = 0
    var jobLat = 0.0
    var jobLong = 0.0
    var applyStatus = 0
    var saveStatus = 0
    var jobStatus: String? = null
    var jobsType: String? = null
    var detailsList: JobDetailsMessage? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_description)
        showGifLoading()
        AppValidator.rotateBackArrow(imgBackJd, this@JobDescriptionActivity)


        if (appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())
        ) {

            userId = appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString()).toInt()
        } else if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

            userId = 0
        }


        if (AppValidator.isInternetAvailable(this)) {

            getJobDescription()

        } else {

            linearLayoutJobDescription.visibility = View.GONE
            llErrorJobDetails.visibility = View.VISIBLE
            loading_job.visibility = View.GONE

        }

        imgMapJd.setOnClickListener(this)
        btnRetryJobDetails.setOnClickListener(this)
        imgBackJd.setOnClickListener(this)
        btnApplyJd.setOnClickListener(this)
        btnUnapplyJd.setOnClickListener(this)
        imgUnSaveJd.setOnClickListener(this)
        imgSaveJd.setOnClickListener(this)
        imgInfoJd.setOnClickListener(this)
        imgChatJd.setOnClickListener(this)
        imgShareJD.setOnClickListener(this)
    }

    /*
    Metho to geth the job description
     */

    private fun getJobDescription() {

      //  var progressBar = ProgressBarUtil().showProgressDialog(this@JobDescriptionActivity)
       // scrolcontain.visibility = View.GONE

        var jobid = intent.getIntExtra(Constants.JOB_ID, 0).toString()

        map.put("userId", userId.toString())
        map.put("jobId", jobid)

        val apiService = ApiService.init()
        val call: Call<JobDetailsResponse> =
            apiService.getJobDeatils(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()), map)

        Log.d("REQUEST", map.toString() + "")
        call.enqueue(object : Callback<JobDetailsResponse> {
            override fun onResponse(
                call: Call<JobDetailsResponse>,
                response: retrofit2.Response<JobDetailsResponse>?
            ) {

                if (response != null) {
                   // ProgressBarUtil().hideProgressDialog(progressBar)

                    loading_job.visibility = View.GONE
                    scrolcontain.visibility = View.VISIBLE


                    if (response.body()?.code == 200) {

                        linearLayoutJobDescription.visibility = View.VISIBLE
                        Toast.makeText(
                            this@JobDescriptionActivity,
                            response.body()?.success.toString(),
                            Toast.LENGTH_LONG
                        )

                        jobId = response.body()?.message?.jobId!!.toInt()
                        jobLat = response.body()?.message?.jobLat!!.toDouble()
                        jobLong = response.body()?.message?.jobLong!!.toDouble()
                        setDetails(response)


                    } else {

                        Toast.makeText(
                            this@JobDescriptionActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        )

                    }

                }

            }

            override fun onFailure(call: Call<JobDetailsResponse>, t: Throwable) {
               // ProgressBarUtil().hideProgressDialog(progressBar)
                loading_job.visibility = View.GONE
                scrolcontain.visibility = View.GONE
                linearLayoutJobDescription.visibility = View.GONE
                llErrorJobDetails.visibility = View.VISIBLE
                Toast.makeText(this@JobDescriptionActivity, t.toString(), Toast.LENGTH_LONG)

            }

        })

    }

    /*
    Method to show the job details
     */

    private fun setDetails(responses: Response<JobDetailsResponse>) {

        detailsList = responses.body()?.message!!

        //url of images .............
        adapter = FragPagerAdapter(this@JobDescriptionActivity, detailsList!!.jobImages)
        viewPager.adapter = adapter
        tabIndicatorDots.setupWithViewPager(viewPager)

        if (detailsList!!.applyJob == 0) {
            btnApplyJd.visibility = View.VISIBLE
            btnUnapplyJd.visibility = View.GONE
        } else if (detailsList!!.applyJob == 1) {

            btnApplyJd.visibility = View.GONE
            btnUnapplyJd.visibility = View.VISIBLE
        }
        if (detailsList!!.savedJob == 0) {
            imgSaveJd.visibility = View.VISIBLE
            imgUnSaveJd.visibility = View.GONE
        } else {
            imgSaveJd.visibility = View.GONE
            imgUnSaveJd.visibility = View.VISIBLE
        }
        if (detailsList!!.jobType.equals("Credited Jobs")) {

            imgStatusJd.setImageResource(R.drawable.small_blue_location)
        } else {

            imgStatusJd.setImageResource(R.drawable.small_black_location)

        }
        if (detailsList!!.enrolledParttimeWorker != 0) {

            txtEnrolledWorkersJd.visibility = View.VISIBLE
            txtJdEnrolledPartTImeWorkers.visibility = View.VISIBLE
            txtJdEnrolledPartTImeWorkers.text = detailsList!!.enrolledParttimeWorker.toString()

        } else {

            txtEnrolledWorkersJd.visibility = View.GONE
            txtJdEnrolledPartTImeWorkers.visibility = View.GONE
            txtJdEnrolledPartTImeWorkers.text = detailsList!!.enrolledParttimeWorker.toString()

        }


        if (detailsList!!.hoursRate.equals("None Of These")) {
            if (detailsList!!.hoursePer != null || !detailsList!!.hoursePer.equals("")) {
                txtJdHourlyRate.text = detailsList!!.hoursePer
                llJdHourlyRate.visibility = View.VISIBLE
                txtHourlyRateJd.visibility = View.VISIBLE
                txtJdHourlyRate.visibility = View.VISIBLE
            } else {

                txtHourlyRateJd.visibility = View.GONE
                txtJdHourlyRate.visibility = View.GONE
                llJdHourlyRate.visibility = View.GONE
            }

        } else {
            txtJdHourlyRate.text = detailsList!!.hoursRate
            llJdHourlyRate.visibility = View.VISIBLE
            txtHourlyRateJd.visibility = View.VISIBLE
            txtJdHourlyRate.visibility = View.VISIBLE

        }

        applyStatus = detailsList!!.applyJob
        saveStatus = detailsList!!.savedJob
        txtJdDate.text = detailsList!!.createdDate

        txtJdTitle.text = detailsList!!.jobTitle
        txtJdPartTimeType.text = detailsList!!.parttimeType
        txtJdLocation.text = detailsList!!.jobLocation
        txtJdBranchName.text = detailsList!!.branchName

        txtJdDescription.text = detailsList!!.jobDescription

        txtCurrency.text = detailsList!!.currency
        // txtJdNoOfApplicants.text=detailsList!!.noOfApplicants

        txtJdWorkingHours.text = detailsList!!.workingHours
        txtJdTotalHourPerWeek.text = detailsList!!.totalHoursPerWeek
        txtJdWorkExperience.text = detailsList!!.workExperience

        txtJdRequirements.text = detailsList!!.requirements
        txtJdBenefits.text = detailsList!!.benifits
        txtJdWorkingGuidelines.text = detailsList!!.workGuidence

        txtJdenterpriseName.text = detailsList!!.enterpriseName
        txtJdEnterpriseCategory.text = detailsList!!.industryType
        txtJdGeneralInfo.text = detailsList!!.generealInfo
        txtJdCredited.text = detailsList!!.jobType

        Picasso.get().load(detailsList!!.enterpriseLogo).placeholder(R.drawable.image_placeholder)
            .into(imgJdEnterpriseLogo)

    }

    override fun onClick(v: View?) {
        if (v == btnRetryJobDetails) {

            llErrorJobDetails.visibility = View.GONE

            if (AppValidator.isInternetAvailable(this)) {

                linearLayoutJobDescription.visibility = View.VISIBLE
                getJobDescription()

            } else {

                linearLayoutJobDescription.visibility = View.GONE
                llErrorJobDetails.visibility = View.VISIBLE

            }

        }
        if (v == imgBackJd) {

            backPress()
        }
        if (v == btnApplyJd) {


            if (AppValidator.isInternetAvailable(this)) {

                if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                    registerDialog()

                } else {
                    applyForJob(jobId, 1)

                }
            } else {

                Toast.makeText(this@JobDescriptionActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
            }


        }
        if (v == btnUnapplyJd) {

            Toast.makeText(this@JobDescriptionActivity, R.string.can_not_apply, Toast.LENGTH_LONG).show()

          /*  //to restrict  unapplying jobs
            if (detailsList!!.jobAppliedStatus.equals("Accepted")) {
                Toast.makeText(this@JobDescriptionActivity, R.string.can_not_apply, Toast.LENGTH_LONG).show()
            } else {
                if (AppValidator.isInternetAvailable(this)) {

                    if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                        registerDialog()

                    } else {
                        applyForJob(jobId, 0)

                    }
                } else {

                    Toast.makeText(this@JobDescriptionActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                }
            }*/
        }
        if (v == imgSaveJd) {
            if (AppValidator.isInternetAvailable(this)) {

                if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                    registerDialog()

                } else if (appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                    saveCurrentJob(
                        jobId,
                        1,
                        appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
                    )


                }
            } else {

                Toast.makeText(this@JobDescriptionActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
            }

        }
        if (v == imgUnSaveJd) {
            if (AppValidator.isInternetAvailable(this)) {

                saveCurrentJob(jobId, 0, appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString()))
            } else {

                Toast.makeText(this@JobDescriptionActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
            }

        }
        if (v == imgMapJd) {


            val strUri = "http://maps.google.com/maps?q=loc:" + jobLat + "," + jobLong + " (" + R.string.map_lable + ")"
            val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri))

            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")

            startActivity(intent)
        }

        if (v == imgInfoJd) {

            infoDialog(detailsList!!.jobType)
        }
        if (v == imgChatJd) {

            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else {
                var intent = Intent(this@JobDescriptionActivity, ChatActivity::class.java)
                intent.putExtra(Constants.RECIEVER_ID, detailsList!!.enterpriseId)
                intent.putExtra(Constants.SENDER_PROFILE_PICTURE, detailsList!!.enterpriseLogo)
                intent.putExtra(Constants.SENDER_NAME, detailsList!!.enterpriseName)
                startActivity(intent)
            }
        }
        if(v==imgShareJD){

            this.shortLinkTask(resources.getString(R.string.share_text)+"  " , buildDeepLink(jobId.toString()))
            buildDeepLink(jobId.toString())
        }

    }

    /*
    Method to apply for job
     */

    private fun applyForJob(jobId: Int, jobType: Int) {

        var progressBar = ProgressBarUtil().showProgressDialog(this)

        var authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString(), " ")


        map.put("jobId", jobId.toString())
        map.put("type", jobType.toString())

        val apiService = ApiService.init()
        val call: Call<ApplyJobResponse> = apiService.applyJob(
            "Bearer $authkey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()),
            map
        )
        Log.d("REQUEST", map.toString() + "")
        call.enqueue(object : Callback<ApplyJobResponse> {
            override fun onResponse(call: Call<ApplyJobResponse>, response: retrofit2.Response<ApplyJobResponse>?) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        if (jobType == 1) {

                            Toast.makeText(
                                this@JobDescriptionActivity,
                                response.body()?.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            applyStatus = 1
                            btnApplyJd.visibility = View.GONE
                            btnUnapplyJd.visibility = View.VISIBLE

                        } else if (jobType == 0) {

                            applyStatus = 0
                            btnApplyJd.visibility = View.VISIBLE
                            btnUnapplyJd.visibility = View.GONE

                        }

                    } else {

                        Toast.makeText(
                            this@JobDescriptionActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

            }

            override fun onFailure(call: Call<ApplyJobResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@JobDescriptionActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    /*
    Method to save the job
     */

    private fun saveCurrentJob(jobId: Int, jobType: Int, authKey: String) {

        var progressBar = ProgressBarUtil().showProgressDialog(this@JobDescriptionActivity)

        map.put("jobId", jobId.toString())
        map.put("type", jobType.toString())

        val apiService = ApiService.init()
        val call: Call<SaveJobResponse> = apiService.saveJobs(
            "Bearer $authKey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()),
            map
        )
        Log.d("REQUEST", map.toString() + "")
        call.enqueue(object : Callback<SaveJobResponse> {
            override fun onResponse(call: Call<SaveJobResponse>, response: retrofit2.Response<SaveJobResponse>?) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        if (jobType == 1) {

                            saveStatus = 1
                            imgSaveJd.visibility = View.GONE
                            imgUnSaveJd.visibility = View.VISIBLE
                        } else {

                            saveStatus = 0
                            imgSaveJd.visibility = View.VISIBLE
                            imgUnSaveJd.visibility = View.GONE
                        }

                        jobStatus = response.body()?.jobStatus
                        jobsType = response.body()?.jobType

                    } else {

                        Toast.makeText(
                            this@JobDescriptionActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        )
                    }

                }

            }

            override fun onFailure(call: Call<SaveJobResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@JobDescriptionActivity, t.toString(), Toast.LENGTH_LONG).show()

            }

        })

    }

    /*
    Method to show the register dialog
     */

    private fun registerDialog() {

        var registerDailog = Dialog(this)

        registerDailog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        registerDailog.setContentView(R.layout.register_dailog)
        registerDailog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        registerDailog.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        registerDailog.btnRegisterDailog.setOnClickListener {

            val intent = Intent(this@JobDescriptionActivity, RegisterActivity::class.java)
            startActivity(intent)
            registerDailog.dismiss()

        }
        registerDailog.txtLoginHereRegisterDailog.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            registerDailog.dismiss()

        }
        registerDailog.show()
    }


    override fun onBackPressed() {
        // super.onBackPressed()
        backPress()
    }

    /*
   Method to braodcast data on back press
    */
    private fun backPress() {

        if(!intent.getStringExtra(Constants.FROM).isNullOrEmpty()||!intent.getStringExtra(Constants.FROM).isNullOrBlank()) {

            if (intent.getStringExtra(Constants.FROM).contentEquals("saved jobs")) {

                var i = Intent(this@JobDescriptionActivity, SavedJobsActivity::class.java)
                i.putExtra(Constants.SJ_APPLY_STATUS, applyStatus)
                i.putExtra(Constants.SJ_SAVE_STATUS, saveStatus)
                i.putExtra(Constants.SJ_ID, jobId)

                if (jobStatus.equals("Saved")) {

                    i.putExtra(Constants.SJ_JOB_STATUS, jobStatus)
                } else {

                    if (activity.appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {
                        i.putExtra(Constants.SJ_JOB_STATUS, "Viewed")
                    }
                }
                if (jobsType != null) {

                    i.putExtra(Constants.SJ_JOB_TYPE, jobsType)
                }

                setResult(21, i)
            }
            if (intent.getStringExtra(Constants.FROM).contentEquals("home") || intent.getStringExtra(Constants.FROM).contentEquals(
                    "map"
                )
            ) {

                val intentHome = Intent(Constants.BROADCAST_HOME)
                intentHome.putExtra(Constants.HOME_APPLY_STATUS, applyStatus)
                intentHome.putExtra(Constants.HOME_SAVE_STATUS, saveStatus)
                intentHome.putExtra(Constants.HOME_ID, jobId)
                if (jobStatus.equals("Saved")) {

                    intentHome.putExtra(Constants.HOME_JOB_STATUS, jobStatus)
                } else {

                    if (activity.appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {
                        intentHome.putExtra(Constants.SJ_JOB_STATUS, "Viewed")
                    }
                }
                if (jobsType != null) {

                    intentHome.putExtra(Constants.HOME_JOB_TYPE, jobsType)
                }
                LocalBroadcastManager.getInstance(this@JobDescriptionActivity).sendBroadcast(intentHome)

            } else if (intent.getStringExtra(Constants.FROM).contentEquals("splash")) {

                var intent = Intent(this@JobDescriptionActivity, HomeActivity::class.java)
                startActivity(intent)

            }
        }
        finish()
    }

    /*
   Method to show the info dialog
    */

    private fun infoDialog(jobType: String) {

        var infoDialog = Dialog(this@JobDescriptionActivity)

        infoDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        infoDialog.setContentView(R.layout.info_dailog)
        infoDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        infoDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        if (jobType == "Credited Jobs") {

            infoDialog.txtInfoDialog.setText(R.string.credited_text)

        } else {

            infoDialog.txtInfoDialog.setText(R.string.non_credited_text)
        }

        infoDialog.btnOkInfoDialog.setOnClickListener {

            infoDialog.dismiss()
        }

        infoDialog.show()
    }
    ///new functions
    fun showGifLoading() {
        val imageView: ImageView = findViewById(R.id.loading)
        Glide.with(this).load(R.drawable.loading).into(imageView)
    }
}
