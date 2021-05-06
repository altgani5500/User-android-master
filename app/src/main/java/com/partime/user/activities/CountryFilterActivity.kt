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
import com.partime.user.Adapters.CountryAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.CountryMessage
import com.partime.user.Responses.CountryResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_country_filter.*
import kotlinx.android.synthetic.main.activity_state_filter.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList

class CountryFilterActivity : BaseActivity(), View.OnClickListener {

    private var countryAdapter: CountryAdapter? = null
    var countryList = ArrayList<CountryMessage>()
    var checkedItems = ArrayList<CountryMessage>()
    var tempList = ArrayList<CountryMessage>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_filter)

        AppValidator.rotateBackArrow(imgBackCountryFilters, this@CountryFilterActivity)

        validateNet()

        //search the country
        edtSearchCountry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(edit: Editable?) {

                if (edit?.length!! >= 1) {
                    var industrySeached = edit.toString().trim()
                    callSearch(industrySeached)
                } else if (edit.isEmpty()) {
                    var industrySeached = edit.toString().trim()
                    callSearch(industrySeached)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }


        })

        imgBackCountryFilters.setOnClickListener(this)
        txtResetCountry.setOnClickListener(this)
        btnApplyCountry.setOnClickListener(this)
        btnRetryCountry.setOnClickListener(this)
    }

    private fun validateNet() {
        if (AppValidator.isInternetAvailable(this@CountryFilterActivity)) {

            getCountry()
        } else {

            llErrorCountry.visibility = View.VISIBLE
            recyclerCountryFilter.visibility = View.GONE
        }

    }

    /*
    Method to get the country list
     */
    private fun getCountry() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@CountryFilterActivity)

        val apiService = ApiService.init()
        val call =
            apiService.getCountryListt(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()))
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<CountryResponse> {
            override fun onResponse(
                call: Call<CountryResponse>,
                response: retrofit2.Response<CountryResponse>?
            ) {

                ProgressBarUtil().hideProgressDialog(progressBar)

                if (response != null) {

                    if (response.body()?.code == 200) {

                        if (!countryList.isEmpty()) {
                            countryList.clear()
                        }

                        //set the country adapter
                        countryList.addAll(response.body()?.message as ArrayList<CountryMessage>)
                        tempList.addAll(response.body()?.message as ArrayList<CountryMessage>)
                        countryAdapter = CountryAdapter(tempList, this@CountryFilterActivity)

                        llErrorCountry.visibility = View.GONE
                        recyclerCountryFilter.visibility = View.VISIBLE
                        recyclerCountryFilter!!.layoutManager =
                            LinearLayoutManager(
                                this@CountryFilterActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        recyclerCountryFilter!!.itemAnimator = DefaultItemAnimator()
                        recyclerCountryFilter!!.adapter = countryAdapter

                        //to  set the selected country checked
                        setSelectedCountry(tempList)


                    } else {

                        Toast.makeText(
                            this@CountryFilterActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<CountryResponse>, t: Throwable) {

                Toast.makeText(this@CountryFilterActivity, R.string.no_internet, Toast.LENGTH_LONG)
                llErrorState.visibility = View.VISIBLE
                recyclerStateFilter.visibility = View.GONE

            }

        })
    }

    /*
    Method to set prec checked  countries
     */

    private fun setSelectedCountry(temp: ArrayList<CountryMessage>) {

        var selectedCountry = intent.getStringExtra(Constants.COUNTRY_SELECTED)
        if (selectedCountry != null) {

            for (i in 0..temp.size - 1) {

                if (temp.get(i).country == selectedCountry) {

                    temp.get(i).isClicked = true

                }
            }
        }
        countryAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackCountryFilters -> onBackPressed()

            txtResetCountry -> {
                for (i in 0..countryList.size - 1) {

                    countryList.get(i).isClicked = false
                }
                countryAdapter!!.notifyDataSetChanged()
            }
            btnApplyCountry -> {

                //get the selected countries
                var country = getCountryFilters()
                val intent = intent
                if (country != null) {
                    intent.putExtra("countryFilters", country)
                }
                setResult(81, intent)
                finish()
            }
            btnRetryCountry -> {

                llErrorCountry.visibility = View.GONE
                validateNet()
            }
        }
    }

    /*
    Method to get selected countries
     */

    private fun getCountryFilters(): ArrayList<CountryMessage> {

        for (i in 0..countryList.size - 1) {
            if (countryList.get(i).isClicked) {
                checkedItems.add(countryList[i])

            }
        }
        return checkedItems

    }

    /*
    Method to search country
     */
    private fun callSearch(title: String) {
        val str: String = title.toLowerCase(Locale.getDefault())
        tempList.clear()
        if (str == "") {
            tempList.addAll(countryList)
        } else {
            for (list in countryList) {
                if (list.country.toLowerCase(Locale.getDefault()).contains(str)) {
                    tempList.add(list)
                }
            }
        }
        countryAdapter!!.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
    }
}
