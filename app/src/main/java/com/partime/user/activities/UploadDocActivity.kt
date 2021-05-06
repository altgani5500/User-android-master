package com.partime.user.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.gems.app.utilities.AppValidator
import com.image.picker.ImagePicker
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.DeleteResponse
import com.partime.user.Responses.ProfileResponse
import com.partime.user.Responses.ProfileUserDocument
import com.partime.user.Responses.UploadDocResponse
import com.partime.user.helpers.PermissionsUtil
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_upload_doc.*
import kotlinx.android.synthetic.main.expand_profile.*
import kotlinx.android.synthetic.main.logout_dialog.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File


class UploadDocActivity : BaseActivity(), View.OnClickListener {

    var resumeFile: String? = null
    var certificateOne: String? = null
    var certificateTwo: String? = null
    var certificateThree: String? = null
    var certificateFour: String? = null
    var certificateFive: String? = null
    var certificateSix: String? = null
    var certificateSeven: String? = null
    var certificateEight: String? = null
    var certificateNine: String? = null
    var certificateTen: String? = null
    var resumeId = 0
    var oneId = 0
    var twoId = 0
    var threeId = 0
    var fourId = 0
    var fiveId = 0
    var sixId = 0
    var sevenId = 0
    var eightId = 0
    var nineId = 0
    var tenId = 0

    var documents = ArrayList<ProfileUserDocument>()

    var imagePicker: ImagePicker? = null

    val map = HashMap<String, RequestBody>()
    val hmap = HashMap<String, String>()

