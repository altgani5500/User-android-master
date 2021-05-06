package com.partime.user.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.gems.app.utilities.AppValidator
import com.google.android.gms.location.FusedLocationProviderClient
import com.partime.user.Constants.Constants
import com.partime.user.Fragments.JobListFragment
import com.partime.user.Fragments.NewMapFragment
import com.partime.user.R
import com.partime.user.Responses.*
import com.partime.user.helpers.PermissionsUtil
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.helpers.Utilities
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import com.parttime.user.uicomman.auth.LoginActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.home_navigation.*
import kotlinx.android.synthetic.main.home_navigation.view.*
import kotlinx.android.synthetic.main.info_dailog.*
import kotlinx.android.synthetic.main.logout_dialog.*
import kotlinx.android.synthetic.main.register_dailog.*
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeActivity : BaseActivity(), View.OnClickListener {
var closemap = 0



    var map = HashMap<String, String>()
    var hmap = HashMap<String, String>()
    var userName: String? = null
    var userEmail: String? = null

    internal var doubleBackToExitPressedOnce = false

    var jobList = ArrayList<JobListMessage>()
    var profileResponse: ProfileMessage? = null

    var cityName = ArrayList<String>()
    //for current location
    private var currentLocation: Location? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val LOCATION_REQUEST_CODE = 101
    lateinit var latitude: String
    lateinit var longitude: String

    var industryFilters: String? = null
    var companiesFilters: String? = null
    var jobTitleFilters: String? = null
    var jobTypesFilters: String? = null
    var countryFilters: String? = null
    var stateFilters: String? = null
    //var cityFilters: String? = null
    var hourWeekFilters: String? = null
    var hourRateFilters: String? = null

    var industry = ArrayList<IndustryMessage>()
    var jobTitle = ArrayList<JobTitleMessage>()
    var company = ArrayList<ComapnyMessage>()
    var countries = ArrayList<String>()
    var states = ArrayList<String>()
    // var cities=ArrayList<String>()
    var cities = ArrayList<CityNames>()
    var hourRate = ArrayList<HoursRate>()
    var hourWeek = ArrayList<HoursPerWeek>()

    var currentAddress: String? = null

    var notificationCount: Int = 0
    var msgCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

// val resignDate = getCurrentDate()
//        Toast.makeText(
//                this,
//             "sucsecc"+resignDate.toString(),
//                Toast.LENGTH_LONG
//        ).show()

        showGifLoading()

        loginnavigation.setOnClickListener {
         OpenLogin()
           // registerDailog.dismiss()

        }

        AppValidator.rotateBackArrow(imgHomeNavigation, this@HomeActivity)

        if (AppValidator.isInternetAvailable(this)) {

            imgFilterHome.isEnabled = true
            imgHomeNavigation.isEnabled = true
            appPrefence.setString(AppPrefence.SharedPreferncesKeys.FROM_HOME.toString(), "LIST")

            requestLocationPermission()

        } else {

            llErrorJobList.visibility = View.VISIBLE
            imgListView.isEnabled = false
            imgFilterHome.isEnabled = false
            imgHomeNavigation.isEnabled = false
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        }

        btnRetryJobList.setOnClickListener(this)
        btnGrantPermissions.setOnClickListener(this)

        imgHomeNavigation.setOnClickListener(this)
        imgCrossHomeNavigation.setOnClickListener(this)


        navigation.llSavedJobs.setOnClickListener(this)
        navigation.llAboutUs.setOnClickListener(this)
        navigation.llContactUs.setOnClickListener(this)
        navigation.llUserProfile.setOnClickListener(this)
        navigation.llLogout.setOnClickListener(this)
        navigation.llSettings.setOnClickListener(this)
        navigation.registerNavigation.setOnClickListener(this)
        navigation.llNotifications.setOnClickListener(this)
        navigation.llMessageHome.setOnClickListener(this)
        navigation.llScheduleTask.setOnClickListener(this)
        navigation.llTimeLog.setOnClickListener(this)
        navigation.llHistory.setOnClickListener(this)
        navigation.ContactCall.setOnClickListener(this)
        imgFilterHome.setOnClickListener(this)
        imgListView.setOnClickListener(this)
        imgMapView.setOnClickListener(this)

    }

    private fun OpenLogin() {


        val intent = Intent(this@HomeActivity, LoginActivity::class.java)
        startActivity(intent)
      drawer_layout.closeDrawer(GravityCompat.START)
    }

    override fun onClick(v: View?) {

        if (v == imgHomeNavigation) {

            drawer_layout.openDrawer(GravityCompat.START)
        }
        if (v == imgCrossHomeNavigation) {

            drawer_layout.closeDrawer(GravityCompat.START)

        }

        if (v == navigation.llSavedJobs) {

           // drawer_layout.closeDrawer(GravityCompat.START)

            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else {

                val intent = Intent(this@HomeActivity, SavedJobsActivity::class.java)
                startActivity(intent)
            }


        }

        if (v == navigation.llAboutUs) {

           // drawer_layout.closeDrawer(GravityCompat.START)

            val intent = Intent(this@HomeActivity, AboutUsActivity::class.java)
            startActivity(intent)
        }
        if (v == navigation.llContactUs) {

           // drawer_layout.closeDrawer(GravityCompat.START)

            if (userName != null && userEmail != null) {

                val intent = Intent(this@HomeActivity, ContactUsActivity::class.java)
                intent.putExtra(Constants.USER_NAME, userName)
                intent.putExtra(Constants.USER_EMAIL, userEmail)
                startActivityForResult(intent, 11)

            } else {

                val intent = Intent(this@HomeActivity, ContactUsActivity::class.java)
                startActivity(intent)
            }

        }

        if (v == navigation.ContactCall) {

          //  drawer_layout.closeDrawer(GravityCompat.START)

            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "8344814819")
            startActivity(dialIntent)

        }


        if (v == navigation.llUserProfile) {

          //  drawer_layout.closeDrawer(GravityCompat.START)

            val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
            intent.putExtra(Constants.PROFILE_RESPONSE, profileResponse)
            startActivityForResult(intent, 11)
        }
        if (v == imgListView) {

            closemap = 1
           // Toast.makeText(this, "this"+closemap, Toast.LENGTH_SHORT).show()

            imgListView.visibility = View.GONE
            imgMapView.visibility = View.VISIBLE
            imgFilterHome.isEnabled = true

            appPrefence.setString(AppPrefence.SharedPreferncesKeys.FROM_HOME.toString(), "MAP")
            loadFragment(NewMapFragment(), jobList)


        }
        if (v == imgMapView) {
            closemap = 0
           // Toast.makeText(this, "this"+closemap, Toast.LENGTH_SHORT).show()


            imgListView.visibility = View.VISIBLE
            imgMapView.visibility = View.GONE
            imgFilterHome.isEnabled = true

            appPrefence.setString(AppPrefence.SharedPreferncesKeys.FROM_HOME.toString(), "LIST")
            loadFragment(JobListFragment(), jobList)

        }
        if (v == btnRetryJobList) {

            llErrorJobList.visibility = View.GONE

            if (AppValidator.isInternetAvailable(this)) {

                imgFilterHome.isEnabled = true
                imgHomeNavigation.isEnabled = true

                requestLocationPermission()

                var authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
                if (!(authkey.isNullOrEmpty()) && !(authkey.isNullOrBlank())) {

                    llHomeNavigationLogin.visibility = View.GONE

                    getUserProf(authkey)
                    llUserProfile.visibility = View.VISIBLE
                    llLogout.visibility = View.VISIBLE
                    drawer_layout.closeDrawer(GravityCompat.START)
                } else {

                    llUserProfile.visibility = View.GONE
                    llLogout.visibility = View.GONE
                    llHomeNavigationLogin.visibility = View.VISIBLE

                }

            } else {

                llErrorJobList.visibility = View.VISIBLE
                imgListView.isEnabled = false
                imgFilterHome.isEnabled = false
                imgHomeNavigation.isEnabled = false
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            }

        }
        if (v == navigation.registerNavigation) {
            drawer_layout.closeDrawer(GravityCompat.START)

            registerDialog()

        }
        if (v == navigation.llLogout) {

            drawer_layout.closeDrawer(GravityCompat.START)

            logoutDialog()


        }
        if (v == navigation.llSettings) {

         //   drawer_layout.closeDrawer(GravityCompat.START)

            val intent = Intent(this@HomeActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
        if (v == btnGrantPermissions) {

            llDenyPermissions.visibility = View.GONE
            if (AppValidator.isInternetAvailable(this)) {

                requestLocationPermission()

            } else {

                llErrorJobList.visibility = View.VISIBLE
                imgListView.isEnabled = false
            }

        }
        if (v == navigation.llMessageHome) {

           // drawer_layout.closeDrawer(GravityCompat.START)

            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else {

                val intent = Intent(this@HomeActivity, MessagesActivity::class.java)
                if (notificationCount > 0) {
                    intent.putExtra(Constants.UNREAD_NOTIFICATION_COUNT, notificationCount)
                }

                startActivity(intent)
            }
        }
        if (v == llNotifications) {

          //  drawer_layout.closeDrawer(GravityCompat.START)


            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else {

                val intent = Intent(this@HomeActivity, NotificationActivity::class.java)
                startActivity(intent)
            }


        }
        if (v == imgFilterHome) {

            //send the pre selected choices on filter activity

            val inten = Intent(this@HomeActivity, FiltersActivity::class.java)

            if (industry != null) {
                inten.putExtra(Constants.INDUSTRIES, industry)
            }
            if (company != null) {
                inten.putExtra(Constants.COMPANY, company)
            }
            if (jobTitle != null) {
                inten.putExtra(Constants.JOB_TITLE, jobTitle)
            }
            if (countries != null) {
                inten.putExtra(Constants.COUNTRIES, countries)

            }
            if (states != null) {
                inten.putExtra(Constants.STATES, states)

            }
            if (cities != null) {
                inten.putExtra(Constants.CITIES, cities)

            }
            if (hourRate != null) {
                inten.putExtra(Constants.HOURS_RATE, hourRate)
            }
            if (hourWeek != null) {
                inten.putExtra(Constants.HOURS_WEEK, hourWeek)
            }
            if (jobTypesFilters != null) {

                inten.putExtra(Constants.JOB_TYPE, jobTypesFilters)
            }

            startActivityForResult(inten, 100)
        }
        if (v == navigation.llScheduleTask) {
        //    drawer_layout.closeDrawer(GravityCompat.START)
            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            }else if(appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())&&appPrefence.getInt(AppPrefence.SharedPreferncesKeys.IS_ENROLLED.toString())!=1){

                infoDialog()
            }
            else {

                val intent = Intent(this@HomeActivity, ScheduleTaskActivity::class.java)
                startActivity(intent)
            }
        }
        if(v==navigation.llTimeLog){

           // drawer_layout.closeDrawer(GravityCompat.START)
            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else {

                val intent = Intent(this@HomeActivity, TimeLogActivity::class.java)
                startActivity(intent)
            }
        }
        if(v==navigation.llHistory){

           // drawer_layout.closeDrawer(GravityCompat.START)
            if (!appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {

                registerDialog()

            } else {

                val intent = Intent(this@HomeActivity, MyHistoryActivity::class.java)
                startActivity(intent)
            }
        }

    }


    /*
    Method to request permission for getting current location
     */
    private fun requestLocationPermission() {

        PermissionsUtil.askPermission(this@HomeActivity, PermissionsUtil.LOCATION,
            object : PermissionsUtil.PermissionListener {
                override fun onPermissionResult(isGranted: Boolean) {
                    if (isGranted) {

                        requestLocation(object : LocationListener,
                            com.google.android.gms.location.LocationListener {
                            override fun onLocationChanged(location: Location?) {
                                getCurrentLocation(location)
                            }

                            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                            }

                            override fun onProviderEnabled(provider: String?) {
                            }

                            override fun onProviderDisabled(provider: String?) {
                            }

                        })

                    } else {

                        imgListView.isEnabled = false
                        imgFilterHome.isEnabled = false
                        imgHomeNavigation.isEnabled = false
                        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                        llDenyPermissions.visibility = View.VISIBLE
                    }
                }

            })

    }

    /*
    Method to get the current location if permissions granted
     */

    private fun getCurrentLocation(location: Location?) {

        if (location != null) {
            currentLocation = location

            latitude = currentLocation?.latitude.toString()
            longitude = currentLocation?.longitude.toString()

            if (latitude != null) {

                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentLat.toString(), latitude)

            }
            if (longitude != null) {

                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentLong.toString(), longitude)

            }

            if (!latitude.isNullOrEmpty() || !longitude.isNullOrEmpty() || !latitude.isNullOrBlank() || !longitude.isNullOrBlank()) {

                var deviceToken = appPrefence.getString(AppPrefence.SharedPreferncesKeys.DEVICE_TOKEN.toString())
                if (!deviceToken.isNullOrBlank() && !deviceToken.isNullOrEmpty()) {

                    if (appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())) {
                        generateToken(latitude, longitude, deviceToken)
                    }
                }
                getJobList()

                imgListView.isEnabled = true
                imgFilterHome.isEnabled = true
                imgHomeNavigation.isEnabled = true

            } else {


                llErrorJobList.visibility = View.VISIBLE
                imgListView.isEnabled = false
                imgFilterHome.isEnabled = false
                imgHomeNavigation.isEnabled = false
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                txtNoInternetHome.setText(R.string.no_location_recorded)

            }


        } else {

            llErrorJobList.visibility = View.VISIBLE
            imgListView.isEnabled = false
            imgFilterHome.isEnabled = false
            imgHomeNavigation.isEnabled = false
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            txtNoInternetHome.setText(R.string.no_location_recorded)
        }

    }


    /*
    Method to generate device token
     */
    private fun generateToken(latitude: String, longitude: String, deviceToken: String) {

        var deviceType = "Android"
        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())


        map.put("deviceToken", deviceToken)
        map.put("deviceType", deviceType)
        map.put("userLat", latitude)
        map.put("userLong", longitude)


        val apiService = ApiService.init()
        val call = apiService.generateToken(
            "Bearer $authKey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()),
            map
        )

        Log.e("REQUEST", call.toString() + "")


        call.enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: retrofit2.Response<TokenResponse>?) {
                if (response != null) {

                    if (response.body()?.code == 200) {

                        Toast.makeText(this@HomeActivity, response.body()?.message, Toast.LENGTH_LONG)

                    } else {

                        Toast.makeText(this@HomeActivity, response.body()?.error_message.toString(), Toast.LENGTH_LONG)

                    }

                }

            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {

                llErrorJobList.visibility = View.VISIBLE
                imgListView.isEnabled = false
                imgFilterHome.isEnabled = false
                imgHomeNavigation.isEnabled = false
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                Toast.makeText(this@HomeActivity, t.toString(), Toast.LENGTH_LONG)

            }

        })
    }

    /*
    Method toh ge the job list
     */
    fun getJobList() {
//        imgFilterHome.isEnabled = false
//        imgHomeNavigation.isEnabled = false

        var userId = appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString())

        if (userId != null && !userId.equals("")) {
            hmap.put("userId", userId)
        } else {

            hmap.put("userId", "0")
        }
        if (industryFilters != null) {

            hmap.put("industryFilter", industryFilters!!)

        }
        if (jobTitleFilters != null) {

            hmap.put("jobTitle", jobTitleFilters!!)

        }
        if (companiesFilters != null) {

            hmap.put("company", companiesFilters!!)

        }
        if (jobTypesFilters != null) {

            hmap.put("jobType", jobTypesFilters!!)

        }
        if (countryFilters != null) {

            hmap.put("country", countryFilters!!)

        }
        if (stateFilters != null) {

            hmap.put("state", stateFilters!!)

        }

        if (cityName != null) {

            hmap.put("city", cityName.joinToString().replace(", ", ","))

        }
        if (currentAddress != null && !currentAddress.equals("currentLocation") && latitude != null && longitude != null) {

            hmap.put("desiredLat", latitude)
            hmap.put("desiredLong", longitude)

        }
        if (hourRateFilters != null) {

            if (hourRateFilters!!.contains("None")) {

                hmap.put("hoursPaidRate", "None Of These")
            } else {
                hmap.put("hoursPaidRate", hourRateFilters!!)
            }


        }
        if (hourWeekFilters != null) {

            hmap.put("hoursPerWeek", hourWeekFilters!!)

        }
        val apiService = ApiService.init()
        val call: Call<JobListResponse> =
            apiService.getJobList(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()), hmap)

        Log.e("-___________REQUEST", hmap.toString() + "")



        call.enqueue(object : Callback<JobListResponse> {
            @SuppressLint("WrongConstant")
            override fun onResponse(
                call: Call<JobListResponse>,
                response: retrofit2.Response<JobListResponse>?
            ) {

                if (response != null) {

                    if (response.body()?.code == 200) {

                        var lastFragment = appPrefence.getString(AppPrefence.SharedPreferncesKeys.FROM_HOME.toString())


                        Toast.makeText(this@HomeActivity, response.body()?.success.toString(), Toast.LENGTH_LONG)

                        if (!jobList.isEmpty()) {

                            jobList.clear()
                        }


                        jobList.addAll(response.body()?.message as ArrayList<JobListMessage>)
                        if (jobList.size == 0) {

                            txtNoJobList.visibility = View.VISIBLE
                            imgListView.isEnabled = false
                            loading_l.visibility = View.GONE
                            imgFilterHome.isEnabled = true
                            imgHomeNavigation.isEnabled = true
                            //to saved opened fragmnet info......
                            if (lastFragment.equals("LIST")) {
                                loadFragment(JobListFragment(), jobList)
                            }

                        } else {

                            //drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                        //   drawer_layout.closeDrawer(GravityCompat.START)
                            txtNoJobList.visibility = View.GONE
                            imgListView.isEnabled = true
                            loading_l.visibility = View.GONE
//                            imgFilterHome.isEnabled = true
//                            imgHomeNavigation.isEnabled = true
                            //to saved opened fragmnet info......


if(BaseActivity.frgament_job_count != 1){

  if (lastFragment != null && lastFragment.equals("LIST")) {
                                loadFragment(JobListFragment(), jobList)
                            }

    BaseActivity.frgament_job_count = 1

}



                        }



                    } else {


                        Toast.makeText(this@HomeActivity, response.body()?.error_message.toString(), Toast.LENGTH_LONG)

                    }
                }

            }

            override fun onFailure(call: Call<JobListResponse>, t: Throwable) {

                llErrorJobList.visibility = View.VISIBLE
                txtNoJobList.visibility = View.GONE
                imgListView.isEnabled = false
                imgFilterHome.isEnabled = false
                imgHomeNavigation.isEnabled = false
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

        })

    }

    /*
    Method to load the fragments
     */
    private fun loadFragment(fragment: Fragment?, jobList: ArrayList<JobListMessage>) {
        //switching fragment
        if (fragment != null) {

            if (industryFilters != null) {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.industryFilter.toString(), industryFilters)
            } else {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.industryFilter.toString(), "")
            }

            if (jobTitleFilters != null) {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.jobTitleFilter.toString(), jobTitleFilters)
            } else {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.jobTitleFilter.toString(), "")
            }

            if (!cityName.isEmpty()) {
                appPrefence.setString(
                    AppPrefence.SharedPreferncesKeys.cityFilter.toString(),
                    cityName.joinToString().replace(", ", ",")
                )

            } else {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.cityFilter.toString(), "")

            }
            if (companiesFilters != null) {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.companyFilter.toString(), companiesFilters)

            } else {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.companyFilter.toString(), "")

            }
            if (stateFilters != null) {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.stateFilter.toString(), stateFilters)

            } else {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.stateFilter.toString(), "")

            }
            if (countryFilters != null) {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.countryFilter.toString(), countryFilters)

            } else {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.countryFilter.toString(), "")

            }
            if (currentAddress != null) {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentLocation.toString(), currentAddress)

            } else {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentLocation.toString(), "")

            }
            if (hourWeekFilters != null) {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.hourweekFilter.toString(), hourWeekFilters)

            } else {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.hourweekFilter.toString(), "")

            }
            if (hourRateFilters != null) {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.hourPaidRateFilter.toString(), hourRateFilters)

            } else {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.hourPaidRateFilter.toString(), "")

            }
            if (jobTypesFilters != null) {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.jobtypeFilter.toString(), jobTypesFilters)

            } else {
                appPrefence.setString(AppPrefence.SharedPreferncesKeys.jobtypeFilter.toString(), "")

            }

            val bundle = Bundle()
            bundle.putSerializable(Constants.JOB_LIST, jobList)
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss()
            imgListView.isEnabled = true

        }

    }

    private fun getUserProf(authKey: String) {

//        var progressBar: ProgressDialog? = null
//        if (!isFinishing) {
//            progressBar = ProgressBarUtil().showProgressDialog(this)
//
//        }

        imgFilterHome.isEnabled = false
        imgHomeNavigation.isEnabled = false

        val apiService = ApiService.init()
        val call: Call<ProfileResponse> = apiService.getProfileDetails(
            "Bearer $authKey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()))

        call.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: retrofit2.Response<ProfileResponse>?) {

                if (response != null) {

//                    if(progressBar!=null){
//                        ProgressBarUtil().hideProgressDialog(progressBar!!)
//                    }


                    imgFilterHome.isEnabled = true
                    imgHomeNavigation.isEnabled = true
                    if (response.body()?.code == 200) {

                        Toast.makeText(this@HomeActivity, response.body()?.success.toString(), Toast.LENGTH_LONG)

                        profileResponse = response.body()?.message

                        userName = response.body()?.message?.name
                        userEmail = response.body()?.message?.email

                        navigation.txtHomeNavigtionName.text = response.body()?.message?.name
                        if(!response.body()?.message?.companyName.isNullOrBlank()||!response.body()?.message?.companyName.isNullOrEmpty()){
                            navigation.txtHomeNaviationCompany.text = response.body()?.message?.companyName
                        }else if(!response.body()?.message?.enrolledEnterpriseName.isNullOrBlank()||!response.body()?.message?.enrolledEnterpriseName.isNullOrBlank()){
                            navigation.txtHomeNaviationCompany.text = response.body()?.message?.enrolledEnterpriseName
                        }


                        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.IS_ENROLLED.toString(),response.body()?.message?.isEnrolled!!)

                        notificationCount = response.body()?.message?.pushNotificationCount!!
                        msgCount = response.body()?.message?.messageCount!!

                        if (notificationCount > 0) {
                            navigation.txtHomeNotificationCount.visibility = View.VISIBLE
                            navigation.txtHomeNotificationCount.text = notificationCount.toString()
                        } else {
                            navigation.txtHomeNotificationCount.visibility = View.GONE
                        }
                        if (msgCount > 0) {

                            navigation.txtHomeMsgCount.text = msgCount.toString()
                            navigation.txtHomeMsgCount.visibility = View.VISIBLE
                        } else {
                            navigation.txtHomeMsgCount.visibility = View.GONE

                        }

                      // drawer_layout.closeDrawer(GravityCompat.START)

                        if (!response.body()?.message?.profilePicture.equals("")) {
                            Picasso.get().load(response.body()?.message?.profilePicture)
                                .placeholder(R.drawable.profile_placeholder).into(navigation.imgHomeNavigationPic)
                        }


                    } else {

                        Toast.makeText(this@HomeActivity, response.body()?.error_message.toString(), Toast.LENGTH_LONG)

                    }
                }

            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {

//                if(progressBar!=null){
//                    ProgressBarUtil().hideProgressDialog(progressBar!!)
//                }

                llErrorJobList.visibility = View.VISIBLE
                imgListView.isEnabled = false
                imgFilterHome.isEnabled = false
                imgHomeNavigation.isEnabled = false
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            }


        })

    }

    /*
    Method to open register dailog
     */
    private fun registerDialog() {

        var registerDailog = Dialog(this@HomeActivity)

        registerDailog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        registerDailog.setContentView(R.layout.register_dailog)
        registerDailog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        registerDailog.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        registerDailog.btnRegisterDailog.setOnClickListener {

            val intent = Intent(this@HomeActivity, RegisterActivity::class.java)
            startActivity(intent)
            registerDailog.dismiss()
        }
        registerDailog.txtLoginHereRegisterDailog.setOnClickListener {

            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
            registerDailog.dismiss()

        }
        registerDailog.show()
    }

    /*
    Method to show logout dialog
     */
    private fun logoutDialog() {

        var logoutDialog = Dialog(this@HomeActivity)

        logoutDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        logoutDialog.setContentView(R.layout.logout_dialog)
        logoutDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        logoutDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        logoutDialog.txtTitleLogoutDialog.setText(R.string.logout_title)

        logoutDialog.btnYesLogout.setOnClickListener {

            logoutDialog.dismiss()
            var lang = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())
            var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
            appPrefence.clearPreferences()
            appPrefence.setString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString(), lang)
            appPrefence.setBoolean(AppPrefence.SharedPreferncesKeys.IS_FIRST_TIME_LAUNCH.toString(), false)
            appPrefence.setBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString(), false)
            appPrefence.setString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString(), authKey)

            //to clear notifications from the status bar.....
            var notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as (NotificationManager)
            notificationManager.cancelAll()
            //to stop notifications after logout....
            generateToken(latitude, longitude, "123")

            Utilities.clearAllgoToActivity(this, HomeActivity::class.java)

        }
        logoutDialog.btnCancleLogout.setOnClickListener {

            logoutDialog.dismiss()

        }
        logoutDialog.show()
    }

    override fun onResume() {
        super.onResume()

        BaseActivity.frgament_job_count = 2

        if (AppValidator.isInternetAvailable(this)) {

            var authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
            var isLogin = appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString())
            if (!(authkey.isNullOrEmpty()) && !(authkey.isNullOrBlank()) && isLogin) {

                llHomeNavigationLogin.visibility = View.GONE

                getUserProf(authkey)

               // txtHomeNavigtionName.text = userName

                llUserProfile.visibility = View.VISIBLE
                llLogout.visibility = View.VISIBLE
               // drawer_layout.closeDrawer(GravityCompat.START)
            } else {

                llUserProfile.visibility = View.GONE
                llLogout.visibility = View.GONE
                llHomeNavigationLogin.visibility = View.VISIBLE

            }

        } else {

            llErrorJobList.visibility = View.VISIBLE
            imgListView.isEnabled = false

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == 100) {

            industryFilters = data!!.getStringExtra(Constants.INDUSTRY_FILTERS)
            companiesFilters = data.getStringExtra(Constants.COMPANY_FILTERS)
            jobTitleFilters = data.getStringExtra(Constants.JOB_TITLE_FILTERS)
            jobTypesFilters = data.getStringExtra(Constants.JOBTYPE_FILTER)
            countryFilters = data.getStringExtra(Constants.COUNTRIES_FILTERS)
            stateFilters = data.getStringExtra(Constants.STATES_FILTERS)
            //cityFilters=data!!.getStringExtra(Constants.CITIES_FILTERS)
            hourRateFilters = data.getStringExtra(Constants.HOURS_RATE_FILTERS)
            hourWeekFilters = data.getStringExtra(Constants.HOURS_PER_WEEK)


            currentAddress = data.getStringExtra(Constants.CURRENT_ADDRESS_SELECTED)

            if (!industry.isEmpty()) {
                industry.clear()
            }
            industry.addAll(data.getSerializableExtra("Industry") as ArrayList<IndustryMessage>)
            if (!jobTitle.isEmpty()) {
                jobTitle.clear()
            }
            jobTitle.addAll(data.getSerializableExtra("Job_title") as ArrayList<JobTitleMessage>)
            if (!company.isEmpty()) {
                company.clear()
            }
            company.addAll(data.getSerializableExtra("Company") as ArrayList<ComapnyMessage>)
            if (!countries.isEmpty()) {
                countries.clear()
            }
            countries.addAll(data.getSerializableExtra("Countries") as ArrayList<String>)
            if (!states.isEmpty()) {
                states.clear()
            }
            states.addAll(data.getSerializableExtra("States") as ArrayList<String>)
            if (!cities.isEmpty()) {
                cities.clear()
            }
            //cities.addAll(data!!.getSerializableExtra("Cities") as ArrayList<String>)
            cities.addAll(data.getSerializableExtra("Cities") as ArrayList<CityNames>)

            if (!cities.isEmpty()) {

                cityName.clear()

                for (i in 0..cities.size - 1) {

                    cityName.add(cities.get(i).city)

                }
            }

            if (!hourWeek.isEmpty()) {
                hourWeek.clear()
            }
            hourWeek.addAll(data.getSerializableExtra("HourWeek") as ArrayList<HoursPerWeek>)
            if (!hourRate.isEmpty()) {
                hourRate.clear()
            }
            hourRate.addAll(data.getSerializableExtra("HourRate") as ArrayList<HoursRate>)

            var lastFragment = appPrefence.getString(AppPrefence.SharedPreferncesKeys.FROM_HOME.toString())
            getJobList()


        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResult: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult)
       PermissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResult)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> if (grantResult.size > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission()
            } else {
                Toast.makeText(this@HomeActivity, com.partime.user.R.string.no_location_permission, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    //to exit on double back pressed
    override fun onBackPressed() {
        // super.onBackPressed()
        if (doubleBackToExitPressedOnce && closemap == 0) {
            super.onBackPressed()
            return
        }
        if (doubleBackToExitPressedOnce && closemap == 1) {
            closemap = 0
          //  Toast.makeText(this, "this"+closemap, Toast.LENGTH_SHORT).show()
            imgListView.visibility = View.VISIBLE
            imgMapView.visibility = View.GONE
            imgFilterHome.isEnabled = true
            appPrefence.setString(AppPrefence.SharedPreferncesKeys.FROM_HOME.toString(), "LIST")
            loadFragment(JobListFragment(), jobList)
        }
        if (!doubleBackToExitPressedOnce && closemap == 1) {
            closemap = 0

           // Toast.makeText(this, "this"+closemap, Toast.LENGTH_SHORT).show()
            imgListView.visibility = View.VISIBLE
            imgMapView.visibility = View.GONE
            imgFilterHome.isEnabled = true
            appPrefence.setString(AppPrefence.SharedPreferncesKeys.FROM_HOME.toString(), "LIST")
            loadFragment(JobListFragment(), jobList)
        }

        if (!doubleBackToExitPressedOnce && closemap == 0) {
            Toast.makeText(this, this.resources.getString(R.string.exit_text), Toast.LENGTH_SHORT).show()
        }

        this.doubleBackToExitPressedOnce = true

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }


    private fun infoDialog() {

        var infoDialog = Dialog(this@HomeActivity)

        infoDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        infoDialog.setContentView(R.layout.info_dailog)
        infoDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        infoDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        infoDialog.txtInfoDialog.setText(R.string.not_enrolled)


        infoDialog.btnOkInfoDialog.setOnClickListener {

            infoDialog.dismiss()
        }

        infoDialog.show()
    }






    ///new functions
    fun showGifLoading() {
        val imageView: ImageView = findViewById(R.id.loading)
        Glide.with(this).load(R.drawable.loading).into(imageView)
    }

    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }

}
