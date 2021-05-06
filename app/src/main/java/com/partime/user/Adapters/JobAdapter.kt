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
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.R
import com.partime.user.Responses.ApplyJobResponse
import com.partime.user.Responses.JobDetailsMessage
import com.partime.user.Responses.JobListMessage
import com.partime.user.Responses.SaveJobResponse
import com.partime.user.activities.RegisterActivity
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.helpers.buildDeepLink
import com.partime.user.helpers.shortLinkTask
import com.partime.user.prefences.AppPrefence
import com.parttime.com.apiclients.ApiService
import com.parttime.user.uicomman.auth.LoginActivity
import kotlinx.android.synthetic.main.apply_dailog.*
import kotlinx.android.synthetic.main.info_dailog.*
import kotlinx.android.synthetic.main.info_dailog.btnOkInfoDialog
import kotlinx.android.synthetic.main.info_dailog.txtInfoDialog
import kotlinx.android.synthetic.main.logout_dialog.*
import kotlinx.android.synthetic.main.register_dailog.*
import retrofit2.Call
import retrofit2.Callback


class JobAdapter(
        private val jobtList: List<JobListMessage>,

        private val jobtListDetails: List<JobDetailsMessage>,



        internal var context: Context?,
        var listener: JobCardListener,
        var savedListener: OnSavedListener
) : RecyclerView.Adapter<JobAdapter.MyViewHolder>() {
    var map = HashMap<String, String>()
    val appPrefence = AppPrefence.INSTANCE

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtJobDesignation: TextView = view.findViewById(R.id.txtJobListDesignation)
        var  CreatingDate: TextView = view.findViewById(R.id.CreatingDate)
        var txtCompanyname: TextView = view.findViewById(R.id.txtJobListCompanyName)
        var txtLocation: TextView = view.findViewById(R.id.txtJobListLocation)
        var txtSalary: TextView = view.findViewById(R.id.txtJobListSalary)
        var txtServices: TextView = view.findViewById(R.id.txtJobListService)
        var txtJobStatus: TextView = view.findViewById(R.id.txtJobListStatus)
        var jobListLayout: CardView = view.findViewById(R.id.linearLayoutJobList)
        var btnApply: Button = view.findViewById(R.id.btnApplyJobList)
        var btnUnapply: Button = view.findViewById(R.id.btnUnapplyJobList)
        var saveJob: Button = view.findViewById(R.id.imgSaveJob)
        var unsaveJob: Button = view.findViewById(R.id.imgUnSaveJob)
        var map: ImageView = view.findViewById(R.id.imgMapJob)
        var share:ImageView=view.findViewById(R.id.imgShareHome)
        var imgJobStatus: ImageView = view.findViewById(R.id.imgJobStatus)
        var jobInfo: ImageView = view.findViewById(R.id.imgJobInfo)
        var viewedLayout: LinearLayout = view.findViewById(R.id.llJobViewedStatus)
        var imgViewed: ImageView = view.findViewById(R.id.imgJobViewedStatus)
        var txtViewedStatus: TextView = view.findViewById(R.id.txtJobListViewedStatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(com.partime.user.R.layout.job_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        appPrefence.initAppPreferences(context)

        var authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString(), " ")

        val jobs = jobtList[position]

      //  val jobDetails = jobtListDetails[position]

        if (jobs.applyJob == 0) {

            viewHolder.btnApply.visibility = View.VISIBLE
            viewHolder.btnUnapply.visibility = View.GONE
        } else if (jobs.applyJob == 1) {

            viewHolder.btnApply.visibility = View.GONE
            viewHolder.btnUnapply.visibility = View.VISIBLE
        } else {

        }

        if (jobs.savedJob == 0) {

            viewHolder.saveJob.visibility = View.VISIBLE
            viewHolder.unsaveJob.visibility = View.GONE
        } else {
            viewHolder.saveJob.visibility = View.GONE
            viewHolder.unsaveJob.visibility = View.VISIBLE
        }
      //  jobs.jobType
        if (jobs.jobType.equals(context!!.resources.getString(R.string.credited_jobs))) {

            viewHolder.imgJobStatus.setImageResource(R.drawable.small_blue_location)

            viewHolder.txtJobStatus.text =  context!!.resources.getString(R.string.credited_jobs)

        } else {

            viewHolder.imgJobStatus.setImageResource(R.drawable.small_black_location)
            viewHolder.txtJobStatus.text = jobs.jobType

        }

        context!!.resources.getString(R.string.viewed)
        //jobs.jobStatus
        if (jobs.jobStatus.equals("Viewed") || jobs.jobStatus.equals("تمت المشاهدة")) {

            viewHolder.viewedLayout.visibility = View.VISIBLE

            viewHolder.imgViewed.setImageResource(R.drawable.small_gray_location)
            viewHolder.txtViewedStatus.text = context!!.resources.getString(R.string.viewed)
        } else {

            viewHolder.viewedLayout.visibility = View.INVISIBLE

        }




       /// viewHolder.CreatingDate.text = jobDetails.createdDate

       viewHolder.txtJobDesignation.text = jobs.jobTitle
        viewHolder.txtCompanyname.text = jobs.enterpriseName
        viewHolder.txtLocation.text = jobs.jobLocation
        viewHolder.txtServices.text = jobs.industryType
        viewHolder.txtSalary.text = jobs.amountPaid



        viewHolder.jobListLayout.setOnClickListener {

            listener.onJobCardListener(position, jobs.jobId)

        }
        viewHolder.saveJob.setOnClickListener {

            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else if (appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {
               // viewHolder.saveJob.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context!!,R.drawable.ic_baseline_star_24), null)
                viewHolder.saveJob.visibility = View.GONE
                viewHolder.unsaveJob.visibility = View.VISIBLE


                saveCurrentJob(jobs.jobId, 1, authkey, position)


            }
        }

        viewHolder.unsaveJob.setOnClickListener {

            viewHolder.saveJob.visibility = View.VISIBLE
            viewHolder.unsaveJob.visibility = View.GONE

            saveCurrentJob(
                jobs.jobId,
                0,
                appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString()),
                position
            )
        }

        viewHolder.btnApply.setOnClickListener {

            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else {
                var infoDialog = Dialog(context)

                infoDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                infoDialog.setContentView(R.layout.apply_dailog)
                infoDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                infoDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

//        infoDialog.txtInfoDialog.setText(R.string.not_enrolled)
//

          infoDialog.btnCanclInfoDialog.setOnClickListener {

                    infoDialog.dismiss()
                }

                infoDialog.btnOkInfoDialog.setOnClickListener {

                    applyForJob(jobs.jobId, 1, authkey, viewHolder.btnUnapply, viewHolder.btnApply, position)
                    infoDialog.dismiss()
                }

                infoDialog.show()
            }

        }
        viewHolder.btnUnapply.setOnClickListener {

            Toast.makeText(context, R.string.can_not_apply, Toast.LENGTH_SHORT).show()

          /*  if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else {
                if (!jobs.jobAppliedStatus.equals(context!!.resources.getString(R.string.accepted))) {
                    applyForJob(jobs.jobId, 0, authkey, viewHolder.btnUnapply, viewHolder.btnApply, position)
                } else {
                    Toast.makeText(context, R.string.can_not_apply, Toast.LENGTH_SHORT).show()
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

        viewHolder.share.setOnClickListener{

            context!!.shortLinkTask(context!!.getString(R.string.share_text)+"  ", buildDeepLink(jobs.jobId.toString()))
            buildDeepLink(jobs.jobId.toString())
        }

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

                            jobtList[position].applyJob = 1
                            btnApply.visibility = View.GONE
                            btnUnapply.visibility = View.VISIBLE


                        } else if (jobType == 0) {

                            jobtList[position].applyJob = 0
                            btnApply.visibility = View.VISIBLE
                            btnUnapply.visibility = View.GONE

                        }
                        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.applyHomeJobId.toString(), jobId)
                        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.applyJobHome.toString(), jobType)
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
                       // Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_LONG).show()
                        Toast.makeText(context, context!!.getString(R.string.processdone)
                                , Toast.LENGTH_LONG).show()
                        if (jobType == 1) {

                            jobtList[position].savedJob = 1

                        } else if (jobType == 0) {

                            jobtList[position].savedJob = 0

                        }
                        jobtList[position].jobStatus = response.body()?.jobStatus!!
                        jobtList[position].jobType = response.body()?.jobType!!

                        savedListener.onSaved(response.body()?.jobStatus!!, response.body()?.jobType!!)

                        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.saveHomeJobId.toString(), jobId)
                        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.saveJobHome.toString(), jobType)

                        notifyDataSetChanged()
                    } else {

                        Toast.makeText(context, response.body()?.error_message.toString(), Toast.LENGTH_LONG)
                    }

                }

            }

            override fun onFailure(call: Call<SaveJobResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    override fun getItemCount(): Int {
        return jobtList.size
    }

    interface JobCardListener {
        fun onJobCardListener(position: Int, jobId: Int)
    }

    interface OnSavedListener {

        fun onSaved(jobStatus: String, jobType: String)
    }



//
//    private fun ApplyDialog() {
//
//
//    }

}