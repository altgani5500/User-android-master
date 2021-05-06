package com.partime.user.Adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.ApplyJobResponse
import com.partime.user.Responses.SaveJobResponse
import com.partime.user.Responses.SavedJobsListMessage
import com.partime.user.activities.RegisterActivity
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.helpers.buildDeepLink
import com.partime.user.helpers.shortLinkTask
import com.partime.user.prefences.AppPrefence
import com.parttime.com.apiclients.ApiService
import com.parttime.user.uicomman.auth.LoginActivity
import kotlinx.android.synthetic.main.info_dailog.*
import kotlinx.android.synthetic.main.register_dailog.*
import retrofit2.Call
import retrofit2.Callback

class SavedJobAdapter(
    private val savedJobtList: MutableList<SavedJobsListMessage>,
    var txtNoJobs: TextView,
    internal var context: Context?,
    var cardListner: SavedCardListener
) :
    RecyclerView.Adapter<SavedJobAdapter.MyViewHolder>() {
    var map = HashMap<String, String>()
    val appPrefence = AppPrefence.INSTANCE

    var applyStatus = 5
    var saveStatus = 1
    var jobStatus: String? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtJobDesignation: TextView = view.findViewById(R.id.txtSavedJobListDesignation)
        var txtCompanyname: TextView = view.findViewById(R.id.txtSavedJobListCompanyName)
        var txtLocation: TextView = view.findViewById(R.id.txtSavedJobListLocation)
        var txtSalary: TextView = view.findViewById(R.id.txtSavedJobListSalary)
        var txtServices: TextView = view.findViewById(R.id.txtSavedJobListService)
        var txtStatus: TextView = view.findViewById(R.id.txtSavedJobListStatus)
        var jobListLayout: LinearLayout = view.findViewById(R.id.linearLayoutSavedJobList)
        var btnApply: Button = view.findViewById(R.id.btnApplySavedJobList)
        var btnUnapply: Button = view.findViewById(R.id.btnUnapplySavedJobList)
        var unsaveJob: Button = view.findViewById(R.id.imgSavedJobUnSaveJob)
        var map: ImageView = view.findViewById(R.id.imgMapSavedJob)
        var imgJobStatus: ImageView = view.findViewById(R.id.imgSavedJobStatus)
        var jobInfo: ImageView = view.findViewById(R.id.imgSavedJobInfo)
        var viewedLayout: LinearLayout = view.findViewById(R.id.llSavedJobViewedStatus)
        var imgViewed: ImageView = view.findViewById(R.id.imgSavedJobViewedStatus)
        var txtViewedStatus: TextView = view.findViewById(R.id.txtSavedJobListViewedStatus)
        var imgShare:ImageView=view.findViewById(R.id.imgSavedShareJob)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(com.partime.user.R.layout.saved_job_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        appPrefence.initAppPreferences(context)

        var authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())


        val jobs = savedJobtList[position]
        viewHolder.txtJobDesignation.text = jobs.jobTitle
        viewHolder.txtCompanyname.text = jobs.enterpriseName
        viewHolder.txtLocation.text = jobs.jobLocation
        viewHolder.txtServices.text = jobs.industryType
        viewHolder.txtSalary.text = jobs.amountPaid

        applyStatus = jobs.applyJob
        saveStatus = jobs.savedJob

        if (jobs.applyJob == 0) {

            viewHolder.btnApply.visibility = View.VISIBLE
            viewHolder.btnUnapply.visibility = View.GONE
        } else if (jobs.applyJob == 1) {

            viewHolder.btnApply.visibility = View.GONE
            viewHolder.btnUnapply.visibility = View.VISIBLE
        }


        if (jobs.jobType.equals(context!!.resources.getString(R.string.credited_jobs))) {

            viewHolder.imgJobStatus.setImageResource(R.drawable.small_blue_location)
            viewHolder.txtStatus.text = jobs.jobType


        } else {

            viewHolder.imgJobStatus.setImageResource(R.drawable.small_black_location)
            viewHolder.txtStatus.text = jobs.jobType

        }

        if (jobs.jobStatus.equals(context!!.resources.getString(R.string.viewed))) {

            viewHolder.viewedLayout.visibility = View.VISIBLE
            viewHolder.txtViewedStatus.text = jobs.jobStatus
            viewHolder.imgViewed.setImageResource(R.drawable.small_gray_location)
        } else {

            viewHolder.viewedLayout.visibility = View.GONE

        }

        viewHolder.jobListLayout.setOnClickListener {

            cardListner.onSavedCardListener(position, jobs.jobId)

        }

        viewHolder.unsaveJob.setOnClickListener {

            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                val intent = Intent(context, LoginActivity::class.java)
                context!!.startActivity(intent)


            } else if (appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                saveCurrentJob(
                    jobs.jobId, 0, appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString()),
                    position
                )

            }

        }

        viewHolder.btnApply.setOnClickListener {

            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else {
                applyForJob(jobs.jobId, 1, authkey, viewHolder.btnUnapply, viewHolder.btnApply, position)

            }

        }
        viewHolder.btnUnapply.setOnClickListener {

            Toast.makeText(context, R.string.can_not_apply, Toast.LENGTH_LONG).show()

           /* if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else {

                if (!jobs.jobAppliedStatus.equals(context!!.resources.getString(R.string.accepted))) {

                    applyForJob(jobs.jobId, 0, authkey, viewHolder.btnUnapply, viewHolder.btnApply, position)
                } else {
                    Toast.makeText(context, R.string.can_not_apply, Toast.LENGTH_LONG).show()
                }
            }*/
        }
        viewHolder.map.setOnClickListener {


            val strUri =
                "http://maps.google.com/maps?q=loc:" + jobs.jobLat + "," + jobs.jobLong + " (" + R.string.map_lable + ")"
            val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri))

            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")

            context?.startActivity(intent)
        }
        viewHolder.jobInfo.setOnClickListener {

            infoDialog(jobs.jobType)
        }

        viewHolder.imgShare.setOnClickListener {

            context!!.shortLinkTask(context!!.getString(R.string.share_text)+"  ", buildDeepLink(jobs.jobId.toString()))
            buildDeepLink(jobs.jobId.toString())
        }
    }

    private fun saveCurrentJob(jobId: Int, jobType: Int, authKey: String, position: Int) {

        var progressBar = ProgressBarUtil().showProgressDialog(context!!)

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

                        Toast.makeText(context, context!!.getString(R.string.processdone), Toast.LENGTH_LONG).show()

                        //remove item.....
                        saveStatus = 0

                        savedJobtList.get(position).jobStatus = response.body()?.jobStatus!!
                        jobStatus = response.body()?.jobStatus
                        homeBroadCast(jobId, position)

//                        if (position < 1) {
//
//                            txtNoJobs.visibility = View.VISIBLE
//
//                        }
                        savedJobtList.removeAt(position)
                        notifyDataSetChanged()

                    } else {

                        Toast.makeText(context, response.body()?.error_message.toString(), Toast.LENGTH_SHORT)
                    }

                }

            }

            override fun onFailure(call: Call<SaveJobResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    private fun registerDialog() {

        var registerDailog = Dialog(context)

        registerDailog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        registerDailog.setContentView(R.layout.register_dailog)
        registerDailog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        registerDailog.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        registerDailog.btnRegisterDailog.setOnClickListener {

            val intent = Intent(context, RegisterActivity::class.java)
            context!!.startActivity(intent)
            registerDailog.dismiss()

        }
        registerDailog.txtLoginHereRegisterDailog.setOnClickListener {

            val intent = Intent(context, LoginActivity::class.java)
            context!!.startActivity(intent)
            registerDailog.dismiss()

        }
        registerDailog.show()
    }

    private fun applyForJob(
        jobId: Int,
        jobType: Int,
        authkey: String,
        btnUnapply: Button,
        btnApply: Button,
        position: Int
    ) {

        var progressBar = ProgressBarUtil().showProgressDialog(context!!)

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

                            Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_LONG).show()

                            applyStatus = 1
                            savedJobtList[position].applyJob = 1
                            btnApply.visibility = View.GONE
                            btnUnapply.visibility = View.VISIBLE


                        } else if (jobType == 0) {

                            applyStatus = 0
                            savedJobtList[position].applyJob = 0
                            btnApply.visibility = View.VISIBLE
                            btnUnapply.visibility = View.GONE

                        }

                        homeBroadCast(jobId, position)
                        notifyDataSetChanged()

                    } else {

                        Toast.makeText(context, response.body()?.error_message.toString(), Toast.LENGTH_LONG).show()
                    }

                }

            }

            override fun onFailure(call: Call<ApplyJobResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    private fun infoDialog(jobType: String) {

        var infoDialog = Dialog(context)

        infoDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        infoDialog.setContentView(R.layout.info_dailog)
        infoDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        infoDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        if (jobType == context!!.resources.getString(R.string.credited_jobs)) {

            infoDialog.txtInfoDialog.setText(R.string.credited_text)

        } else {

            infoDialog.txtInfoDialog.setText(R.string.non_credited_text)
        }

        infoDialog.btnOkInfoDialog.setOnClickListener {

            infoDialog.dismiss()
        }

        infoDialog.show()
    }


    override fun getItemCount(): Int {
        return savedJobtList.size
    }

    interface SavedCardListener {
        fun onSavedCardListener(position: Int, jobId: Int)
    }

    fun homeBroadCast(jobId: Int, position: Int) {

        val intentSj = Intent(Constants.BRODCAST_SAVED_JOB)

        intentSj.putExtra(Constants.SAVED_APPLY_STATUS, applyStatus)
        intentSj.putExtra(Constants.SAVED_SAVE_STATUS, saveStatus)
        intentSj.putExtra(Constants.SAVED_ID, jobId)


        if (jobStatus != null) {

            intentSj.putExtra(Constants.SAVED_JOB_STATUS, jobStatus)
        }
        intentSj.putExtra(Constants.SAVED_JOB_TYPE, savedJobtList.get(position).jobType)

        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentSj)
    }
}