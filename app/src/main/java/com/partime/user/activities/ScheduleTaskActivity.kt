package com.partime.user.activities

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.ScheduleViewpagerAdapter
import com.partime.user.R
import com.partime.user.helpers.Utilities
import com.partime.user.uicomman.BaseActivity
import kotlinx.android.synthetic.main.activity_schedule_task.*


class ScheduleTaskActivity : BaseActivity(), View.OnClickListener {


    private var viewpageradapter: ScheduleViewpagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_task)

        AppValidator.rotateBackArrow(imgBackScheduleTask, this@ScheduleTaskActivity)

        var flag=intent.getIntExtra("SCHEDULE_FLAG",0)

        viewpageradapter = ScheduleViewpagerAdapter(supportFragmentManager, this@ScheduleTaskActivity,tabLayoutSchedduleTask,flag)
        viewPagerScheduleTask.adapter = viewpageradapter

        viewPagerScheduleTask.setPagingEnabled(false)

        tabLayoutSchedduleTask.setupWithViewPager(this.viewPagerScheduleTask) //Binding ViewPager with TabLayout

        viewPagerScheduleTask.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {

                Utilities.hideKeyboard(this@ScheduleTaskActivity)
            }

        })

        imgBackScheduleTask.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackScheduleTask -> {

                onBackPressed()
            }
        }
    }

}
