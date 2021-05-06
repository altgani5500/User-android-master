package com.partime.user.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.gems.app.utilities.AppValidator
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.partime.user.Adapters.TaskTypeAdapter
import com.partime.user.R
import com.partime.user.Responses.*
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_my_history.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList


class MyHistoryActivity : BaseActivity(), View.OnClickListener {

    var selectedYear: String? = null
    var yearList = ArrayList<String>()
    var map = HashMap<String, String>()

    private var yearAdapter: TaskTypeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_history)

        AppValidator.rotateBackArrow(imgBackHistory, this@MyHistoryActivity)

        selectedYear = Calendar.getInstance().get(Calendar.YEAR).toString()

        txtYearHistory.text = selectedYear

        validateNet()

        addYears()

        //  llTotalWorkHoursHistory.setOnClickListener(this)
        imgBackHistory.setOnClickListener(this)
        btnRetryHistory.setOnClickListener(this)
        txtYearHistory.setOnClickListener(this)
        cardAllTaksHistory.setOnClickListener(this)
        llTotalWorkHistory.setOnClickListener(this)
        txtPastHstory.setOnClickListener(this)
        txtTotalWorkHistory.setOnClickListener(this)
    }

    private fun validateNet() {

        if (AppValidator.isInternetAvailable(this@MyHistoryActivity)) {

            getHistory()
        } else {

            llErrorHistory.visibility = View.VISIBLE
            llHistoryScroll.visibility = View.GONE
        }
    }

    private fun getHistory() {

        var progressBar = ProgressBarUtil().showProgressDialog(this)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        map.put("year", selectedYear.toString())

        val apiService = ApiService.init()
        val call: Call<HistoryResponse> = apiService.getHistory(
            "Bearer $authKey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()), map
        )

        call.enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: retrofit2.Response<HistoryResponse>?
            ) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {


                        llHistoryScroll.visibility = View.VISIBLE
                        llErrorHistory.visibility = View.GONE
                        saveHistoryDetails(response.body()?.message)

                        setGraph(
                            response.body()?.message!!.graphData,
                            response.body()?.firstLimit,
                            response.body()?.difference,
                            response.body()?.lastLimit
                        )


                    } else {

                        Toast.makeText(
                            this@MyHistoryActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                llHistoryScroll.visibility = View.GONE
                llErrorHistory.visibility = View.VISIBLE

            }

        })

    }

    private fun setGraph(
        graphData: GraphData,
        firstLimit: Int?,
        difference: Int?,
        lastLimit: Int?
    ) {

        val lineAllTasks = LineDataSet(
            getAllTasks(graphData.allTask, difference),
            resources.getString(R.string.all_tasks_assigned)
        )
        val lineWorkHistory = LineDataSet(
            getWorkHistory(graphData.workistory, difference),
            resources.getString(R.string.total_work_history)
        )
        val lineTotalHours = LineDataSet(
            getTotalHours(graphData.noOfHours, difference),
            resources.getString(R.string.total_hours_worked)
        )

        //set the line configurations
        setLineConfigurations(lineAllTasks, R.color.profile_green, 1)
        setLineConfigurations(lineWorkHistory, R.color.blue, 2)
        setLineConfigurations(lineTotalHours, R.color.profile_magenta, 3)

        val xAxis = lineChartHistory.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val month = ArrayList<String>()
        month.add("hdhhddjdjd")//because first value n x axis not visible
        month.add(resources.getString(R.string.jan))
        month.add(resources.getString(R.string.feb))
        month.add(resources.getString(R.string.mar))
        month.add(resources.getString(R.string.apr))
        month.add(resources.getString(R.string.may))
        month.add(resources.getString(R.string.jun))
        month.add(resources.getString(R.string.jul))
        month.add(resources.getString(R.string.aug))
        month.add(resources.getString(R.string.sep))
        month.add(resources.getString(R.string.oct))
        month.add(resources.getString(R.string.nov))
        month.add(resources.getString(R.string.dec))
        month.add("hdhhddjdjd")//because loop goes doesnt show last value

        val yAxis = lineChartHistory.axisLeft

        val last = lastLimit
        val date = ArrayList<String>()

        for (it in firstLimit!!..lastLimit!! step difference!!) {
            date.add(it.toString())
        }

        val xformatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {

                if (value.toInt() != month.size) {
                    return month[value.toInt()]
                } else {
                    return resources.getString(R.string.dec)
                }

            }
        }

        val yformatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {

                if (value.toInt() >= 0 && value.toInt() != date.size) {
                    return date[value.toInt()]
                } else if (value.toInt() >= 0 && value.toInt() == date.size) {
                    return last.toString()
                } else {

                    return ""
                }
            }
        }
        xAxis.granularity = 0.5f
        xAxis.labelRotationAngle = 270f
        xAxis.valueFormatter = xformatter

        yAxis.granularity = 0.5f
        yAxis.valueFormatter = yformatter

        val yAxisRight = lineChartHistory.axisRight
        yAxisRight.isEnabled = false

        val yAxisLeft = lineChartHistory.axisLeft
        yAxisLeft.granularity = 1f

        val dataSets = ArrayList<ILineDataSet>()

        //dataSets.add(lineAllTasks)
        dataSets.add(lineWorkHistory)
        dataSets.add(lineTotalHours)
        dataSets.add(lineAllTasks)

        val datafinal = LineData(dataSets)
        lineChartHistory.data = datafinal

        lineChartHistory.description.text = ""
        lineChartHistory.isHorizontalScrollBarEnabled = true
        lineChartHistory.isVerticalScrollBarEnabled = true

        lineChartHistory.setVisibleYRangeMaximum(10f, YAxis.AxisDependency.LEFT)

        lineChartHistory.setVisibleXRangeMaximum(6f)
        lineChartHistory.moveViewToX(6f)
        lineChartHistory.animateX(2000)
        lineChartHistory.setScaleEnabled(false)
        lineChartHistory.invalidate()

    }

    private fun getTotalHours(noOfHours: List<NoOfHour>, difference: Int?): MutableList<Entry>? {

        val entries = ArrayList<Entry>()

        for (i in 0..noOfHours.size - 1) {

            var month = noOfHours.get(i).month.toFloat()
            var value = noOfHours.get(i).value.toFloat().div(difference!!.toFloat())

            entries.add(Entry(month, value))
        }

        return entries
    }

    private fun getWorkHistory(
        workistory: List<Workistory>,
        difference: Int?
    ): MutableList<Entry>? {
        val entries = ArrayList<Entry>()

        for (i in 0..workistory.size - 1) {

            var month = workistory.get(i).month.toFloat()
            var value = workistory.get(i).value.toFloat().div(difference!!.toFloat())

            entries.add(Entry(month, value))
        }

        return entries

    }

    private fun getAllTasks(allTask: List<AllTask>, difference: Int?): MutableList<Entry>? {
        val entries = ArrayList<Entry>()

        for (i in 0..allTask.size - 1) {

            var month = allTask.get(i).month.toFloat()
            var value = allTask.get(i).value.toFloat().div(difference!!.toFloat())

            entries.add(Entry(month, value))
        }

        return entries
    }

    private fun saveHistoryDetails(message: HistoryMessage?) {

        txtAllTaskHistory.text = message!!.taskDetail.allTask.toString()
        txtCompleteHistory.text = message.taskDetail.completed.toString()
        txtPendingHistory.text = message.taskDetail.pending.toString()
        txtInProcessHistory.text = message.taskDetail.inprocess.toString()
        txtTotalWorkHistory.text = message.taskDetail.userEnroll.toString()
        txtTotalHourHistory.text = message.taskDetail.workDone.toString()

    }

    private fun setLineConfigurations(graphLines: LineDataSet, lineColor: Int, flag: Int) {

        var gradient: Drawable? = null

        graphLines.color = ContextCompat.getColor(this, lineColor)
        graphLines.setCircleColor(ContextCompat.getColor(this, lineColor))
        graphLines.valueTextColor = ContextCompat.getColor(this, lineColor)
        graphLines.setDrawFilled(true)

        //for chnaging gradient.........
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            if (flag == 1) {
                gradient = resources.getDrawable(R.drawable.all_task_gradient)

            } else if (flag == 2) {
                gradient = resources.getDrawable(R.drawable.work_history_gradient)
            } else {
                gradient = resources.getDrawable(R.drawable.total_hour_gradient)
            }

            graphLines.fillDrawable = gradient
        } else {
            graphLines.fillColor = Color.WHITE
        }

        graphLines.circleRadius = 0F
        graphLines.circleHoleRadius = 5f
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackHistory -> {

                onBackPressed()
            }
            btnRetryHistory -> {

                llErrorHistory.visibility = View.GONE
                validateNet()
            }
            txtYearHistory -> {

                getYearList(yearList)

            }
            llTotalWorkHistory, txtPastHstory, txtTotalWorkHistory -> {
                if(!txtTotalWorkHistory.text.toString().trim().contentEquals("0.0")) {
                    var intent = Intent(this@MyHistoryActivity, WorkHistoryActivity::class.java)
                    startActivity(intent)
                }
            }

            cardAllTaksHistory -> {
                if (!txtAllTaskHistory.text.toString().trim().contentEquals("0.0") &&
                    !txtCompleteHistory.text.toString().trim().contentEquals("0.0") &&
                    !txtPendingHistory.text.toString().trim().contentEquals("0.0") &&
                    !txtInProcessHistory.text.toString().trim().contentEquals("0.0")
                ) {
                    var intent = Intent(this@MyHistoryActivity, TaskHistoryActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun addYears() {

        yearList.clear()
        var currentYear = Calendar.getInstance().get(Calendar.YEAR)

        for (year in currentYear downTo (currentYear - 1)) {

            yearList.add(year.toString())

        }
    }

    private fun getYearList(yearList: ArrayList<String>) {
        yearAdapter = TaskTypeAdapter(yearList, this@MyHistoryActivity)
        val popupWindow = ListPopupWindow(this@MyHistoryActivity)
        popupWindow.setOnItemClickListener { parent, view, position, id ->

            txtYearHistory.text = yearList[position]
            selectedYear = yearList[position]

            getHistory()

            popupWindow.dismiss()
        }
        popupWindow.height = 300
        popupWindow.anchorView = txtYearHistory
        popupWindow.setAdapter(yearAdapter)
        popupWindow.show()
    }
}




