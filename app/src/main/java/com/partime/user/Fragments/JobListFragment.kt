package com.partime.user.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Adapters.JobAdapter
import com.partime.user.Constants.Constants
import com.partime.user.Responses.JobDetailsMessage
import com.partime.user.Responses.JobListMessage
import com.partime.user.activities.HomeActivity
import com.partime.user.activities.JobDescriptionActivity
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.partime.user.uicomman.BaseFragment
import kotlinx.android.synthetic.main.fragment_job_list.*

class JobListFragment : BaseFragment() {

    var map = HashMap<String, String>()

    private var jobListAdapter: JobAdapter? = null
    var jobList = ArrayList<JobListMessage>()

    var jobLdetails = ArrayList<JobDetailsMessage>()

    val appPrefence = AppPrefence.INSTANCE


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(com.partime.user.R.layout.fragment_job_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        appPrefence.initAppPreferences(context)

        swipe_refresh_layout.setOnRefreshListener {

            BaseActivity.frgament_job_count = 2
            (context as HomeActivity).getJobList()


        }

        jobList.clear()
        jobList.addAll(arguments!!.getSerializable(Constants.JOB_LIST) as ArrayList<JobListMessage>)

        jobListAdapter = JobAdapter(jobList,jobLdetails, context, object : JobAdapter.JobCardListener {
            override fun onJobCardListener(position: Int, jobId: Int) {
                val intent = Intent(context, JobDescriptionActivity::class.java)
                intent.putExtra(Constants.JOB_ID, jobId)
                intent.putExtra(Constants.FROM, "home")
                context!!.startActivity(intent)
            }

        }, object : JobAdapter.OnSavedListener {
            override fun onSaved(savedStatus: String, jobType: String) {

            }


        })
        recyclerJobList!!.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerJobList!!.itemAnimator = DefaultItemAnimator()

        recyclerJobList!!.adapter = jobListAdapter


    }

    /* broadcast from job description */
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.hasExtra(Constants.HOME_ID)) {
                var applyStatus = intent.getIntExtra(Constants.HOME_APPLY_STATUS, 0)
                var saveStatus = intent.getIntExtra(Constants.HOME_SAVE_STATUS, 0)
                var jobId = intent.getIntExtra(Constants.HOME_ID, 0)
                var jobType = intent.getStringExtra(Constants.HOME_JOB_TYPE)
                var jobStatus = intent.getStringExtra(Constants.HOME_JOB_STATUS)


                if (jobStatus != null || jobType != null) {
                    cardUpdate(applyStatus, saveStatus, jobId, jobStatus, jobType)
                    Log.d("ReceiverData", "1")
                    jobListAdapter!!.notifyDataSetChanged()
                } else {
                    cardUpdate(applyStatus, saveStatus, jobId, null, null)
                    Log.d("ReceiverData", "1")
                    jobListAdapter!!.notifyDataSetChanged()
                }
            }
            jobListAdapter!!.notifyDataSetChanged()
        }
    }

    private val mSavedJobReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.hasExtra(Constants.SAVED_ID)) {

                var jobId = intent.getIntExtra(Constants.SAVED_ID, 0)

                val applyStatus = intent.getIntExtra(Constants.SAVED_APPLY_STATUS, 0)
                val saveStatus = intent.getIntExtra(Constants.SAVED_SAVE_STATUS, 0)
                val jobStatus = intent.getStringExtra(Constants.SAVED_JOB_STATUS)

                val jobType = intent.getStringExtra(Constants.SAVED_JOB_TYPE)

                if (jobStatus != null || jobType != null) {
                    cardUpdate(applyStatus, saveStatus, jobId, jobStatus, jobType)
                    Log.d("ReceiverData", "0")
                    jobListAdapter!!.notifyDataSetChanged()
                } else {
                    cardUpdate(applyStatus, saveStatus, jobId, null, null)
                    Log.d("ReceiverData", "0")
                    jobListAdapter!!.notifyDataSetChanged()
                }
            }
            jobListAdapter!!.notifyDataSetChanged()
        }
    }

    private fun cardUpdate(applyStatus: Int, saveStatus: Int, jobId: Int, jobStatus: String?, jobType: String?) {

        for (i in 0..jobList.size - 1) {

            if (jobList.get(i).jobId == jobId) {

                if (applyStatus != 5) {
                    jobList.get(i).applyJob = applyStatus

                }
                if (saveStatus != 5) {
                    jobList.get(i).savedJob = saveStatus

                }
                if (jobStatus != null) {

                    jobList.get(i).jobStatus = jobStatus
                }
                if (jobType != null) {

                    jobList.get(i).jobType = jobType

                }

            }
        }


    }

    private fun jobListReceiver() {
        try {
            LocalBroadcastManager.getInstance(context!!)
                .registerReceiver(mMessageReceiver, IntentFilter(Constants.BROADCAST_HOME))
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }
    }

    private fun jobListDeReceiver() {
        try {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(mMessageReceiver)
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }
    }

    private fun savedJobMapReceiver() {
        try {
            LocalBroadcastManager.getInstance(context!!)
                .registerReceiver(mSavedJobReceiver, IntentFilter(Constants.BRODCAST_SAVED_JOB))
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }
    }

    private fun savedJobMapDeReceiver() {
        try {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(mMessageReceiver)
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        jobListReceiver()
        savedJobMapReceiver()
        if (jobListAdapter != null) {
            jobListAdapter!!.notifyDataSetChanged()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        jobListDeReceiver()
        savedJobMapDeReceiver()
    }

}


