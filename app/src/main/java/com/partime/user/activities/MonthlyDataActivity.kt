package com.partime.user.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.MonthlyDataAdapter
import com.partime.user.Adapters.WeekAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.*
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_monthly_data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class MonthlyDataActivity : BaseActivity(), View.OnClickListener {

    private var adapter: MonthlyDataAdapter? = null
    var map = HashMap<String, String>()
    var month:String?=null
    var year:String?=null
    var monthNo=0
    var buttonPosition=0
    var isButtonChanged=false
    var weekkList=ArrayList<WorkDetailsMessage>()
    var workList=ArrayList<Work>()

    private  val weekAdapter by lazy {
        WeekAdapter(weekkList,this , View.OnClickListener {
            val pos = it?.tag as Int
            val data = weekkList[pos]
            if (it.id == R.id.weekButton){
                weekkList.forEach {
                    it.isClicked = false
                }
                data.isClicked = true
                selfNotify(pos)
            }
        })
    }

    private fun selfNotify(pos: Int) {
        weekAdapter.notifyDataSetChanged()
        setWeeklyData(pos)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_data)

        AppValidator.rotateBackArrow(imgBackMonthlyData,this@MonthlyDataActivity)

        if(!intent.getStringExtra(Constants.SELECTED_MONTH).isNullOrBlank()||!intent.getStringExtra(Constants.SELECTED_MONTH).isNullOrEmpty()){

            month=intent.getStringExtra(Constants.SELECTED_MONTH)
        }

        if(!intent.getStringExtra(Constants.SELECTED_YEAR).isNullOrBlank()||!intent.getStringExtra(Constants.SELECTED_YEAR).isNullOrEmpty()){

            year=intent.getStringExtra(Constants.SELECTED_YEAR)
        }
        if(intent.getIntExtra(Constants.MONTH_NO,0)!=0){

            monthNo=intent.getIntExtra(Constants.MONTH_NO,0)
        }

        if(monthNo!=0&&( monthNo<0||monthNo>10)){

            var mm=0.toString()+monthNo.toString()
            monthNo=mm.toInt()
        }

        txtYearMonthlyData.setText(month+"  "+year)

        validateNet()

        imgBackMonthlyData.setOnClickListener(this)
        btnRetryMonthlyData.setOnClickListener(this)

        recyclerWeekMonthlyData.layoutManager =LinearLayoutManager(this@MonthlyDataActivity,RecyclerView.HORIZONTAL,false)
        recyclerWeekMonthlyData.adapter = weekAdapter

    }

    private fun validateNet() {

        if(AppValidator.isInternetAvailable(this@MonthlyDataActivity)){
            //to get the user report
            getUserReport()

        }else{

            llMonthlyData.visibility=View.GONE
            txtNoWorkList.visibility=View.GONE
            llErrorMonthlyData.visibility=View.VISIBLE
        }
    }

    private fun getUserReport() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@MonthlyDataActivity)
        val authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        var language = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())

        map.put("userId",appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString()))
        map.put("month", monthNo.toString())
        map.put("year", year.toString())

        val apiService = ApiService.init()
        val call: Call<WorkDetailsResponse> = apiService.getUserWorkReport("Bearer $authkey",language, map)
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<WorkDetailsResponse> {
            override fun onResponse(call: Call<WorkDetailsResponse>, response: retrofit2.Response<WorkDetailsResponse>?) {
                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        setMonthlyData(response)

                        if(response.body()?.message!!.size>0){

                            weekkList.clear()
                            weekkList.addAll(response.body()?.message as ArrayList<WorkDetailsMessage>)
                            weekkList.get(0).isClicked = true
                            weekAdapter.notifyDataSetChanged()

                            setWeeklyData(0)

                            llMonthlyData.visibility=View.VISIBLE
                            txtNoWorkList.visibility=View.GONE
                            llErrorMonthlyData.visibility =View.GONE

                        }else{

                            llMonthlyData.visibility=View.GONE
                            txtNoWorkList.visibility=View.VISIBLE
                            llErrorMonthlyData.visibility=View.GONE

                        }

                    } else {

                        Toast.makeText(
                            this@MonthlyDataActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

            }

            override fun onFailure(call: Call<WorkDetailsResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                llMonthlyData.visibility=View.GONE
                txtNoWorkList.visibility=View.GONE
                llErrorMonthlyData.visibility=View.VISIBLE

            }

        })

    }

    private fun setWeeklyData(buttonPosition: Int) {

        if(!weekkList.isNullOrEmpty()) {

            if (weekkList.get(buttonPosition).work.size > 0) {

                workList.clear()
                workList.addAll(weekkList.get(buttonPosition).work)

                adapter = MonthlyDataAdapter(workList, this@MonthlyDataActivity)

                recyclerMonthlyData.layoutManager = LinearLayoutManager(this@MonthlyDataActivity)
                recyclerMonthlyData.adapter = adapter

                recyclerMonthlyData.visibility = View.VISIBLE
                txtNoWorkList.visibility = View.GONE
                llErrorMonthlyData.visibility = View.GONE

                txtWeeklyHoursMonthlyData.setText(weekkList.get(buttonPosition).workHourDone.toString() + " / " + weekkList.get(buttonPosition).workHourSchedule.toString())

            } else {

                recyclerMonthlyData.visibility = View.GONE
                txtNoWorkList.visibility = View.VISIBLE
                llErrorMonthlyData.visibility = View.GONE
            }

        }
    }

    private fun setMonthlyData(work: Response<WorkDetailsResponse>?) {

        txtWorkedHourMD.setText(work!!.body()?.totalDone.toString()+" "+resources.getString(R.string.hours_out_of)+" "+work!!.body()?.totalSchedule.toString()+" "+resources.getString(R.string.hours))
    }

    override fun onClick(v: View?) {

        when(v){


            imgBackMonthlyData->{
                onBackPressed()
            }
            btnRetryMonthlyData->{

                llErrorMonthlyData.visibility=View.GONE

                validateNet()
            }
        }

    }

    override fun onResume() {
        super.onResume()

        if(isButtonChanged){

            isButtonChanged=false

            setWeeklyData(buttonPosition)
        }

    }

}
