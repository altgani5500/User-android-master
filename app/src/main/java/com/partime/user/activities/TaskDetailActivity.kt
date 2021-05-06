package com.partime.user.activities

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.View
import android.widget.Toast
import com.gems.app.utilities.AppValidator
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.TaskButtonResponse
import com.partime.user.Responses.TaskDetailMessage
import com.partime.user.Responses.TaskDetailResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_task_detail.*
import kotlinx.android.synthetic.main.task_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.HashMap
import java.text.ParseException
import java.text.SimpleDateFormat


class TaskDetailActivity : BaseActivity(), View.OnClickListener {

    var taskId: Int? = 0
    var map = HashMap<String, String>()
    var taskStatus: String? = null
    var taskDetailsResponse: TaskDetailMessage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        AppValidator.rotateBackArrow(imgBackTaskDetail, this@TaskDetailActivity)

        if (intent.getIntExtra(Constants.TASK_ID, 0) != 0) {
            taskId = intent.getIntExtra(Constants.TASK_ID, 0)
        } else {
            taskId = intent.getIntExtra("TASK_ID", 0)
        }

        validateNet()

        imgBackTaskDetail.setOnClickListener(this)
        btnRetryTaskDetail.setOnClickListener(this)
        btnOneTaskDetails.setOnClickListener(this)
        btnTwoTaskDetails.setOnClickListener(this)
        imgSaveTaskCalendar.setOnClickListener(this)

    }

    private fun validateNet() {
        if (AppValidator.isInternetAvailable(this@TaskDetailActivity)) {

            //get task setails
            getTaskDetails()
        } else {

            llErrorJobList.visibility = View.VISIBLE
            llTaskDetails.visibility = View.GONE

        }
    }

    private fun getTaskDetails() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@TaskDetailActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val language = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())
        map.put("taskId", taskId.toString())

        val apiService = ApiService.init()
        val call = apiService.getTaskDetails("Bearer $authKey", language, map)
        call.enqueue(object : Callback<TaskDetailResponse> {
            override fun onResponse(
                call: Call<TaskDetailResponse>,
                response: retrofit2.Response<TaskDetailResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        llTaskDetails.visibility = View.VISIBLE
                        taskDetailsResponse = response.body()?.message!!
                        setDetails(response.body()?.message!!)

                    } else {

                        Toast.makeText(
                            this@TaskDetailActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<TaskDetailResponse>, t: Throwable) {

                Toast.makeText(this@TaskDetailActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                llErrorJobList.visibility = View.VISIBLE
                llTaskDetails.visibility = View.GONE

            }

        })
    }

    private fun setDetails(message: TaskDetailMessage) {

        taskStatus = message.taskStatus

        txtEnterpriseNameTaskDetail.text = message.enterpriseName
        txtTaskNameTaskDetail.text = message.taskName
        txtTaskTypeTaskDetail.text = message.taskType




        if (message.taskType.equals("Time bound")||message.taskType.equals("Time Bound")) {
            imgSaveTaskCalendar.visibility = View.VISIBLE
            txtExecutionText.visibility = View.VISIBLE
            txtExecutionTimeTaskDetail.visibility = View.VISIBLE
            val executionDate=formatDate( message.taskDate)
            val sTime=formatTime(message.startTime)
            val eTime=formatTime(message.endTime)
            txtExecutionTimeTaskDetail.text = executionDate+ "  " + sTime + " to " +eTime

        } else {
            txtExecutionText.visibility = View.GONE
            txtExecutionTimeTaskDetail.visibility = View.GONE
            imgSaveTaskCalendar.visibility = View.GONE
        }

        txtDescriptionTaskDetail.text = message.taskDescription
        txtTaskStatusTaskDetail.text = message.taskStatus

        setTaskStatus(taskStatus!!, message.evaluation, message.reevaluation,message.taskType)

        val creation=message.creationTime.split(" ")
        val creationDate=formatDate(creation[0])
        val creationTime=formatCTime(creation[1])

        txtCreationTimeTaskDetail.text = creationDate+"  "+creationTime

    }


    private fun setTaskStatus(tasksStatus: String, evaluation: String, reevaluation: String, taskType: String) {

        if (tasksStatus.equals("Pending")) {

            viewTaskStatusTaskDetail.setBackgroundDrawable(this.getDrawable(R.drawable.yellow_circle))
            txtTaskStatusTaskDetail.setTextColor(this.resources.getColor(R.color.profile_yellow))

            if(taskType.equals(resources.getString(R.string.time_boound))){
                btnOneTaskDetails.visibility = View.VISIBLE
                btnOneTaskDetails.text = this.getString(R.string.reschedule_request)
            }else{
                btnOneTaskDetails.visibility = View.GONE
            }

            btnTwoTaskDetails.visibility = View.VISIBLE
            btnTwoTaskDetails.text = this.getString(R.string.start_task)

        } else if (tasksStatus.equals("In Process")) {
            viewTaskStatusTaskDetail.setBackgroundDrawable(this.getDrawable(R.drawable.green_circle))
            txtTaskStatusTaskDetail.setTextColor(this.resources.getColor(R.color.profile_green))

            btnOneTaskDetails.visibility = View.VISIBLE
            btnOneTaskDetails.text = this.getString(R.string.done)

            btnTwoTaskDetails.visibility = View.GONE

        } else if (tasksStatus.equals("Completed")) {

            btnOneTaskDetails.visibility = View.GONE
            btnTwoTaskDetails.visibility = View.GONE

            viewTaskStatusTaskDetail.setBackgroundDrawable(this.getDrawable(R.drawable.blue_circle))
            txtTaskStatusTaskDetail.setTextColor(this.resources.getColor(R.color.blue))


            if (!reevaluation.isNullOrEmpty() || !reevaluation.isNullOrBlank() || !evaluation.isNullOrBlank() || !evaluation.isNullOrEmpty()) {

                if(evaluation.toInt()==0){
                    txtTaskStatusTaskDetail.text=resources.getString(R.string.pending_evaluation)
                    txtTaskStatusTaskDetail.setTextColor(this.resources.getColor(R.color.pending_eval))
                    viewTaskStatusTaskDetail.setBackgroundDrawable(this.getDrawable(R.drawable.pending_eval_circle))

                }else{
                    viewTaskStatusTaskDetail.setBackgroundDrawable(this.getDrawable(R.drawable.blue_circle))
                    txtTaskStatusTaskDetail.setTextColor(this.resources.getColor(R.color.blue))
                }

                if (evaluation.toInt() == 1 && reevaluation.toInt() == 0) {
                    btnOneTaskDetails.visibility = View.VISIBLE
                    btnOneTaskDetails.text = this.resources.getString(R.string.revaluate)

                } else {
                    btnOneTaskDetails.visibility = View.GONE
                }
            }


        } else if (tasksStatus.equals("Rescheduled")) {

            btnOneTaskDetails.visibility = View.GONE
            btnTwoTaskDetails.visibility = View.GONE

            viewTaskStatusTaskDetail.setBackgroundDrawable(this.getDrawable(R.drawable.red_circle))
            txtTaskStatusTaskDetail.setTextColor(this.resources.getColor(R.color.applied_color))
        }else{
            //................for re evaluate.................
            btnOneTaskDetails.visibility = View.GONE
            btnTwoTaskDetails.visibility = View.GONE

            viewTaskStatusTaskDetail.setBackgroundDrawable(this.getDrawable(R.drawable.red_circle))
            txtTaskStatusTaskDetail.setTextColor(this.resources.getColor(R.color.applied_color))
        }

        //txtTaskStatusTaskDetail.text = tasksStatus
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackTaskDetail -> {

                onBackPressed()
            }
            btnRetryTaskDetail -> {

                llErrorJobList.visibility = View.GONE
                validateNet()

            }
            btnOneTaskDetails -> {

                if (btnOneTaskDetails.text.equals(this.getString(R.string.reschedule_request))) {
                    changeStatus(taskId, "Rescheduled")
                } else if (btnOneTaskDetails.text.equals(this.getString(R.string.done))) {
                    changeStatus(taskId, "Completed")
                } else if (btnOneTaskDetails.text.equals(this.getString(R.string.revaluate))) {
                    changeStatus(taskId, "ReEvaluation")
                }
            }


            btnTwoTaskDetails -> {

                changeStatus(taskId, "In Process")
            }
            imgSaveTaskCalendar -> {

                if (taskDetailsResponse != null) {
                    openCalendar(
                        taskDetailsResponse!!.taskDate,
                        taskDetailsResponse!!.startTime,
                        taskDetailsResponse!!.endTime,
                        taskDetailsResponse!!.taskName
                    )
                }

            }
        }


    }

    private fun openCalendar(taskDate: String, sTime: String, eTime: String, taskName: String) {

        var date = taskDate.toString().split("-")
        var calStartTime = sTime.split(":")
        var calEndTIme = eTime.split(":")

        val startTime = Calendar.getInstance()
        startTime.set(
            date[0].toInt(),
            date[1].toInt(),
            date[2].toInt(),
            calStartTime[0].toInt(),
            calStartTime[1].toInt()
        )
        val endTime = Calendar.getInstance()
        endTime.set(date[1].toInt(), date[1].toInt(), date[2].toInt(), calEndTIme[0].toInt(), calEndTIme[1].toInt())

        var intent = Intent(Intent.ACTION_EDIT)
        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.timeInMillis)
        intent.putExtra("allDay", false)
        intent.putExtra("rrule", "FREQ=YEARLY")
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
        intent.putExtra("title", taskName)
        startActivity(intent)

    }

    private fun changeStatus(taskId: Int?, status: String) {

        var progressBar = ProgressBarUtil().showProgressDialog(this@TaskDetailActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        map.put("taskId", taskId.toString())
        map.put("status", status)

        val apiService = ApiService.init()
        val call = apiService.taskButtonCLick("Bearer $authKey", map)
        call.enqueue(object : Callback<TaskButtonResponse> {
            override fun onResponse(
                call: Call<TaskButtonResponse>,
                response: retrofit2.Response<TaskButtonResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {
                        Toast.makeText(
                            this@TaskDetailActivity,
                            response.body()?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                        taskStatus = response.body()?.status

                        setTaskStatus(taskStatus!!, "", "", "")

                    } else {

                        Toast.makeText(
                            this@TaskDetailActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<TaskButtonResponse>, t: Throwable) {

                Toast.makeText(this@TaskDetailActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.taskDetailId.toString(), taskId!!)
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.taskDetailStatus.toString(), taskStatus)
        finish()
    }

   fun formatDate(dateStr:String): String {

       val oldFormat = "yyyy-MM-dd"
       val newFormat = "dd-MMM-yyyy"

       var formatedDate = ""
       val dateFormat = SimpleDateFormat(oldFormat)
       var myDate: Date? = null
       try {
           myDate = dateFormat.parse(dateStr)
       } catch (e: ParseException) {
           e.printStackTrace()
       }


       val timeFormat = SimpleDateFormat(newFormat)
       formatedDate = timeFormat.format(myDate)

       return formatedDate

   }

   fun formatTime(time:String): String? {

       var formatedTime:String?=null

       try {
           val sdf = SimpleDateFormat("H:mm")
           val dateObj = sdf.parse(time)

           formatedTime=SimpleDateFormat("hh:mm a").format(dateObj)
       } catch (e: ParseException) {
           e.printStackTrace()
       }

       return formatedTime
   }

    private fun formatCTime(time: String): String? {
        var formatedTime:String?=null

        try {
            val sdf = SimpleDateFormat("H:mm:ss")
            val dateObj = sdf.parse(time)

            formatedTime=SimpleDateFormat("hh:mm a").format(dateObj)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return formatedTime
    }

}
