package com.parttime.com.apiclients


import com.partime.user.Responses.*
import com.partime.user.Responses.wifyResponse.WifyResponse
import com.partime.user.helpers.Utilities
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface ApiService {

    @GET("getNationality")
    fun getNationalityList(@Header("lang") header: String): Call<NationalityResponse>


    @POST("userLogin")
    fun login(@Header("lang") header: String, @Body map: HashMap<String, String>): Call<LoginResponse>


    @POST("updateDeviceToken")
    fun generateToken(@Header("Authorization") header: String, @Header("lang") headerLang: String, @Body map: HashMap<String, String>): Call<TokenResponse>


    @POST("forgotPassword")
    fun forgot(@Header("lang") header: String, @Body map: HashMap<String, String>): Call<ForgotResponse>


    @POST("getJobList")
    fun getJobList(@Header("lang") header: String, @Body map: HashMap<String, String>): Call<JobListResponse>


    @POST("getJobDetail")
    fun getJobDeatils(@Header("lang") header: String, @Body map: HashMap<String, String>): Call<JobDetailsResponse>

    @POST("getMultipleJobsWithLatLong")
    fun getJobDeatilsMap(@Header("lang") header: String, @Body map: HashMap<String, String>): Call<JobDetailsMap>


    @POST("saveJob")
    fun saveJobs(@Header("Authorization") header: String, @Header("lang") headerLang: String, @Body map: HashMap<String, String>): Call<SaveJobResponse>

    @POST("getSavedJobList")
    fun getSavedJobDeatils(@Header("Authorization") header: String, @Header("lang") headerLang: String): Call<SavedJobsListResponse>

    @POST("userProfile")
    fun getProfileDetails(@Header("Authorization") header: String, @Header("lang") headerLang: String): Call<ProfileResponse>

    @POST("applyJob")
    fun applyJob(@Header("Authorization") header: String, @Header("lang") headerLang: String, @Body map: HashMap<String, String>): Call<ApplyJobResponse>

    @POST("contactUs")
    fun contactUs(@Header("Authorization") header: String, @Header("lang") headerLang: String, @Body map: HashMap<String, String>): Call<ContactUsResponse>

    @GET("aboutUs")
    fun aboutUs(@Header("lang") headerLang: String): Call<AboutUsResponse>

    @GET("getEducationList")
    fun educationDetails(@Header("Authorization") header: String, @Header("lang") headerLang: String): Call<EducationListResponse>


    @GET("getIndustryList")
    fun getIndustryList(@Header("lang") header: String): Call<IndustryResponse>

    @GET("getJobTitle")
    fun getJobFilList(@Header("lang") header: String): Call<JobTitleResponse>

    @GET("getCompany")
    fun getCompanyList(@Header("lang") header: String): Call<ComapnyResponse>


    @GET("getCountry")
    fun getCountryListt(@Header("lang") header: String): Call<CountryResponse>


    @POST("getState")
    fun getStateList(@Header("lang") header: String, @Body map: HashMap<String, String>): Call<StateResponse>


    @POST("getCity")
    fun getCityList(@Header("lang") header: String, @Body map: HashMap<String, String>): Call<CityResponse>


    @Multipart
    @POST("userSignup")
    fun register(@Header("lang") header: String, @PartMap map: HashMap<String, RequestBody>, @Part part: MultipartBody.Part?): Call<SignupResponse>

    @Multipart
    @POST("editUserProfile")
    fun editProfile(@Header("Authorization") header: String, @Header("lang") headerLang: String, @PartMap map: HashMap<String, RequestBody>, @Part part: MultipartBody.Part?): Call<EditProfileResponse>

    @POST("changePassword")
    fun changeUserPassword(@Header("Authorization") header: String, @Header("lang") headerLang: String, @Body map: HashMap<String, String>): Call<ChangePasswordResponse>

    @POST("editEmail")
    fun changeEmail(@Header("Authorization") header: String, @Header("lang") headerLang: String, @Body map: HashMap<String, String>): Call<EditEmailResponse>


    @POST("checkNationalId")
    fun checkNationalId(@Header("Authorization") header: String, @Header("lang") headerLang: String,@Body map: HashMap<String, String>): Call<NationalIdVerificationRespose>



    @Multipart
    @POST("uploadDocument")
    fun uploadDoc(
        @Header("Authorization") header: String,
        @PartMap map: HashMap<String, RequestBody>,
        @Part part: MultipartBody.Part?
    ): Call<UploadDocResponse>


    @POST("deleteDocument")
    fun deleteDoc(@Header("Authorization") header: String, @Body map: HashMap<String, String>): Call<DeleteResponse>

    @POST("changeUserSetting")
    fun changeUserSetting(@Header("Authorization") header: String, @Body map: HashMap<String, String>): Call<ChangeUserSettingsResponse>

    @POST("getUserSetting")
    fun getUserSetting(@Header("Authorization") header: String): Call<GetSettingsResponse>

    @POST("getAllChatMessages")
    fun getMessageList(@Header("Authorization") header: String, @Header("lang") headerLang: String): Call<MessageResponse>

    @POST("notificationList")
    fun getNotifications(@Header("Authorization") header: String, @Header("lang") headerLang: String): Call<NotificationRespose>

    @POST("getTaskList")
    fun getTaskDetailsList(@Header("Authorization") header: String, @Header("lang") headerLang: String, @Body map: HashMap<String, String>): Call<TaskListResponse>

    @POST("getTaskDetail")
    fun getTaskDetails(@Header("Authorization") header: String, @Header("lang") headerLang: String, @Body map: HashMap<String, String>): Call<TaskDetailResponse>

    @POST("changeTaskStatus")
    fun taskButtonCLick(@Header("Authorization") header: String, @Body map: HashMap<String, String>): Call<TaskButtonResponse>


    @POST("getPerticularScheduleList")
    fun getSchedule(@Header("Authorization") header: String,@Header("lang") headerLang: String,@Body map: HashMap<String, String>): Call<ViewScheduleResponse>

    @POST("addWorkNetwork")
    fun connectNetwork(@Header("Authorization") header: String,@Body map: HashMap<String, String>): Call<ConnectNetwork>

    @POST("punchIn")
    fun punchIn(@Header("Authorization") header: String,@Body map: HashMap<String, String>): Call<PunchInResponse>

    @POST("checkPunchOutStatus")
    fun punchOutStatus(@Header("Authorization") header: String,@Body map: HashMap<String, String>): Call<PunchInResponse>

    @Multipart
    @POST("punchOut")
    fun punchOut(@Header("Authorization") header: String, @PartMap map: HashMap<String, RequestBody>, @Part part: MultipartBody.Part?): Call<PunchInResponse>

    @POST("getUserWorkReport")
    fun getUserWorkReport(@Header("Authorization") header: String, @Header("lang") headerLang: String,@Body map: HashMap<String, String>): Call<WorkDetailsResponse>

    @POST("getNetworkCredentials")
    fun getNetworkCredentials(@Header("Authorization") header: String, @Header("lang") headerLang: String): Call<NetworkCredentialsResponse>

    @POST("myWorkHistory")
    fun getHistory(@Header("Authorization") header: String, @Header("lang") headerLang: String,@Body map: HashMap<String, String>): Call<HistoryResponse>

    @POST("myWorkHistoryList")
    fun getHistoryList(@Header("Authorization") header: String, @Header("lang") headerLang: String): Call<WorkHistoryListResponse>

    @POST("myTaskHistoryList")
    fun getTaskHistoryList(@Header("Authorization") header: String, @Header("lang") headerLang: String): Call<TaskHistoryResponse>

    @POST("resignationRequest")
    fun requestResignation(@Header("Authorization") header: String, @Header("lang") headerLang: String,@Body map: HashMap<String, String>): Call<PunchInResponse>


    // added by vkash
    @POST("checkWifiConnection")
    fun wifyNetworkVerify(@Header("Authorization") header: String,@Body map: HashMap<String, String>): Call<WifyResponse>

    companion object Factory {
        var interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        var client=Utilities.getUnsafeOkHttpClient()
       /* var client = OkHttpClient.Builder()*/.connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .build()
        // prev dev bse url:http://13.232.62.239/
       //  https://www.partime.org

        fun init(): ApiService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl("https://www.partime.org/partime/public/index.php/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()//http://13.232.62.239
            return retrofit.create(ApiService::class.java)
        }

    }

}