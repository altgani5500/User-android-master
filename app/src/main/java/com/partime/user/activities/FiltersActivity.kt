package com.partime.user.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.*
import com.partime.user.Constants.Constants
import com.partime.user.Listeners.*
import com.partime.user.R
import com.partime.user.Responses.*
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import kotlinx.android.synthetic.main.activity_filters.*

class FiltersActivity : BaseActivity(), View.OnClickListener {

    var industryFilters = ArrayList<IndustryMessage>()
    private var filteredIndustryAdapter: FilteredIndutriesAdapter? = null

    var jobFilters = ArrayList<JobTitleMessage>()
    private var filteredJobAdapter: FilteredJobAdapter? = null

    var companyFilters = ArrayList<ComapnyMessage>()
    private var filteredCompanyAdapter: FilteredCompanyAdapter? = null

    var hourRateFilters = ArrayList<HoursRate>()
    private var filteredHourRateAdapter: FilteredHourRateAdapter? = null

    var hourWeekFilters = ArrayList<HoursPerWeek>()
    private var filteredHourWeekAdapter: FilteredHoursPerWeek? = null

    var countryList: ArrayList<String> = ArrayList()
    private var filteredCountryAdapter: CountryListAdapter? = null

    var stateList: ArrayList<String> = ArrayList()
    private var filteredStateAdapter: StateListAdapter? = null

    var cityList = ArrayList<CityNames>()
    private var filteredCityAdapter: CityListAdapter? = null


    var creditedClicked = false
    var nonCreditedClicked = false

    var currentAddress = ArrayList<String>()

    var industries = StringBuffer()
    var companies = StringBuffer()
    var jobTitles = StringBuffer()
    var hourWeeks = StringBuffer()
    var hourRates = StringBuffer()
    var jobTypes = StringBuffer()
    var countries: String? = null
    var states = StringBuffer()
    var cities = StringBuffer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        AppValidator.rotateBackArrow(imgBackFilters, this@FiltersActivity)


