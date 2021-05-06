package com.partime.user.Fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gems.app.utilities.AppValidator
import com.google.gson.Gson
import com.partime.user.Adapters.GroupChatAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.*
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.helpers.SocketManager
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseFragment
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.fragment_calendar_view.*
import kotlinx.android.synthetic.main.save_to_calendar_dialog.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewFragment : BaseFragment(), View.OnClickListener {

    private var eventMap:Map<Int, List<CustomEvent>>?=null

    lateinit var root:View

    private val allEventsList = ArrayList<ScheduleMessage>()
    val appPrefence = AppPrefence.INSTANCE

    private lateinit var socketInstance: SocketManager
    var chatMessages = ArrayList<GroupChatListReponse>()

    var enterpriseId:Int = 0
    var progressBar:ProgressDialog?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_view, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        appPrefence.initAppPreferences(context)

       validateNet()
        loadFragment(CalendarFragment())
        //for inner scroll of the chat.......

        recyclerGroupChatCalendar.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (v.id == R.id.recyclerGroupChatCalendar) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                return false
            }
        })

        initialize()

        btnRetryCalendar.setOnClickListener(this)

        imgSendMessageCalendar.setOnClickListener(this)
    }

    private fun validateNet() {

        if(AppValidator.isInternetAvailable(context!!)){
           // setData()
        }else{
            llErrorCalendar.visibility=View.VISIBLE
            llCalendar.visibility=View.GONE
        }
    }

    /*private fun setData() {

     //set the tasks from api.......
        var progressBar = ProgressBarUtil().showProgressDialog(context!!)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())
        val language = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())

        val apiService = ApiService.init()
        val call = apiService.getSchedule("Bearer $authKey", language)
        call.enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(
                call: Call<ScheduleResponse>,
                response: retrofit2.Response<ScheduleResponse>?
            ) {

                if (response != null) {

                    ProgressBarUtil().hideProgressDialog(progressBar)

                    if (response.body()?.code == 200) {
                        if (!allEventsList.isNullOrEmpty()){
                            allEventsList.clear()
                        }

                        allEventsList.addAll(response.body()?.message as ArrayList<ScheduleMessage>)


                        initialize()

                        appPrefence.setInt(AppPrefence.SharedPreferncesKeys.ENROLLED_E_ID.toString(),response.body()?.enrolledEnterpriseId!!)
                        enterpriseId=response.body()?.enrolledEnterpriseId!!

                    } else {

                        Toast.makeText(
                            context!!,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {

                ProgressBarUtil().hideProgressDialog(progressBar)

                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show()
                llCalendar.visibility=View.GONE
                llErrorCalendar.visibility=View.VISIBLE
            }

        })
    }*/

    private fun loadFragment(calendarFragment: CalendarFragment) {

        childFragmentManager.beginTransaction().replace(R.id.fragCalendar, calendarFragment)
            .commitAllowingStateLoss()
    }


    override fun onClick(v: View?) {

        when(v){

            btnRetryCalendar->{

                llErrorCalendar.visibility=View.GONE
                validateNet()
            }
            imgSendMessageCalendar->{
                if (validation()) {
                    onSend(edtSendMessageCalendar.text.toString())
                }
                chatAdapter.notifyDataSetChanged()
            }
        }
    }

    /*
    Method to show the save to calendar dialog
   */
    private fun saveToCalendar(name: String, startTime: String, endTime: String, date: String) {

        var saveToCalDialog = Dialog(context!!)

        saveToCalDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        saveToCalDialog.setContentView(R.layout.save_to_calendar_dialog)
        saveToCalDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        saveToCalDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        saveToCalDialog.txtSaveToCalDialog.setText(name+"   "+startTime+"  to  "+endTime)

        saveToCalDialog.btnCancleSaveToCal.setOnClickListener {

            saveToCalDialog.dismiss()
        }

        saveToCalDialog.btSaveToCalDialog.setOnClickListener{

            openCalendar(name,startTime,endTime,date)
            saveToCalDialog.dismiss()

        }

        saveToCalDialog.show()
    }

    /*
     Method to open calendar
      */
    private fun openCalendar(name: String, sTime: String, eTime: String, calDate: String) {

        var date = calDate.split("-")
        var calStartTime = sTime.split(":")
        var calEndTIme = eTime.split(":")

        val startTime = Calendar.getInstance()
        startTime.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), calStartTime[0].toInt(), calStartTime[1].toInt())
        val endTime = Calendar.getInstance()
        endTime.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), calEndTIme[0].toInt(), calEndTIme[1].toInt())

        var intent = Intent(Intent.ACTION_EDIT)
        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.timeInMillis)
        intent.putExtra("allDay", false)
        intent.putExtra("rrule", "FREQ=YEARLY")
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
        intent.putExtra("title", name)
        context!!.startActivity(intent)
    }

    /*Initialize RecycleView */
    private fun initialize() {
        chatMessages.clear()
        recyclerGroupChatCalendar.layoutManager = LinearLayoutManager(context)
        recyclerGroupChatCalendar.adapter = chatAdapter
    }

    /*
    method to set adapter
     */
    private val chatAdapter by lazy {

        GroupChatAdapter(chatMessages, context!!)

    }

    override fun onResume() {
        super.onResume()
        socketInstance = SocketManager.getInstance(context!!)
        onAddListeners()
        if (!socketInstance.isConnected) {
            socketInstance.connect()
        }
    }

    private fun onAddListeners() {

        /* SocketManager initialize Change Online Status*/
        socketInstance.initialize(object : SocketManager.SocketListener {
            override fun onConnected() {
                val addUser = JSONObject()
                addUser.accumulate("userId", appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString()))
                addUser.accumulate("type", "user")
                socketInstance.sendMsg("save_socket", addUser)

                //ProgressBarUtil().hideProgressDialog(progressBar!!)
                enterpriseId=appPrefence.getInt(AppPrefence.SharedPreferncesKeys.ENROLLED_E_ID.toString())
                if (chatMessages.size == 0) {
                    val getHistory = JSONObject()
                    try {
                        if (enterpriseId != null) {
                            getHistory.accumulate("enterpriseId", enterpriseId)

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    socketInstance.sendMsg("getGroupMessage", getHistory)
                }
            }

            override fun onDisConnected() {
                socketInstance.connect()
            }
        })

        /*get messages List*/
        socketInstance.addListener("chatList", object : SocketManager.SocketMessageListener {
            override fun onMessage(vararg args: Any) {
                val chatMessage = Gson().fromJson(args[0].toString(), GroupChat::class.java)
                //isLoading = chatMessage.result.size >= 15
                    chatMessages.clear()
                    chatMessages.addAll(chatMessage.chat)
                if(cardGroupChat!=null) {
                    if (chatMessages.size > 0) {
                        chatAdapter.notifyDataSetChanged()
                        cardGroupChat.visibility = View.VISIBLE
                    } else {
                        cardGroupChat.visibility = View.GONE
                    }
                }

               // ProgressBarUtil().hideProgressDialog(progressBar!!)
            }
        })

        /*get single messages List*/
        socketInstance.addListener("oneMessage", object : SocketManager.SocketMessageListener {
            override fun onMessage(vararg args: Any) {
                try {
                    val response = args[0] as JSONObject
                    var user_id = ""
                    var enterprise_id = ""
                    var message = ""
                    var chat_date = ""


                    // val jobjResult = response.getJSONObject("chat")

                    if (response.has("userId")) {
                        user_id = response.getString("userId")
                    }

                    if (response.has("enterpriseId")) {
                        enterprise_id = response.getString("enterpriseId")
                    }
                    if (response.has("message")) {
                        message = response.getString("message")
                    }
                    if (response.has("created_at")) {
                        chat_date = response.getString("created_at")

                    }

                    val chatData = GroupChatListReponse()
                    chatData.message = message
                    chatData.userId = user_id.toInt()
                    chatData.enterpriseId = enterprise_id.toInt()
                    chatData.created_at = chat_date


                    chatMessages.add(chatData)
                    if(chatMessages.size>0){
                        chatAdapter.notifyDataSetChanged()
                        cardGroupChat.visibility=View.VISIBLE
                    }else{
                        cardGroupChat.visibility=View.GONE

                    }

                    //recyclerGroupChatCalendar.scrollToPosition(chatMessages.size - 1)
                    Log.e("chatdata", chatData.toString())

                } catch (ex: Exception) {
                    Log.e("exceptiontag", ex.toString())
                }
            }
        })

    }

    /*Send Msg Using Socket */
    private fun onSend(msg: String) {
        if (SocketManager.socket.connected()) {
            val addUserjsonObject = JSONObject()
            addUserjsonObject.accumulate(
                "userId",
                appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString())
            )
            enterpriseId=appPrefence.getInt(AppPrefence.SharedPreferncesKeys.ENROLLED_E_ID.toString())
            addUserjsonObject.accumulate("enterpriseId", enterpriseId)
            addUserjsonObject.accumulate("message", msg)
            addUserjsonObject.accumulate("sender_type", "user")
            addUserjsonObject.accumulate("receiver_type", "enterprise")
            socketInstance.sendMsg("sendGroupMessage", addUserjsonObject)
            edtSendMessageCalendar.setText("")
        } else {
            Toast.makeText(context!!, R.string.no_internet, Toast.LENGTH_LONG).show()
            socketInstance.connect()
        }
    }

    /*
   Method  to check validations
    */
    private fun validation(): Boolean {
        if (edtSendMessageCalendar.text.toString().isBlank()) {
            return false
        }
        return true
    }

    //to call scoket when the
    /*override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser == true){

            progressBar = ProgressBarUtil().showProgressDialog(context!!)

            //to give delay
            Handler().postDelayed(object : Runnable{
                override fun run() {
                    Log.d("callScreen", "dummy")
                    socketInstance = SocketManager.getInstance(context!!)
                    onAddListeners()

                    if (!socketInstance.isConnected) {
                        socketInstance.connect()
                    }
                }

            },1000)

            //to set the scroll to top
            calendarScrollView.scrollTo(0,0)

        }
    }*/


}
