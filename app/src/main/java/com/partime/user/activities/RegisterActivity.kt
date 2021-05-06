package com.partime.user.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ListPopupWindow
import android.widget.RadioButton
import android.widget.Toast
import com.gems.app.utilities.AppValidator
import com.gems.app.utilities.AppValidator.isValidEmail
import com.gems.app.utilities.AppValidator.isValidPassword
import com.image.picker.ImagePicker
import com.partime.user.Adapters.NationalityDropDownAdapter
import com.partime.user.R
import com.partime.user.Responses.NationalIdVerificationRespose
import com.partime.user.Responses.NationalityMessage
import com.partime.user.Responses.NationalityResponse
import com.partime.user.Responses.SignupResponse
import com.partime.user.helpers.ImagePickerUtil
import com.partime.user.helpers.PermissionsUtil
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.helpers.Utilities
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import com.parttime.user.uicomman.auth.LoginActivity
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File


class RegisterActivity : BaseActivity(), View.OnClickListener {


    var imagesFile: File? = null
    var map = HashMap<String, RequestBody>()
    var hmap = HashMap<String, String>()
    var nationalityResponse = ArrayList<NationalityMessage>()
    private var adapter: NationalityDropDownAdapter? = null
    private lateinit var gender: String
    var nationalityId: Int = 0
    lateinit var instance: RegisterActivity
    var imagePicker: ImagePicker? = null
    var isNationalIdExist=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.partime.user.R.layout.activity_register)

        instance = this@RegisterActivity

        if (appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()) == "ar") {

            imgBackRegister.visibility = View.GONE
        }

        getNationality()

        //remove the space from the user name
        edtUserName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                var result = s.toString().replace(" ", "")
                if (!s.toString().equals(result)) {
                    edtUserName.setText(result)
                    edtUserName.setSelection(result.length)
                }
            }

        })

        //remove the space from the user name
        edtNationalId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s!!.length==10){

                    checkAvailability()
                }
            }

        })

        edtPassword.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {

                Toast.makeText(this, com.partime.user.R.string.invalid_password, Toast.LENGTH_LONG).show()

            }

        }

        txtLoginHere.setOnClickListener(this)
        btnRegisterSubmit.setOnClickListener(this)
        imgEditProfilePic.setOnClickListener(this)
        imgNationalityDropDown.setOnClickListener(this)
        edtNationality.setOnClickListener(this)
        imgBackRegister.setOnClickListener(this)
        txtTermsNconditions.setOnClickListener(this)


    }

    private fun checkAvailability() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@RegisterActivity)

        hmap.clear()

        hmap.put("nationalId",edtNationalId.text.toString())

        val lang=appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())
        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        val apiService = ApiService.init()
        val call =
            apiService.checkNationalId("Bearer $authKey",lang,hmap)
        Log.d("REQUEST", call.toString() + "")
        call.enqueue(object : Callback<NationalIdVerificationRespose> {
            override fun onResponse(
                call: Call<NationalIdVerificationRespose>,
                response: retrofit2.Response<NationalIdVerificationRespose>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        isNationalIdExist=false
                      /*  Toast.makeText(
                            this@RegisterActivity,
                            response.body()?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()*/

                    } else {

                        isNationalIdExist=true

                        Toast.makeText(
                            this@RegisterActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }


            }

            override fun onFailure(call: Call<NationalIdVerificationRespose>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@RegisterActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }
        })
    }

    /*
    Method to get the selected gender
     */
    private fun getGender(): String {

        val selectedId = genderRadioGroup.checkedRadioButtonId
        val radioButton = findViewById<View>(selectedId) as RadioButton
        return radioButton.text.toString()

    }

    /*
    Method to get the nationalities
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


                    } else {

                        Toast.makeText(
                            this@RegisterActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }


            }

            override fun onFailure(call: Call<NationalityResponse>, t: Throwable) {

                Toast.makeText(this@RegisterActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }
        })

    }

    /*
    Method to add nationality in list
     */
    private fun addNationalityList() {

        adapter = NationalityDropDownAdapter(nationalityResponse, this@RegisterActivity)
        val popupWindow = ListPopupWindow(this@RegisterActivity)
        popupWindow.setOnItemClickListener { parent, view, position, id ->


            edtNationality.text = nationalityResponse[position].nationality
            nationalityId = nationalityResponse[position].nationalityId

            popupWindow.dismiss()
        }
        popupWindow.height = 300
        popupWindow.anchorView = edtNationality
        popupWindow.setAdapter(adapter)
        popupWindow.show()

    }


    override fun onClick(v: View?) {

        if (v == txtLoginHere) {

            var intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            //clearData()
            startActivity(intent)
            finish()

        }
        if (v == btnRegisterSubmit) {

            //hide the keypad when done
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(btnRegisterSubmit.windowToken, 0)

            if (checkValidation()) {

                if (AppValidator.isInternetAvailable(this)) {
                    registerUser()
                } else {

                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show()

                }
            }

        }
        if (v == imgEditProfilePic) {

            pickImageDialog()
        }
        if (v == imgNationalityDropDown || v == edtNationality) {

            if (AppValidator.isInternetAvailable(this)) {

                if (!nationalityResponse.isEmpty()) {
                    addNationalityList()
                } else {

                    Toast.makeText(this@RegisterActivity, R.string.no_data_found, Toast.LENGTH_SHORT).show()
                }


            } else {

                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show()
            }

        }

        if (v == txtTermsNconditions) {
            val intent = Intent(this@RegisterActivity, TermsConditionsActivity::class.java)
            startActivity(intent)
        }
        if (v == imgBackRegister) {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        finish()
    }

    /*
    Method to register user
     */
    private fun registerUser() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@RegisterActivity)

        gender = getGender()

        map.put("firstName", getRequestBody(edtFirstName.text.toString().trim()))
        map.put("lastName", getRequestBody(edtLastName.text.toString().trim()))
        map.put("gender", getRequestBody(gender.trim()))
        map.put("nationality", getRequestBody(nationalityId.toString().trim()))
        map.put("nationalId", getRequestBody(edtNationalId.text.toString().trim()))
        map.put("email", getRequestBody(edtEmailId.text.toString().trim()))
        map.put("userName", getRequestBody(edtUserName.text.toString().trim()))
        map.put("password", getRequestBody(edtPassword.text.toString().trim()))


        var bodyProfilePic: MultipartBody.Part? = null


        if (imagesFile != null) {

            var compressedImageFile = Compressor(this).compressToFile(imagesFile)
            val image = RequestBody.create(MediaType.parse("image/*"), compressedImageFile)
            bodyProfilePic = MultipartBody.Part.createFormData("profilePicture", compressedImageFile?.name, image)
        }

        val apiService = ApiService.init()
        val call: Call<SignupResponse> = apiService.register(
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()),
            map,
            bodyProfilePic
        )

        Log.d("REQUEST", map.toString() + "")
        call.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(
                call: Call<SignupResponse>,
                response: retrofit2.Response<SignupResponse>?
            ) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        var va: Int = response.body()!!.message.userId

                        Toast.makeText(this@RegisterActivity, R.string.signup_succcessful, Toast.LENGTH_LONG)
                        saveUserDetails(response.body())
                        Utilities.clearAllgoToActivity(this@RegisterActivity, HomeActivity::class.java)
                        finishAffinity()

                    } else {


                        Toast.makeText(
                            this@RegisterActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()


                    }

                }

            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@RegisterActivity, R.string.no_internet, Toast.LENGTH_LONG).show()

            }

        })

    }

    /*
    Method to save user details
     */

    private fun saveUserDetails(responses: SignupResponse?) {

        appPrefence.setBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString(), true)
        appPrefence.setString(
            AppPrefence.SharedPreferncesKeys.USER_ID.toString(),
            responses?.message?.userId.toString()
        )
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString(), responses?.message?.apiToken)
    }

    private fun pickImage(mode:String) {
        imagePicker = ImagePicker(this@RegisterActivity)
        imagePicker!!.setMode(mode)
            .setCompress(true)
            .setCrop(false)
            .setTag("pic_" + System.currentTimeMillis())
            .setImagePickerListener { imageFile, tag ->
                if (imageFile != null) {
                    Log.e("FileName", tag.toString())

                    imagesFile = imageFile
                    Picasso.get().load(imagesFile!!).placeholder(R.drawable.profile_placeholder).into(imgUserProfilePicture)
                }
            }
            .pick()

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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

    /*
    Method to check validations
     */
    private fun checkValidation(): Boolean {

        if (edtFirstName.text.isNullOrEmpty() || edtFirstName.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_fname, Toast.LENGTH_LONG).show()
            return false
        } else if (edtLastName.text.isNullOrEmpty() || edtLastName.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_lname, Toast.LENGTH_LONG).show()
            return false
        } else if (genderRadioGroup.checkedRadioButtonId == -1) {

            Toast.makeText(this, com.partime.user.R.string.no_gender, Toast.LENGTH_LONG).show()
            return false
        } else if (edtNationality.text.isNullOrEmpty() || edtNationality.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_nationality, Toast.LENGTH_LONG).show()
            return false
        } else if (edtNationalId.text.isNullOrEmpty() || edtNationalId.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_national_id, Toast.LENGTH_LONG).show()
            return false
        } else if (edtNationalId.text.length != 10) {

            Toast.makeText(this, com.partime.user.R.string.valid_national_id, Toast.LENGTH_LONG).show()
            return false
        }else if(isNationalIdExist){

            Toast.makeText(this,R.string.national_id_exist, Toast.LENGTH_LONG).show()
            return false
        }else if (edtEmailId.text.isNullOrEmpty() || edtEmailId.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_email, Toast.LENGTH_LONG).show()
            return false
        } else if (!isValidEmail(edtEmailId.text.toString())) {

            Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_LONG).show()
            return false

        } else if (edtUserName.text.isNullOrEmpty() || edtUserName.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_user_name, Toast.LENGTH_LONG).show()
            return false
        } else if (edtPassword.text.isNullOrEmpty() || edtPassword.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_password, Toast.LENGTH_LONG).show()
            return false
        } else if (!isValidPassword(edtPassword.text.toString())) {

            Toast.makeText(this, com.partime.user.R.string.invalid_password, Toast.LENGTH_LONG).show()
            return false
        } else if (edtConfirmPassword.text.isNullOrEmpty() || edtConfirmPassword.text.isNullOrBlank()) {

            Toast.makeText(this, com.partime.user.R.string.no_confrim_password, Toast.LENGTH_LONG).show()
            return false
        } else if (!edtConfirmPassword.text.toString().equals(edtPassword.text.toString())) {

            Toast.makeText(this, com.partime.user.R.string.error_password_not_match, Toast.LENGTH_LONG).show()
            return false
        } else if (!checkBoxConfirmation.isChecked) {

            Toast.makeText(this, com.partime.user.R.string.no_confirmation, Toast.LENGTH_LONG).show()
            return false

        } else if (!checkBoxConfirmation.isChecked || !checkBoxAgreement.isChecked) {

            Toast.makeText(this, com.partime.user.R.string.no_agree, Toast.LENGTH_LONG).show()
            return false

        } else {

            return true
        }

    }

    //hide keyboard on any touch on screen
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)
        return super.onTouchEvent(event)
    }

    /*
Method to show the dialog to select the documnet
 */
    fun pickImageDialog() {

        val items = arrayOf<CharSequence>(resources.getString(R.string.camera), resources.getString(R.string.gallery))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(null)
        builder.setItems(items) { dialog, item ->
            if (items[item] ==resources.getString(R.string.camera)) {

                pickImage(ImagePicker.MODE_CAMERA)
            } else if (items[item] == resources.getString(R.string.gallery)) {

                pickImage(ImagePicker.MODE_GALLERY)

            }
        }
        builder.show()
    }
}