        //get current
        if (!appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString()).isNullOrBlank() &&
            !appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString()).isNullOrEmpty() &&
            !appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString()).isNullOrEmpty() &&
            !appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString()).isNullOrBlank()
        ) {

            if (!appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString()).equals(
                    ""
                ) &&
                !appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString()).equals(
                    ""
                )
            ) {

                llFilterCurrentAddressData.visibility = View.VISIBLE

                txtFilterCurrAddressLocationOne.text =
                    appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString())
                txtFilterCurrAddressLocationTwo.text =
                    appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString())

            }
        } else {
            llFilterCurrentAddressData.visibility = View.GONE
        }


        //get pre selected industries
        if (intent.getSerializableExtra(Constants.INDUSTRIES) != null) {

            industryFilters.clear()
            industryFilters.addAll(intent.getSerializableExtra(Constants.INDUSTRIES) as ArrayList<IndustryMessage>)

            if (industryFilters.size > 0) {

                industries(industryFilters)
            }
        }
        //get pre selected company
        if (intent.getSerializableExtra(Constants.COMPANY) != null) {

            companyFilters.clear()
            companyFilters.addAll(intent.getSerializableExtra(Constants.COMPANY) as ArrayList<ComapnyMessage>)

            if (companyFilters.size > 0) {

                companies(companyFilters)
            }
        }
        //get pre selected jobTitles
        if (intent.getSerializableExtra(Constants.JOB_TITLE) != null) {

            jobFilters.clear()
            jobFilters.addAll(intent.getSerializableExtra(Constants.JOB_TITLE) as ArrayList<JobTitleMessage>)

            if (jobFilters.size > 0) {

                jobTitles(jobFilters)
            }
        }
        if (intent.getSerializableExtra(Constants.COUNTRIES) != null) {

            countryList.clear()
            countryList.addAll(intent.getSerializableExtra(Constants.COUNTRIES) as ArrayList<String>)

            if (countryList.size > 0) {

                selectedCountry(countryList)
            }
        }
        ///get pre selected states
        if (intent.getSerializableExtra(Constants.STATES) != null) {

            stateList.clear()
            stateList.addAll(intent.getSerializableExtra(Constants.STATES) as ArrayList<String>)

            if (stateList.size > 0) {

                selectedState(stateList)
            }
        }
        // get the pre selected city
        if (intent.getSerializableExtra(Constants.CITIES) != null) {

            cityList.clear()
            cityList.addAll(intent.getSerializableExtra(Constants.CITIES) as ArrayList<CityNames>)

            if (cityList.size > 0) {

                selectedCity(cityList)
            }
        }
        // get the pre selected hour rates
        if (intent.getSerializableExtra(Constants.HOURS_RATE) != null) {

            hourRateFilters.clear()
            hourRateFilters.addAll(intent.getSerializableExtra(Constants.HOURS_RATE) as ArrayList<HoursRate>)

            if (hourRateFilters.size > 0) {

                hourPaidRate(hourRateFilters)
            }
        }
        //get the pre selected hour weeks
        if (intent.getSerializableExtra(Constants.HOURS_WEEK) != null) {

            hourWeekFilters.clear()
            hourWeekFilters.addAll(intent.getSerializableExtra(Constants.HOURS_WEEK) as ArrayList<HoursPerWeek>)

            if (hourWeekFilters.size > 0) {

                hourPerWeek(hourWeekFilters)
            }
        }
        // get pre selected choices
        if (intent.getStringExtra(Constants.JOB_TYPE) != null) {

            var jobType = intent.getStringExtra(Constants.JOB_TYPE)
            if (jobType.equals("2")) {

                creditedClicked = true
                imgCreditedFilter.setImageResource(R.drawable.checkbox_blue)
            } else if (jobType.equals("1")) {

                nonCreditedClicked = true
                imgNonCreditedFilter.setImageResource(R.drawable.checkbox_blue)
            } else if (jobType.equals("1,2")) {

                creditedClicked = true
                imgCreditedFilter.setImageResource(R.drawable.checkbox_blue)
                nonCreditedClicked = true
                imgNonCreditedFilter.setImageResource(R.drawable.checkbox_blue)
            } else {


                creditedClicked = false
                imgCreditedFilter.setImageResource(R.drawable.checkbox_gray)
                nonCreditedClicked = false
                imgNonCreditedFilter.setImageResource(R.drawable.checkbox_gray)
            }
        } else {

            creditedClicked = false
            imgCreditedFilter.setImageResource(R.drawable.checkbox_gray)
            nonCreditedClicked = false
            imgNonCreditedFilter.setImageResource(R.drawable.checkbox_gray)
        }


        imgBackFilters.setOnClickListener(this)
        llIndustryFilter.setOnClickListener(this)
        llJobTitleFilter.setOnClickListener(this)
        llCompanyFilter.setOnClickListener(this)
        llLocationFilter.setOnClickListener(this)
        llHoursPerWeekFilter.setOnClickListener(this)
        llHourPaidFilter.setOnClickListener(this)
        rlCreditedFilter.setOnClickListener(this)
        rlNonCreditedFilter.setOnClickListener(this)
        btnApplyFilter.setOnClickListener(this)
        imgFilterCrossCurrentLocation.setOnClickListener(this)
        buttonResetFilters.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackFilters -> onBackPressed()

            llIndustryFilter -> {
                val intent = Intent(this@FiltersActivity, IndustryFilterActivity::class.java)
                if (industryFilters != null) {

                    intent.putExtra(Constants.INDUSTRIES_SELECTED, industryFilters)
                }
                startActivityForResult(intent, 51)
            }
            llJobTitleFilter -> {
                val intent = Intent(this@FiltersActivity, JobTitleFilterActivity::class.java)
                if (jobFilters != null) {

                    intent.putExtra(Constants.JOB_TITLE_SELECTED, jobFilters)
                }
                startActivityForResult(intent, 61)
            }
            llCompanyFilter -> {
                val intent = Intent(this@FiltersActivity, CompanyFilterActivity::class.java)
                if (companyFilters != null) {

                    intent.putExtra(Constants.COMPANY_SELECTED, companyFilters)
                }
                startActivityForResult(intent, 71)
            }
            llLocationFilter -> {
                val intent = Intent(this@FiltersActivity, LocationFilterActivity::class.java)
                if (countryList != null) {

                    intent.putExtra(Constants.COUNTRIES_SELECTED, countryList)
                }
                if (stateList != null) {

                    intent.putExtra(Constants.STATES_SELECTED, stateList)
                }
                if (cityList != null) {

                    intent.putExtra(Constants.CITIES_SELECTED, cityList)
                }
                startActivityForResult(intent, 105)
            }
            llHoursPerWeekFilter -> {

                val intent = Intent(this@FiltersActivity, HoursPerWeekActivity::class.java)
                if (hourWeekFilters != null) {

                    intent.putExtra(Constants.HOURS_WEEK_SELECTED, hourWeekFilters)
                }
                startActivityForResult(intent, 121)
            }
            llHourPaidFilter -> {
                val intent = Intent(this@FiltersActivity, HourRateActivity::class.java)
                if (hourRateFilters != null) {

                    intent.putExtra(Constants.HOURS_RATE_SELECTED, hourRateFilters)
                }
                startActivityForResult(intent, 131)
            }
            rlCreditedFilter -> {

                if (creditedClicked) {

                    creditedClicked = false
                    imgCreditedFilter.setImageResource(R.drawable.checkbox_gray)
                } else {

                    creditedClicked = true
                    imgCreditedFilter.setImageResource(R.drawable.checkbox_blue)
                }

            }
            rlNonCreditedFilter -> {

                if (nonCreditedClicked) {

                    nonCreditedClicked = false
                    imgNonCreditedFilter.setImageResource(R.drawable.checkbox_gray)
                } else {

                    nonCreditedClicked = true
                    imgNonCreditedFilter.setImageResource(R.drawable.checkbox_blue)
                }
            }
            btnApplyFilter -> {

                addJobTypes()

                //send the selected feilds
                //var intent = intent
                val intentFilter = Intent(Constants.BROADCAST_FILTER_MAP)
                intentFilter.putExtra(Constants.FILTERS, "TRUE")

                intentFilter.putExtra(Constants.INDUSTRY_FILTERS, industries.toString())
                intentFilter.putExtra("Industry", industryFilters)
                intentFilter.putExtra(Constants.JOB_TITLE_FILTERS, jobTitles.toString())
                intentFilter.putExtra("Job_title", jobFilters)
                intentFilter.putExtra(Constants.COMPANY_FILTERS, companies.toString())
                intentFilter.putExtra("Company", companyFilters)
                if (countries != null) {
                    intentFilter.putExtra(Constants.COUNTRIES_FILTERS, countries)
                }
                intentFilter.putExtra("Countries", countryList)
                intentFilter.putExtra(Constants.STATES_FILTERS, states.toString())
                intentFilter.putExtra("States", stateList)
                //intent.putExtra(Constants.CITIES_FILTERS,cities.toString())
                intentFilter.putExtra("Cities", cityList)
                intentFilter.putExtra(Constants.HOURS_PER_WEEK, hourWeeks.toString())
                intentFilter.putExtra("HourWeek", hourWeekFilters)
                intentFilter.putExtra(Constants.HOURS_RATE_FILTERS, hourRates.toString())
                intentFilter.putExtra("HourRate", hourRateFilters)
                intentFilter.putExtra(Constants.JOBTYPE_FILTER, jobTypes.toString())
                intentFilter.putExtra(Constants.CURRENT_ADDRESS_SELECTED, "currentLocation")

                LocalBroadcastManager.getInstance(this@FiltersActivity).sendBroadcast(intentFilter)

                setResult(100, intentFilter)
                finish()
            }
            buttonResetFilters -> {

                llFilterCurrentAddressData.visibility = View.GONE
                currentAddress.clear()

                industryFilters.clear()
                industries.setLength(0)
                if (filteredIndustryAdapter != null) {
                    filteredIndustryAdapter!!.notifyDataSetChanged()
                }

                jobFilters.clear()
                jobTitles.setLength(0)
                if (filteredJobAdapter != null) {
                    filteredJobAdapter!!.notifyDataSetChanged()
                }

                companyFilters.clear()
                companies.setLength(0)
                if (filteredCompanyAdapter != null) {
                    filteredCompanyAdapter!!.notifyDataSetChanged()
                }

                hourRateFilters.clear()
                hourRates.setLength(0)
                if (filteredHourRateAdapter != null) {
                    filteredHourRateAdapter!!.notifyDataSetChanged()
                }

                hourWeekFilters.clear()
                hourWeeks.setLength(0)
                if (filteredHourWeekAdapter != null) {
                    filteredHourWeekAdapter!!.notifyDataSetChanged()
                }

                countryList.clear()
                countries = null
                if (filteredCountryAdapter != null) {
                    filteredCountryAdapter!!.notifyDataSetChanged()
                }
                txtFilterCountry.visibility = View.GONE
                recyclerFilterCountry.visibility = View.GONE

                stateList.clear()
                states.setLength(0)
                if (filteredStateAdapter != null) {
                    filteredStateAdapter!!.notifyDataSetChanged()
                }
                txtFilterState.visibility = View.GONE
                recyclerFilterStates.visibility = View.GONE

                cityList.clear()
                cities.setLength(0)
                if (filteredCityAdapter != null) {
                    filteredCityAdapter!!.notifyDataSetChanged()
                }
                txtFilterCity.visibility = View.GONE
                recyclerFilterCity.visibility = View.GONE

                nonCreditedClicked = false
                imgNonCreditedFilter.setImageResource(R.drawable.checkbox_gray)

                creditedClicked = false
                imgCreditedFilter.setImageResource(R.drawable.checkbox_gray)

                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString(), "")
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(), "")
            }
            imgFilterCrossCurrentLocation -> {

                llFilterCurrentAddressData.visibility = View.GONE
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString(), "")
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(), "")
                currentAddress.clear()
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 51 && requestCode == 51) {

            //get selected industries
            if (!industryFilters.isEmpty()) {

                industryFilters.clear()
            }
            industryFilters =
                data!!.getSerializableExtra("industryFilters") as ArrayList<IndustryMessage>

            if (industryFilters.size > 0) {

                industries(industryFilters)

            } else {

                if (filteredIndustryAdapter != null) {
                    filteredIndustryAdapter!!.notifyDataSetChanged()
                }
            }


        } else if (resultCode == 61 && requestCode == 61) {

            //get selected job titiles

            if (!jobFilters.isEmpty()) {
                jobFilters.clear()
            }

            jobFilters =
                data!!.getSerializableExtra("jobTitleFilters") as ArrayList<JobTitleMessage>

            if (jobFilters.size > 0) {

                jobTitles(jobFilters)

            } else {

                if (filteredJobAdapter != null) {
                    filteredJobAdapter!!.notifyDataSetChanged()
                }
            }
        } else if (resultCode == 71 && requestCode == 71) {

            //get selected companies

            if (!companyFilters.isEmpty()) {

                companyFilters.clear()

            }

            companyFilters =
                data!!.getSerializableExtra("companyFilters") as ArrayList<ComapnyMessage>

            if (companyFilters.size > 0) {

                companies(companyFilters)

            } else {

                if (filteredCompanyAdapter != null) {
                    filteredCompanyAdapter!!.notifyDataSetChanged()
                }
            }

        } else if (resultCode == 131 && requestCode == 131) {

            //get selected hour rates

            if (!hourRateFilters.isEmpty()) {

                hourRateFilters.clear()

            }

            hourRateFilters = data!!.getSerializableExtra("hourRateFilters") as ArrayList<HoursRate>

            if (hourRateFilters.size > 0) {

                hourPaidRate(hourRateFilters)
            } else {

                if (filteredHourRateAdapter != null) {
                    filteredHourRateAdapter!!.notifyDataSetChanged()

                }
            }

        } else if (resultCode == 121 && requestCode == 121) {

            //get selected hour weeks

            if (!hourWeekFilters.isEmpty()) {
                hourWeekFilters.clear()
            }

            hourWeekFilters =
                data!!.getSerializableExtra("companyHourWeek") as ArrayList<HoursPerWeek>

            if (hourWeekFilters.size > 0) {
                hourPerWeek(hourWeekFilters)
            } else {

                if (filteredHourWeekAdapter != null) {
                    filteredHourWeekAdapter!!.notifyDataSetChanged()
                }
            }
        } else if (resultCode == 105 && requestCode == 105) {

            //get selected current address

            if (!countryList.isEmpty()) {
                countryList.clear()
            }
            if (!stateList.isEmpty()) {
                stateList.clear()
            }
            if (!cityList.isEmpty()) {
                cityList.clear()
            }
            if (data!!.hasExtra(Constants.CURRENT_ADDRESS) && data.extras != null) {

                if (data.getSerializableExtra(Constants.CURRENT_ADDRESS) != null) {

                    currentAddress.clear()
                    currentAddress.addAll(data.getSerializableExtra(Constants.CURRENT_ADDRESS) as ArrayList<String>)
                    if (currentAddress.size > 0) {
                        var tempCountry = ""
                        if (currentAddress[5].length > 7) {
                            tempCountry =
                                currentAddress[5].substring(0, currentAddress[5].length - 7)
                        } else {
                            tempCountry = currentAddress[5]
                        }
                        txtFilterCurrAddressLocationOne.text =
                            currentAddress[0].substring(
                                0,
                                currentAddress[0].length
                            ) + "," + currentAddress[1]
                        txtFilterCurrAddressLocationTwo.text = currentAddress[2].substring(
                            1,
                            currentAddress[2].length
                        ) + "," + currentAddress[3] + "," + currentAddress[4] + "," + tempCountry


                        appPrefence.setString(
                            AppPrefence.SharedPreferncesKeys.currentAddOne.toString(),
                            txtFilterCurrAddressLocationOne.text.toString()
                        )
                        appPrefence.setString(
                            AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(),
                            txtFilterCurrAddressLocationTwo.text.toString()
                        )

                        llFilterCurrentAddressData.visibility = View.VISIBLE
                    } else {

                        llFilterCurrentAddressData.visibility = View.GONE

                        appPrefence.setString(
                            AppPrefence.SharedPreferncesKeys.currentAddOne.toString(),
                            ""
                        )
                        appPrefence.setString(
                            AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(),
                            ""
                        )

                    }

                }
            }

            if (data.getSerializableExtra(Constants.COUNRTY) != null) {

                //get selected countries
                countryList.addAll(data.getSerializableExtra(Constants.COUNRTY) as ArrayList<String>)

                if (countryList.size > 0) {

                    selectedCountry(countryList)
                    llFilterCurrentAddressData.visibility = View.GONE
                    currentAddress.clear()
                } else {
                    if (filteredCountryAdapter != null)
                        filteredCountryAdapter!!.notifyDataSetChanged()
                }
            }

            if (data.getSerializableExtra(Constants.STATE) != null) {

                //get selected states
                stateList.addAll(data.getSerializableExtra(Constants.STATE) as ArrayList<String>)
                if (stateList.size > 0) {

                    selectedState(stateList)
                    llFilterCurrentAddressData.visibility = View.GONE
                    currentAddress.clear()

                } else {
                    if (filteredStateAdapter != null)
                        filteredStateAdapter!!.notifyDataSetChanged()
                }
            }

            if (data.getSerializableExtra(Constants.CITY) != null) {

                //get selected city
                cityList.addAll(data.getSerializableExtra(Constants.CITY) as ArrayList<CityNames>)
                if (cityList.size > 0) {

                    selectedCity(cityList)
                    llFilterCurrentAddressData.visibility = View.GONE
                    currentAddress.clear()
                } else {
                    if (filteredCityAdapter != null)
                        filteredCityAdapter!!.notifyDataSetChanged()
                }
            }


        }
    }

    private fun selectedCity(city: ArrayList<CityNames>) {

        if (!city.isEmpty()) {

            filteredCityAdapter = CityListAdapter(city, this@FiltersActivity, object : City {
                override fun city(size: Int) {
                    if (size == 0) {
                        cityList.clear()
                        cities.setLength(0)
                        recyclerFilterCity.visibility = View.GONE
                        txtFilterCity.visibility = View.GONE

                    }
                }

            })

            txtFilterCity.visibility = View.VISIBLE

            recyclerFilterCity.visibility = View.VISIBLE
            recyclerFilterCity!!.layoutManager =
                StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterCity!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterCity!!.adapter = filteredCityAdapter

            addCity(city)
        }

    }

    /*
    Method to set selected states
     */
    private fun selectedState(state: ArrayList<String>) {

        if (!state.isEmpty()) {

            filteredStateAdapter =
                StateListAdapter(state, this@FiltersActivity, object : StateListener {
                    override fun state(size: Int, deletedState: String) {
                        if (size == 0) {

                            stateList.clear()
                            states.setLength(0)
                            recyclerFilterStates.visibility = View.GONE
                            txtFilterState.visibility = View.GONE

                            //remove city
                            cityList.clear()
                            cities.setLength(0)
                            recyclerFilterCity.visibility = View.GONE
                            txtFilterCity.visibility = View.GONE


                        } else {
                            //remove coresponding cities...
                            removeCities(deletedState)
                        }
                    }

                })

            txtFilterState.visibility = View.VISIBLE

            recyclerFilterStates.visibility = View.VISIBLE
            recyclerFilterStates!!.layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterStates!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterStates!!.adapter = filteredStateAdapter

            addState(state)
        }

    }

    /*
       Method to set selected country
        */
    private fun selectedCountry(country: ArrayList<String>) {

        if (!country.isEmpty()) {

            filteredCountryAdapter =
                CountryListAdapter(country, this@FiltersActivity, object : Country {
                    override fun country(size: Int) {
                        if (size == 0) {

                            countryList.clear()
                            countries = null
                            txtFilterCountry.visibility = View.GONE
                            recyclerFilterCountry.visibility = View.GONE

                            //remove states
                            stateList.clear()
                            states.setLength(0)
                            recyclerFilterStates.visibility = View.GONE
                            txtFilterState.visibility = View.GONE

                            //remove city
                            cityList.clear()
                            cities.setLength(0)
                            recyclerFilterCity.visibility = View.GONE
                            txtFilterCity.visibility = View.GONE
                        }
                    }

                })

            txtFilterCountry.visibility = View.VISIBLE

            recyclerFilterCountry.visibility = View.VISIBLE
            recyclerFilterCountry!!.layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterCountry!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterCountry!!.adapter = filteredCountryAdapter

            addCountry(country)
        }

    }

    /*
       Method to set selected hour weeks
        */
    private fun hourPerWeek(hourWeekFilter: ArrayList<HoursPerWeek>) {

        if (!hourWeekFilter.isEmpty()) {

            filteredHourWeekAdapter =
                FilteredHoursPerWeek(
                    hourWeekFilter,
                    this@FiltersActivity,
                    object : SelectedHourWeek {
                        override fun selectedHourWeekListener(size: Int) {
                            if (size == 0) {
                                hourWeekFilters.clear()
                                hourWeeks.setLength(0)
                                recyclerFilterHoursPerWeek.visibility = View.GONE

                            }
                        }

                    })

            recyclerFilterHoursPerWeek.visibility = View.VISIBLE
            recyclerFilterHoursPerWeek!!.layoutManager =
                StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterHoursPerWeek!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterHoursPerWeek!!.adapter = filteredHourWeekAdapter

            addHourWeeks(hourWeekFilter)
        }

    }

    /*
       Method to set selected hour rates
        */
    private fun hourPaidRate(hourRateFilter: ArrayList<HoursRate>) {

        if (!hourRateFilter.isEmpty() && hourRateFilter.size > 0) {

            filteredHourRateAdapter =
                FilteredHourRateAdapter(
                    hourRateFilter,
                    this@FiltersActivity,
                    object : SelectedHourRate {
                        override fun selectedHourRateListener(size: Int) {
                            if (size == 0) {
                                hourRateFilters.clear()
                                hourRates.setLength(0)
                                recyclerFilterHoursRate.visibility = View.GONE

                            }
                        }

                    })

            recyclerFilterHoursRate.visibility = View.VISIBLE
            recyclerFilterHoursRate!!.layoutManager =
                StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterHoursRate!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterHoursRate!!.adapter = filteredHourRateAdapter

            addHoursRate(hourRateFilter)
        }

    }

    /*
       Method to set selected company
        */
    private fun companies(companyFilter: ArrayList<ComapnyMessage>) {

        if (!companyFilter.isEmpty()) {

            filteredCompanyAdapter =
                FilteredCompanyAdapter(
                    companyFilter,
                    this@FiltersActivity,
                    object : SelectedCompany {
                        override fun selectedCompanyListener(size: Int) {
                            if (size == 0) {
                                companyFilters.clear()
                                companies.setLength(0)
                                recyclerFilterCompanies.visibility = View.GONE

                            }
                        }

                    })

            recyclerFilterCompanies.visibility = View.VISIBLE
            recyclerFilterCompanies!!.layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterCompanies!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterCompanies!!.adapter = filteredCompanyAdapter

            addCompanies(companyFilter)
        }
    }

    /*
       Method to set selected job titles
        */
    private fun jobTitles(jobFilter: ArrayList<JobTitleMessage>) {

        if (!jobFilter.isEmpty()) {

            filteredJobAdapter =
                FilteredJobAdapter(jobFilter, this@FiltersActivity, object : SelectedJobTitle {
                    override fun selectedJobTitleListener(size: Int) {
                        if (size == 0) {
                            jobFilters.clear()
                            jobTitles.setLength(0)
                            recyclerFilterJobTitles.visibility = View.GONE

                        }
                    }

                })

            recyclerFilterJobTitles.visibility = View.VISIBLE
            recyclerFilterJobTitles!!.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterJobTitles!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterJobTitles!!.adapter = filteredJobAdapter

            addJobTitles(jobFilter)
        }
    }

    /*
       Method to set selected industries
        */
    private fun industries(industryFilter: ArrayList<IndustryMessage>) {

        if (!industryFilter.isEmpty()) {

            filteredIndustryAdapter =
                FilteredIndutriesAdapter(
                    industryFilter,
                    this@FiltersActivity,
                    object : SelectedIndustry {
                        override fun selectedIndustryListener(size: Int) {

                            if (size == 0) {

                                industryFilters.clear()
                                industries.setLength(0)
                                recyclerFilterIndustries.visibility = View.GONE


                            }
                        }

                    })

            recyclerFilterIndustries.visibility = View.VISIBLE
            recyclerFilterIndustries!!.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            recyclerFilterIndustries!!.itemAnimator = DefaultItemAnimator()
            recyclerFilterIndustries!!.adapter = filteredIndustryAdapter

            //add indutries.....

            addIndustry(industryFilter)

        }
    }

    /*
       Method to get list of selected industry
        */
    private fun addIndustry(industryFilters: ArrayList<IndustryMessage>) {
        if (!industries.isEmpty()) {

            industries.setLength(0)
        }

        for (i in 0..industryFilters.size - 1) {

            if (i != industryFilters.size - 1) {

                industries.append(industryFilters.get(i).industryId).append(",")
            } else {

                industries.append(industryFilters.get(i).industryId)

            }
        }
    }

    /*
           Method to get list of selected job titles
            */
    private fun addJobTitles(jobFilters: ArrayList<JobTitleMessage>) {

        if (!jobTitles.isEmpty()) {

            jobTitles.setLength(0)
        }

        for (i in 0..jobFilters.size - 1) {

            if (i != jobFilters.size - 1) {

                jobTitles.append(jobFilters.get(i).jobTitle).append(",")
            } else {

                jobTitles.append(jobFilters.get(i).jobTitle)

            }
        }
    }
    /*
       Method to get list of selected company
        */

    private fun addCompanies(companyFilters: ArrayList<ComapnyMessage>) {
        if (!companies.isEmpty()) {

            companies.setLength(0)
        }

        for (i in 0..companyFilters.size - 1) {

            if (i != companyFilters.size - 1) {

                companies.append(companyFilters.get(i).companyId).append(",")
            } else {

                companies.append(companyFilters.get(i).companyId)

            }
        }
    }

    /*
           Method to get list of selected hour rates
            */
    private fun addHoursRate(hourRateFilters: ArrayList<HoursRate>) {
        if (!hourRates.isEmpty()) {

            hourRates.setLength(0)
        }

        for (i in 0..hourRateFilters.size - 1) {

            if (i != hourRateFilters.size - 1) {

                hourRates.append(hourRateFilters.get(i).hoursRate).append(",")
            } else {

                hourRates.append(hourRateFilters.get(i).hoursRate)

            }
        }
    }

    /*
           Method to get list of selected hour weeks
            */
    private fun addHourWeeks(hourWeekFilters: ArrayList<HoursPerWeek>) {
        if (!hourWeeks.isEmpty()) {

            hourWeeks.setLength(0)
        }

        for (i in 0..hourWeekFilters.size - 1) {

            if (i != hourWeekFilters.size - 1) {

                hourWeeks.append(hourWeekFilters.get(i).hoursPerWeek).append(",")
            } else {

                hourWeeks.append(hourWeekFilters.get(i).hoursPerWeek)

            }
        }
    }

    /*
           Method to get list of selected country
            */
    private fun addCountry(country: ArrayList<String>) {

        if (country.size > 0) {

            countries = country.joinToString()
        }

    }

    /*
           Method to get list of selected state
            */
    private fun addState(state: ArrayList<String>) {

        if (!states.isEmpty()) {

            states.setLength(0)
        }

        for (i in 0..state.size - 1) {

            if (i != state.size - 1) {

                states.append(state.get(i)).append(",")
            } else {

                states.append(state.get(i))

            }
        }

    }

    /*
           Method to get list of selected city
            */
    private fun addCity(city: ArrayList<CityNames>) {

        if (!cities.isEmpty()) {

            cities.setLength(0)
        }

        for (i in 0..city.size - 1) {

            if (i != city.size - 1) {

                cities.append(city.get(i)).append(",")
            } else {

                cities.append(city.get(i))

            }
        }

    }

    /*
           Method to get list of selected job types
            */
    private fun addJobTypes() {

        if (!jobTypes.isEmpty()) {

            jobTypes.setLength(0)
        }

        if (creditedClicked && !nonCreditedClicked) {

            jobTypes.setLength(0)
            jobTypes.append("2")

        } else if (nonCreditedClicked && !creditedClicked) {

            jobTypes.setLength(0)
            jobTypes.append("1")

        } else if (creditedClicked && nonCreditedClicked) {

            jobTypes.setLength(0)
            jobTypes.append("1,2")

        } else {

            jobTypes.setLength(0)
        }

    }

    /*
       Method to remove city corresponding to state
        */

    private fun removeCities(deletedState: String) {

        var size = cityList.size
        for (i in 0..size - 1) {
            if (cityList.get(i).stateName != null) {

                if (cityList.get(i).stateName == deletedState) {
                    cityList.removeAt(i)
                    break
                }
            }

        }
        filteredCityAdapter!!.notifyDataSetChanged()

    }
}
