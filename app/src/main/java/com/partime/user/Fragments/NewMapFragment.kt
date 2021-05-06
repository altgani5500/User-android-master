package com.partime.user.Fragments


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.partime.user.Adapters.JobAdapter
import com.partime.user.Constants.Constants
import com.partime.user.Listeners.OnSnapPositionChangeListener
import com.partime.user.R
import com.partime.user.Responses.*
import com.partime.user.activities.JobDescriptionActivity
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.helpers.SnapOnScrollListener
import com.partime.user.helpers.attachSnapHelperWithListener
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseFragment
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.fragment_new_map.*
import retrofit2.Call
import retrofit2.Callback

class NewMapFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener,
    OnSnapPositionChangeListener {
    var forFilterApply=0 // if filter use then valua is 1 other wise 0

    var industryFilters: String? = null
    var companiesFilters: String? = null
    var jobTitleFilters: String? = null
    var jobTypesFilters: String? = null
    var countryFilters: String? = null
    var stateFilters: String? = null
    var cities = ArrayList<CityNames>()
    var hourWeekFilters: String? = null
    var hourRateFilters: String? = null
    var currentAddress: String? = null
    var city: String? = null

    var userLat = 0.0
    var userLong = 0.0


    var cityName = ArrayList<String>()
    //get current job position
    override fun onSnapPositionChange(position: Int) {
        jobPosition = position + 1

        if (jobListDetails.size > 1) {
            txtJobCountMap.visibility = View.VISIBLE
            txtJobCountMap.text = jobPosition.toString() + "/" + jobListDetails.size.toString()
        } else {
            txtJobCountMap.visibility = View.GONE

        }

    }

    private lateinit var mMap: GoogleMap
    private var mSelectedMarker: Marker? = null

    var jobList = ArrayList<JobDetailsMapMessage>()
    var jobListDetails = ArrayList<JobListMessage>()
    var jobLdetails = ArrayList<JobDetailsMessage>()
    private val mHandler: Handler
    private var jobListAdapter: JobAdapter? = null

    val appPrefence = AppPrefence.INSTANCE

    var jobPosition = 1

    var hashMap = HashMap<String, String>()
    var previous = 0

    private var mAnimation: Runnable = Runnable { }

    init {
        mHandler = Handler()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //to get ccurrent job position in recycler
        var snaphelper = LinearSnapHelper()

        mapListRecycler.attachSnapHelperWithListener(snaphelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, this)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        appPrefence.initAppPreferences(context)

        imgCrossMapRecycler.setOnClickListener(this)

        //preselected filters.........
        var bundle = this.arguments
        if (bundle != null) {

            industryFilters = appPrefence.getString(AppPrefence.SharedPreferncesKeys.industryFilter.toString())
            jobTitleFilters = appPrefence.getString(AppPrefence.SharedPreferncesKeys.jobTitleFilter.toString())
            city = appPrefence.getString(AppPrefence.SharedPreferncesKeys.cityFilter.toString())
            companiesFilters = appPrefence.getString(AppPrefence.SharedPreferncesKeys.companyFilter.toString())
            stateFilters = appPrefence.getString(AppPrefence.SharedPreferncesKeys.stateFilter.toString())
            countryFilters = appPrefence.getString(AppPrefence.SharedPreferncesKeys.countryFilter.toString())
            currentAddress = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLocation.toString())
            hourWeekFilters = appPrefence.getString(AppPrefence.SharedPreferncesKeys.hourweekFilter.toString())
            hourRateFilters = appPrefence.getString(AppPrefence.SharedPreferncesKeys.hourPaidRateFilter.toString())
            jobTypesFilters = appPrefence.getString(AppPrefence.SharedPreferncesKeys.jobtypeFilter.toString())

        }

        rlRecyclerMap.visibility = View.GONE

        //register filter broadcast
        filtersReceiver()
        getJobList()
        Log.d("create", "new")
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.setOnMarkerClickListener(this)

        userLat = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLat.toString()).toDouble()

        userLong = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLong.toString()).toDouble()

        var cameraPosition = CameraPosition.Builder()
            .target(LatLng(userLat, userLong))
            .zoom(12f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

    }

    private fun setMarkerColor(mMap: GoogleMap, i: Int, latlng: LatLng) {
        val v=jobList.get(i).jobList.size

        if (jobList.get(i).jobList.size == 1) {

            if (jobList.get(i).jobList.get(0).jobStatus.equals(resources.getString(R.string.new_job))
                || jobList.get(i).jobList.get(0).jobStatus.equals("New")   ) {

                if (jobList.get(i).jobList.get(0).jobType.equals(resources.getString(R.string.credited_jobs)) ||
                    jobList.get(i).jobList.get(0).jobType.equals("Credited Jobs")) {

                    mMap.addMarker(
                        MarkerOptions().position(latlng).title("").icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.small_blue_location)
                        )
                    )

                } else if (jobList.get(i).jobList.get(0).jobType.equals(resources.getString(R.string.non_credited_jobs))
                    ||  jobList.get(i).jobList.get(0).jobType.equals("Non Credited Jobs")) {

                    mMap.addMarker(
                        MarkerOptions().position(latlng).title("").icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.small_black_location)
                        )
                    )

                }


            }
            if (jobList.get(i).jobList.get(0).jobStatus.equals(resources.getString(R.string.saved))
                ||  jobList.get(i).jobList.get(0).jobStatus.equals("Saved")) {
                mMap.addMarker(
                    MarkerOptions().position(latlng).title("").icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.small_yellow_location)
                    )
                )
            }

            if ( jobList.get(i).jobList.get(0).jobStatus.contains("Viewed") || jobList.get(i).jobList.get(0).jobStatus.equals(resources.getString(R.string.viewed))
                    ) {

                mMap.addMarker(
                    MarkerOptions().position(latlng).title("").icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.small_gray_location)
                    ))

            }

        } else if (jobList[i].jobList.size > 1) {
            mMap.addMarker(
                MarkerOptions().position(latlng).title("").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.orange_location_small)
                )
            )
        }

    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        val start = SystemClock.uptimeMillis()
        val duration = 1000L

        mHandler.removeCallbacks(mAnimation)
        // Starts the bounce animation
        mAnimation = BounceAnimation(start, duration, marker!!, mHandler)
        mHandler.post(mAnimation)

        //mapListRecycler.visibility = View.VISIBLE

        for (i in 0..jobList.size - 1) {

            var latLng = marker.position

            if (jobList.get(i).jobLat.toDouble() == latLng.latitude && jobList.get(i).jobLong.toDouble() == latLng.longitude) {

                //set Adapter.....
                setAdapter(jobList.get(i))


                if (mSelectedMarker != null && previous != 0) {

                    //Log.e("SelectedMarkerCheck", mSelectedMarker.toString())

                    if (jobList.get(previous).jobList.size == 1) {
                        if (jobList.get(previous).jobList.get(0).jobStatus.equals(resources.getString(R.string.new_job))) {

                            if (jobList.get(previous).jobList.get(0).jobType.equals(resources.getString(R.string.credited_jobs))) {

                                mSelectedMarker!!.setIcon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.small_blue_location)
                                )

                                // Log.e("marker", "bluesmall" + mSelectedMarker.toString() +"   "+ jobList.get(previous).jobList.get(0).jobId)

                            } else if (jobList.get(previous).jobList.get(0).jobType.equals(resources.getString(R.string.non_credited_jobs))) {

                                mSelectedMarker!!.setIcon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.small_black_location)
                                )

                                // Log.e("marker", "small_black" + mSelectedMarker.toString() +"   "+ jobList.get(previous).jobList.get(0).jobId)
                            }

                        }

                        if (jobList.get(previous).jobList.get(0).jobStatus.equals(resources.getString(R.string.saved))) {


                            mSelectedMarker!!.setIcon(
                                BitmapDescriptorFactory.fromResource(R.drawable.small_yellow_location)
                            )
                            // Log.e("marker", "small_yellow" + mSelectedMarker.toString() +"   "+ jobList.get(previous).jobList.get(0).jobId)
                        }

                        if (jobList.get(previous).jobList.get(0).jobStatus.equals(resources.getString(R.string.viewed))) {

                            mSelectedMarker!!.setIcon(
                                BitmapDescriptorFactory.fromResource(R.drawable.small_gray_location)
                            )


                            //  Log.e("marker", "small_gray" + mSelectedMarker.toString() +"   "+ jobList.get(previous).jobList.get(0).jobId)
                        }

                    } else {

                        mSelectedMarker!!.setIcon(
                            BitmapDescriptorFactory.fromResource(R.drawable.orange_location_small)
                        )

                        //Log.e("marker", "multi_small" + marker.toString() +"   "+ jobList.get(previous).jobList.get(0).jobId)

                    }
                }

                if (jobList.get(i).jobList.size == 1) {

                    if (jobList.get(i).jobList.get(0).jobStatus.equals(resources.getString(R.string.new_job))) {
                        previous = i
                        if (jobList.get(i).jobList.get(0).jobType.equals(resources.getString(R.string.credited_jobs))) {

                            marker.setIcon(
                                BitmapDescriptorFactory.fromResource(R.drawable.big_blue_location)
                            )


                            // Log.e("marker", "big_blue" + marker.toString() +"   "+ jobList.get(i).jobList.get(0).jobId)
                        } else if (jobList.get(i).jobList.get(0).jobType.equals(resources.getString(R.string.non_credited_jobs))) {

                            marker.setIcon(
                                BitmapDescriptorFactory.fromResource(R.drawable.big_black_location)
                            )

                            //Log.e("marker", "big_black" + marker.toString() +"   "+ jobList.get(i).jobList.get(0).jobId)
                        }
                    }

                    if (jobList.get(i).jobList.get(0).jobStatus.equals(resources.getString(R.string.saved))) {
                        previous = i
                        marker.setIcon(
                            BitmapDescriptorFactory.fromResource(R.drawable.big_yellow_location)
                        )


                        // Log.e("marker", "big_yellow" + marker.toString() +"   "+ jobList.get(i).jobList.get(0).jobId)

                    }

                    if (jobList.get(i).jobList.get(0).jobStatus.equals(resources.getString(R.string.viewed))) {
                        previous = i
                        marker.setIcon(
                            BitmapDescriptorFactory.fromResource(R.drawable.big_gray_location)
                        )
                        // Log.e("marker", "big_gray" + marker.toString() +"   "+ jobList.get(i).jobList.get(0).jobId)
                    }

                } else if (jobList.get(i).jobList.size > 1) {
                    previous = i
                    marker.setIcon(
                        BitmapDescriptorFactory.fromResource(R.drawable.orange_location)
                    )


                    //Log.e("marker", "multi_big" + marker.toString() + "   "+jobList.get(i).jobList.get(0).jobId)

                }
                // Log.e("SelectedMarker", marker.toString())
            }

        }

        mSelectedMarker = marker
        return true
    }

    private fun setAdapter(jobs: JobDetailsMapMessage) {
        jobListDetails.clear()
        jobListDetails.addAll(jobs.jobList)

        jobListAdapter = JobAdapter(jobListDetails,jobLdetails, context, object : JobAdapter.JobCardListener {
            override fun onJobCardListener(position: Int, jobId: Int) {

                val intent = Intent(context, JobDescriptionActivity::class.java)
                intent.putExtra(Constants.JOB_ID, jobId)
                intent.putExtra(Constants.FROM, "map")
                context!!.startActivity(intent)
            }

        }, object : JobAdapter.OnSavedListener {
            override fun onSaved(sjobStatus: String, sjobType: String) {

                setBigMarker(sjobStatus, sjobType)


            }

        })

        var layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        mapListRecycler!!.layoutManager = layoutManager
        mapListRecycler!!.adapter = jobListAdapter

        if (jobListDetails.size > 1) {
            txtJobCountMap.visibility = View.VISIBLE
            txtJobCountMap.text = jobPosition.toString() + "/" + jobListDetails.size.toString()
        } else {
            txtJobCountMap.visibility = View.GONE
        }

        rlRecyclerMap.visibility = View.VISIBLE


    }

    class BounceAnimation(start: Long, duration: Long, marker: Marker, handler: Handler) : Runnable {
        private var mStart: Long = 0
        private var mDuration: Long = 0
        private val mInterpolator: Interpolator
        private val mMarker: Marker
        private val mHandler: Handler

        init {
            mStart = start
            mDuration = duration
            mMarker = marker
            mHandler = handler
            mInterpolator = BounceInterpolator()
        }

        override fun run() {
            val elapsed = SystemClock.uptimeMillis() - mStart
            val t = Math.max(1 - mInterpolator.getInterpolation(elapsed.toFloat() / mDuration), 0f)
            mMarker.setAnchor(0.5f, 1.0f + 1.2f * t)
            if (t > 0.0) {
                // Post again 16ms later.
                mHandler.postDelayed(this, 16L)
            }
        }
    }

    override fun onClick(v: View?) {

        when (v) {

            imgCrossMapRecycler -> {

                rlRecyclerMap.visibility = View.GONE

            }


        }

    }

    private fun getJobList() {

        var progressBar = ProgressBarUtil().showProgressDialog(context!!)

        //close the card view whenever api hit.......
        rlRecyclerMap.visibility = View.GONE

        var userId = appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString())

        if (userId != null && !userId.equals("")) {
            hashMap.put("userId", userId)
        } else {
            hashMap.put("userId", "0")
        }

        if (industryFilters != null) {
            forFilterApply=1 // if filter use then valua is 1 other wise 0
            hashMap.put("industryFilter", industryFilters!!)

        }
        if (jobTitleFilters != null) {
            forFilterApply=1 // if filter use then valua is 1 other wise 0
            hashMap.put("jobTitle", jobTitleFilters!!)

        }
        if (companiesFilters != null) {
            forFilterApply=1 // if filter use then valua is 1 other wise 0
            hashMap.put("company", companiesFilters!!)

        }
        if (jobTypesFilters != null) {
            forFilterApply=1 // if filter use then valua is 1 other wise 0
            hashMap.put("jobType", jobTypesFilters!!)

        }
        if (countryFilters != null) {
            forFilterApply=1 // if filter use then valua is 1 other wise 0
            hashMap.put("country", countryFilters!!)

        }
        if (stateFilters != null) {
            forFilterApply=1 // if filter use then valua is 1 other wise 0
            hashMap.put("state", stateFilters!!)

        }
        if (cities != null) {

            if (!cities.isEmpty()) {

                cityName.clear()

                for (i in 0..cities.size - 1) {

                    cityName.add(cities.get(i).city)

                }
            }
            if (cityName != null) {
                hashMap.put("city", cityName.joinToString().replace(", ", ","))
            }

        } else if (city != null) {
            forFilterApply=1 // if filter use then valua is 1 other wise 0
            hashMap.put("city", city!!)
        }
        if (currentAddress != null && currentAddress.equals("currentLocation")) {
            //previous  = !currentAddress.equals("currentLocation")
            var latitude = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLat.toString())
            var longitude = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLong.toString())
           // if( forFilterApply==1) {
                hashMap.put("desiredLat", latitude)
                hashMap.put("desiredLong", longitude)
           // }

        }
        if (hourRateFilters != null) {

            if (hourRateFilters!!.contains(resources.getString(R.string.none_job))) {

                hashMap.put("hoursPaidRate", resources.getString(R.string.none_of_these))
            } else {
                hashMap.put("hoursPaidRate", hourRateFilters!!)
            }


        }
        if (hourWeekFilters != null) {

            hashMap.put("hoursPerWeek", hourWeekFilters!!)

        }


        val apiService = ApiService.init()
        val call: Call<JobDetailsMap> =
            apiService.getJobDeatilsMap(
                appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()),
                hashMap
            )

        Log.d("REQUEST", hashMap.toString() + "")

        call.enqueue(object : Callback<JobDetailsMap> {
            @SuppressLint("MissingPermission")
            override fun onResponse(
                call: Call<JobDetailsMap>,
                response: retrofit2.Response<JobDetailsMap>?
            ) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        //Toast.makeText(context!!, response.body()?.success.toString(), Toast.LENGTH_LONG)

                        if (!jobList.isEmpty()) {

                            jobList.clear()
                        }
                        jobList.addAll(response.body()?.message as ArrayList<JobDetailsMapMessage>)


                        //set marker

                        //clear pervious existing data.....
                        if (mMap != null) {
                            mMap.clear()
                        }
                        previous = 0
                        //setMarker on current location
                        mMap.isMyLocationEnabled = true
                        //mMap.addMarker(MarkerOptions().position(LatLng(userLat, userLong)).title("").icon(BitmapDescriptorFactory.defaultMarker()))
                        for (i in 0 until jobList.size) {
                            // Add a marker in Sydney and move the camera
                            var latlng = LatLng(jobList[i].jobLat.toDouble(), jobList.get(i).jobLong.toDouble())
                            //set maker color when  fragment attached
                            if(getActivity()!=null&&isAdded){
                                setMarkerColor(mMap, i, latlng)
                            }
                        }


                    } else {

                        //Toast.makeText(context, response.body()?.error_message.toString(), Toast.LENGTH_LONG)

                    }
                }

            }

            override fun onFailure(call: Call<JobDetailsMap>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)
                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    /* broadcast from job description */
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.hasExtra(Constants.HOME_ID)) {
                var applyStatus = intent.getIntExtra(Constants.HOME_APPLY_STATUS, 0)
                var saveStatus = intent.getIntExtra(Constants.HOME_SAVE_STATUS, 0)
                var jobId = intent.getIntExtra(Constants.HOME_ID, 0)
                var jobType = intent.getStringExtra(Constants.HOME_JOB_TYPE)
                var jobStatus = intent.getStringExtra(Constants.HOME_JOB_STATUS)

                //to set apply and save status.......
                appPrefence.setInt(AppPrefence.SharedPreferncesKeys.applyJobHome.toString(), applyStatus)
                appPrefence.setInt(AppPrefence.SharedPreferncesKeys.applyHomeJobId.toString(), jobId)

                appPrefence.setInt(AppPrefence.SharedPreferncesKeys.saveHomeJobId.toString(), jobId)
                appPrefence.setInt(AppPrefence.SharedPreferncesKeys.saveJobHome.toString(), saveStatus)

                if (jobStatus != null || jobType != null) {
                    cardUpdate(applyStatus, saveStatus, jobId, jobStatus, jobType)

                } else {
                    cardUpdate(applyStatus, saveStatus, jobId, null, null)

                }
            }
        }
    }

    private val mSavedJobReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.hasExtra(Constants.SAVED_ID)) {

                var jobId = intent.getIntExtra(Constants.SAVED_ID, 0)

                //if job id exists in job list then update data
                for (i in 0..jobListDetails.size - 1) {

                    if (jobId == jobListDetails.get(i).jobId) {

                        var applyStatus = intent.getIntExtra(Constants.SAVED_APPLY_STATUS, 0)
                        var saveStatus = intent.getIntExtra(Constants.SAVED_SAVE_STATUS, 0)
                        var jobStatus = intent.getStringExtra(Constants.SAVED_JOB_STATUS)

                        var jobType = intent.getStringExtra(Constants.SAVED_JOB_TYPE)

                        if (jobStatus != null || jobType != null) {

                            cardUpdate(applyStatus, saveStatus, jobId, jobStatus, jobType)

                        } else {

                            cardUpdate(applyStatus, saveStatus, jobId, null, null)

                        }
                    }
                }

            }
        }
    }

    private val filtersReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intentFilter: Intent) {

            if (intentFilter.hasExtra(Constants.FILTERS)) {

                //if job id exists in job list then update data
                industryFilters = intentFilter.getStringExtra(Constants.INDUSTRY_FILTERS)
                jobTitleFilters = intentFilter.getStringExtra(Constants.JOB_TITLE_FILTERS)
                companiesFilters = intentFilter.getStringExtra(Constants.COMPANY_FILTERS)
                countryFilters = intentFilter.getStringExtra(Constants.COUNTRIES_FILTERS)
                stateFilters = intentFilter.getStringExtra(Constants.STATES_FILTERS)

                cities.clear()
                cities.addAll(intentFilter.getSerializableExtra("Cities") as ArrayList<CityNames>)
                hourWeekFilters = intentFilter.getStringExtra(Constants.HOURS_PER_WEEK)
                hourRateFilters = intentFilter.getStringExtra(Constants.HOURS_RATE_FILTERS)
                jobTypesFilters = intentFilter.getStringExtra(Constants.JOBTYPE_FILTER)
                currentAddress = intentFilter.getStringExtra(Constants.CURRENT_ADDRESS_SELECTED)

                getJobList()
                Log.d("create", "reciver")
            }
        }
    }

    private fun cardUpdate(applyStatus: Int, saveStatus: Int, jobId: Int, jobStatus: String?, jobType: String?) {

        for (i in 0..jobListDetails.size - 1) {

            if (jobListDetails.get(i).jobId == jobId) {

                if (applyStatus != 5) {
                    jobListDetails.get(i).applyJob = applyStatus

                }
                if (saveStatus != 5) {
                    jobListDetails.get(i).savedJob = saveStatus
                }

                if (jobStatus != null) {

                    jobListDetails.get(i).jobStatus = jobStatus


                }
                if (jobType != null) {

                    jobListDetails.get(i).jobType = jobType

                }
                setBigMarker(jobListDetails.get(i).jobStatus, jobListDetails.get(i).jobType)
            }
        }

        jobListAdapter!!.notifyDataSetChanged()
    }

    private fun setBigMarker(jobStatus: String, jobType: String) {

        if (jobListDetails.size == 1) {


            if (jobStatus.equals(resources.getString(R.string.saved))) {

                mSelectedMarker!!.setIcon(
                    BitmapDescriptorFactory.fromResource(R.drawable.big_yellow_location)
                )

            } else {
                if (jobStatus.equals(resources.getString(R.string.new_job))) {

                    if (jobType.equals(resources.getString(R.string.credited_jobs))) {

                        mSelectedMarker!!.setIcon(
                            BitmapDescriptorFactory.fromResource(R.drawable.big_blue_location)
                        )


                    } else if (jobType.equals(resources.getString(R.string.non_credited_jobs))) {

                        mSelectedMarker!!.setIcon(
                            BitmapDescriptorFactory.fromResource(R.drawable.big_black_location)
                        )

                    }
                } else if (jobStatus.equals(resources.getString(R.string.viewed))) {

                    mSelectedMarker!!.setIcon(
                        BitmapDescriptorFactory.fromResource(R.drawable.big_gray_location)
                    )

                }

            }
        } else if (jobListDetails.size > 1) {

            mSelectedMarker!!.setIcon(
                BitmapDescriptorFactory.fromResource(R.drawable.orange_location)
            )

        }
    }

    private fun jobListReceiver() {
        try {
            LocalBroadcastManager.getInstance(context!!)
                .registerReceiver(mMessageReceiver, IntentFilter(Constants.BROADCAST_HOME))
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }
    }

    private fun jobListDeReceiver() {
        try {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(mMessageReceiver)
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }
    }

    private fun savedJobMapReceiver() {
        try {
            LocalBroadcastManager.getInstance(context!!)
                .registerReceiver(mSavedJobReceiver, IntentFilter(Constants.BRODCAST_SAVED_JOB))
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }
    }

    private fun savedJobMapDeReceiver() {
        try {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(mMessageReceiver)
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }
    }

    private fun filtersReceiver() {
        try {
            LocalBroadcastManager.getInstance(context!!)
                .registerReceiver(filtersReciever, IntentFilter(Constants.BROADCAST_FILTER_MAP))
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }
    }

    private fun filtersDeReceiver() {
        try {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(filtersReciever)
        } catch (ex: Exception) {
            Log.e("Exception", ex.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        jobListReceiver()
        savedJobMapReceiver()
        setSavedStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        jobListDeReceiver()
        savedJobMapDeReceiver()
        filtersDeReceiver()
        setApplyStatus()

    }

    private fun setSavedStatus() {

        var jobId = appPrefence.getInt(AppPrefence.SharedPreferncesKeys.saveHomeJobId.toString(), 0)
        var saveStatus = appPrefence.getInt(AppPrefence.SharedPreferncesKeys.saveJobHome.toString(), 5)

        if (jobId > 0) {

            for (i in 0..jobList.size - 1) {

                for (ii in 0..jobList.get(i).jobList.size - 1) {


                    if (jobList.get(i).jobList.get(ii).jobId == jobId) {

                        if (saveStatus != 5) {
                            jobList.get(i).jobList.get(ii).savedJob = saveStatus
                        }
                    }
                }
                if (jobListAdapter != null) {
                    jobListAdapter!!.notifyDataSetChanged()
                }
            }
        }

        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.saveHomeJobId.toString(), 0)
        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.saveJobHome.toString(), 5)

    }

    private fun setApplyStatus() {

        var jobId = appPrefence.getInt(AppPrefence.SharedPreferncesKeys.applyHomeJobId.toString(), 0)
        var applyStatus = appPrefence.getInt(AppPrefence.SharedPreferncesKeys.applyJobHome.toString(), 5)

        if (jobId > 0) {

            for (i in 0..jobList.size - 1) {

                for (ii in 0..jobList.get(i).jobList.size - 1) {
                    if (jobList.get(i).jobList.get(ii).jobId == jobId) {

                        if (applyStatus != 5) {
                            jobList.get(i).jobList.get(ii).applyJob = applyStatus
                        }
                    }
                }
                if (jobListAdapter != null) {
                    jobListAdapter!!.notifyDataSetChanged()
                }
            }
        }

        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.applyHomeJobId.toString(), 0)
        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.applyJobHome.toString(), 5)

    }

}


