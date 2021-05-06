package com.partime.user.activities

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.*
import com.partime.user.Constants.Constants
import com.partime.user.Listeners.*
import com.partime.user.R
import com.partime.user.Responses.CityNames
import com.partime.user.Responses.CountryMessage
import com.partime.user.Responses.StateMessage
import com.partime.user.helpers.PermissionsUtil
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import kotlinx.android.synthetic.main.activity_location_filter.*
import java.util.*
import kotlin.collections.ArrayList


class LocationFilterActivity : BaseActivity(), View.OnClickListener {


    var countryFilters = ArrayList<CountryMessage>()
    private var filteredCountryList: FilteredCountryAdapter? = null
    private var countryAdapter: CountryListAdapter? = null

    var countrySelected: ArrayList<String> = ArrayList()

    var cityFilters = ArrayList<CityNames>()
    private var filteredCityAdapter: FiltereCityAdapter? = null
    //var citySelected=ArrayList<CityNames>()
    private var cityAdapter: CityListAdapter? = null

    var stateFilters = ArrayList<StateMessage>()
    private var filteredStateAdapter: FilteredStateAdapter? = null
    var listState: ArrayList<String> = ArrayList()
    private var stateAdapter: StateListAdapter? = null

    var address = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_filter)

        AppValidator.rotateBackArrow(imgBackLocationFilters, this@LocationFilterActivity)

        //get pre selected current address

        //get current
        if ((!appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString()).isNullOrBlank() &&
                    !appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString()).isNullOrEmpty()) ||
            (!appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString()).isNullOrEmpty() &&
                    !appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString()).isNullOrBlank())
        ) {

            if (!appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString()).equals("") ||
                !appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString()).equals("")
            ) {

                llLocationAddText.visibility = View.VISIBLE

                switchAutoDetectLocation.isChecked = true

                txtAddressLocationOne.text =
                    appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString())
                txtAddressLocationTwo.text =
                    appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString())

            }
        } else {

            switchAutoDetectLocation.isChecked = false

            llLocationAddText.visibility = View.GONE
        }

        getSelectedCountries()

        getSelectedStates()

        getSelectedCities()

        if (switchAutoDetectLocation.isChecked) {

            disableClicks()
        } else {

            enableClikcs()
        }

        // if auto location not changed........
        if (llLocationAddText.visibility == View.VISIBLE) {

            address.clear()
            if (!txtAddressLocationOne.text.isNullOrBlank() && !txtAddressLocationOne.text.isNullOrEmpty()) {
                address.addAll(txtAddressLocationOne.text.split(","))
                appPrefence.setString(
                    AppPrefence.SharedPreferncesKeys.currentAddOne.toString(),
                    txtAddressLocationOne.text.toString()
                )
            }
            if (!txtAddressLocationTwo.text.isNullOrBlank() && !txtAddressLocationTwo.text.isNullOrEmpty()) {
                address.addAll(txtAddressLocationTwo.text.split(","))
                appPrefence.setString(
                    AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(),
                    txtAddressLocationTwo.text.toString()
                )
            }
        }

        imgBackLocationFilters.setOnClickListener(this)
        llCountryFilter.setOnClickListener(this)
        llStateFilter.setOnClickListener(this)
        llCityFilter.setOnClickListener(this)
        txtResetLocationFilters.setOnClickListener(this)

        switchAutoDetectLocation.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                disableClicks()

                autoDetectLocation()
            } else {
                llLocationAddText.visibility = View.GONE
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString(), "")
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(), "")
                enableClikcs()
            }
        }

    }

    /*
       Method to get pre selected cities
        */
    private fun getSelectedCities() {

        if (intent.getSerializableExtra(Constants.CITIES_SELECTED) != null) {

            var selectedList = ArrayList<CityNames>()
            selectedList.addAll(intent.getSerializableExtra(Constants.CITIES_SELECTED) as ArrayList<CityNames>)

            cityFilters.clear()
            cityFilters.addAll(selectedList)
            cities(selectedList)
        }
    }

    /*
       Method to get pre selected states
        */
    private fun getSelectedStates() {

        if (intent.getSerializableExtra(Constants.STATES_SELECTED) != null) {

            var selectedList = ArrayList<String>()
            selectedList.addAll(intent.getSerializableExtra(Constants.STATES_SELECTED) as ArrayList<String>)

            statesSelected(selectedList)
        }
    }

    /*
       Method to pre selected country
        */
    private fun getSelectedCountries() {

        if (intent.getSerializableExtra(Constants.COUNTRIES_SELECTED) != null) {

            var selectedList = ArrayList<String>()
            selectedList.addAll(intent.getSerializableExtra(Constants.COUNTRIES_SELECTED) as ArrayList<String>)

            countriesSelected(selectedList)
        }
    }

    /*
       Method to enable clicks
        */
    private fun enableClikcs() {

        appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(), "")

        address.clear()

        llLocationAddText.visibility = View.GONE
        llCountryFilter.isEnabled = true
        llStateFilter.isEnabled = true
        llCityFilter.isEnabled = true

    }

    /*
   Method to disable clicks
    */
    private fun disableClicks() {

        llLocationAddText.visibility = View.VISIBLE
        llCountryFilter.isEnabled = false
        llStateFilter.isEnabled = false
        llCityFilter.isEnabled = false

        countryFilters.clear()
        countrySelected.clear()
        if (filteredCityAdapter != null) {
            filteredCityAdapter!!.notifyDataSetChanged()
        }
        recyclerFilterCountries.visibility = View.GONE

        stateFilters.clear()
        listState.clear()
        if (filteredStateAdapter != null) {
            filteredStateAdapter!!.notifyDataSetChanged()
        }
        recyclerFilterStates.visibility = View.GONE

        //citySelected.clear()
        cityFilters.clear()
        if (filteredCityAdapter != null) {
            filteredCityAdapter!!.notifyDataSetChanged()
        }
        recyclerFilterCities.visibility = View.GONE
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        var intent = intent

        if (countrySelected != null) {
            intent.putExtra(Constants.COUNRTY, countrySelected)
        }
        if (listState != null) {
            intent.putExtra(Constants.STATE, listState)
        }
        if (cityFilters != null) {
            //intent.putExtra(Constants.CITY, citySelected)
            intent.putExtra(Constants.CITY, cityFilters)
        }
        if (address != null) {

            intent.putExtra(Constants.CURRENT_ADDRESS, address)
        }
        setResult(105, intent)
        finish()
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackLocationFilters -> onBackPressed()

            llCountryFilter -> {
                val intent = Intent(this@LocationFilterActivity, CountryFilterActivity::class.java)
                intent.putExtra(Constants.COUNTRY_SELECTED, countrySelected.joinToString())
                startActivityForResult(intent, 81)
            }
            llStateFilter -> {

                if (countrySelected.size > 0) {
                    val intent = Intent(this@LocationFilterActivity, StateFilterActivity::class.java)
                    intent.putExtra(Constants.SELECTED_COUNTRY, countrySelected)
                    intent.putExtra(Constants.STATE_SELECTED, listState)
                    startActivityForResult(intent, 91)
                } else {

                    //toast..........
                    Toast.makeText(this@LocationFilterActivity, R.string.select_country, Toast.LENGTH_LONG).show()
                }

            }
            llCityFilter -> {

                if (listState.size > 0) {
                    val intent = Intent(this@LocationFilterActivity, CityFilterActivity::class.java)
                    intent.putExtra(Constants.SELECTED_STATE, listState)
                    intent.putExtra(Constants.CITY_SELECTED, cityFilters)
                    startActivityForResult(intent, 111)
                } else {

                    //toast..........
                    Toast.makeText(this@LocationFilterActivity, R.string.select_state, Toast.LENGTH_LONG).show()
                }

            }

            txtResetLocationFilters -> {
                //to reset location filters
                disableClicks()

                //to clear autodetection data.......
                llLocationAddText.visibility = View.GONE
                switchAutoDetectLocation.isChecked = false

                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString(), "")
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(), "")

            }
        }

    }


    /*
    Method to get the current location
     */
    private fun autoDetectLocation() {

        var progressBar = ProgressBarUtil().showProgressDialog(this)

        PermissionsUtil.askPermission(
            this@LocationFilterActivity, PermissionsUtil.LOCATION
        ) { isGranted ->
            if (isGranted) {
                requestLocation(object : LocationListener,
                    com.google.android.gms.location.LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        getCurrentLocation(location)
                        ProgressBarUtil().hideProgressDialog(progressBar)

                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {
                    }

                })

            } else {
                Toast.makeText(
                    this@LocationFilterActivity,
                    R.string.no_location_recorded,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    /* Method to check if permissions granted*/

    private fun getCurrentLocation(location: Location?) {
        if (location != null) {

            //address from latitude and longitutes
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(this, Locale.getDefault())

            try {

                addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                address = addresses[0].getAddressLine(0).split(",") as ArrayList<String>
               var tempCountry=" "
                if(address[5].length>7){
                    tempCountry=address[5].substring(0, address[5].length - 7)
                }else{
                    tempCountry=address[5]
                }
                txtAddressLocationOne.text = address[0].substring(0, address[0].length) + "," + address[1]
                txtAddressLocationTwo.text = address[2].substring(
                    1,
                    address[2].length
                ) + "," + address[3] + "," + address[4] + "," + tempCountry
                llLocationAddText.visibility = View.VISIBLE
                switchAutoDetectLocation.isChecked = true

                if (txtAddressLocationOne.text.isNullOrBlank() && !txtAddressLocationOne.text.isNullOrEmpty()) {
                    appPrefence.setString(
                        AppPrefence.SharedPreferncesKeys.currentAddOne.toString(),
                        txtAddressLocationOne.text.toString()
                    )
                }
                if (txtAddressLocationTwo.text.isNullOrBlank() && !txtAddressLocationTwo.text.isNullOrEmpty()) {
                    appPrefence.setString(
                        AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(),
                        txtAddressLocationTwo.text.toString()
                    )
                }

            } catch (e: Exception) {
                e.cause
                Toast.makeText(this@LocationFilterActivity, R.string.service_not_available, Toast.LENGTH_SHORT)
                    .show()
               // Log.e("LOCERR",e.getMessage())
                llLocationAddText.visibility = View.GONE
                switchAutoDetectLocation.isChecked = false

                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString(), "")
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(), "")
            }
        } else {
            llLocationAddText.visibility = View.GONE
            switchAutoDetectLocation.isChecked = false
            appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString(), "")
            appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(), "")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 91 && resultCode == 91) {

            if (data!!.getSerializableExtra("stateFilters") != null) {

                stateFilters = data.getSerializableExtra("stateFilters") as ArrayList<StateMessage>

                states(stateFilters)
            }

        }
        if (requestCode == 81 && resultCode == 81) {


            if (data!!.getSerializableExtra("countryFilters") != null) {

                countryFilters = data.getSerializableExtra("countryFilters") as ArrayList<CountryMessage>

                countries(countryFilters)
            }


        }
        if (requestCode == 111 && resultCode == 111) {

            if (data!!.getSerializableExtra("cityFilters") != null) {

                cityFilters.clear()
                cityFilters = data.getSerializableExtra("cityFilters") as ArrayList<CityNames>

                cities(cityFilters)
            }

        }

    }

    /*Method to set the selected cities*/
    private fun cities(cityFilter: ArrayList<CityNames>) {

        if (!cityFilter.isEmpty()) {


            filteredCityAdapter = FiltereCityAdapter(cityFilter, this@LocationFilterActivity, object : SelectedCity {
                override fun selectedCityListener(size: Int) {

                    if (size > 0) {

                        filterCity()
                    } else {

                        cityFilters.clear()
                    }
                }

            })

            recyclerFilterCities.visibility = View.VISIBLE
            recyclerFilterCities!!.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterCities!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterCities!!.adapter = filteredCityAdapter
        }
        if (cityFilters.size > 0) {
            filterCity()

        }
    }

    /*
       Method to set the selected countries
        */
    private fun countries(countryFilter: ArrayList<CountryMessage>) {

        if (!countryFilter.isEmpty()) {

            filteredCountryList =
                FilteredCountryAdapter(countryFilter, this@LocationFilterActivity, object : SelectedCountry {
                    override fun selectedCountryListener(size: Int) {

                        if (size > 0) {
                            filterCountries(size)
                        } else {

                            countryFilters.clear()
                            countrySelected.clear()
                            recyclerFilterCountries.visibility = View.GONE

                            // remove corresponding states
                            stateFilters.clear()
                            listState.clear()
                            recyclerFilterStates.visibility = View.GONE

                            //remove corresponding city
                            cityFilters.clear()
                            //citySelected.clear()
                            recyclerFilterCities.visibility = View.GONE

                        }
                    }

                })

            recyclerFilterCountries.visibility = View.VISIBLE
            recyclerFilterCountries!!.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterCountries!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterCountries!!.adapter = filteredCountryList

            if (countryFilters.size > 0) {
                filterCountries(countryFilters.size)

            }
        }

    }

    /*
       Method to set selected states
        */
    private fun states(stateFilter: ArrayList<StateMessage>) {

        if (!stateFilter.isEmpty()) {

            filteredStateAdapter =
                FilteredStateAdapter(stateFilter, this@LocationFilterActivity, object : SelectedList {
                    override fun selectedList(size: Int, deletedState: String) {
                        if (size > 0) {
                            filterStates(size)

                            //remove coreesponding city
                            removeCities(deletedState)

                        } else {

                            stateFilters.clear()
                            listState.clear()
                            recyclerFilterStates.visibility = View.GONE

                            cityFilters.clear()
                            //citySelected.clear()
                            recyclerFilterCities.visibility = View.GONE

                        }

                    }

                })

            recyclerFilterStates.visibility = View.VISIBLE
            recyclerFilterStates!!.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterStates!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterStates!!.adapter = filteredStateAdapter



            if (stateFilters.size > 0) {
                filterStates(stateFilters.size)

            }

        }
    }

    /*
    Method to remove the city corresponding to state
     */
    private fun removeCities(deletedState: String) {

        var size = cityFilters.size

        for (i in 0..size - 1) {
            if (cityFilters.get(i).stateName != null) {
                if (cityFilters.get(i).stateName.equals(deletedState)) {
                    cityFilters.removeAt(i)
                    break
                }
            }

        }
        if (filteredCityAdapter != null) {
            filteredCityAdapter!!.notifyDataSetChanged()
        }

    }

    /*
    Method to set the pre selected countries
     */
    private fun countriesSelected(selectedList: ArrayList<String>) {

        if (!selectedList.isEmpty()) {

            countryAdapter = CountryListAdapter(selectedList, this@LocationFilterActivity, object : Country {
                override fun country(size: Int) {
                    if (size > 0) {
                        countrySelected.clear()
                        countrySelected.addAll(selectedList)
                    } else {

                        countrySelected.clear()
                    }
                }

            })
            recyclerFilterCountries.visibility = View.VISIBLE
            recyclerFilterCountries!!.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterCountries!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterCountries!!.adapter = countryAdapter

            if (selectedList.size > 0) {
                countrySelected.clear()
                countrySelected.addAll(selectedList)

            }
        }

    }

    /*
    Method to set the pre selected states
     */
    private fun statesSelected(selectedList: ArrayList<String>) {

        if (!selectedList.isEmpty()) {

            stateAdapter = StateListAdapter(selectedList, this@LocationFilterActivity, object : StateListener {
                override fun state(size: Int, states: String) {
                    if (size > 0) {
                        listState.clear()
                        listState.addAll(selectedList)
                    } else {

                        listState.clear()
                    }
                }

            })
            recyclerFilterStates.visibility = View.VISIBLE
            recyclerFilterStates!!.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterStates!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterStates!!.adapter = stateAdapter
            if (selectedList.size > 0) {
                listState.clear()
                listState.addAll(selectedList)

            }
        }

    }

    /*
    Method to save the selected states
     */
    fun filterStates(size: Int) {
        listState.clear()
        for (i in 0..size - 1) {

            listState.add(stateFilters.get(i).state)

        }
    }

    /*
    Method to save the selectetd country
     */
    fun filterCountries(size: Int) {
        countrySelected.clear()

        for (i in 0..size - 1) {

            countrySelected.add(countryFilters.get(i).country)
        }
    }

    fun filterCity() {
        /* citySelected.clear()
         citySelected.addAll(cityFilters)
    */
    }

}
