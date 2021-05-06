package com.partime.user.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.SavedJobAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.SavedJobsListMessage
import com.partime.user.Responses.SavedJobsListResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_saved_jobs.*
import retrofit2.Call
import retrofit2.Callback

class SavedJobsActivity : BaseActivity(), View.OnClickListener {


    var map = HashMap<String, String>()

    private var savedJobListAdapter: SavedJobAdapter? = null
    var savedJobList = ArrayList<SavedJobsListMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_jobs)
      showGifLoading()

        AppValidator.rotateBackArrow(imgBackSavedJobs, this@SavedJobsActivity)
        if (AppValidator.isInternetAvailable(this)) {


            getSavedJobList()

        } else {

            llErrorSavedJob.visibility = View.VISIBLE
            recyclerSavedJob.visibility = View.GONE
            txtNoSavedJob.visibility = View.GONE

        }

        btnRetrySavedJob.setOnClickListener(this)
        imgBackSavedJobs.setOnClickListener(this)

    }

   private fun showGifLoading() {
        val imageView: ImageView = findViewById(R.id.loading)
        Glide.with(this).load(R.drawable.loading).into(imageView)
    }

    /*
    Method to get the saved jobs list
     */

    private fun getSavedJobList() {

//        var progressBar = ProgressBarUtil().showProgressDialog(this)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        val apiService = ApiService.init()
        val call: Call<SavedJobsListResponse> = apiService.getSavedJobDeatils(
            "Bearer $authKey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())
        )

        Log.d("REQUEST", map.toString() + "")
        call.enqueue(object : Callback<SavedJobsListResponse> {
            override fun onResponse(
                call: Call<SavedJobsListResponse>,
                response: retrofit2.Response<SavedJobsListResponse>?
            ) {

                if (response != null) {

                    loading_l_saved.visibility = View.GONE
//                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        //response.body()?.success.toString()

//

                        if (!savedJobList.isEmpty()) {

                            savedJobList.clear()
                        }

                        savedJobList = response.body()?.message as ArrayList<SavedJobsListMessage>
                        if (savedJobList.size == 0) {
                            txtNoSavedJob.visibility = View.VISIBLE
                            recyclerSavedJob.visibility = View.GONE
                            llErrorSavedJob.visibility = View.GONE

                        } else {

                            txtNoSavedJob.visibility = View.GONE
                            llErrorSavedJob.visibility = View.GONE
                            recyclerSavedJob.visibility = View.VISIBLE



                            savedJobListAdapter = SavedJobAdapter(
                                savedJobList,
                                txtNoSavedJob,
                                this@SavedJobsActivity,
                                object : SavedJobAdapter.SavedCardListener {
                                    override fun onSavedCardListener(position: Int, jobId: Int) {

                                        val intent = Intent(this@SavedJobsActivity, JobDescriptionActivity::class.java)
                                        intent.putExtra(Constants.JOB_ID, jobId)
                                        intent.putExtra(Constants.FROM, "saved jobs")
                                        startActivityForResult(intent, 21)
                                    }


                                })
                            recyclerSavedJob!!.layoutManager =
                                LinearLayoutManager(this@SavedJobsActivity, RecyclerView.VERTICAL, false)
                            recyclerSavedJob!!.itemAnimator = DefaultItemAnimator()
                            recyclerSavedJob!!.adapter = savedJobListAdapter
                        }

                    } else {

                        Toast.makeText(
                            this@SavedJobsActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        )

                    }
                }

            }

            override fun onFailure(call: Call<SavedJobsListResponse>, t: Throwable) {
                loading_l_saved.visibility = View.GONE


                llErrorSavedJob.visibility = View.VISIBLE
                recyclerSavedJob.visibility = View.GONE
                txtNoSavedJob.visibility = View.GONE

                Toast.makeText(this@SavedJobsActivity, t.toString(), Toast.LENGTH_LONG).show()

            }

        })

    }

    override fun onClick(v: View?) {

        if (v == imgBackSavedJobs) {
            //setResult(11)
            finish()
        }
        if (v == btnRetryJobList) {

            llErrorSavedJob.visibility = View.GONE

            if (AppValidator.isInternetAvailable(this)) {

                txtNoSavedJob.visibility = View.GONE
                recyclerSavedJob.visibility = View.VISIBLE
                getSavedJobList()

            } else {

                llErrorSavedJob.visibility = View.VISIBLE
                recyclerSavedJob.visibility = View.GONE
                txtNoSavedJob.visibility = View.GONE
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 21 && resultCode == 21) {

            var apply = data!!.getIntExtra(Constants.SJ_APPLY_STATUS, 0)
            var save = data.getIntExtra(Constants.SJ_SAVE_STATUS, 0)
            var jobStatus = data.getStringExtra(Constants.SJ_JOB_STATUS)
            var jobType = data.getStringExtra(Constants.SJ_JOB_TYPE)
            var sjId = data.getIntExtra(Constants.SJ_ID, 0)
            for (i in 0..savedJobList.size - 1) {

                if (savedJobList.get(i).jobId == sjId) {

                    savedJobList.get(i).applyJob = apply
                    savedJobList.get(i).savedJob = save

                    if (jobStatus != null) {
                        savedJobList.get(i).jobStatus = jobStatus
                    }

                    if (jobType != null) {
                        savedJobList.get(i).jobType = jobType

                    }
                }
            }

            savedJobListAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
    }

}

