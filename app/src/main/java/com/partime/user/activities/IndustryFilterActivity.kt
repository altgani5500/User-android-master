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
import com.partime.user.Adapters.IndustryAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.IndustryMessage
import com.partime.user.Responses.IndustryResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_industry_filter.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList

class IndustryFilterActivity : BaseActivity(), View.OnClickListener {

    private var industryAdapter: IndustryAdapter? = null
    var industryList = ArrayList<IndustryMessage>()
    var checkedItems = ArrayList<IndustryMessage>()
    var tempList = ArrayList<IndustryMessage>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.partime.user.R.layout.activity_industry_filter)

        AppValidator.rotateBackArrow(imgBackIndustryFilters, this@IndustryFilterActivity)

        validateNet()

        //search industry
        edtSearchIndustry.addTextChangedListener(object : TextWatcher {
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

        imgBackIndustryFilters.setOnClickListener(this)
        btnApplyIndustry.setOnClickListener(this)
        btnRetryIndustry.setOnClickListener(this)
        btnResetIndustry.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackIndustryFilters -> onBackPressed()

            btnApplyIndustry -> {

                var industries = getIndustryFilters()
                val intent = intent
                intent.putExtra("industryFilters", industries)
                setResult(51, intent)
                finish()
            }

            btnRetryIndustry -> {

                llErrorIndustry.visibility = View.VISIBLE
                validateNet()
            }

            btnResetIndustry -> {

                for (i in 0..industryList.size - 1) {

                    industryList.get(i).isClicked = false
                }
                if (industryAdapter != null) {
                    industryAdapter!!.notifyDataSetChanged()
                }
            }

        }
    }

    /*
    Method to get the selected industry list
     */
    private fun getIndustryFilters(): ArrayList<IndustryMessage> {

        for (i in 0..industryList.size - 1) {
            if (industryList.get(i).isClicked) {
                checkedItems.add(industryList[i])

            }
        }
        return checkedItems
    }

    /*
    Method to get industry list
     */
    private fun getIndustry() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@IndustryFilterActivity)

        val apiService = ApiService.init()
        val call =
            apiService.getIndustryList(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()))
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<IndustryResponse> {
            override fun onResponse(
                call: Call<IndustryResponse>,
                response: retrofit2.Response<IndustryResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        if (!industryList.isEmpty()) {
                            industryList.clear()
                        }

                        industryList.addAll(response.body()?.message as ArrayList<IndustryMessage>)
                        tempList.addAll(response.body()?.message as ArrayList<IndustryMessage>)
                        industryAdapter = IndustryAdapter(tempList, this@IndustryFilterActivity)

                        llErrorIndustry.visibility = View.GONE
                        recyclerIndustryFilter.visibility = View.VISIBLE
                        recyclerIndustryFilter!!.layoutManager =
                            LinearLayoutManager(
                                this@IndustryFilterActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        recyclerIndustryFilter!!.itemAnimator = DefaultItemAnimator()
                        recyclerIndustryFilter!!.adapter = industryAdapter

                        //to set the selected filters......

                        if (intent.getSerializableExtra(Constants.INDUSTRIES_SELECTED) != null) {

                            var selectedList = java.util.ArrayList<IndustryMessage>()
                            selectedList.addAll(intent.getSerializableExtra(Constants.INDUSTRIES_SELECTED) as ArrayList<IndustryMessage>)

                           for (i in 0..tempList.size - 1) {

                                for (ii in 0..selectedList.size - 1) {

                                    if (tempList.get(i).industryId == selectedList.get(ii).industryId) {

                                        tempList.get(i).isClicked = true
                                        if (industryAdapter != null) {
                                            industryAdapter!!.notifyDataSetChanged()
                                        }
                                    }

                                }

                            }
                        }

                    } else {

                        Toast.makeText(
                            this@IndustryFilterActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<IndustryResponse>, t: Throwable) {

                Toast.makeText(this@IndustryFilterActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                llErrorIndustry.visibility = View.VISIBLE
                recyclerIndustryFilter.visibility = View.GONE

            }

        })


    }

    /*
    method to validate network
     */

    fun validateNet() {

        if (AppValidator.isInternetAvailable(this@IndustryFilterActivity)) {

            getIndustry()

        } else {

            llErrorIndustry.visibility = View.VISIBLE
            recyclerIndustryFilter.visibility = View.GONE
        }
    }

    /*
    Method to search the indusstries
     */
    private fun callSearch(title: String) {
        val str: String = title.toLowerCase(Locale.getDefault())
        tempList.clear()
        if (str == "") {
            tempList.addAll(industryList)
        } else {
            for (list in industryList) {
                if (list.industry.toLowerCase(Locale.getDefault()).contains(str)) {
                    tempList.add(list)
                }
            }
        }
        if (industryAdapter != null) {
            industryAdapter!!.notifyDataSetChanged()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
