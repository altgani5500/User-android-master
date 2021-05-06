package com.partime.user.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.gems.app.utilities.AppValidator
import com.google.gson.Gson
import com.partime.user.Adapters.ChatAdapter
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.Responses.ChatData
import com.partime.user.Responses.ChatListReponse
import com.partime.user.helpers.SocketManager
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONException
import org.json.JSONObject

class ChatActivity : BaseActivity(), View.OnClickListener {

    private lateinit var socketInstance: SocketManager
    var chatMessages = ArrayList<ChatListReponse>()
    var recieverId: String? = null
    var profilePicUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        AppValidator.rotateBackArrow(imgBackChat, this@ChatActivity)
        AppValidator.rotateBackArrow(imgSendMessageChats, this@ChatActivity)

        //get pofile pic
        if (intent.getStringExtra(Constants.SENDER_PROFILE_PICTURE) != null) {
            profilePicUrl = intent.getStringExtra(Constants.SENDER_PROFILE_PICTURE)
        } else {
            profilePicUrl = intent.getStringExtra("SENDER_PROFILE_PICTURE")
        }

        if (!intent.getStringExtra(Constants.SENDER_NAME).isNullOrBlank() || !intent.getStringExtra(Constants.SENDER_NAME).isNullOrEmpty()) {
            txtSenderNameChat.text = intent.getStringExtra(Constants.SENDER_NAME)
        } else {
            txtSenderNameChat.text = intent.getStringExtra("SENDER_NAME")
        }
        //initialze adapter
        initialize()

        //get reciever id
        if (intent.getIntExtra(Constants.RECIEVER_ID, 0) != 0) {
            recieverId = intent.getIntExtra(Constants.RECIEVER_ID, 0).toString()

        } else {
            recieverId = intent.getIntExtra("RECIEVER_ID", 0).toString()

        }

        imgBackChat.setOnClickListener(this)
        imgSendMessageChats.setOnClickListener(this)
    }

    /*
    method to set adapter
     */
    private val chatAdapter by lazy {

        ChatAdapter(chatMessages, this@ChatActivity, profilePicUrl!!)

    }

    /*Initialize RecycleView */
    private fun initialize() {
        chatMessages.clear()
        recyclerChat.layoutManager = LinearLayoutManager(this@ChatActivity)
        recyclerChat.adapter = chatAdapter
    }

    override fun onResume() {
        super.onResume()
        socketInstance = SocketManager.getInstance(this@ChatActivity)
        onAddListeners()
        if (!socketInstance.isConnected) {
            socketInstance.connect()
        }
    }

    /*Socket Listener */
    private fun onAddListeners() {
        /* SocketManager initialize Change Online Status*/
        socketInstance.initialize(object : SocketManager.SocketListener {
            override fun onConnected() {
                val addUser = JSONObject()
                addUser.accumulate("userId", appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString()))
                addUser.accumulate("type", "user")
                socketInstance.sendMsg("save_socket", addUser)

                if (chatMessages.size == 0) {
                    val getHistory = JSONObject()
                    try {
                        getHistory.accumulate(
                            "senderId",
                            appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString())
                        )
                        if (recieverId != null) {
                            getHistory.accumulate("receiverId", recieverId)

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    socketInstance.sendMsg("getChatList", getHistory)
                }
            }

            override fun onDisConnected() {
                socketInstance.connect()
            }
        })

        /*get messages List*/
        socketInstance.addListener("chatList", object : SocketManager.SocketMessageListener {
            override fun onMessage(vararg args: Any) {
                val chatMessage = Gson().fromJson(args[0].toString(), ChatData::class.java)
                //isLoading = chatMessage.result.size >= 15
                chatMessages.addAll(chatMessage.chat)

                chatAdapter.notifyDataSetChanged()
                recyclerChat.scrollToPosition(chatMessages.size - 1)
            }
        })

        /*get single messages List*/
        socketInstance.addListener("oneMessage", object : SocketManager.SocketMessageListener {
            override fun onMessage(vararg args: Any) {
                try {
                    val response = args[0] as JSONObject
                    var sender_id = ""
                    var receiver_id = ""
                    var message = ""
                    var chat_date = ""

                    if (response.has("sender_id")) {
                        sender_id = response.getString("sender_id")
                    }

                    if (response.has("receiver_id")) {
                        receiver_id = response.getString("receiver_id")
                    }
                    if (response.has("message")) {
                        message = response.getString("message")
                    }
                    if (response.has("created_at")) {
                        chat_date = response.getString("created_at")

                    }

                    val chatData = ChatListReponse()
                    chatData.message = message
                    chatData.sender_id = sender_id.toInt()
                    chatData.receiver_id = receiver_id.toInt()
                    chatData.created_at = chat_date

                    chatMessages.add(chatData)
                    chatAdapter.notifyDataSetChanged()
                    recyclerChat.scrollToPosition(chatMessages.size - 1)

                    Log.e("chatdata", chatData.toString())

                } catch (ex: Exception) {
                    Log.e("exceptiontag", ex.toString())
                }
            }
        })
    }

    override fun onClick(v: View?) {

        when (v) {

            imgBackChat -> {

                onBackPressed()
            }
            imgSendMessageChats -> {
                if (validation()) {
                    onSend(edtSendMessageChat.text.toString())
                }
                chatAdapter.notifyDataSetChanged()
            }

        }
    }

    /*
    Method  to check validations
     */
    private fun validation(): Boolean {
        if (edtSendMessageChat.text.toString().isBlank()) {
            return false
        }
        return true
    }

    /*Send Msg Using Socket */
    private fun onSend(msg: String) {
        if (SocketManager.socket.connected()) {
            Toast.makeText(this@ChatActivity, "alhamdullah aloooooot", Toast.LENGTH_LONG).show()

            val addUserjsonObject = JSONObject()
            addUserjsonObject.accumulate(
                "senderId",
                appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString()))
            addUserjsonObject.accumulate("receiverId", recieverId)
            addUserjsonObject.accumulate("message", msg)
            addUserjsonObject.accumulate("sender_type", "user")
            addUserjsonObject.accumulate("receiver_type", "enterprise")
            socketInstance.sendMsg("sendMessage", addUserjsonObject)
            edtSendMessageChat.setText("")
        } else {
            Toast.makeText(this@ChatActivity, "قيد التطوير", Toast.LENGTH_LONG).show()
           // Toast.makeText(this@ChatActivity, R.string.no_internet, Toast.LENGTH_LONG).show()
            socketInstance.connect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //SocketManager.disConnect()
        Log.d("onDestroy", "DestroySocket")
    }

    override fun onPause() {
        super.onPause()
        socketInstance.sendMsg("disconnect", "")
        Log.d("onPause", "PauseChat")
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if (intent.hasExtra("FLAGS_BACK")) {
            if (intent.getIntExtra("FLAGS_BACK", 0) == 1) {
                launchActivityMain(HomeActivity::class.java)
                finish()
            } else {
                finish()
            }
        } else {
            finish()
        }
    }
}
