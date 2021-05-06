package com.partime.user.activities

import android.app.Dialog
import android.content.Context
import android.net.NetworkInfo
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.WifiAdapter
import com.partime.user.Listeners.WifiListener
import com.partime.user.R
import com.partime.user.Responses.ConnectNetwork
import com.partime.user.Responses.NetworkCredentialsResponse
import com.partime.user.helpers.PermissionsUtil
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.partime.user.viewutils.MYWifyUtlis
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_add_network.*
import kotlinx.android.synthetic.main.add_network_dialog.*
import kotlinx.android.synthetic.main.info_dailog.*
import retrofit2.Call
import retrofit2.Callback


class AddNetworkActivity : BaseActivity(), View.OnClickListener {

    var map = HashMap<String, String>()

    var wifi: WifiManager? = null
    var size = 0

    private var adapter: WifiAdapter? = null
    var arraylist = ArrayList<String>()
    internal var ITEM_KEY = "key"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_network)

        AppValidator.rotateBackArrow(imgBackAddNetwork, this@AddNetworkActivity)

        askPermmissions()

        wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifi!!.isWifiEnabled == false) {
            Toast.makeText(applicationContext, R.string.enabling_wifi, Toast.LENGTH_LONG).show()
            wifi!!.isWifiEnabled = true
        }


        /* registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                results = wifi!!.scanResults as ArrayList<ScanResult>
                size = results!!.size

                addNetworks(results as ArrayList<String>)
            }
        }, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))*/


        //btnAddNetworks.setOnClickListener(this)
        imgBackAddNetwork.setOnClickListener(this)
        btnRetryAddNetwork.setOnClickListener(this)
        btnGrantPermissionsAddNet.setOnClickListener(this)
    }

    private fun validateNet() {


        if (AppValidator.isInternetAvailable(this@AddNetworkActivity)) {
            llDenyPermissionsAddNet.visibility = View.GONE
            getNetworkDetails()
        } else {
            Toast.makeText(this, com.partime.user.R.string.no_internet, Toast.LENGTH_LONG).show()
        }
    }

    private fun getNetworkDetails() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@AddNetworkActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        var lang = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())

        val apiService = ApiService.init()
        val call = apiService.getNetworkCredentials("Bearer $authKey", lang)
        call.enqueue(object : Callback<NetworkCredentialsResponse> {
            override fun onResponse(
                call: Call<NetworkCredentialsResponse>,
                response: retrofit2.Response<NetworkCredentialsResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        txtNetName.text = response.body()?.username
                        txtNetPassword.text = response.body()?.password

                        llAddNetworks.visibility = View.VISIBLE
                        llErrorAddNetwork.visibility = View.GONE

                    } else {

                        Toast.makeText(
                            this@AddNetworkActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<NetworkCredentialsResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                llAddNetworks.visibility = View.GONE
                llErrorAddNetwork.visibility = View.VISIBLE

            }

        })
    }

    private fun addNetworks(results: ArrayList<String>) {

        /* try {
            size = size - 1
            while (size >= 0) {

                arraylist.addAll(results)*/

        adapter = WifiAdapter(results, this@AddNetworkActivity, object : WifiListener {
            override fun onWifiNetworkSelect(networkName: String) {

                addNetworkDailog(networkName)

            }

        })

        recyclerAddNetwork.visibility = View.VISIBLE
        recyclerAddNetwork!!.layoutManager =
            LinearLayoutManager(this@AddNetworkActivity, RecyclerView.VERTICAL, false)
        recyclerAddNetwork!!.itemAnimator = DefaultItemAnimator()
        recyclerAddNetwork!!.adapter = adapter

        txtNoWifi.visibility = View.GONE
        txtSearchingNetwork.visibility = View.INVISIBLE
        progressWifi.visibility = View.GONE
    }
    /*} catch (e: Exception) {
        }*/

    private fun validateUserNetwork(): Boolean {
        var ssid: String? = null
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo

        wifiInfo = wifiManager.connectionInfo
        if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            ssid = wifiInfo.ssid
        }

        val networkName = txtNetName.text.toString()
        return networkName.equals(ssid!!.substring(1, ssid.length - 1))
    }

    private fun validations(userName: String, password: String): Boolean {

        if (userName.isNullOrEmpty() && userName.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_user_name, Toast.LENGTH_LONG).show()
            return false
        } else if (password.isNullOrEmpty() && password.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_password, Toast.LENGTH_LONG).show()
            return false
        } else {

            return true
        }
    }

    private fun addWorkNetwork(edtUserName: String, edtPassword: String) {

        var progressBar = ProgressBarUtil().showProgressDialog(this@AddNetworkActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val lat = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLat.toString())
        val long = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLong.toString())

        map.put("username", edtUserName)
        map.put("password", edtPassword)
        map.put("lat", lat)
        map.put("long", long)

        val apiService = ApiService.init()
        val call = apiService.connectNetwork("Bearer $authKey", map)
        call.enqueue(object : Callback<ConnectNetwork> {
            override fun onResponse(
                call: Call<ConnectNetwork>,
                response: retrofit2.Response<ConnectNetwork>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        Toast.makeText(
                            this@AddNetworkActivity,
                            response.body()?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        //  if()
                        // var b=getWifyStatus(this@AddNetworkActivity)


                    } else {

                        Toast.makeText(
                            this@AddNetworkActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<ConnectNetwork>, t: Throwable) {

                Toast.makeText(this@AddNetworkActivity, R.string.no_internet, Toast.LENGTH_LONG)
                    .show()

            }

        })
    }

    /* fun checkWifiOnAndConnected(context: Context): Boolean {
        if (wifi!!.isWifiEnabled) {
            return true
        } else {

            wifi!!.isWifiEnabled = true
            return true
        }

    }*/

    override fun onClick(v: View?) {

        when (v) {

            imgBackAddNetwork -> {

                onBackPressed()
            }
            btnRetryAddNetwork -> {

                llErrorAddNetwork.visibility = View.GONE
                validateNet()
            }

            btnGrantPermissionsAddNet -> {

                llDenyPermissionsAddNet.visibility = View.GONE

                askPermmissions()
            }
        }
    }

    private fun askPermmissions() {
        PermissionsUtil.askPermissions(
            this@AddNetworkActivity,
            PermissionsUtil.LOCATION,
            PermissionsUtil.CORASE_LOCATION,
            object : PermissionsUtil.PermissionListener {
                override fun onPermissionResult(isGranted: Boolean) {

                    if (isGranted) {
                        validateNet()
                    } else {

                        llDenyPermissionsAddNet.visibility = View.VISIBLE
                        llAddNetworks.visibility = View.GONE
                    }
                }
            })
    }

    private fun infoDialog() {

        var infoDialog = Dialog(this@AddNetworkActivity)

        infoDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        infoDialog.setContentView(R.layout.info_dailog)
        infoDialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        infoDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val network = txtNetName.text.toString()

        infoDialog.txtInfoDialog.text =
            resources.getString(R.string.connect_to_wifi) + " " + network + " " + resources.getString(
                R.string.network
            )

        infoDialog.btnOkInfoDialog.setOnClickListener {

            infoDialog.dismiss()
        }

        infoDialog.show()
    }

    private fun addNetworkDailog(networkName: String) {

        var networkDailog = Dialog(this@AddNetworkActivity)

        networkDailog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        networkDailog.setContentView(R.layout.add_network_dialog)
        networkDailog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        networkDailog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        networkDailog.txtWifiDialogTitle.text =
            resources.getString(R.string.wifi) + "  " + networkName

        networkDailog.edtUserNameWifiDialog.text = networkName

        networkDailog.btnSubmitWifiDailog.setOnClickListener {

            var userName = networkDailog.edtUserNameWifiDialog.text.toString()
            var password = networkDailog.edtPasswordWifiDialog.text.toString()

            if (validations(userName, password)) {

                if (AppValidator.isInternetAvailable(this@AddNetworkActivity)) {

                    //get the user network
                    if (validateUserNetwork()) {


                        addWorkNetwork(userName, password)
                    } else {
                        infoDialog()
                    }
                    networkDailog.dismiss()

                } else {
                    Toast.makeText(this, com.partime.user.R.string.no_internet, Toast.LENGTH_LONG)
                        .show()
                    networkDailog.dismiss()

                }
            }
        }

        networkDailog.show()
    }

    override fun onResume() {
        super.onResume()

        if (wifi != null) {

            if (wifi!!.scanResults.size > 0) {

                arraylist.clear()

                for (i in 0..wifi!!.scanResults.size - 1) {

                    arraylist.add(wifi!!.scanResults.get(i).SSID)
                }

                addNetworks(arraylist)
            } else {

                recyclerAddNetwork.visibility = View.GONE
                progressWifi.visibility = View.GONE
                llErrorAddNetwork.visibility = View.GONE
                llDenyPermissionsAddNet.visibility = View.GONE
                txtNoWifi.visibility = View.VISIBLE
            }
        }
    }


    // get current wify network connected
    private fun getWifyStatus(context: Context): Boolean {
        val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (manager.isWifiEnabled) {
            val wifiInfo = manager.connectionInfo
            if (wifiInfo != null) {
                val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
                return state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR
            }
        }

        return false

    }


    // get conected wify network name
    private fun getWifyName(context: Context): String {
        val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (manager.isWifiEnabled) {
            val wifiInfo = manager.connectionInfo
            if (wifiInfo != null) {
                val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.ssid
                }
            }
        }
        return "NA"

    }


}
