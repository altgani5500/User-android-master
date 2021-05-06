package com.partime.user.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.TaskHistoryAdapter
import com.partime.user.R
import com.partime.user.Responses.TaskHistoryMessage
import com.partime.user.Responses.TaskHistoryResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_task_history.*
import retrofit2.Call
import retrofit2.Callback

class TaskHistoryActivity : BaseActivity(), View.OnClickListener {


    private var adapter: TaskHistoryAdapter? = null
    var taskHistoryList = ArrayList<TaskHistoryMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_history)

        AppValidator.rotateBackArrow(imgBackTaskHistory, this@TaskHistoryActivity)

        validateNet()

        imgBackTaskHistory.setOnClickListener(this)
        btnRetryTaskHistory.setOnClickListener(this)

    }

    private fun validateNet() {

        if (AppValidator.isInternetAvailable(this@TaskHistoryActivity)) {

            getTaskHistory()
        } else {

            llErrorTaskHistory.visibility = View.VISIBLE
            recyclerTaskHistory.visibility = View.GONE
        }

    }

    private fun getTaskHistory() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@TaskHistoryActivity)
        val authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val lang = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())

        val apiService = ApiService.init()
        val call: Call<TaskHistoryResponse> =
            apiService.getTaskHistoryList("Bearer $authKey", lang)

        call.enqueue(object : Callback<TaskHistoryResponse> {
            override fun onResponse(
                call: Call<TaskHistoryResponse>,
                response: retrofit2.Response<TaskHistoryResponse>?
            ) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        if (response.body()?.message!!.size > 0) {

                            taskHistoryList.clear()
                            taskHistoryList.addAll(response.body()?.message as ArrayList<TaskHistoryMessage>)

                            adapter = TaskHistoryAdapter(taskHistoryList, this@TaskHistoryActivity)

                            txtNoTaskTaskHistory.visibility = View.GONE
                            recyclerTaskHistory.visibility = View.VISIBLE
                            llErrorTaskHistory.visibility = View.GONE

                            recyclerTaskHistory.layoutManager = LinearLayoutManager(this@TaskHistoryActivity)
                            recyclerTaskHistory.adapter = adapter


                        } else {

                            txtNoTaskTaskHistory.visibility = View.VISIBLE
                            recyclerTaskHistory.visibility = View.GONE
                            llErrorTaskHistory.visibility = View.GONE
                        }

                    } else {

                        Toast.makeText(
                            this@TaskHistoryActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

            }

            override fun onFailure(call: Call<TaskHistoryResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                recyclerTaskHistory.visibility = View.GONE
                llErrorTaskHistory.visibility = View.VISIBLE
                txtNoTaskTaskHistory.visibility = View.GONE
            }

        })
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackTaskHistory -> {

                onBackPressed()
            }
            btnRetryTaskHistory -> {

                llErrorTaskHistory.visibility = View.GONE
                validateNet()

            }
        }
    }
}
