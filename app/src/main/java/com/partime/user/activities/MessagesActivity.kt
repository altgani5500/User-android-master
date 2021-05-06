package com.partime.user.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.Adapters.MessageAdapter
import com.partime.user.Constants.Constants
import com.partime.user.Listeners.MessageListListener
import com.partime.user.R
import com.partime.user.Responses.MessageList
import com.partime.user.Responses.MessageResponse
import com.partime.user.helpers.ProgressBarUtil
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.parttime.com.apiclients.ApiService
import kotlinx.android.synthetic.main.activity_messages.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList


class MessagesActivity : BaseActivity(), View.OnClickListener {

    var messageList = ArrayList<MessageList>()
    private var msgAdapter: MessageAdapter? = null
    var tempList = ArrayList<MessageList>()


    var notificationCount: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        AppValidator.rotateBackArrow(imgBackMessage, this@MessagesActivity)

        notificationCount = intent.getIntExtra(Constants.UNREAD_NOTIFICATION_COUNT, 0)

        if (notificationCount > 0) {

            txtNotificationCountMsg.text = notificationCount.toString()
            txtNotificationCountMsg.visibility = View.VISIBLE
        } else {
            txtNotificationCountMsg.visibility = View.GONE

        }

        swipe_refresh_messages.setOnRefreshListener {
            validateNet()
        }

        //search the messages
        edtSearchMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(edit: Editable?) {

                if (edit?.length!! >= 1) {
                    var msgSearched = edit.toString().trim()
                    callSearch(msgSearched)
                } else if (edit.isEmpty()) {
                    var msgSearched = edit.toString().trim()
                    callSearch(msgSearched)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        btnRetryMessage.setOnClickListener(this)
        imgBackMessage.setOnClickListener(this)
        imgNotificationsMsg.setOnClickListener(this)
        imgSearchMessage.setOnClickListener(this)
        imgCrossSearchChat.setOnClickListener(this)

    }

    /*
    Search the message list
     */
    private fun callSearch(msgSearched: String) {
        val str: String = msgSearched.toLowerCase(Locale.getDefault())
        tempList.clear()
        if (str == "") {
            tempList.addAll(messageList)
        } else {
            for (list in messageList) {
                if (list.name.toLowerCase(Locale.getDefault()).contains(str) || list.lastMessage.toLowerCase(Locale.getDefault()).contains(
                        str
                    )
                ) {
                    tempList.add(list)
                }
            }
        }
        if(msgAdapter!=null)
        msgAdapter!!.notifyDataSetChanged()

    }

    /*
    Method to validate network
     */
    private fun validateNet() {

        if (AppValidator.isInternetAvailable(this@MessagesActivity)) {

            getMessageList()
        } else {

            llErrorMessage.visibility = View.VISIBLE
            recyclerMessage.visibility = View.GONE
            llNoMessage.visibility = View.GONE
        }
    }

    /*
    Method to get the message list
     */
    private fun getMessageList() {

        var progressBar = ProgressBarUtil().showProgressDialog(this@MessagesActivity)

        var authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString())

        val language = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())

        val apiService = ApiService.init()
        val call: Call<MessageResponse> = apiService.getMessageList("Bearer $authKey", language)

        call.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: retrofit2.Response<MessageResponse>?) {

                if (response != null) {
                    ProgressBarUtil().hideProgressDialog(progressBar)
                    swipe_refresh_messages.isRefreshing = false

                    if (response.body()?.code == 200) {

                        Toast.makeText(this@MessagesActivity, response.body()?.success.toString(), Toast.LENGTH_LONG)

                        if (!messageList.isEmpty()) {
                            messageList.clear()
                        }
                        if (!tempList.isEmpty()) {
                            tempList.clear()
                        }


                        messageList.addAll(response.body()?.message as ArrayList<MessageList>)
                        tempList.addAll(response.body()?.message as ArrayList<MessageList>)

                        if (tempList.size > 0) {

                            msgAdapter = MessageAdapter(tempList, this@MessagesActivity, object : MessageListListener {
                                override fun onMessageListClick(receiverId: Int, position: Int) {

                                    //go to chat section

                                    var intent = Intent(this@MessagesActivity, ChatActivity::class.java)
                                    intent.putExtra(Constants.RECIEVER_ID, receiverId)
                                    intent.putExtra(
                                        Constants.SENDER_PROFILE_PICTURE,
                                        response.body()?.message!!.get(position).profilePicture
                                    )
                                    intent.putExtra(
                                        Constants.SENDER_NAME,
                                        response.body()?.message!!.get(position).name
                                    )
                                    startActivity(intent)

                                }

                            })

                            llErrorMessage.visibility = View.GONE
                            recyclerMessage.visibility = View.VISIBLE
                            llNoMessage.visibility = View.GONE

                            recyclerMessage!!.layoutManager =
                                LinearLayoutManager(this@MessagesActivity, RecyclerView.VERTICAL, false)
                            recyclerMessage!!.adapter = msgAdapter

                        } else {

                            llNoMessage.visibility = View.VISIBLE
                            llErrorMessage.visibility = View.GONE
                            recyclerMessage.visibility = View.GONE
                        }

                    } else {

                        Toast.makeText(
                            this@MessagesActivity,
                            response.body()?.error_message.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }


            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                ProgressBarUtil().hideProgressDialog(progressBar)
                swipe_refresh_messages.isRefreshing = false

                recyclerMessage.visibility = View.GONE
                llErrorMessage.visibility = View.VISIBLE
                llNoMessage.visibility = View.GONE


                Toast.makeText(this@MessagesActivity, R.string.no_internet, Toast.LENGTH_LONG)

            }

        })

    }

    override fun onClick(v: View?) {

        when (v) {

            btnRetryMessage -> {

                llNoMessage.visibility = View.GONE
                validateNet()

            }
            imgBackMessage -> {

                onBackPressed()
            }
            imgNotificationsMsg -> {

                val intent = Intent(this@MessagesActivity, NotificationActivity::class.java)
                startActivityForResult(intent, 100)
            }
            imgSearchMessage -> {

                txtMessageHeader.visibility = View.GONE
                llSearchMsg.visibility = View.VISIBLE
            }
            imgCrossSearchChat -> {

                edtSearchMessage.setText("")
                txtMessageHeader.visibility = View.VISIBLE
                llSearchMsg.visibility = View.GONE
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == 100) {

            intent.removeExtra(Constants.UNREAD_NOTIFICATION_COUNT)
            notificationCount = 0
            txtNotificationCountMsg.visibility = View.GONE

        }
    }

    override fun onResume() {
        super.onResume()
        validateNet()

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
    }

}
