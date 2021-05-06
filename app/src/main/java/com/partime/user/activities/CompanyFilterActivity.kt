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
import com.partime.user.Adapters.CompanyAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.ComapnyMessage
import com.partime.user.Responses.ComapnyResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_company_filter.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList

class CompanyFilterActivity : BaseActivity(), View.OnClickListener {


    private var companyAdapter: CompanyAdapter? = null
    var companyList = ArrayList<ComapnyMessage>()
    var tempList = ArrayList<ComapnyMessage>()

    var checkedItems = ArrayList<ComapnyMessage>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_filter)

        AppValidator.rotateBackArrow(imgBackCompanyFilters, this@CompanyFilterActivity)

        validateNet()

        //search the company
        edtSearchCompany.addTextChangedListener(object : TextWatcher {
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

        imgBackCompanyFilters.setOnClickListener(this)
        btnResetCompany.setOnClickListener(this)
        btnResetCompany.setOnClickListener(this)
        btnApplyCompany.setOnClickListener(this)
    }

    private fun validateNet() {

        if (AppValidator.isInternetAvailable(this@CompanyFilterActivity)) {

            //get company list
            getCompany()
        } else {

            llErrorCompany.visibility = View.VISIBLE
            recyclerCompanyFilter.visibility = View.GONE

        }
    }

    /*
    Method to get company list
     */

    private fun getCompany() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@CompanyFilterActivity)


        val apiService = ApiService.init()
        val call =
            apiService.getCompanyList(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()))
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<ComapnyResponse> {
            override fun onResponse(
                call: Call<ComapnyResponse>,
                response: retrofit2.Response<ComapnyResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        if (!companyList.isEmpty()) {
                            companyList.clear()
                        }

                        //set companny adapter.....
                        companyList.addAll(response.body()?.message as ArrayList<ComapnyMessage>)
                        tempList.addAll(response.body()?.message as ArrayList<ComapnyMessage>)

                        companyAdapter = CompanyAdapter(tempList, this@CompanyFilterActivity)

                        llErrorCompany.visibility = View.GONE
                        recyclerCompanyFilter.visibility = View.VISIBLE
                        recyclerCompanyFilter!!.layoutManager =
                            LinearLayoutManager(
                                this@CompanyFilterActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        recyclerCompanyFilter!!.itemAnimator = DefaultItemAnimator()
                        recyclerCompanyFilter!!.adapter = companyAdapter

                        if (intent.getSerializableExtra(Constants.COMPANY_SELECTED) != null) {

                            //check the pre selected company
                            var selectedList = java.util.ArrayList<ComapnyMessage>()
                            selectedList.addAll(intent.getSerializableExtra(Constants.COMPANY_SELECTED) as ArrayList<ComapnyMessage>)

                            for (i in 0..tempList.size - 1) {

                                for (ii in 0..selectedList.size - 1) {

                                    if (tempList.get(i).companyId == selectedList.get(ii).companyId) {

                                        tempList.get(i).isClicked = true
                                        companyAdapter!!.notifyDataSetChanged()
                                    }

                                }

                            }
                        }


                    } else {

                        Toast.makeText(
                            this@CompanyFilterActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<ComapnyResponse>, t: Throwable) {

                Toast.makeText(this@CompanyFilterActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                llErrorCompany.visibility = View.VISIBLE
                recyclerCompanyFilter.visibility = View.GONE

            }

        })


    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackCompanyFilters -> onBackPressed()

            btnRetryCompany -> {

                llErrorCompany.visibility = View.GONE
                validateNet()
            }
            btnApplyCompany -> {
                //get checked companies
                var companies = getFilteredCompanies()
                val intent = intent
                intent.putExtra("companyFilters", companies)
                setResult(71, intent)
                finish()
            }
            btnResetCompany -> {

                for (i in 0..companyList.size - 1) {

                    companyList.get(i).isClicked = false
                }
                if (companyAdapter != null) {
                    companyAdapter!!.notifyDataSetChanged()

                }
            }
        }
    }

    /*
    Method to get checked companies....
     */
    private fun getFilteredCompanies(): ArrayList<ComapnyMessage> {

        for (i in 0..companyList.size - 1) {
            if (companyList.get(i).isClicked) {
                checkedItems.add(companyList[i])

            }
        }
        return checkedItems

    }

    /*
    Method to search company
     */
    private fun callSearch(title: String) {
        val str: String = title.toLowerCase(Locale.getDefault())
        tempList.clear()
        if (str == "") {
            tempList.addAll(companyList)
        } else {
            for (list in companyList) {
                if (list.companyName.toLowerCase(Locale.getDefault()).contains(str)) {
                    tempList.add(list)
                }
            }
        }
        if (companyAdapter != null) {
            companyAdapter!!.notifyDataSetChanged()

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
