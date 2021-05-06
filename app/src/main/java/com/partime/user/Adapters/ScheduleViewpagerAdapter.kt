package com.partime.user.Adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.partime.user.Fragments.CalendarFragment
import com.partime.user.Fragments.CalendarViewFragment
import com.partime.user.Fragments.TaskListFragment
import com.partime.user.R

class ScheduleViewpagerAdapter(
    fm: FragmentManager,
    context: Context,
    tabLayout: TabLayout,
    flag: Int
) : FragmentPagerAdapter(fm) {

    private var context: Context
    private var tabLayout: TabLayout
    private var flag: Int


    init {

        this.flag = flag
        this.tabLayout = tabLayout
        this.context = context
    }

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = TaskListFragment()
        } else if (position == 1) {

            //for redirecting to second tab in chat notification
            if(flag==1){
                tabLayout.getTabAt(1)?.select()
            }
            fragment = CalendarViewFragment()
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) {
            title = context.getString(R.string.task_list)
        } else if (position == 1) {
            title = context.getString(R.string.calendar_view)
        }
        return title
    }

}