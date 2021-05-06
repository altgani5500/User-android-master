package com.partime.user.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.image.picker.ImagePicker
import com.partime.user.Adapters.MonthAdapter
import com.partime.user.Adapters.TaskTypeAdapter
import com.partime.user.Constants.Constants
import com.partime.user.Listeners.MonthClickListener
import com.partime.user.R
import com.partime.user.Responses.PunchInResponse
import com.partime.user.Responses.wifyResponse.WifyResponse
import com.partime.user.helpers.PermissionsUtil
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_time_log.*
import kotlinx.android.synthetic.main.info_dailog.*
import kotlinx.android.synthetic.main.reason_dialog.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//BY ALTGANI


class TimeLogActivity : BaseActivity(), View.OnClickListener {

    var hmap = HashMap<String, RequestBody>()
    var map = HashMap<String, String>()

    var imagePicker: ImagePicker? = null

    var reasonDialog: Dialog? = null
    var reasonImage: File? = null

    var yearList = ArrayList<String>()
    private var yearAdapter: TaskTypeAdapter? = null

    var selectedYear: String? = null

    var monthsList = ArrayList<String>()
    private var monthAdapter: MonthAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_log)



        AppValidator.rotateBackArrow(imgBackTimeLog, this@TimeLogActivity)

        selectedYear = Calendar.getInstance().get(Calendar.YEAR).toString()
        txtListTimeLog.text = selectedYear
        //to add the years in list
        addYearList()
        //add months in list
        addMonths()
        // to set the months
        setMonthRecycler()

        if (appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_PUNCH_IN.toString())) {

            imgPunchinTimeLog.setImageDrawable(resources.getDrawable(R.drawable.punch_out))
            txtPunchinTimeLog.text = resources.getString(R.string.punch_out)
            txtPunchinTimeLog.setTextColor(resources.getColor(R.color.applied_color))
        }

        txtListTimeLog.setOnClickListener(this)
        imgPunchinTimeLog.setOnClickListener(this)
        imgBackTimeLog.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v) {
            //to show the year list
            txtListTimeLog -> {
               // Toast.makeText(this, "date", Toast.LENGTH_LONG).show()
                getYearList(yearList)
            }

            imgPunchinTimeLog -> {
              //  Toast.makeText(this, "puncu", Toast.LENGTH_LONG).show()
                var wifyName = getWifyNme(this@TimeLogActivity).replace("\"", "")
//                Toast.makeText(this, wifyName, Toast.LENGTH_LONG).show()
//
                if(!wifyName.contains("NA")) {

                    wifyVerify(wifyName)
                }else {


                  //  Toast.makeText(this, "WIFI NOT CONNECTED WITH NETWORK", Toast.LENGTH_LONG).show()
                WifiDialog()
                }


            }

            imgBackTimeLog -> {
                //    Toast.makeText(this, "msgfff", Toast.LENGTH_LONG).show()
                onBackPressed()
            }

        }
    }

    private fun WifiDialog() {

            var infoDialog = Dialog(this)

            infoDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            infoDialog.setContentView(R.layout.network_dialog)
            infoDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            infoDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            infoDialog.btnOkInfoDialog.setOnClickListener {

                infoDialog.dismiss()
            }

            infoDialog.show()

    }


    private fun addMonths() {

        monthsList.clear()

        monthsList.add(resources.getString(R.string.january))
        monthsList.add(resources.getString(R.string.february))
        monthsList.add(resources.getString(R.string.march))
        monthsList.add(resources.getString(R.string.april))
        monthsList.add(resources.getString(R.string.may))
        monthsList.add(resources.getString(R.string.june))
        monthsList.add(resources.getString(R.string.july))
        monthsList.add(resources.getString(R.string.august))
        monthsList.add(resources.getString(R.string.september))
        monthsList.add(resources.getString(R.string.october))
        monthsList.add(resources.getString(R.string.november))
        monthsList.add(resources.getString(R.string.december))

    }

    private fun addYearList() {

        yearList.clear()
        var currentYear = Calendar.getInstance().get(Calendar.YEAR)

        for (year in currentYear downTo (currentYear - 1)) {

            yearList.add(year.toString())

        }

    }

    private fun getYearList(yearList: ArrayList<String>) {
        yearAdapter = TaskTypeAdapter(yearList, this@TimeLogActivity)
        val popupWindow = ListPopupWindow(this@TimeLogActivity)
        popupWindow.setOnItemClickListener { parent, view, position, id ->

            txtListTimeLog.text = yearList[position]
            selectedYear = yearList[position]
            popupWindow.dismiss()
        }
        popupWindow.height = 300
        popupWindow.anchorView = txtListTimeLog
        popupWindow.setAdapter(yearAdapter)
        popupWindow.show()
    }

    private fun setMonthRecycler() {

        monthAdapter = MonthAdapter(
            monthsList,
            selectedYear!!,
            this@TimeLogActivity,
            object : MonthClickListener {
                override fun onMonthClick(month: String, year: String, monthNo: Int) {

                    var intent = Intent(this@TimeLogActivity, MonthlyDataActivity::class.java)
                    intent.putExtra(Constants.SELECTED_MONTH, month)
                    intent.putExtra(Constants.MONTH_NO, monthNo)
                    intent.putExtra(Constants.SELECTED_YEAR, year)
                    startActivity(intent)
                }

            })

        recyclerTimeLog.layoutManager =
            GridLayoutManager(this@TimeLogActivity, 3, RecyclerView.VERTICAL, false)
        recyclerTimeLog.adapter = monthAdapter

    }




    private fun wifyVerify( wifyName: String) {


      //Toast.makeText(this, "HIIIIIII"+wifyName, Toast.LENGTH_LONG).show()

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        var map1 = HashMap<String, String>()

        map1.put("wifiName", wifyName)

        val apiService = ApiService.init()
        val call = apiService.wifyNetworkVerify("Bearer $authKey", map1)
        call.enqueue(object : Callback<WifyResponse> {
            override fun onFailure(call: Call<WifyResponse>, t: Throwable) {

                Toast.makeText(this@TimeLogActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<WifyResponse>, response: Response<WifyResponse>) {
                if (response != null) {
                    if (response.body()?.code == 200) {
/*
                        Toast.makeText(
                            this@TimeLogActivity,
                            response.body()?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()*/
                        if (txtPunchinTimeLog.text.equals(resources.getString(R.string.punch_in))) {
                            punchIn()
                        } else {
                            punchOutStatus()
                        }
                    } else if (response.body()?.code == 422) {
                        infoDialog(response.body()?.error_message.toString())
                        /*Toast.makeText(
                            this@TimeLogActivity,
                            response.body()?.error_message.toString() + wifyName,
                            Toast.LENGTH_LONG
                        ).show()*/

                    } else {
                        Toast.makeText(
                            this@TimeLogActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

            }
        })


    }

    private fun punchIn() {
        map.clear()
        var progressBar = ProgressBarUtil().showProgressDialog(this@TimeLogActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val lat = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLat.toString())
        val long = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLong.toString())

        var inTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        var inDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())


//        Toast.makeText(
//                this@TimeLogActivity,
//               inDate+"---------"+inTime,
//                Toast.LENGTH_LONG
//        ).show()


        map.put("inTime", inTime)
        map.put("inDate", inDate)
        map.put("lat", lat)
        map.put("long", long)

        val apiService = ApiService.init()
        val call = apiService.punchIn("Bearer $authKey", map)
        call.enqueue(object : Callback<PunchInResponse> {
            override fun onResponse(
                call: Call<PunchInResponse>,
                response: retrofit2.Response<PunchInResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        Toast.makeText(
                            this@TimeLogActivity,
                            response.body()?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                        appPrefence.setBoolean(
                            AppPrefence.SharedPreferncesKeys.IS_PUNCH_IN.toString(),
                            true
                        )
                        imgPunchinTimeLog.setImageDrawable(resources.getDrawable(R.drawable.punch_out))
                        txtPunchinTimeLog.text = resources.getString(R.string.punch_out)
                        txtPunchinTimeLog.setTextColor(resources.getColor(R.color.applied_color))

                    } else if (response.body()?.code == 421) {
                        infoDialog(response.body()?.error_message.toString())
                    } else {

                        Toast.makeText(
                            this@TimeLogActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<PunchInResponse>, t: Throwable) {

                Toast.makeText(this@TimeLogActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    private fun reasonDialog(status: String) {

        reasonDialog = Dialog(this@TimeLogActivity)
        reasonDialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
        reasonDialog!!.setContentView(R.layout.reason_dialog)
        reasonDialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        reasonDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        reasonDialog!!.imgAttachReasonDialog.setOnClickListener {

            openGallery()
        }
        reasonDialog!!.btnSubmitReasonDialog.setOnClickListener {

            if (validations()) {
                punchOut(status, reasonDialog!!.edtReasonDialog.text.toString(), reasonImage)
            }

        }

        reasonDialog!!.show()
    }

    private fun validations(): Boolean {

        if (reasonDialog!!.edtReasonDialog.text.isNullOrEmpty() || reasonDialog!!.edtReasonDialog.text.isNullOrBlank()) {

            Toast.makeText(this, R.string.no_reason, Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }

    }

    private fun punchOutStatus() {

        map.clear()

        var progressBar = ProgressBarUtil().showProgressDialog(this@TimeLogActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        var outTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        var outDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        map.put("outDate", outDate)
        map.put("outTime", outTime)

        val apiService = ApiService.init()
        val call = apiService.punchOutStatus("Bearer $authKey", map)
        call.enqueue(object : Callback<PunchInResponse> {
            override fun onResponse(
                call: Call<PunchInResponse>,
                response: retrofit2.Response<PunchInResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        if (response.body()?.message.equals("Over Time") || response.body()?.message.equals(
                                "Early Hour"
                            )
                        ) {

                            reasonDialog(response.body()?.message!!)
                        } else {

                            //punch out.............
                            punchOut(response.message(), "", null)
                        }

                    } else {

                        Toast.makeText(
                            this@TimeLogActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<PunchInResponse>, t: Throwable) {

                Toast.makeText(this@TimeLogActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    private fun punchOut(status: String, reason: String, reasonImage: File?) {

        var progressBar = ProgressBarUtil().showProgressDialog(this@TimeLogActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val lat = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLat.toString())
        val long = appPrefence.getString(AppPrefence.SharedPreferncesKeys.currentLong.toString())

        var outTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        var outDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        hmap.put("outTime", getRequestBody(outTime))
        hmap.put("outDate", getRequestBody(outDate))
        hmap.put("outLat", getRequestBody(lat))
        hmap.put("outLong", getRequestBody(long))
        hmap.put("status", getRequestBody(status))
        hmap.put("reason", getRequestBody(reason))


        var reasonPic: MultipartBody.Part? = null

        if (reasonImage != null) {

            var compressedImageFile = Compressor(this).compressToFile(reasonImage)
            val image = RequestBody.create(MediaType.parse("image/*"), compressedImageFile)
            reasonPic = MultipartBody.Part.createFormData(
                "profilePicture",
                compressedImageFile?.name,
                image
            )
        }

        val apiService = ApiService.init()
        val call = apiService.punchOut("Bearer $authKey", hmap, reasonPic)
        call.enqueue(object : Callback<PunchInResponse> {
            override fun onResponse(
                call: Call<PunchInResponse>,
                response: retrofit2.Response<PunchInResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        Toast.makeText(
                            this@TimeLogActivity,
                            response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()
                        reasonDialog!!.dismiss()


                        appPrefence.setBoolean(
                            AppPrefence.SharedPreferncesKeys.IS_PUNCH_IN.toString(),
                            false
                        )
                        imgPunchinTimeLog.setImageDrawable(resources.getDrawable(R.drawable.punch_in))
                        txtPunchinTimeLog.text = resources.getString(R.string.punch_in)
                        txtPunchinTimeLog.setTextColor(resources.getColor(R.color.blue))

                    } else {

                        Toast.makeText(
                            this@TimeLogActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<PunchInResponse>, t: Throwable) {

                Toast.makeText(this@TimeLogActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    /*
    Method to select reason
     */
    fun openGallery() {
        imagePicker = ImagePicker(this@TimeLogActivity)
        imagePicker!!.setMode(ImagePicker.MODE_GALLERY)
            .setCompress(true)
            .setCrop(false)
            .setTag("pic_" + System.currentTimeMillis())
            .setImagePickerListener { imageFile, tag ->
                if (imageFile != null) {
                    Log.e("FileName", tag.toString())

                    reasonImage = imageFile
                    //to set image
                    reasonDialog!!.imgReasonDialog.visibility = View.VISIBLE
                    Picasso.get().load(imageFile).placeholder(R.drawable.profile_placeholder)
                        .into(reasonDialog!!.imgReasonDialog)
                }
            }
            .pick()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (imagePicker != null) {

            imagePicker?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        PermissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (imagePicker != null) {

            imagePicker?.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun getRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), value)
    }

    private fun infoDialog(errorMsg: String) {

        var infoDialog = Dialog(this@TimeLogActivity)

        infoDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        infoDialog.setContentView(R.layout.info_dailog)
        infoDialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        infoDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        infoDialog.txtInfoDialog.text =
            errorMsg + "\n\n" + resources.getString(R.string.no_network_msg)


        infoDialog.btnOkInfoDialog.setOnClickListener {

            var intent = Intent(this@TimeLogActivity, AddNetworkActivity::class.java)
            startActivity(intent)
            infoDialog.dismiss()
        }

        infoDialog.show()
    }


    // get wify name from connect
    private fun getWifyNme(context: Context): String {
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
