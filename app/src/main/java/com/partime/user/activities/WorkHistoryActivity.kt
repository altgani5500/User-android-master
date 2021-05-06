package com.partime.user.activities

import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.TaskTypeAdapter
import com.partime.user.Adapters.WorkHistoryAdapter
import com.partime.user.R
import com.partime.user.Responses.*
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.helpers.Utilities
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_time_log.*
import kotlinx.android.synthetic.main.activity_work_history.*
import kotlinx.android.synthetic.main.logout_dialog.*
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WorkHistoryActivity : BaseActivity(), View.OnClickListener {

    private var adapter: WorkHistoryAdapter? = null
    var workHistoryList=ArrayList<WorkHistoryListMessage>()

    var enrolledUid=0
    var map = HashMap<String, String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_history)

        AppValidator.rotateBackArrow(imgBackWorkHistory, this@WorkHistoryActivity)

        validateNet()

        imgBackWorkHistory.setOnClickListener(this)
        btnRetryWorkHistory.setOnClickListener(this)
    }

    private fun validateNet() {

        if(AppValidator.isInternetAvailable(this@WorkHistoryActivity)){

            getWorkHistory()
        }else{

            llErrorWorkHistory.visibility=View.VISIBLE
            recyclerWorkHistory.visibility=View.GONE
        }
    }

    private fun getWorkHistory() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@WorkHistoryActivity)
        val authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val lang= appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())

        val apiService = ApiService.init()
        val call: Call<WorkHistoryListResponse> =
            apiService.getHistoryList("Bearer $authKey", lang)

        call.enqueue(object : Callback<WorkHistoryListResponse> {
            override fun onResponse(call: Call<WorkHistoryListResponse>, response: retrofit2.Response<WorkHistoryListResponse>?) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        if(response.body()?.message!!.size>0){

                            workHistoryList.clear()

                            workHistoryList.addAll(response.body()?.message as ArrayList<WorkHistoryListMessage>)

                            adapter= WorkHistoryAdapter(workHistoryList,this@WorkHistoryActivity,View.OnClickListener {

                                val pos = it?.tag as Int
                                if(it.id==R.id.btnResignationTimeLog){
                                    var logoutDialog = Dialog(this@WorkHistoryActivity)

                                    logoutDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                                    logoutDialog.setContentView(R.layout.logout_dialog)
                                    logoutDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                    logoutDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)



                                    logoutDialog.txtTitleLogoutDialog.setText(R.string.resign_requist)

                                    logoutDialog.btnYesLogout.setOnClickListener {

                                        resignationRequest()
                                        logoutDialog.dismiss()

                                    }
                                    logoutDialog.btnCancleLogout.setOnClickListener {

                                        logoutDialog.dismiss()

                                    }
                                    logoutDialog.show()

                                }
                            })

                            for(i in 0..workHistoryList.size-1){

                                if(workHistoryList.get(i).isWorking==1){
                                  enrolledUid=response.body()?.message!!.get(i).enrolledUserId
                                }
                            }
                            txtNoWorkHistory.visibility=View.GONE
                            recyclerWorkHistory.visibility = View.VISIBLE
                            llErrorWorkHistory.visibility = View.GONE

                            recyclerWorkHistory.layoutManager=LinearLayoutManager(this@WorkHistoryActivity)
                            recyclerWorkHistory.adapter=adapter


                        }else{

                            txtNoWorkHistory.visibility=View.VISIBLE
                            recyclerWorkHistory.visibility = View.GONE
                            llErrorWorkHistory.visibility = View.GONE
                        }

                    } else {

                        Toast.makeText(
                            this@WorkHistoryActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

            }

            override fun onFailure(call: Call<WorkHistoryListResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                recyclerWorkHistory.visibility = View.GONE
                llErrorWorkHistory.visibility = View.VISIBLE
                txtNoWorkHistory.visibility=View.GONE

            }

        })
    }

    private fun resignationRequest() {
      //  val resignDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        var progressBar = ProgressBarUtil().showProgressDialog(this@WorkHistoryActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val lang = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())

        val resignDate = getCurrentDate()

        map.put("enrolledUserId",enrolledUid.toString())
        map.put("resignDate",resignDate.toString())

        val apiService = ApiService.init()
        val call = apiService.requestResignation("Bearer $authKey",lang, map)
        call.enqueue(object : Callback<PunchInResponse> {
            override fun onResponse(
                call: Call<PunchInResponse>,
                response: retrofit2.Response<PunchInResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        Toast.makeText(
                            this@WorkHistoryActivity,
                            response.body()?.message.toString()+"sucsecc",
                            Toast.LENGTH_LONG
                        ).show()


                        //change the status of the resignation button
                        for(i in 0..workHistoryList.size-1){

                            if(workHistoryList.get(i).enrolledUserId==enrolledUid){

                                workHistoryList.get(i).isResignRequest=1

                            }
                        }
                        adapter!!.notifyDataSetChanged()

                    } else {

                        Toast.makeText(
                            this@WorkHistoryActivity,
                            response.body()?.error_message.toString()+"big else",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<PunchInResponse>, t: Throwable) {

                Toast.makeText(this@WorkHistoryActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })
    }

    override fun onClick(v: View?) {

        when(v){

            imgBackWorkHistory->{

                onBackPressed()
            }

        }
    }
//get crrunet date
    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }


}
