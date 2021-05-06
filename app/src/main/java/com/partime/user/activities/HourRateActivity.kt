package com.partime.user.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.HoursRateAdapter
import com.partime.user.Constants.Constants
import com.partime.user.Listeners.HoursRateListener
import com.partime.user.R
import com.partime.user.Responses.HoursRate
import com.partime.user.uicomman.BaseActivity
import kotlinx.android.synthetic.main.activity_hour_rate.*

class HourRateActivity : BaseActivity(), View.OnClickListener {


    var hoursRate = ArrayList<HoursRate>()
    var checkedItems = ArrayList<HoursRate>()

    private var hoursRateAdapter: HoursRateAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hour_rate)

        AppValidator.rotateBackArrow(imgBackHourRate, this@HourRateActivity)

        //add hour rates
        addHoursRate()

        hoursRateAdapter = HoursRateAdapter(hoursRate, this@HourRateActivity, object : HoursRateListener {
            override fun onSelectNone(position: Int, txtCheckBox: ImageView, size: Int) {


                if (hoursRate.get(position).isClicked) {

                    hoursRate.get(position).isClicked = false
                    txtCheckBox.setImageResource(R.drawable.checkbox_gray)


                } else {

                    hoursRate.get(position).isClicked = true

                    if (hoursRate.get(position).hoursRate.contains("None")) {

                        for (i in 0..size - 1) {
                            if (i != position) {
                                hoursRate.get(i).isClicked = false
                            }

                        }
                    } else {

                        hoursRate.get(5).isClicked = false
                    }
                    hoursRateAdapter!!.notifyDataSetChanged()
                    txtCheckBox.setImageResource(R.drawable.checkbox_blue)
                }
            }

        })

        recyclerHourRate!!.layoutManager = LinearLayoutManager(this@HourRateActivity, RecyclerView.VERTICAL, false)
        recyclerHourRate!!.itemAnimator = DefaultItemAnimator()
        recyclerHourRate!!.adapter = hoursRateAdapter

        if (intent.getSerializableExtra(Constants.HOURS_RATE_SELECTED) != null) {

            var selectedList = java.util.ArrayList<HoursRate>()
            selectedList.addAll(intent.getSerializableExtra(Constants.HOURS_RATE_SELECTED) as ArrayList<HoursRate>)

            for (i in 0..hoursRate.size - 1) {

                for (ii in 0..selectedList.size - 1) {

                    if (hoursRate.get(i).hoursRate == selectedList.get(ii).hoursRate) {

                        hoursRate.get(i).isClicked = true
                        hoursRateAdapter!!.notifyDataSetChanged()
                    }

                }

            }
        }

        imgBackHourRate.setOnClickListener(this)
        btnApplyHourRate.setOnClickListener(this)
        btnResetHourRate.setOnClickListener(this)
    }

    /*
    Method to add hour rates
     */
    private fun addHoursRate() {

        hoursRate.add(HoursRate("1-5", false))
        hoursRate.add(HoursRate("6-10", false))
        hoursRate.add(HoursRate("11-15", false))
        hoursRate.add(HoursRate("16-20", false))
        hoursRate.add(HoursRate("21+", false))
        hoursRate.add(HoursRate("None", false))


    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackHourRate -> onBackPressed()

            btnApplyHourRate -> {


                var hoursPaidRate = getFilteredHourRate()
                val intent = intent
                intent.putExtra("hourRateFilters", hoursPaidRate)
                setResult(131, intent)
                finish()

            }
            btnResetHourRate -> {

                for (i in 0..hoursRate.size - 1) {

                    hoursRate.get(i).isClicked = false
                }
                hoursRateAdapter!!.notifyDataSetChanged()

            }
        }
    }

    /*
    Method to get the seleted hour rate
     */
    private fun getFilteredHourRate(): ArrayList<HoursRate> {

        if (!checkedItems.isEmpty()) {
            checkedItems.clear()
        }

        for (i in 0..hoursRate.size - 1) {
            if (hoursRate.get(i).isClicked) {
                checkedItems.add(hoursRate[i])

            }
        }
        return checkedItems

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
