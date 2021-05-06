package com.partime.user.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.ViewScheduleAdapter
import com.partime.user.R
import com.partime.user.Responses.ViewScheduleMessage
import com.partime.user.Responses.ViewScheduleResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.activity_view_schedule.*
import retrofit2.Call
import retrofit2.Callback
import java.util.HashMap

class ViewScheduleActivity : BaseActivity() {

    var map = HashMap<String, String>()
    var list=ArrayList<ViewScheduleMessage>()
    private var adapter: ViewScheduleAdapter? = null
    var date:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_schedule)

        AppValidator.rotateBackArrow(imgBackViewSchedule, this@ViewScheduleActivity)

        if(!intent.getStringExtra("SELECTED_DATE").isNullOrBlank()||!intent.getStringExtra("SELECTED_DATE").isNullOrEmpty()){

            date=intent.getStringExtra("SELECTED_DATE")
            txtDateViewSchedule.text=intent.getStringExtra("TITLE")
        }



        validateNet(date)

        imgBackViewSchedule.setOnClickListener{

            onBackPressed()
        }

    }

    private fun validateNet(date: String?) {

        if(AppValidator.isInternetAvailable(this@ViewScheduleActivity)){

            getSchedule(date)
        }else{

            llErrorViewSchedule.visibility= View.VISIBLE
            recyclerViewSchedule.visibility=View.GONE
        }
    }

    private fun getSchedule(date: String?) {

        var progressBar = ProgressBarUtil().showProgressDialog(this@ViewScheduleActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val language = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())

        map.put("date",date!!)

        val apiService = ApiService.init()
        val call: Call<ViewScheduleResponse> = apiService.getSchedule("Bearer $authKey", language,map)

        call.enqueue(object : Callback<ViewScheduleResponse> {
            override fun onResponse(
                call: Call<ViewScheduleResponse>,
                response: retrofit2.Response<ViewScheduleResponse>?
            ) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)
                    if (response.body()?.code == 200) {

                        if (!list.isEmpty()) {
                            list.clear()
                        }

                        list.addAll(response.body()?.message as ArrayList<ViewScheduleMessage>)

                        if (list.size > 0) {

                            adapter = ViewScheduleAdapter(list, this@ViewScheduleActivity)

                            llErrorViewSchedule.visibility = View.GONE
                            recyclerViewSchedule.visibility = View.VISIBLE
                            recyclerViewSchedule!!.layoutManager =
                                LinearLayoutManager(this@ViewScheduleActivity, RecyclerView.VERTICAL, false)
                            recyclerViewSchedule!!.adapter = adapter

                        } else {

                            recyclerViewSchedule.visibility = View.GONE
                            llErrorViewSchedule.visibility = View.GONE

                        }

                    } else {

                        Toast.makeText(
                            this@ViewScheduleActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

            }

            override fun onFailure(call: Call<ViewScheduleResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                recyclerViewSchedule.visibility = View.GONE
                llErrorViewSchedule.visibility = View.VISIBLE

            }

        })
    }
}
