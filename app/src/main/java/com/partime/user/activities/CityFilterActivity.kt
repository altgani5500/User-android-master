package com.partime.user.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.CitiesAdapter
import com.partime.user.Adapters.CityAdapter
import com.partime.user.Constants.Constants
import com.partime.user.Listeners.SetCityAdpater
import com.partime.user.R
import com.partime.user.Responses.CityMessage
import com.partime.user.Responses.CityNames
import com.partime.user.Responses.CityResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_city_filter.*
import kotlinx.android.synthetic.main.activity_state_filter.*
import retrofit2.Call
import retrofit2.Callback

class CityFilterActivity : BaseActivity(), View.OnClickListener {

    private var cityAdapter: CityAdapter? = null
    var cityList = ArrayList<CityMessage>()
    var cities = ArrayList<CityNames>()

    private var adapter: CitiesAdapter? = null
    var tempList = ArrayList<CityNames>()

    var selectedState: ArrayList<String> = ArrayList()

    var checkedItems = ArrayList<CityNames>()
    var map = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_filter)

        AppValidator.rotateBackArrow(imgBackCityFilters, this@CityFilterActivity)

        //to get the selected states
        selectedState.addAll(intent.getSerializableExtra(Constants.SELECTED_STATE) as ArrayList<String>)

        valiadteNet(selectedState)

        //search city
        edtSearchCity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(edit: Editable?) {

                if (edit?.length!! >= 3) {

                    getCity(selectedState, edit.toString().trim())

                } else if (edit.isEmpty()) {

                    getCity(selectedState, "")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        btnApplyCity.setOnClickListener(this)
        btnRetryCity.setOnClickListener(this)
        imgBackCityFilters.setOnClickListener(this)
    }

    private fun valiadteNet(selectedState: ArrayList<String>) {

        if (AppValidator.isInternetAvailable(this@CityFilterActivity)) {
            //get city list
            getCity(selectedState, "")
        } else {

            llErrorCity.visibility = View.VISIBLE
            recyclerCityFilter.visibility = View.GONE
        }
    }

    /*
    Method to get the city list
     */
    private fun getCity(selectedState: ArrayList<String>, search: String) {

        var progressBar = ProgressBarUtil().showProgressDialog(this@CityFilterActivity)

        map.put("state", selectedState.joinToString().replace(", ", ","))

        if (!search.isNullOrBlank() || !search.isNullOrEmpty()) {

            map.put("search", search)

        }

        val apiService = ApiService.init()
        val call =
            apiService.getCityList(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()), map)
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<CityResponse> {
            override fun onResponse(
                call: Call<CityResponse>,
                response: retrofit2.Response<CityResponse>?
            ) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                if (response != null) {

                    if (response.body()?.code == 200) {

                        if (!cityList.isEmpty()) {
                            cityList.clear()
                        }

                        cityList.addAll(response.body()?.message as ArrayList<CityMessage>)

                        cities.clear()

                        cityList.forEach {

                            cities.addAll(it.cityList)
                        }

                        //set city adatper with states
                        cityAdapter = CityAdapter(cityList, this@CityFilterActivity, object : SetCityAdpater {
                            override fun setCity(recyclerCity: RecyclerView, position: Int) {

                                //  cities.addAll(cityList.get(position).cityList)
                                //set the cities......
                                tempList.clear()
                                tempList.addAll(cityList.get(position).cityList)
                                adapter = CitiesAdapter(tempList, this@CityFilterActivity)
                                recyclerCity.layoutManager = LinearLayoutManager(this@CityFilterActivity)
                                recyclerCity.adapter = adapter


                            }

                        })

                        //the recycler view.....
                        llErrorCity.visibility = View.GONE
                        recyclerCityFilter.visibility = View.VISIBLE
                        recyclerCityFilter!!.layoutManager =
                            LinearLayoutManager(
                                this@CityFilterActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        recyclerCityFilter!!.itemAnimator = DefaultItemAnimator()
                        recyclerCityFilter!!.adapter = cityAdapter

                        ///set selected city
                        setSelectedCity(cities)

                    } else {

                        Toast.makeText(
                            this@CityFilterActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {

                Toast.makeText(this@CityFilterActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                llErrorState.visibility = View.VISIBLE
                recyclerStateFilter.visibility = View.GONE

            }

        })
    }

    /*
    Method to set selected city.....
     */
    private fun setSelectedCity(temp: ArrayList<CityNames>) {

        var selectedCity = ArrayList<CityNames>()
        selectedCity.addAll(intent.getSerializableExtra(Constants.CITY_SELECTED) as ArrayList<CityNames>)
        if (selectedCity != null) {

            for (i in 0..temp.size - 1) {

                for (ii in 0..selectedCity.size - 1) {

                    if (temp.get(i).city == selectedCity.get(ii).city) {

                        temp.get(i).isClicked = true
                    }

                }

            }
        }
        if (cityAdapter != null) {
            cityAdapter!!.notifyDataSetChanged()

        }
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackCityFilters -> onBackPressed()

            txtResetCity -> {
                for (i in 0..cities.size - 1) {

                    cities.get(i).isClicked = false
                }
                if (cityAdapter != null) {
                    cityAdapter!!.notifyDataSetChanged()

                }
            }
            btnApplyCity -> {

                var city = getCityFilters()
                val intent = intent
                if (city != null) {
                    intent.putExtra("cityFilters", city)
                }
                setResult(111, intent)
                finish()
            }

            btnRetryCity -> {

                llErrorCity.visibility = View.GONE
                valiadteNet(selectedState)
            }
        }
    }

    /*
    Method to get selected cities...
     */
    private fun getCityFilters(): ArrayList<CityNames> {

        for (i in 0..cities.size - 1) {
            if (cities.get(i).isClicked) {
                checkedItems.add(cities[i])

            }
        }
        return checkedItems
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
    }

}
