package com.partime.user.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.HoursWeekAdapter
import com.partime.user.Constants.Constants
import com.partime.user.Listeners.HoursWeekListener
import com.partime.user.R
import com.partime.user.Responses.HoursPerWeek
import com.partime.user.uicomman.BaseActivity
import kotlinx.android.synthetic.main.activity_hours_perweek.*

class HoursPerWeekActivity : BaseActivity(), View.OnClickListener {

    var hoursWeek = ArrayList<HoursPerWeek>()
    var checkedItems = ArrayList<HoursPerWeek>()

    private var hoursAdapter: HoursWeekAdapter? = null
    var selectALlClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hours_perweek)

        AppValidator.rotateBackArrow(imgBackHourPerWeek, this@HoursPerWeekActivity)

        //add hour rates
        addHoursRate()

        hoursAdapter = HoursWeekAdapter(hoursWeek, this@HoursPerWeekActivity, object : HoursWeekListener {
            override fun onSelect(position: Int, txtCheckBox: ImageView) {

                if (hoursWeek.get(position).isClicked) {

                    hoursWeek.get(position).isClicked = false
                    selectALlClicked = false

                    txtCheckBox.setImageResource(R.drawable.checkbox_gray)
                    imgSelectAllHourPerWeek.setImageResource(R.drawable.checkbox_gray)


                } else {

                    hoursWeek.get(position).isClicked = true
                    txtCheckBox.setImageResource(R.drawable.checkbox_blue)
                }
            }


        })

        //set adapter
        recyclerPerWeek!!.layoutManager = LinearLayoutManager(this@HoursPerWeekActivity, RecyclerView.VERTICAL, false)
        recyclerPerWeek!!.itemAnimator = DefaultItemAnimator()
        recyclerPerWeek!!.adapter = hoursAdapter

        if (intent.getSerializableExtra(Constants.HOURS_WEEK_SELECTED) != null) {

            var selectedList = java.util.ArrayList<HoursPerWeek>()
            selectedList.addAll(intent.getSerializableExtra(Constants.HOURS_WEEK_SELECTED) as ArrayList<HoursPerWeek>)

            for (i in 0..hoursWeek.size - 1) {

                for (ii in 0..selectedList.size - 1) {

                    if (hoursWeek.get(i).hoursPerWeek == selectedList.get(ii).hoursPerWeek) {

                        hoursWeek.get(i).isClicked = true
                        if (hoursAdapter != null) {
                            hoursAdapter!!.notifyDataSetChanged()
                        }
                    }

                }

            }
        }

        rlSelectAllHourPerWeek.setOnClickListener(this)
        imgBackHourPerWeek.setOnClickListener(this)
        btnApplyHourWeek.setOnClickListener(this)
        btnResetHoursWeek.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackHourPerWeek -> onBackPressed()

            rlSelectAllHourPerWeek -> {


                if (selectALlClicked) {

                    selectALlClicked = false
                    imgSelectAllHourPerWeek.setImageResource(R.drawable.checkbox_gray)

                    for (i in 0..hoursWeek.size - 1) {

                        hoursWeek.get(i).isClicked = false
                        if (hoursAdapter != null) {
                            hoursAdapter!!.notifyDataSetChanged()
                        }
                    }


                } else {

                    selectALlClicked = true
                    imgSelectAllHourPerWeek.setImageResource(R.drawable.checkbox_blue)


                    for (i in 0..hoursWeek.size - 1) {

                        hoursWeek.get(i).isClicked = true
                        if (hoursAdapter != null) {
                            hoursAdapter!!.notifyDataSetChanged()
                        }
                    }

                }
            }
            btnApplyHourWeek -> {

                var hourWeek = getFilteredHourWeek()
                val intent = intent
                intent.putExtra("companyHourWeek", hourWeek)
                setResult(121, intent)
                finish()

            }

            btnResetHoursWeek -> {
                for (i in 0..hoursWeek.size - 1) {

                    hoursWeek.get(i).isClicked = false
                }

                if (hoursAdapter != null) {
                    hoursAdapter!!.notifyDataSetChanged()
                }
                if (selectALlClicked) {

                    selectALlClicked = false
                    imgSelectAllHourPerWeek.setImageResource(R.drawable.checkbox_gray)
                }

            }
        }
    }

    /**
     *Method to get the selected hour rates
     */
    private fun getFilteredHourWeek(): ArrayList<HoursPerWeek> {

        if (!checkedItems.isEmpty()) {
            checkedItems.clear()
        }

        for (i in 0..hoursWeek.size - 1) {
            if (hoursWeek.get(i).isClicked) {
                checkedItems.add(hoursWeek[i])

            }
        }
        return checkedItems
    }

    private fun addHoursRate() {

        for (i in 1..24) {

            hoursWeek.add(HoursPerWeek(i, false))
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
    }

}
