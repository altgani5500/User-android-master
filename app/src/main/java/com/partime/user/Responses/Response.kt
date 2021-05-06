package com.partime.user.Responses

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NationalityResponse(
    val code: Int,
    val error_message: Any,
    val message: ArrayList<NationalityMessage>,
    val success: Boolean
) : Serializable

data class NationalityMessage(
    val nationality: String,
    val nationalityId: Int
) : Serializable

data class SignupResponse(
    val code: Int,
    val error_message: Any,
    val message: SignupMessage,
    val success: Boolean
)

data class SignupMessage(
    val apiToken: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val socialProvider: String,
    val userId: Int,
    val username: String
)

data class LoginResponse(
    val code: Int,
    val error_message: Any,
    val message: LoginMessage,
    val success: Boolean
)

data class LoginMessage(
    val apiToken: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val socialProvider: String,
    val userId: Int,
    val username: String
)


data class TokenResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)

data class ForgotResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)

data class JobDetailsResponse(
    val code: Int,
    val error_message: Any,
    val message: JobDetailsMessage,
    val success: Boolean
)

data class JobDetailsMessage(
    val amountPaid: String,
    val applyJob: Int,
    val benifits: String,
    val branchName: String,
    val createdDate: String,
    val currency: String,
    val currencyId: Int,
    val editWorkingHours: List<Any>,
    val enrolledParttimeWorker: Int,
    val enrolledWorker: String,
    val enterpriseLogo: String,
    val enterpriseName: String,
    val enterpriseId: Int,
    val generealInfo: String,
    val hourlyRate: String,
    val hoursRate: String,
    val hoursRates: String,
    val hoursePer: String,
    val industryType: String,
    val isFlexible: String,
    val isSame: String,
    val jobDescription: String,
    val jobId: Int,
    val jobImages: List<JobDetailsImage>,
    val jobLat: String,
    val jobLocation: String,
    val jobLong: String,
    val jobStatus: String,
    val jobTitle: String,
    val jobType: String,
    val max_hours_exp: String,
    val min_hours_exp: String,
    val noOfApplicants: String,
    val parttimeType: String,
    val parttimeTypeId: Int,
    val password: String,
    val requirements: String,
    val savedJob: Int,
    val totalHoursPerWeek: String,
    val username: String,
    val workExperience: String,
    val workGuidence: String,
    val workingHours: String,
    val jobAppliedStatus: String

)

data class JobDetailsImage(
    val jobImage: String,
    val jobImageId: Int
)

// save jobs.......
data class SaveJobResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean,
    val jobStatus: String,
    val jobType: String

)

//saved jobs list
data class SavedJobsListResponse(
    val code: Int,
    val error_message: Any,
    val message: MutableList<SavedJobsListMessage>,
    val success: Boolean
)

data class SavedJobsListMessage(
    val amountPaid: String,
    var applyJob: Int,
    val enterpriseName: String,
    val industryType: String,
    val jobId: Int,
    val jobLat: String,
    val jobLocation: String,
    val jobLong: String,
    var jobStatus: String,
    val jobTitle: String,
    var jobType: String,
    var savedJob: Int,
    val userId: String,
    val jobAppliedStatus: String
)

//profile response
data class ProfileResponse(
    val code: Int,
    val error_message: Any,
    val message: ProfileMessage,
    val success: Boolean
) : Serializable

data class ProfileMessage(
    val address: String,
    val age: String,
    val branchName: String,
    val companyName: String,
    val education: String,
    val educationDetail: String,
    val email: String,
    val firstName: String,
    val gender: String,
    val lastName: String,
    val name: String,
    val nation: String,
    val profilePicture: String,
    val pushNotificationCount: Int,
    val userDocuments: ArrayList<ProfileUserDocument>,
    val userId: Int,
    val userLat: Any,
    val userLong: Any,
    val messageCount: Int,
    val isEnrolled: Int,
    val enrolledEnterpriseName:String
) : Serializable

data class ProfileUserDocument(
    val docUrl: String,
    val documentId: Int,
    val documentName: String,
    val documentType: String

) : Serializable

//apply jobs response
data class ApplyJobResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)

//Conatct us
data class ContactUsResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)

//about us

data class AboutUsResponse(
    val code: Int,
    val error_message: Any,
    val message: AboutUsMessage,
    val success: Boolean
)

data class AboutUsMessage(
    val aboutUs: String,
    val enterpriseCount: Int,
    val userCount: Int
)

//educational details
data class EducationListResponse(
    val code: Int,
    val error_message: Any,
    val message: EducationListMessage,
    val success: Boolean
)

data class EducationListMessage(
    val education: ArrayList<Education>,
    val educationDetail: ArrayList<EducationDetail>
)

data class EducationDetail(
    val educationDetail: String,
    val educationDetailId: Int
)

data class Education(
    val education: String,
    val educationId: Int
)

//edit profile
data class EditProfileResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)

//industry list
data class IndustryResponse(
    val code: Int,
    val error_message: Any,
    val message: MutableList<IndustryMessage>,
    val success: Boolean
) : Serializable

