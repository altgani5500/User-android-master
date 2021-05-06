package com.partime.user.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ListPopupWindow
import android.widget.RadioButton
import android.widget.Toast
import com.gems.app.utilities.AppValidator
import com.google.android.libraries.places.api.model.Place
import com.image.picker.ImagePicker
import com.partime.user.Adapters.AgeAdapter
import com.partime.user.Adapters.EducationAdapter
import com.partime.user.Adapters.EducationDetailsAdapter
import com.partime.user.Adapters.NationalityDropDownAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.*
import com.partime.user.helpers.ImagePickerUtil
import com.partime.user.helpers.PermissionsUtil
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_edit_profile.*
import net.simplifiedcoding.mvvmsampleapp.util.toast
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class EditProfileActivity : BaseActivity(), View.OnClickListener {
    internal lateinit var permissionsUtil: PermissionsUtil
    var imagesFile: File? = null
    var map = HashMap<String, RequestBody>()
    private lateinit var gender: String

    var nationalityResponse = ArrayList<NationalityMessage>()
    private var nationlityAdapter: NationalityDropDownAdapter? = null
    var nationalityId: Int = 0

    var educationResponse = ArrayList<Education>()
    private var eductionAdapter: EducationAdapter? = null
    var educationId: Int = 0

    var educationDetail = ArrayList<EducationDetail>()
    private var educationDetailsAdapter: EducationDetailsAdapter? = null
    var educationDetailsId: Int = 0

    lateinit var profile: ProfileMessage

    var age = ArrayList<Age>()
    private var ageAdapter: AgeAdapter? = null

    val pingActivityRequestCode = 1001
    var userLat = 0.0
    var userLong = 0.0

    var userCountry: String? = null
    var userState: String? = null
    var userCity: String? = null
    var imagePicker: ImagePicker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        AppValidator.rotateBackArrow(imgBackEditProfile, this@EditProfileActivity)

        profile = intent.getSerializableExtra(Constants.EDIT_PROFILE_RESPONSE) as ProfileMessage

        getNationality()
        getEducation()

        saveProfileDetails(profile)

        dropDownEducationalDetails.setOnClickListener(this)
        dropDownEducationalMore.setOnClickListener(this)
        dropDownNationalityEdit.setOnClickListener(this)
        imgPickPictureEdit.setOnClickListener(this)
        btnSaveEdit.setOnClickListener(this)
        imgBackEditProfile.setOnClickListener(this)
        edtEducationalDetailsEdit.setOnClickListener(this)
        edtEducationalMoreEdit.setOnClickListener(this)
        edtResumeEdit.setOnClickListener(this)
        edtAgeEdit.setOnClickListener(this)
        dropDownAge.setOnClickListener(this)
        llResumeEditProfile.setOnClickListener(this)
        llLocationEditProfile.setOnClickListener(this)
    }

    /*
    Method to get the nationality list
     */

    private fun getNationality() {

        val apiService = ApiService.init()
        val call =
            apiService.getNationalityList(appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()))
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<NationalityResponse> {
            override fun onResponse(
                call: Call<NationalityResponse>,
                response: retrofit2.Response<NationalityResponse>?
            ) {

                if (response != null) {

                    if (response.body()?.code == 200) {

                        nationalityResponse.addAll(response.body()?.message as ArrayList<NationalityMessage>)
                        //get the nationality id...
                        nationalityId = getNationalityId(profile, nationalityResponse)

                    } else {

                        Toast.makeText(
                            this@EditProfileActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }


            }

            override fun onFailure(call: Call<NationalityResponse>, t: Throwable) {

                Toast.makeText(this@EditProfileActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }


        })

    }

    /*
    Method to get education list
     */
    private fun getEducation() {

        var authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        val apiService = ApiService.init()
        val call = apiService.educationDetails(
            "Bearer $authkey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())
        )
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<EducationListResponse> {
            override fun onResponse(
                call: Call<EducationListResponse>,
                response: retrofit2.Response<EducationListResponse>?
            ) {

                if (response != null) {

                    if (response.body()?.code == 200) {

                        //get the education details and education level
                        educationResponse.addAll(response.body()?.message?.education as ArrayList<Education>)
                        educationId = getEducationId(profile, educationResponse)
                        educationDetail.addAll(response.body()?.message?.educationDetail as ArrayList<EducationDetail>)
                        educationDetailsId = getEducationDetailsId(profile, educationDetail)


                    } else {

                        Toast.makeText(
                            this@EditProfileActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }


            }

            override fun onFailure(call: Call<EducationListResponse>, t: Throwable) {

                Toast.makeText(this@EditProfileActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })
    }

    /*
    Method to add eductaion details in list
     */
    private fun getEducationDetailsId(
        profileDetails: ProfileMessage,
        educationDetail: ArrayList<EducationDetail>
    ): Int {
        var eId = 0

        for (i in 0..educationDetail.size - 1) {

            if (profileDetails.educationDetail == educationDetail.get(i).educationDetail) {

                eId = educationDetail.get(i).educationDetailId

            }
        }
        return eId
    }

    /*
        Method to add eductaion id in list
         */
    private fun getEducationId(profileDetails: ProfileMessage, educationResponse: ArrayList<Education>): Int {

        var eId = 0

        for (i in 0..educationResponse.size - 1) {

            if (profileDetails.nation == educationResponse.get(i).education) {

                eId = educationResponse.get(i).educationId

            }
        }
        return eId
    }


    /*
    Method to add ntionality in list
     */
    private fun addNationalityList() {

        nationlityAdapter = NationalityDropDownAdapter(nationalityResponse, this@EditProfileActivity)
        val popupWindow = ListPopupWindow(this@EditProfileActivity)
        popupWindow.setOnItemClickListener { parent, view, position, id ->


            edtNationalityEdit.text = nationalityResponse[position].nationality
            nationalityId = nationalityResponse[position].nationalityId

            popupWindow.dismiss()
        }
        popupWindow.height = 300
        popupWindow.anchorView = edtNationalityEdit
        popupWindow.setAdapter(nationlityAdapter)
        popupWindow.show()

    }

    /*
    Method to save proile details.....
     */
    private fun saveProfileDetails(profileDetails: ProfileMessage) {

        val name = profileDetails.name.split(" ")


        edtFirstNameEdit.setText(name[0])
        edtLastNameEdit.setText(name[1])
        if (profileDetails.gender == resources.getString(R.string.female) || profileDetails.gender ==resources.getString(R.string.s_female)) {

            radioBtnFemaleEdit.isChecked = true
        }
        if (profileDetails.gender == resources.getString(R.string.male) || profileDetails.gender == resources.getString(R.string.s_male)) {

            radioBtnMaleEdit.isChecked = true
        }

        edtNationalityEdit.text = profileDetails.nation
        edtEmailIdEdit.setText(profileDetails.email)

        if (!profileDetails.companyName.isNullOrBlank() || !profileDetails.companyName.isNullOrEmpty()) {

            edtEnterpriseEdit.setText(profileDetails.companyName)

        }else{

            if(!profileDetails.enrolledEnterpriseName.isNullOrEmpty()||!profileDetails.enrolledEnterpriseName.isNullOrBlank()){

                edtEnterpriseEdit.setText(profileDetails.enrolledEnterpriseName)
            }
        }
        if (!profileDetails.branchName.isNullOrBlank() || !profileDetails.branchName.isNullOrEmpty()) {

            edtLocationEdit.setText(profileDetails.branchName)

        }
        if (!profileDetails.education.isNullOrBlank() || !profileDetails.education.isNullOrEmpty()) {

            edtEducationalDetailsEdit.text = profileDetails.education

            if (profileDetails.education.equals(resources.getString(R.string.graduate)) || profileDetails.education.equals(resources.getString(R.string.postgraduate)) || profileDetails.education.equals(
                    resources.getString(R.string.undergraduate)
                )
            ) {

                llEducationMore.visibility = View.VISIBLE

                if (!profileDetails.educationDetail.isNullOrBlank() || !profileDetails.educationDetail.isNullOrEmpty()) {

                    edtEducationalMoreEdit.text = profileDetails.educationDetail

                }
            } else {

                llEducationMore.visibility = View.GONE

            }

        }

        if (!profileDetails.address.isNullOrBlank() || !profileDetails.address.isNullOrEmpty()) {

            edtAddressEdit.text = profileDetails.address

        }
        if (!profileDetails.age.isNullOrEmpty() || !profileDetails.age.isNullOrBlank()) {

            edtAgeEdit.text = profileDetails.age
        }

        if (profileDetails.userDocuments.size > 0) {
            var docs = StringBuffer()
            for (i in 0..profileDetails.userDocuments.size - 1) {

                if (i != profileDetails.userDocuments.size - 1) {

                    docs.append(profileDetails.userDocuments.get(i).documentName).append(" , ")

                } else {

                    docs.append(profileDetails.userDocuments.get(i).documentName)

                }

                edtResumeEdit.text = docs
            }
        }
        if (!profileDetails.profilePicture.equals("")) {
            Picasso.get().load(profileDetails.profilePicture).placeholder(R.drawable.profile_placeholder)
                .into(imgUserPictureEdit)
        }


    }

    /*
    Method to get nationality id
     */
    private fun getNationalityId(
        profileDetails: ProfileMessage,
        nationalityResponse: ArrayList<NationalityMessage>
    ): Int {

        var nId = 0

        for (i in 0..nationalityResponse.size - 1) {

            if (profileDetails.nation == this.nationalityResponse.get(i).nationality) {

                nId = nationalityResponse.get(i).nationalityId

            }
        }
        return nId
    }

    override fun onClick(v: View?) {

        if (v == dropDownNationalityEdit || v == edtNationalityEdit) {

            if (AppValidator.isInternetAvailable(this)) {

                if (!nationalityResponse.isEmpty()) {
                    addNationalityList()

                } else {

                    Toast.makeText(this@EditProfileActivity, R.string.no_data_found, Toast.LENGTH_SHORT).show()

                }


            } else {

                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show()
            }
        }
        if (v == dropDownEducationalDetails || v == edtEducationalDetailsEdit) {

            if (AppValidator.isInternetAvailable(this)) {

                if (!educationResponse.isEmpty()) {
                    addEducationList()

                } else {

                    Toast.makeText(this@EditProfileActivity, R.string.no_data_found, Toast.LENGTH_SHORT).show()
                }


            } else {

                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show()
            }
        }
        if (v == dropDownEducationalMore || v == edtEducationalMoreEdit) {

            if (AppValidator.isInternetAvailable(this)) {

                if (!educationDetail.isEmpty()) {

                    addEducationDetails()

                } else {

                    Toast.makeText(this@EditProfileActivity, R.string.no_data_found, Toast.LENGTH_SHORT).show()

                }


            } else {

                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show()
            }
        }
        if (v == btnSaveEdit) {
            if (AppValidator.isInternetAvailable(this)) {

                if (checkValidations()) {

                    editProfile()
                }


            } else {

                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show()
            }
        }
        if (v == imgPickPictureEdit) {

            pickImageDialog()

        }
        if (v == imgBackEditProfile) {

            onBackPressed()
        }
        if (v == edtAgeEdit || v == dropDownAge) {

            getAgeList()
        }
        if (v == llResumeEditProfile || v == edtResumeEdit) {

            val intent = Intent(this@EditProfileActivity, UploadDocActivity::class.java)
            if (profile.userDocuments.size > 0) {

                intent.putExtra(Constants.DOCUMENTS, profile.userDocuments)
            }
            startActivityForResult(intent, 505)
        }
        if (v == llLocationEditProfile) {

            showPlacePicker()
        }

    }

    /*
    Method to get age list
     */
    private fun getAgeList() {

        age.clear()
        addAges()

        ageAdapter = AgeAdapter(age, this@EditProfileActivity)
        val popupWindow = ListPopupWindow(this@EditProfileActivity)
        popupWindow.setOnItemClickListener { parent, view, position, id ->


            edtAgeEdit.text = age[position].age
            popupWindow.dismiss()
        }
        popupWindow.height = 300
        popupWindow.anchorView = edtAgeEdit
        popupWindow.setAdapter(ageAdapter)
        popupWindow.show()

    }

    /*
    Method to add ages
     */
    private fun addAges() {

        age.add(Age(resources.getString(R.string.age_one)))
        age.add(Age(resources.getString(R.string.age_two)))
        age.add(Age(resources.getString(R.string.age_three)))
        age.add(Age(resources.getString(R.string.age_four)))

    }

    /*
    Method to check validations
     */
    private fun checkValidations(): Boolean {

        if (edtFirstNameEdit.text.isNullOrEmpty() || edtFirstNameEdit.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_fname, Toast.LENGTH_LONG).show()
            return false
        } else if (edtLastNameEdit.text.isNullOrEmpty() || edtLastNameEdit.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_lname, Toast.LENGTH_LONG).show()
            return false
        } else if (!AppValidator.isValidEmail(edtEmailIdEdit.text.toString())) {

            Toast.makeText(this, com.partime.user.R.string.error_invalid_email, Toast.LENGTH_LONG).show()
            return false

        } else if (genderRadioGroupEdit.checkedRadioButtonId == -1) {

            Toast.makeText(this, com.partime.user.R.string.no_gender, Toast.LENGTH_LONG).show()
            return false
        } else {

            return true
        }

    }

    /*
    Method to picck image
     */
    private fun pickImage(mode: String) {

        imagePicker = ImagePicker(this@EditProfileActivity)
        imagePicker!!.setMode(mode)
            .setCompress(true)
            .setCrop(false)
            .setTag("pic_" + System.currentTimeMillis())
            .setImagePickerListener { imageFile, tag ->
                if (imageFile != null) {
                    Log.e("FileName", tag.toString())

                    imagesFile = imageFile
                    Picasso.get().load(imagesFile!!).placeholder(R.drawable.profile_placeholder).into(imgUserPictureEdit)
                }
            }
            .pick()
    }

    /*
    Method to edit profile
     */
    private fun editProfile() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@EditProfileActivity)


        var authkey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        gender = getGender()

        map.put("firstName", getRequestBody(edtFirstNameEdit.text.toString().trim()))
        map.put("lastName", getRequestBody(edtLastNameEdit.text.toString().trim()))
        map.put("gender", getRequestBody(gender.trim()))
        map.put("nationality", getRequestBody(nationalityId.toString().trim()))
        map.put("email", getRequestBody(edtEmailIdEdit.text.toString().trim()))


        if (!edtEnterpriseEdit.text.toString().trim().isEmpty()) {

            map.put("companyName", getRequestBody(edtEnterpriseEdit.text.toString().trim()))

        }
        if (!edtLocationEdit.text.toString().trim().isNullOrBlank() || !edtLocationEdit.text.toString().trim().isNullOrEmpty()) {

            map.put("branchName", getRequestBody(edtLocationEdit.text.toString().trim()))

        }
        if (!edtEducationalDetailsEdit.text.toString().trim().isNullOrEmpty() || !edtEducationalDetailsEdit.text.toString().trim().isNullOrBlank()) {

            map.put("educationId", getRequestBody(educationId.toString().trim()))
        }
        if (!edtEducationalMoreEdit.text.toString().trim().isNullOrBlank() || !edtEducationalMoreEdit.text.toString().trim().isNullOrEmpty()) {

            map.put("educationDetailId", getRequestBody(educationDetailsId.toString().trim()))
        }
        if (!edtAddressEdit.text.toString().isNullOrEmpty() || !edtAddressEdit.text.toString().isNullOrBlank()) {
            map.put("address", getRequestBody(edtAddressEdit.text.toString().trim()))
        }
        if (!edtAgeEdit.text.toString().isNullOrEmpty() || !edtAgeEdit.text.toString().isNullOrBlank()) {

            map.put("age", getRequestBody(edtAgeEdit.text.toString().trim()))
        }
        if (userLat != 0.0) {
            map.put("userLat", getRequestBody(userLat.toString()))
        }
        if (userLong != 0.0) {
            map.put("userLong", getRequestBody(userLong.toString()))
        }
        if (userCountry != null) {

            map.put("country", getRequestBody(userCountry.toString()))
        }
        if (userState != null) {

            map.put("state", getRequestBody(userState.toString()))
        }
        if (userCity != null) {

            map.put("city", getRequestBody(userCity.toString()))

        }
        var bodyProfilePic: MultipartBody.Part? = null


        if (imagesFile != null) {

            var compressedImageFile = Compressor(this).compressToFile(imagesFile)
            val image = RequestBody.create(MediaType.parse("image/*"), compressedImageFile)
            bodyProfilePic = MultipartBody.Part.createFormData("profilePicture", compressedImageFile?.name, image)
        }

        val apiService = ApiService.init()
        val call: Call<EditProfileResponse> = apiService.editProfile(
            "Bearer $authkey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()),
            map,
            bodyProfilePic
        )

        Log.d("REQUEST", map.toString() + "")
        call.enqueue(object : Callback<EditProfileResponse> {
            override fun onResponse(
                call: Call<EditProfileResponse>,
                response: retrofit2.Response<EditProfileResponse>?
            ) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {


                        Toast.makeText(this@EditProfileActivity, response.body()?.message, Toast.LENGTH_LONG).show()

                        val intent = Intent(this@EditProfileActivity, ProfileActivity::class.java)
                        setResult(1005,intent)
                        finish()

                    } else {


                        Toast.makeText(
                            this@EditProfileActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()


                    }

                }

            }

            override fun onFailure(call: Call<EditProfileResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@EditProfileActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    /*
    method to add educcation details in list
     */

    private fun addEducationDetails() {


        educationDetailsAdapter = EducationDetailsAdapter(educationDetail, this@EditProfileActivity)
        val popupWindow = ListPopupWindow(this@EditProfileActivity)
        popupWindow.setOnItemClickListener { parent, view, position, id ->


            edtEducationalMoreEdit.text = educationDetail[position].educationDetail
            educationDetailsId = educationDetail[position].educationDetailId

            popupWindow.dismiss()
        }
        popupWindow.height = 300
        popupWindow.anchorView = edtEducationalMoreEdit
        popupWindow.setAdapter(educationDetailsAdapter)
        popupWindow.show()

    }

    /*
    method to add educcation level in list
     */

    private fun addEducationList() {
        eductionAdapter = EducationAdapter(educationResponse, this@EditProfileActivity)
        val popupWindow = ListPopupWindow(this@EditProfileActivity)
        popupWindow.setOnItemClickListener { parent, view, position, id ->


            edtEducationalDetailsEdit.text = educationResponse[position].education
            educationId = educationResponse[position].educationId

            if (edtEducationalDetailsEdit.text.equals(resources.getString(R.string.graduate)) || edtEducationalDetailsEdit.text.equals(resources.getString(R.string.postgraduate)) || edtEducationalDetailsEdit.text.equals(
                   resources.getString(R.string.undergraduate)
                )
            ) {

                llEducationMore.visibility = View.VISIBLE
            } else {

                llEducationMore.visibility = View.GONE

            }



            popupWindow.dismiss()
        }
        popupWindow.height = 300
        popupWindow.anchorView = edtEducationalDetailsEdit
        popupWindow.setAdapter(eductionAdapter)
        popupWindow.show()
    }

    /*
    Method to get the gender
     */

    private fun getGender(): String {

        val selectedId = genderRadioGroupEdit.checkedRadioButtonId
        var radioButton = findViewById<View>(selectedId) as RadioButton
        return radioButton.text.toString()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (imagePicker != null) {

            imagePicker?.onActivityResult(requestCode, resultCode, data)
        }

        //get the address picked using place picker

        if ((requestCode == pingActivityRequestCode) && (resultCode == Activity.RESULT_OK)) {

            val place: Place? = PingPlacePicker.getPlace(data!!)
            userLat = place?.latLng?.latitude!!
            userLong = place.latLng?.longitude!!
            edtAddressEdit.text = place.name

            var state = place.address?.split(",")
            if (!state.isNullOrEmpty()) {
                Collections.reverse(state)
                for (i in 0 until state.size) {
                    if (i == 0) {
                        userCountry = state[0]
                    }
                    if (i == 1) {

                        userState = state[1]
                    }
                    if (i == 2) {
                        userCity = state[2]
                    }
                }
            }

        }

        if (requestCode == 505 && resultCode == 506) {

            //get the uploaded documents name
            if (data!!.getSerializableExtra(Constants.DOC_NAME) != null) {

                var documents = StringBuffer()
                var docNames = ArrayList<ProfileUserDocument>()

                docNames.addAll(data.getSerializableExtra(Constants.DOC_NAME) as ArrayList<ProfileUserDocument>)

                if (docNames.size > 0) {

                    for (i in 0..docNames.size - 1) {

                        if (i != docNames.size - 1) {

                            documents.append(docNames.get(i).documentName).append(" , ")
                        } else {

                            documents.append(docNames.get(i).documentName)

                        }
                    }

                    edtResumeEdit.text = documents
                } else {

                    edtResumeEdit.setText(R.string.resume)

                }
            }
        }

    }

    private fun getRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), value)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    //hide keyboard on any touch on screen
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)
        return super.onTouchEvent(event)
    }

    // place picker
    private fun showPlacePicker() {

        val builder = PingPlacePicker.IntentBuilder()
        builder.setAndroidApiKey(getString(R.string.api_key))
            .setMapsApiKey(getString(R.string.api_key))
        // If you want to set a initial location
        // rather then the current device location.
        try {
            val placeIntent = builder.build(this)
            startActivityForResult(placeIntent, pingActivityRequestCode)
        } catch (ex: Exception) {
            toast(resources.getString(R.string.no_google_service))
        }
    }

    /*
   Method to show the dialog to select the documnet
    */
    fun pickImageDialog() {

        val items = arrayOf<CharSequence>(resources.getString(R.string.camera), resources.getString(R.string.gallery))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(null)
        builder.setItems(items) { dialog, item ->
            if (items[item] == resources.getString(R.string.camera)) {
                pickImage(ImagePicker.MODE_CAMERA)
            } else if (items[item] == resources.getString(R.string.gallery)) {
                PermissionsUtil.askPermissions(this,
                    PermissionsUtil.CAMERA,
                    PermissionsUtil.STORAGE,
                    object : PermissionsUtil.PermissionListener {
                        override fun onPermissionResult(isGranted: Boolean) {
                            if (isGranted) {
                                pickImage(ImagePicker.MODE_GALLERY)
                            }
                        }
                    })
            }
        }
        builder.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (imagePicker != null) {
            imagePicker?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
       // PermissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
