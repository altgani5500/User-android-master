package com.partime.user.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.JobTitleAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.JobTitleMessage
import com.partime.user.Responses.JobTitleResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_job_filter.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList

class JobTitleFilterActivity : BaseActivity(), View.OnClickListener {

    private var jobTitleAdapter: JobTitleAdapter? = null
    var jobTitleList = ArrayList<JobTitleMessage>()
    var tempList = ArrayList<JobTitleMessage>()
    var checkedItems = ArrayList<JobTitleMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_filter)
        AppValidator.rotateBackArrow(imgBackJobFilters, this@JobTitleFilterActivity)

        validateNet()

        //search the job titles
        edtSearchJobTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(edit: Editable?) {

                if (edit?.length!! >= 1) {
                    var industrySeached = edit.toString().trim()
                    callSearch(industrySeached)
                } else if (edit.isEmpty()) {
                    var industrySeached = edit.toString().trim()
                    callSearch(industrySeached)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        imgBackJobFilters.setOnClickListener(this)
        txtResetJobFilters.setOnClickListener(this)
        btnApplyJobTitleFilter.setOnClickListener(this)
        btnRetryJobTitleFilter.setOnClickListener(this)

    }

    /*
    Method to validate network
     */

    private fun validateNet() {

        if (AppValidator.isInternetAvailable(this@JobTitleFilterActivity)) {

            getJobTitles()
        } else {

            llErrorJobTitleFilter.visibility = View.VISIBLE
            recyclerJobFilter.visibility = View.GONE
        }
    }

    /*
   Method to get the job titles
    */

    private fun getJobTitles() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@JobTitleFilterActivity)

        val apiService = ApiService.init()
        val call = apiService.getJobFilList(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()))
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<JobTitleResponse> {
            override fun onResponse(
                call: Call<JobTitleResponse>,
                response: retrofit2.Response<JobTitleResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        if (!jobTitleList.isEmpty()) {
                            jobTitleList.clear()
                        }

                        jobTitleList.addAll(response.body()?.message as ArrayList<JobTitleMessage>)
                        tempList.addAll(response.body()?.message as ArrayList<JobTitleMessage>)
                        jobTitleAdapter = JobTitleAdapter(tempList, this@JobTitleFilterActivity)

                        llErrorJobTitleFilter.visibility = View.GONE
                        recyclerJobFilter.visibility = View.VISIBLE
                        recyclerJobFilter!!.layoutManager =
                            LinearLayoutManager(
                                this@JobTitleFilterActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        recyclerJobFilter!!.itemAnimator = DefaultItemAnimator()
                        recyclerJobFilter!!.adapter = jobTitleAdapter

                        //to check the pre selected job titles
                        if (intent.getSerializableExtra(Constants.JOB_TITLE_SELECTED) != null) {

                            var selectedList = java.util.ArrayList<JobTitleMessage>()
                            selectedList.addAll(intent.getSerializableExtra(Constants.JOB_TITLE_SELECTED) as ArrayList<JobTitleMessage>)

                            for (i in 0..tempList.size - 1) {

                                for (ii in 0..selectedList.size - 1) {

                                    ///job id..................

                                    if (tempList.get(i).jobTitle == selectedList.get(ii).jobTitle) {

                                        tempList.get(i).isClicked = true
                                        if (jobTitleAdapter != null) {
                                            jobTitleAdapter!!.notifyDataSetChanged()
                                        }
                                    }

                                }

                            }
                        }


                    } else {

                        Toast.makeText(
                            this@JobTitleFilterActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<JobTitleResponse>, t: Throwable) {

                Toast.makeText(this@JobTitleFilterActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                llErrorJobTitleFilter.visibility = View.VISIBLE
                recyclerJobFilter.visibility = View.GONE

            }

        })


    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackJobFilters -> onBackPressed()

            btnRetryJobTitleFilter -> {

                llErrorJobTitleFilter.visibility = View.GONE

                validateNet()
            }

            btnApplyJobTitleFilter -> {

                var jobtitles = getJobFilters()
                val intent = intent
                intent.putExtra("jobTitleFilters", jobtitles)
                setResult(61, intent)
                finish()

            }
            txtResetJobFilters -> {

                for (i in 0..jobTitleList.size - 1) {

                    jobTitleList.get(i).isClicked = false
                }
                if (jobTitleAdapter != null) {
                    jobTitleAdapter!!.notifyDataSetChanged()
                }
            }

        }
    }

    /*
   Method to get selected job titles
    */

    private fun getJobFilters(): ArrayList<JobTitleMessage> {

        for (i in 0..jobTitleList.size - 1) {
            if (jobTitleList.get(i).isClicked) {
                checkedItems.add(jobTitleList[i])

            }
        }
        return checkedItems

    }

    /*
       Method to search the job titles
        */
    private fun callSearch(title: String) {
        val str: String = title.toLowerCase(Locale.getDefault())
        tempList.clear()
        if (str == "") {
            tempList.addAll(jobTitleList)
        } else {
            for (list in jobTitleList) {
                if (list.jobTitle.toLowerCase(Locale.getDefault()).contains(str)) {
                    tempList.add(list)
                }
            }
        }
        if (jobTitleAdapter != null) {
            jobTitleAdapter!!.notifyDataSetChanged()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