data class IndustryMessage(
    val industry: String,
    val industryId: Int,
    var isClicked: Boolean
) : Serializable

//comapny filters
data class ComapnyResponse(
    val code: Int,
    val error_message: Any,
    val message: MutableList<ComapnyMessage>,
    val success: Boolean
) : Serializable

data class ComapnyMessage(
    val companyId: Int,
    val companyName: String,
    var isClicked: Boolean

) : Serializable

//job title filters
data class JobTitleResponse(
    val code: Int,
    val error_message: Any,
    val message: MutableList<JobTitleMessage>,
    val success: Boolean

) : Serializable


data class JobTitleMessage(
    val jobTitle: String,
    val jobTitleId: Int,
    var isClicked: Boolean

) : Serializable

//country list
data class CountryResponse(
    val code: Int,
    val error_message: Any,
    val message: List<CountryMessage>,
    val success: Boolean
) : Serializable

data class CountryMessage(
    val country: String,
    var isClicked: Boolean

) : Serializable

//city list

data class CityResponse(
    val code: Int,
    val error_message: Any,
    val message: MutableList<CityMessage>,
    val success: Boolean
) : Serializable

data class CityMessage(
    val cityList: MutableList<CityNames>,
    val stateName: String
) : Serializable

data class CityNames(
    val city: String,
    val stateName: String,
    var isClicked: Boolean

) : Serializable

//State list
data class StateResponse(
    val code: Int,
    val error_message: Any,
    val message: MutableList<StateMessage>,
    val success: Boolean
) : Serializable

data class StateMessage(
    val state: String,
    var isClicked: Boolean

) : Serializable

//upload doc
data class UploadDocResponse(
    val code: Int,
    val docUrl: String,
    val documentId: Int,
    val documentName: String,
    val documentType: String,
    val error_message: Any,
    val message: String,
    val success: Boolean
)

//delete Doc
data class DeleteResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)

//change user setting
data class ChangeUserSettingsResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)

//get user setting...

data class GetSettingsResponse(
    val code: Int,
    val error_message: Any,
    val message: GetSettingsMessage,
    val success: Boolean
)

data class GetSettingsMessage(
    val age: String,
    val certificates: String,
    val complete_profile: String,
    val created_at: String,
    val cv: String,
    val education_level: String,
    val first_name: String,
    val gender: String,
    val grades: String,
    val job_update_notification: String,
    val last_name: String,
    val location: String,
    val message_notification: String,
    val schedule_notification: String,
    val task_notification: String,
    val updated_at: String,
    val user_id: Int,
    val user_setting_id: Int,
    val work_hours: String,
    val working_at: String
)

//messaging list
data class MessageResponse(
    val code: Int,
    val error_message: Any,
    val message: ArrayList<MessageList>,
    val success: Boolean
)


data class MessageList(
    val name: String,
    val isRead: Int,
    val lastMessage: String,
    val profilePicture: String,
    val receiverId: Int,
    val senderId: Int,
    val time: String,
    val unreadCount: Int
)

//chat list
data class ChatData(
    @SerializedName("chat") var chat: ArrayList<ChatListReponse>
) : Serializable

data class ChatListReponse(
    var created_at: String = "",
    var message: String = "",
    var message_type: String = "",
    var read_status: String = "",
    var receiver_id: Int = 0,
    var receiver_type: String = "",
    var sender_id: Int = 0,
    var sender_type: String = "",
    var start_time: String = "",
    var end_time: String = "",
    var date: String = ""

)

//notification response
data class NotificationRespose(
    val code: Int,
    val error_message: Any,
    val message: List<NotificationMessage>,
    val success: Boolean
)

data class NotificationMessage(
    val message: String,
    val name: String,
    val profilePicture: String,
    val time: String
)

//change password
data class ChangePasswordResponse(
    val code: Int,
    val error_message: String,
    val message: Any,
    val success: Boolean
)

//change email
data class EditEmailResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)

//map job details..........
data class JobDetailsMap(
    val code: Int,
    val error_message: Any,
    val message: List<JobDetailsMapMessage>,
    val success: Boolean
)

data class JobDetailsMapMessage(
    val jobLat: String,
    val jobList: List<JobListMessage>,
    val jobLong: String
)



data class JobListResponse(
    val code: Int,
    val error_message: Any,
    val message: List<JobListMessage>,
    val success: Boolean
) : Serializable

data class JobListMessage(
    val amountPaid: String,
    var applyJob: Int,
    var jobStatus: String,
    val enterpriseName: String,
    val industryType: String,
    val jobId: Int,
    val jobLat: String,
    val jobLocation: String,
    val jobLong: String,
    val jobTitle: String,
    var jobType: String,
    var savedJob: Int,
    val userId: String,
    val jobAppliedStatus: String

) : Serializable

// task details
data class TaskListResponse(
    val code: Int,
    val error_message: Any,
    val message: List<TaskListMessage>,
    val success: Boolean
)

