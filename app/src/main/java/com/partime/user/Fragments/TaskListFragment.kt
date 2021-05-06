package com.partime.user.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.TaskListAdapter
import com.partime.user.Adapters.TaskTypeAdapter
import com.partime.user.Constants.Constants
import com.partime.user.Listeners.TaskButtonListener
import com.partime.user.Listeners.TaskClickListener
import com.partime.user.R
import com.partime.user.Responses.TaskButtonResponse
import com.partime.user.Responses.TaskListMessage
import com.partime.user.Responses.TaskListResponse
import com.partime.user.activities.TaskDetailActivity
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseFragment
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.fragment_task_list.*
import retrofit2.Call
import retrofit2.Callback
import android.app.Activity




class TaskListFragment : BaseFragment(), View.OnClickListener {

    val appPrefence = AppPrefence.INSTANCE
    var map = HashMap<String, String>()
    private var adapter: TaskTypeAdapter? = null
    var taskType = ArrayList<String>()
    var taskTypeId = 2

    private var taskListAdapter: TaskListAdapter? = null
    var taskList = ArrayList<TaskListMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        appPrefence.initAppPreferences(context)

        txtListScheduleFrag.text = context!!.getString(R.string.time_boound)
        validateNet()

        btnRetryScheduleTask.setOnClickListener(this)
        txtListScheduleFrag.setOnClickListener(this)

    }

    private fun getTaskListType() {

        taskType.clear()

        taskType.add(context!!.getString(R.string.continous))
        taskType.add(context!!.getString(R.string.time_boound))

        adapter = TaskTypeAdapter(taskType, context!!)
        val popupWindow = ListPopupWindow(context!!)
        popupWindow.setOnItemClickListener { parent, view, position, id ->


            txtListScheduleFrag.text = taskType[position]
            taskTypeId = position + 1
            validateNet()
            popupWindow.dismiss()
        }
        popupWindow.height = 170
        popupWindow.anchorView = txtListScheduleFrag
        popupWindow.setAdapter(adapter)
        popupWindow.show()
    }

    private fun validateNet() {

        if (AppValidator.isInternetAvailable(context!!)) {

            //get task list
            getTaskList()
        } else {

            llErrorFragScheduleTask.visibility = View.VISIBLE
            recyclerTasks.visibility = View.GONE

        }
    }

    private fun getTaskList() {

        var progressBar = ProgressBarUtil().showProgressDialog(context!!)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val language = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())
        map.put("taskType", taskTypeId.toString())

        val apiService = ApiService.init()
        val call = apiService.getTaskDetailsList("Bearer $authKey", language, map)
        call.enqueue(object : Callback<TaskListResponse> {
            override fun onResponse(
                call: Call<TaskListResponse>,
                response: retrofit2.Response<TaskListResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        taskList.clear()
                        taskList.addAll(response.body()?.message as ArrayList<TaskListMessage>)

                        if (taskList.size > 0) {

                            taskListAdapter = TaskListAdapter(taskList, context!!, object : TaskClickListener {
                                override fun onTaskCikcListener(taskId: Int) {

                                    //go to task details...........
                                    var intent = Intent(context!!, TaskDetailActivity::class.java)
                                    intent.putExtra(Constants.TASK_ID, taskId)
                                    context!!.startActivity(intent)
                                }
                            }, object : TaskButtonListener {
                                override fun onTaskButtonClick(
                                    taskId: Int,
                                    status: String,
                                    position: Int
                                ) {

                                    changeStatus(taskId, status, position)
                                }

                            })

                            llEmptyTask.visibility = View.GONE
                            recyclerTasks.visibility = View.VISIBLE
                            recyclerTasks!!.layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                            recyclerTasks!!.itemAnimator = DefaultItemAnimator()
                            recyclerTasks!!.adapter = taskListAdapter
                        } else {

                            llEmptyTask.visibility = View.VISIBLE
                            recyclerTasks.visibility = View.GONE
                        }


                    } else {

                        Toast.makeText(
                            context!!,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<TaskListResponse>, t: Throwable) {

                Toast.makeText(context!!, R.string.no_internet, Toast.LENGTH_LONG).show()
                llErrorFragScheduleTask.visibility = View.VISIBLE
                recyclerTasks.visibility = View.GONE

            }

        })

    }

    private fun changeStatus(taskId: Int, status: String, position: Int) {

        var progressBar = ProgressBarUtil().showProgressDialog(context!!)

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
                            context!!,
                            response.body()?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                        taskList.get(position).taskStatus = response.body()?.status!!
                        taskListAdapter!!.notifyDataSetChanged()

                    } else {

                        Toast.makeText(
                            context!!,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<TaskButtonResponse>, t: Throwable) {

                Toast.makeText(context!!, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    override fun onClick(v: View?) {

        when (v) {

            btnRetryScheduleTask -> {

                llErrorFragScheduleTask.visibility = View.GONE
                validateNet()

            }
            txtListScheduleFrag -> {

                getTaskListType()
            }
        }
    }

    override fun onResume() {
        super.onResume()


        //if status of the task changes.........
        val id = appPrefence.getInt(AppPrefence.SharedPreferncesKeys.taskDetailId.toString(), 0)
        val status = appPrefence.getString(AppPrefence.SharedPreferncesKeys.taskDetailStatus.toString())
        if (id != 0) {

            for (i in 0..taskList.size - 1) {

                if (taskList.get(i).taskId == id) {

                    taskList.get(i).taskStatus = status
                    taskListAdapter!!.notifyDataSetChanged()
                }

            }
        }

        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.taskDetailId.toString(), 0)
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.taskDetailStatus.toString(), "")
    }

    fun hideKeyboard(activity: Activity) {
        try {
            val inputManager = activity
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val currentFocusedView = activity.currentFocus
            if (currentFocusedView != null) {
                inputManager.hideSoftInputFromWindow(currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
