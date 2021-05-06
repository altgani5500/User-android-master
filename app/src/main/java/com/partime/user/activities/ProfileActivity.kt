package com.partime.user.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.ResumeAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.ProfileMessage
import com.partime.user.Responses.ProfileResponse
import com.partime.user.Responses.ProfileUserDocument
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.expand_profile.*
import retrofit2.Call
import retrofit2.Callback


class ProfileActivity : BaseActivity(), View.OnClickListener {

    var profileDetails: ProfileMessage? = null
    private var adapter: ResumeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        AppValidator.rotateBackArrow(imgProfileBack, this@ProfileActivity)

        validateNet()

        imgFullprofile.setOnClickListener(this)
        imgProfileBack.setOnClickListener(this)
        txtEditProfile.setOnClickListener(this)
        imgUserPic.setOnClickListener(this)
        btnRetryProfile.setOnClickListener(this)
    }
    /*
    Method to save profile details
     */
    private fun saveProfileDetails(profileDetails: ProfileMessage) {

        txtProfileName.text = profileDetails.name
        txtProfileGender.text = profileDetails.gender
        txtProfileNationality.text = profileDetails.nation
        txtProfileEmail.text = profileDetails.email

        if (!profileDetails.companyName.isNullOrBlank() || !profileDetails.companyName.isNullOrEmpty()) {
            txtProfileCompanyName.text = profileDetails.companyName
        }else if(!profileDetails.enrolledEnterpriseName.isNullOrBlank() || !profileDetails.enrolledEnterpriseName.isNullOrEmpty()){
            txtProfileCompanyName.text = profileDetails.enrolledEnterpriseName
        }
        if (!profileDetails.branchName.isNullOrBlank() || !profileDetails.branchName.isNullOrEmpty()) {
            txtProfileBranch.text = profileDetails.branchName

        }
        if (!profileDetails.education.isNullOrBlank() || !profileDetails.education.isNullOrEmpty()) {
            txtProfileEducationLevel.text = profileDetails.education

        }
        if (txtProfileEducationLevel.text.equals(R.string.graduate)|| txtProfileEducationLevel.text.equals(R.string.postgraduate) || txtProfileEducationLevel.text.equals(R.string.undergraduate)) {

            llProfileEduMore.visibility = View.VISIBLE
            if (!profileDetails.educationDetail.isNullOrBlank() || !profileDetails.educationDetail.isNullOrEmpty()) {
                txtMoreEduDetails.text = profileDetails.educationDetail

            }
        } else {

            llProfileEduMore.visibility = View.GONE
        }

        if (!profileDetails.address.isNullOrBlank() || !profileDetails.address.isNullOrEmpty()) {
            txtLocationProfile.text = profileDetails.address
        }
        if (!profileDetails.age.isNullOrBlank() || !profileDetails.age.isNullOrEmpty()) {

            txtProfileAge.text = profileDetails.age
        }
        //txtProfileCv.text=profileDetails.userDocuments.

        if (profileDetails.userDocuments.size > 0) {

            txtProfileCv.visibility = View.GONE

            var userDoc = ArrayList<ProfileUserDocument>()
            userDoc.addAll(profileDetails.userDocuments)
            adapter = ResumeAdapter(userDoc, this)

            recyclerCvProfile.visibility = View.VISIBLE
            recyclerCvProfile!!.layoutManager =
                LinearLayoutManager(this@ProfileActivity, RecyclerView.VERTICAL, false)
            recyclerCvProfile!!.itemAnimator = DefaultItemAnimator()
            recyclerCvProfile!!.adapter = adapter
        }

        if (!profileDetails.profilePicture.equals("")) {
            Picasso.get().load(profileDetails.profilePicture).placeholder(R.drawable.profile_placeholder)
                .into(imgUserPic)

        }

    }

    override fun onClick(v: View?) {

        if (v == imgFullprofile) {

            taskCardView.visibility = View.VISIBLE
            taskHistoryCardView.visibility = View.VISIBLE
            imgFullprofile.visibility = View.GONE

        }
        if (v == imgProfileBack) {
            onBackPressed()
        }
        if (v == txtEditProfile) {

            val intent = Intent(this@ProfileActivity, EditProfileActivity::class.java)
            intent.putExtra(Constants.EDIT_PROFILE_RESPONSE, profileDetails)
            startActivityForResult(intent,1005)
        }
        if (v == imgUserPic) {
            if (!profileDetails?.profilePicture.equals("")) {

                profilePicDialog()

            }
        }
        if (v == btnRetryProfile) {

            llErrorprofile.visibility = View.GONE
            validateNet()
        }

    }
    /*
    Method to open the zoomable profile picture
     */
    private fun profilePicDialog() {

        var profilePicDialog = Dialog(this@ProfileActivity)

        profilePicDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        profilePicDialog.setContentView(R.layout.expand_profile)
        profilePicDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        profilePicDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        Picasso.get().load(profileDetails?.profilePicture).into(profilePicDialog.imgPicDialog)
        profilePicDialog.btnCanclePicDialog.setOnClickListener {

            profilePicDialog.dismiss()
        }

        profilePicDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(11)
        finish()
    }
    /*
    Method to validate network
     */
    private fun validateNet() {

        if (AppValidator.isInternetAvailable(this@ProfileActivity)) {

            getUserProfile()

        } else {

            llUserProfile.visibility = View.GONE
            llErrorprofile.visibility = View.VISIBLE
        }
    }
    /*
    Method to get the prdfile details
     */
    private fun getUserProfile() {

        var progressBar = ProgressBarUtil().showProgressDialog(this)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        val apiService = ApiService.init()
        val call: Call<ProfileResponse> = apiService.getProfileDetails(
            "Bearer $authKey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()))

        call.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: retrofit2.Response<ProfileResponse>?) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        Toast.makeText(this@ProfileActivity, response.body()?.success.toString(), Toast.LENGTH_LONG)
                        txtEditProfile.isEnabled = true

                        profileDetails = response.body()?.message

                        llUserProfile.visibility = View.VISIBLE
                        saveProfileDetails(profileDetails!!)


                    } else {

                        Toast.makeText(
                            this@ProfileActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        )

                    }
                }

            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(this@ProfileActivity, R.string.no_internet, Toast.LENGTH_LONG)
                llUserProfile.visibility = View.GONE
                llErrorprofile.visibility = View.VISIBLE
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1005&&resultCode==1005){

            validateNet()
        }
    }
}