data class TaskListMessage(
    val enterpriseName: String,
    val evaluators: String,
    val reason: String,
    val taskId: Int,
    val taskName: String,
    val taskRating: String,
    var taskStatus: String,
    val taskType: String,
    val evaluation: String,
    val reevaluation: String
)

//task details
data class TaskDetailResponse(
    val code: Int,
    val error_message: Any,
    val message: TaskDetailMessage,
    val success: Boolean
)

data class TaskDetailMessage(
    val creationTime: String,
    val endTime: String,
    val enterpriseName: String,
    val evaluation: String,
    val evaluators: String,
    val reason: String,
    val reevaluation: String,
    val startTime: String,
    val taskDate: String,
    val taskDescription: String,
    val taskId: Int,
    val taskName: String,
    val taskRating: String,
    val taskStatus: String,
    val taskType: String
)

// task button
data class TaskButtonResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val status: String,
    val success: Boolean
)

//schedule response...........
/*data class ScheduleResponse(
    val code: Int,
    val error_message: Any,
    val message: List<ScheduleMessage>,
    val success: Boolean,
    val enrolledEnterpriseId:Int
)*/

data class ScheduleResponse(
    val code: Int,
    val error_message: Any,
    @SerializedName("message")
    val message: List<ScheduleMessage>,
    @SerializedName("noOfDays")
    val noOfDays: Int,
    @SerializedName("enrolledEnterpriseId")
    var enrolledEnterpriseId:Int = 0,
    @SerializedName("success")
    val success: Boolean
) : Serializable

data class ScheduleMessage(
    val date: String,
    val count: Int

)

//group chat .........
data class GroupChat(
    @SerializedName("chat") var chat: ArrayList<GroupChatListReponse>
) : Serializable

data class GroupChatListReponse(
    var created_at: String = "",
    var message: String = "",
    var userId: Int = 0,
    var receiver_type: String = "",
    var enterpriseId: Int = 0,
    var sender_type: String = "",
    var userPic:String="",
    var name:String=""

)
//connect wifi network
data class ConnectNetwork(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)

//punch in
data class PunchInResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)


//time logs
data class WorkDetailsResponse(
    val code: Int,
    val error_message: Any,
    val message: List<WorkDetailsMessage>,
    val noOfWeek: Int,
    val success: Boolean,
    val totalDone: Double,
    val totalSchedule: Double
)


data class WorkDetailsMessage(

    val work: List<Work>,
    val workHourDone: Double,
    val workHourSchedule: Double,
    var weekName:String,
    var isClicked: Boolean
)

data class Work(
    val date: String,
    val enterpriseStatus: String,
    val lateEarlyHour: String,
    val punchIn: String,
    val punchOut: String,
    val reason: String,
    val reasonDoc: String,
    val schedule: String,
    val status: String,
    val userPunchId: String,
    val workTime: String
)

//network credentials
data class NetworkCredentialsResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val password: String,
    val success: Boolean,
    val username: String
)

//history
data class HistoryResponse(
    val code: Int,
    val difference: Int,
    val error_message: Any,
    val firstLimit: Int,
    val lastLimit: Int,
    val message: HistoryMessage,
    val success: Boolean
)


data class HistoryMessage(
    val graphData: GraphData,
    val taskDetail: TaskDetail
)

data class GraphData(
    val allTask: List<AllTask>,
    val noOfHours: List<NoOfHour>,
    val workistory: List<Workistory>
)


data class NoOfHour(
    val month: Int,
    val value: Float
)

data class AllTask(
    val month: Int,
    val value: Float
)

data class Workistory(
    val month: Int,
    val value: Float
)

data class TaskDetail(
    val allTask: Float,
    val completed: Float,
    val inprocess: Float,
    val pending: Float,
    val userEnroll: Float,
    val workDone: Double
)

//work history list.............
data class WorkHistoryListResponse(
    val code: Int,
    val error_message: Any,
    val message: List<WorkHistoryListMessage>,
    val success: Boolean
)

data class WorkHistoryListMessage(
    val enrolledUserId: Int,
    val enterpriseName: String,
    val isWorking: Int,
    val jobJoining: String,
    val jobTitle: String,
    val profilePicture: String,
    val schedule: String,
    val totalWorks: Double,
    var isResignRequest:Int
)

//task history

data class TaskHistoryResponse(
    val code: Int,
    val error_message: Any,
    val message: List<TaskHistoryMessage>,
    val success: Boolean
)
data class TaskHistoryMessage(
    val enterpriseName: String,
    val evaluatorName: String,
    val reason: String,
    val taskName: String,
    val taskRating: String,
    val taskStatus: String,
    val taskType: String,
    val evaluation:String
)

data class NationalIdVerificationRespose(
    val code: Int,
    val error_message: String,
    val message: Any,
    val success: Boolean
)

//View schedule
data class ViewScheduleResponse(
    val code: Int,
    val error_message: Any,
    val message: List<ViewScheduleMessage>,
    val success: Boolean
)

data class ViewScheduleMessage(
    val jobTitle: String,
    val name: String,
    val prifilePicture: String,
    val time: String
)