package com.partime.user.Listeners

import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

interface SelectedList {

    fun selectedList(size: Int, position: String)
}

interface SelectedCountry {

    fun selectedCountryListener(size: Int)
}

interface SelectedCity {

    fun selectedCityListener(size: Int)
}

interface HoursRateListener {

    fun onSelectNone(position: Int, txtCheckBox: ImageView, size: Int)
}

interface HoursWeekListener {

    fun onSelect(position: Int, txtCheckBox: ImageView)

}

interface SelectedIndustry {

    fun selectedIndustryListener(size: Int)
}

interface SelectedCompany {

    fun selectedCompanyListener(size: Int)
}

interface SelectedJobTitle {

    fun selectedJobTitleListener(size: Int)
}

interface SelectedHourRate {

    fun selectedHourRateListener(size: Int)
}

interface SelectedHourWeek {

    fun selectedHourWeekListener(size: Int)
}

interface StateListener {

    fun state(size: Int, states: String)
}

interface Country {

    fun country(size: Int)
}

interface City {

    fun city(size: Int)
}

/*interface CityCheckedListener {

    fun onCityChecked(position: Int, txtCheckBox: ImageView)
}*/

interface SetCityAdpater {

    fun setCity(recyclerCity: RecyclerView, position: Int)
}

interface MessageListListener {

    fun onMessageListClick(receiverId: Int, position: Int)
}

interface TaskClickListener {

    fun onTaskCikcListener(taskId: Int)
}

interface TaskButtonListener {

    fun onTaskButtonClick(taskId: Int, status: String, button: Int)
}

interface WifiListener {

    fun onWifiNetworkSelect(ssid: String)
}

interface MonthClickListener {

    fun onMonthClick(month: String, year: String, monthNo: Int)
}

interface WeekClickListener {

    fun onWeekClick(position: Int)
}

interface WeekIsClickedListener{

    fun onWeekIsClick(position: Int)
}