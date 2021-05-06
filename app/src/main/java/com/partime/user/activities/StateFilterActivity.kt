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
import com.partime.user.Adapters.StateAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.StateMessage
import com.partime.user.Responses.StateResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_state_filter.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StateFilterActivity : BaseActivity(), View.OnClickListener {

    private var stateAdapter: StateAdapter? = null
    var stateList = ArrayList<StateMessage>()
    var tempList = ArrayList<StateMessage>()

    var selectedCountry: ArrayList<String> = ArrayList()

    var checkedItems = ArrayList<StateMessage>()

    var map = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_filter)

        AppValidator.rotateBackArrow(imgBackStateFilters, this@StateFilterActivity)

        selectedCountry.addAll(intent.getSerializableExtra(Constants.SELECTED_COUNTRY) as ArrayList<String>)

        validateNet(selectedCountry)

        //search the states
        edtSearchState.addTextChangedListener(object : TextWatcher {
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

        imgBackStateFilters.setOnClickListener(this)
        txtResetState.setOnClickListener(this)
        btnApplyState.setOnClickListener(this)
        btnRetryState.setOnClickListener(this)
    }

    /*
    Method to validate network
     */
    private fun validateNet(selectedCountry: ArrayList<String>) {

        if (AppValidator.isInternetAvailable(this@StateFilterActivity)) {

            getStates(selectedCountry)
        } else {

            llErrorState.visibility = View.VISIBLE
            recyclerStateFilter.visibility = View.GONE
        }

    }

    /*
    Method to get the state list
     */
    private fun getStates(selectedCountry: ArrayList<String>) {

        var progressBar = ProgressBarUtil().showProgressDialog(this@StateFilterActivity)

        map.put("country", selectedCountry.joinToString())
        val apiService = ApiService.init()
        val call =
            apiService.getStateList(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()), map)
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<StateResponse> {
            override fun onResponse(
                call: Call<StateResponse>,
                response: retrofit2.Response<StateResponse>?
            ) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                if (response != null) {

                    if (response.body()?.code == 200) {

                        if (!stateList.isEmpty()) {
                            stateList.clear()
                        }

                        stateList.addAll(response.body()?.message as ArrayList<StateMessage>)
                        tempList.addAll(response.body()?.message as ArrayList<StateMessage>)

                        stateAdapter = StateAdapter(tempList, this@StateFilterActivity)

                        llErrorState.visibility = View.GONE
                        recyclerStateFilter.visibility = View.VISIBLE
                        recyclerStateFilter!!.layoutManager =
                            LinearLayoutManager(this@StateFilterActivity, RecyclerView.VERTICAL, false)
                        recyclerStateFilter!!.itemAnimator = DefaultItemAnimator()
                        recyclerStateFilter!!.adapter = stateAdapter

                        //to set selected state
                        setSelectedState(tempList)


                    } else {

                        Toast.makeText(
                            this@StateFilterActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<StateResponse>, t: Throwable) {

                Toast.makeText(this@StateFilterActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                llErrorState.visibility = View.VISIBLE
                recyclerStateFilter.visibility = View.GONE

            }

        })
    }

    /*
    Method to get the selected states
     */
    private fun setSelectedState(temp: ArrayList<StateMessage>) {

        var selectedState = ArrayList<String>()
        selectedState.addAll(intent.getSerializableExtra(Constants.STATE_SELECTED) as ArrayList<String>)
        if (selectedState != null) {

            for (i in 0..temp.size - 1) {

                for (ii in 0..selectedState.size - 1) {

                    if (temp.get(i).state == selectedState.get(ii)) {

                        temp.get(i).isClicked = true
                    }

                }

            }
        }
        if (stateAdapter != null) {
            stateAdapter!!.notifyDataSetChanged()

        }

    }


    override fun onClick(v: View?) {

        when (v) {

            imgBackStateFilters -> onBackPressed()

            txtResetState -> {

                for (i in 0..stateList.size - 1) {

                    stateList.get(i).isClicked = false
                }
                if (stateAdapter != null) {
                    stateAdapter!!.notifyDataSetChanged()

                }
            }

            btnRetryState -> {

                llErrorState.visibility = View.GONE

                validateNet(selectedCountry)
            }

            btnApplyState -> {

                var states = getStateFilters()
                val intent = intent
                if (states != null) {
                    intent.putExtra("stateFilters", states)
                }
                setResult(91, intent)
                finish()
            }
        }
    }

    /*
    Method to get list of selected states
     */
    private fun getStateFilters(): ArrayList<StateMessage> {

        for (i in 0..stateList.size - 1) {
            if (stateList.get(i).isClicked) {
                checkedItems.add(stateList[i])

            }
        }
        return checkedItems

    }

    /*
    Method to search states
     */
    private fun callSearch(title: String) {
        val str: String = title.toLowerCase(Locale.getDefault())
        tempList.clear()
        if (str == "") {
            tempList.addAll(stateList)
        } else {
            for (list in stateList) {
                if (list.state.toLowerCase(Locale.getDefault()).contains(str)) {
                    tempList.add(list)
                }
            }
        }
        if (stateAdapter != null) {
            stateAdapter!!.notifyDataSetChanged()

        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
    }
}