    var documentsList = ArrayList<ProfileUserDocument>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_doc)

        AppValidator.rotateBackArrow(imgBackUploadDoc, this@UploadDocActivity)

        //to get pre uploaded documents
        if (intent.getSerializableExtra(Constants.DOCUMENTS) != null) {

            documents = intent.getSerializableExtra(Constants.DOCUMENTS) as ArrayList<ProfileUserDocument>

            if (documents.size > 0) {

                getDocuments(documents)
            }
        }

        imgBackUploadDoc.setOnClickListener(this)
        llCvResumeUpload.setOnClickListener(this)
        llOneCertificate.setOnClickListener(this)
        llTwoCertificate.setOnClickListener(this)
        llThreeCertificate.setOnClickListener(this)
        llFourCertificate.setOnClickListener(this)
        llFiveCertificate.setOnClickListener(this)
        llSixCertificate.setOnClickListener(this)
        llSevenCertificate.setOnClickListener(this)
        llEightCertificate.setOnClickListener(this)
        llNineCertificate.setOnClickListener(this)
        llTenCertificate.setOnClickListener(this)
        imgRemoveCv.setOnClickListener(this)
        imgCrossOne.setOnClickListener(this)
        imgCrossTwo.setOnClickListener(this)
        imgCrossThree.setOnClickListener(this)
        imgCrossFour.setOnClickListener(this)
        imgCrossFive.setOnClickListener(this)
        imgCrossSix.setOnClickListener(this)
        imgCrossSeven.setOnClickListener(this)
        imgCrossEight.setOnClickListener(this)
        imgCrossNine.setOnClickListener(this)
        imgCrossTen.setOnClickListener(this)
    }

    /*
    Method to get and set the pre uploaded documents
     */
    private fun getDocuments(documents: ArrayList<ProfileUserDocument>) {

        for (i in 0..documents.size - 1) {

            if (documents.get(i).documentName.contains("resume")) {

                txtCv.visibility = View.GONE

                resumeFile = setDoc(
                    imgCv,
                    txtCvUploadResume,
                    imgRemoveCv,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                resumeId = documents.get(i).documentId

            }
            if (documents.get(i).documentName.contains("certificate1")) {

                certificateOne = setDoc(
                    imgOne,
                    txtCertificateOne,
                    imgCrossOne,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                oneId = documents.get(i).documentId

            }
            if (documents.get(i).documentName.contains("certificate2")) {

                certificateTwo = setDoc(
                    imgTwo,
                    txtCertificateTwo,
                    imgCrossTwo,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                twoId = documents.get(i).documentId

            }
            if (documents.get(i).documentName.contains("certificate3")) {

                certificateThree = setDoc(
                    imgThree,
                    txtCertificateThree,
                    imgCrossThree,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                threeId = documents.get(i).documentId

            }
            if (documents.get(i).documentName.contains("certificate4")) {

                certificateFour = setDoc(
                    imgFour,
                    txtCertificateFour,
                    imgCrossFour,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                fourId = documents.get(i).documentId

            }
            if (documents.get(i).documentName.contains("certificate5")) {

                certificateFive = setDoc(
                    imgFive,
                    txtCertificateFive,
                    imgCrossFive,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                fiveId = documents.get(i).documentId

            }
            if (documents.get(i).documentName.contains("certificate6")) {

                certificateSix = setDoc(
                    imgNine,
                    txtCertificateSix,
                    imgCrossSix,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                sixId = documents.get(i).documentId

            }
            if (documents.get(i).documentName.contains("certificate7")) {

                certificateSeven = setDoc(
                    imgSeven,
                    txtCertificateSeven,
                    imgCrossSeven,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                sevenId = documents.get(i).documentId

            }
            if (documents.get(i).documentName.contains("certificate8")) {

                certificateEight = setDoc(
                    imgEight,
                    txtCertificateEight,
                    imgCrossEight,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                eightId = documents.get(i).documentId

            }
            if (documents.get(i).documentName.contains("certificate9")) {

                certificateNine = setDoc(
                    imgNine,
                    txtCertificateNine,
                    imgCrossNine,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                nineId = documents.get(i).documentId

            }
            if (documents.get(i).documentName.contains("certificate10")) {

                certificateTen = setDoc(
                    imgTen,
                    txtCertificateTen,
                    imgCrossTen,
                    documents.get(i).docUrl,
                    documents.get(i).documentName
                )
                tenId = documents.get(i).documentId

            }
        }

    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackUploadDoc -> {

                onBackPressed()
            }
            llCvResumeUpload -> {

                if (resumeFile == null) {

                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickPdfDialog(1, "resume")

                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }
                } else {

                    openDoc(resumeFile)

                }

            }
            llOneCertificate -> {
                if (certificateOne == null) {
                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickDocDialog(2, "certificate1")
                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }

                } else {

                    openDoc(certificateOne)

                }
            }
            llTwoCertificate -> {
                if (certificateTwo == null) {
                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickDocDialog(3, "certificate2")
                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }
                } else {

                    openDoc(certificateTwo)

                }
            }
            llThreeCertificate -> {

                if (certificateThree == null) {
                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickDocDialog(4, "certificate3")
                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }
                } else {

                    openDoc(certificateThree)
                }

            }
            llFourCertificate -> {

                if (certificateFour == null) {
                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickDocDialog(5, "certificate4")
                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }
                } else {

                    openDoc(certificateFour)

                }

            }
            llFiveCertificate -> {

                if (certificateFive == null) {
                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickDocDialog(6, "certificate5")
                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }
                } else {

                    openDoc(certificateFive)

                }
            }
            llSixCertificate -> {

                if (certificateSix == null) {
                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickDocDialog(7, "certificate6")
                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }
                } else {

                    openDoc(certificateSix)

                }
            }
            llSevenCertificate -> {

                if (certificateSeven == null) {
                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickDocDialog(8, "certificate7")
                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }
                } else {

                    openDoc(certificateSeven)

                }
            }
            llEightCertificate -> {

                if (certificateEight == null) {
                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickDocDialog(9, "certificate8")
                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }
                } else {

                    openDoc(certificateEight)

                }
            }
            llNineCertificate -> {

                if (certificateNine == null) {
                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickDocDialog(10, "certificate9")
                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }
                } else {

                    openDoc(certificateNine)

                }
            }
            llTenCertificate -> {

                if (certificateTen == null) {
                    if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

                        pickDocDialog(11, "certificate10")
                    } else {

                        Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
                    }
                } else {

                    openDoc(certificateTen)

                }
            }
            imgRemoveCv -> {

                deleteDocDialog(1)
            }
            imgCrossOne -> {

                deleteDocDialog(2)

            }
            imgCrossTwo -> {

                deleteDocDialog(3)

            }
            imgCrossThree -> {

                deleteDocDialog(4)

            }
            imgCrossFour -> {

                deleteDocDialog(5)

            }
            imgCrossFive -> {

                deleteDocDialog(6)

            }
            imgCrossSix -> {

                deleteDocDialog(7)
            }
            imgCrossSeven -> {

                deleteDocDialog(8)

            }
            imgCrossEight -> {

                deleteDocDialog(9)

            }
            imgCrossNine -> {

                deleteDocDialog(10)

            }
            imgCrossTen -> {

                deleteDocDialog(11)
            }

        }

    }

    /*
    Method to set the pre uploaded document
     */
    private fun setDoc(
        image: ImageView?,
        text: TextView?,
        imgDelete: ImageView?,
        docUrl: String?,
        fileName: String
    ): String {

        image!!.visibility = View.GONE
        text!!.text = fileName
        text.visibility = View.VISIBLE
        imgDelete!!.visibility = View.VISIBLE

        return docUrl!!
    }

    /*
    Method to view the document
     */
    private fun openDoc(url: String?) {

        if (url!!.contains(".pdf")) {

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(url), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)

        } else {
            certificateImage(url)
        }

    }

    /*
    Method to view uploadded image
     */
    private fun certificateImage(url: String) {

        var cerificate = Dialog(this@UploadDocActivity)

        cerificate.window!!.attributes.windowAnimations = R.style.DialogAnimation
        cerificate.setContentView(R.layout.expand_profile)
        cerificate.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        cerificate.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        Picasso.get().load(url).into(cerificate.imgPicDialog)
        cerificate.btnCanclePicDialog.setOnClickListener {

            cerificate.dismiss()
        }

        cerificate.show()
    }

    /*
    Method to upload the document
     */
    private fun uploadDoc(imageFile: File, flag: Int, docType: String, docName: String) {

        var progressBar = ProgressBarUtil().showProgressDialog(this@UploadDocActivity)

        var bodyProfilePic: MultipartBody.Part? = null

        if (imageFile != null) {
            //  val compressedImageFile = Compressor(this).compressToFile(imageFile)
            val image = RequestBody.create(MediaType.parse("*/*"), imageFile)
            bodyProfilePic = MultipartBody.Part.createFormData("document", imageFile.name, image)
        }

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())


        map.put("type", getRequestBody(docType))
        map.put("docName", getRequestBody(docName))
        val apiService = ApiService.init()
        val call: Call<UploadDocResponse> = apiService.uploadDoc("Bearer $authKey", map, bodyProfilePic)

        Log.d("REQUEST", map.toString() + "")
        call.enqueue(object : Callback<UploadDocResponse> {
            override fun onResponse(
                call: Call<UploadDocResponse>,
                response: retrofit2.Response<UploadDocResponse>?
            ) {
                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)
                    if (response.body()?.code == 200) {

                        Toast.makeText(this@UploadDocActivity, response.body()?.message, Toast.LENGTH_LONG)

                        showUploadedDoc(
                            flag,
                            response.body()?.docUrl,
                            response.body()?.documentId!!,
                            response.body()?.documentName!!
                        )
                        getUserProfile()

                    } else {
                        Toast.makeText(
                            this@UploadDocActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UploadDocResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)
                Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
            }
        })
    }

    /*
    Method to remove the document
     */
    private fun removeDoc(imgUpload: ImageView, text: TextView, imgRemove: ImageView, docId: Int) {

        var progressBar = ProgressBarUtil().showProgressDialog(this@UploadDocActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())


        hmap.put("userDocumentId", docId.toString())
        val apiService = ApiService.init()
        val call: Call<DeleteResponse> = apiService.deleteDoc("Bearer $authKey", hmap)

        Log.d("REQUEST", hmap.toString() + "")
        call.enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(
                call: Call<DeleteResponse>,
                response: retrofit2.Response<DeleteResponse>?
            ) {
                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)
                    if (response.body()?.code == 200) {
                        Toast.makeText(this@UploadDocActivity, response.body()?.message, Toast.LENGTH_LONG)
                        removeDocument(imgUpload, text, imgRemove)
                        getUserProfile()


                    } else {
                        Toast.makeText(
                            this@UploadDocActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)
                Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
            }
        })
    }

    /*
    Method to set the uploaded document
     */
    private fun showUploadedDoc(flag: Int, docUrl: String?, documentId: Int, documentName: String) {

        if (flag == 1) {

            txtCv.visibility = View.GONE
            resumeFile = setDoc(imgCv, txtCvUploadResume, imgRemoveCv, docUrl, documentName)
            resumeId = documentId

        } else if (flag == 2) {

            certificateOne = setDoc(imgOne, txtCertificateOne, imgCrossOne, docUrl, documentName)
            oneId = documentId

        } else if (flag == 3) {

            certificateTwo = setDoc(imgTwo, txtCertificateTwo, imgCrossTwo, docUrl, documentName)
            twoId = documentId

        } else if (flag == 4) {

            certificateThree = setDoc(imgThree, txtCertificateThree, imgCrossThree, docUrl, documentName)
            threeId = documentId

        } else if (flag == 5) {

            certificateFour = setDoc(imgFour, txtCertificateFour, imgCrossFour, docUrl, documentName)
            fourId = documentId

        } else if (flag == 6) {

            certificateFive = setDoc(imgFive, txtCertificateFive, imgCrossFive, docUrl, documentName)
            fiveId = documentId

        } else if (flag == 7) {

            certificateSix = setDoc(imgSix, txtCertificateSix, imgCrossSix, docUrl, documentName)
            sixId = documentId

        } else if (flag == 8) {

            certificateSeven = setDoc(imgSeven, txtCertificateSeven, imgCrossSeven, docUrl, documentName)
            sevenId = documentId

        } else if (flag == 9) {

            certificateEight = setDoc(imgEight, txtCertificateEight, imgCrossEight, docUrl, documentName)
            eightId = documentId
        } else if (flag == 10) {

            certificateNine = setDoc(imgNine, txtCertificateNine, imgCrossNine, docUrl, documentName)
            nineId = documentId
        } else if (flag == 11) {

            certificateTen = setDoc(imgTen, txtCertificateTen, imgCrossTen, docUrl, documentName)
            tenId = documentId
        }
    }

    /*
    Method to remove the documnet details
     */
    private fun removeDocument(imgUpload: ImageView, text: TextView, imgRemove: ImageView) {

        text.text = ""
        text.visibility = View.GONE
        imgRemove.visibility = View.GONE
        imgUpload.visibility = View.VISIBLE

    }

    /*
    Method to selecte image
     */
    fun pickFile(mode: String, flag: Int, docName: String) {
        imagePicker = ImagePicker(this@UploadDocActivity)
        imagePicker!!.setMode(mode)
            .setCompress(true)
            .setCrop(false)
            .setTag("pic_" + System.currentTimeMillis())
            .setImagePickerListener { imageFile, tag ->
                if (imageFile != null) {
                    Log.e("FileName", tag.toString())

                    if (flag == 1) {
                        if (!imageFile.absolutePath.contains(".pdf")) {

                            Toast.makeText(this@UploadDocActivity, R.string.resume_format, Toast.LENGTH_LONG).show()
                        } else {

                            uploadDoc(imageFile, flag, "resume", docName)

                        }
                    } else {

                        uploadDoc(imageFile, flag, "certificate", docName)

                    }

                }
            }
            .pick()
    }

    /*
    Method to select the doccument
     */
    fun pickPdfDialog(flag: Int, docName: String) {

        val items = arrayOf<CharSequence>(resources.getString(R.string.pick_pdf))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(null)
        builder.setItems(items) { dialog, item ->
            if (items[item] == resources.getString(R.string.pick_pdf)) {
                pickFile(ImagePicker.MODE_DOCUMENT, flag, docName)
            }
        }
        builder.show()
    }

    /*
    Method to show the dialog to select the documnet
     */
    fun pickDocDialog(flag: Int, docName: String) {

        val items = arrayOf<CharSequence>(resources.getString(R.string.camera), resources.getString(R.string.gallery), resources.getString(R.string.pick_pdf))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(null)
        builder.setItems(items) { dialog, item ->
            if (items[item] == resources.getString(R.string.camera)) {

                pickFile(ImagePicker.MODE_CAMERA, flag, docName)
            } else if (items[item] == resources.getString(R.string.gallery)) {

                pickFile(ImagePicker.MODE_GALLERY, flag, docName)

            } else {
                pickFile(ImagePicker.MODE_DOCUMENT, flag, docName)

            }
        }
        builder.show()
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

    override fun onBackPressed() {
        // super.onBackPressed()

        val intentDocs = Intent(this@UploadDocActivity, EditProfileActivity::class.java)
        if (documentsList.size > 0) {

            intentDocs.putExtra(Constants.DOC_NAME, documentsList)
        }
        setResult(506, intentDocs)

        finish()
    }

    /*
    Method to show delete dialog
     */
    private fun deleteDocDialog(flag: Int) {

        var deleteDocDialog = Dialog(this@UploadDocActivity)

        deleteDocDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        deleteDocDialog.setContentView(R.layout.logout_dialog)
        deleteDocDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        deleteDocDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        deleteDocDialog.txtTitleLogoutDialog.setText(R.string.permanently_delete_file)

        deleteDocDialog.btnYesLogout.setText(R.string.delete)

        deleteDocDialog.btnYesLogout.setOnClickListener {

            deleteDocDialog.dismiss()

            deleteUserDocument(flag)

        }
        deleteDocDialog.btnCancleLogout.setOnClickListener {

            deleteDocDialog.dismiss()

        }
        deleteDocDialog.show()
    }

    /*
    Method to remove the details of removed documnet
     */
    private fun deleteUserDocument(flag: Int) {

        if (AppValidator.isInternetAvailable(this@UploadDocActivity)) {

            when (flag) {

                1 -> {

                    removeDoc(imgCv, txtCvUploadResume, imgRemoveCv, resumeId)
                    txtCv.visibility = View.VISIBLE
                    resumeFile = null
                    resumeId = 0
                }
                2 -> {
                    removeDoc(imgOne, txtCertificateOne, imgCrossOne, oneId)
                    certificateOne = null
                    oneId = 0
                }
                3 -> {
                    removeDoc(imgTwo, txtCertificateTwo, imgCrossTwo, twoId)
                    certificateTwo = null
                    twoId = 0
                }
                4 -> {
                    removeDoc(imgThree, txtCertificateThree, imgCrossThree, threeId)
                    certificateThree = null
                    threeId = 0
                }
                5 -> {

                    removeDoc(imgFour, txtCertificateFour, imgCrossFour, fourId)
                    certificateFour = null
                    fourId = 0
                }
                6 -> {

                    removeDoc(imgFive, txtCertificateFive, imgCrossFive, fiveId)
                    certificateFive = null
                    fiveId = 0
                }
                7 -> {

                    removeDoc(imgSix, txtCertificateSix, imgCrossSix, sixId)
                    certificateSix = null
                    sixId = 0
                }
                8 -> {
                    removeDoc(imgSeven, txtCertificateSeven, imgCrossSeven, sevenId)
                    certificateSeven = null
                    sevenId = 0
                }
                9 -> {
                    removeDoc(imgEight, txtCertificateEight, imgCrossEight, eightId)
                    certificateEight = null
                    eightId = 0
                }
                10 -> {
                    removeDoc(imgNine, txtCertificateNine, imgCrossNine, nineId)
                    certificateNine = null
                    nineId = 0
                }
                11 -> {
                    removeDoc(imgTen, txtCertificateTen, imgCrossTen, tenId)
                    certificateTen = null
                    tenId = 0
                }

            }
        } else {

            Toast.makeText(this@UploadDocActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
        }
    }

    /*
    Method to get the refreshed list of documents
     */
    private fun getUserProfile() {

        var progressBar = ProgressBarUtil().showProgressDialog(this)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())


        val apiService = ApiService.init()
        val call: Call<ProfileResponse> = apiService.getProfileDetails(
            "Bearer $authKey",
            appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())
        )

        call.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: retrofit2.Response<ProfileResponse>?) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {

                        //save user documents........
                        if (!documentsList.isEmpty()) {

                            documentsList.clear()
                        }

                        documentsList.addAll(response.body()?.message?.userDocuments as ArrayList<ProfileUserDocument>)


                    }
                }

            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)

            }

        })

    }

}