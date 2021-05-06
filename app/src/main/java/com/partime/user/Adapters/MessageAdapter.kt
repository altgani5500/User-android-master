package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.MessageListListener
import com.partime.user.R
import com.partime.user.Responses.MessageList
import com.partime.user.prefences.AppPrefence
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.message_layout.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MessageAdapter(
    var messages: ArrayList<MessageList>,
    internal var context: Context?,
    listener: MessageListListener
) : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {


    val appPrefence = AppPrefence.INSTANCE


    var listener: MessageListListener


    init {
        this.listener = listener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view: View

        init {
            this.view = view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val msg = messages[position]

        if (msg.unreadCount == 0 || msg.unreadCount.toString() == "") {

            viewHolder.view.txtUnreadCountMessag.visibility = View.GONE

            //notifyDataSetChanged()
        } else {

            viewHolder.view.txtUnreadCountMessag.visibility = View.VISIBLE
            viewHolder.view.txtUnreadCountMessag.text = msg.unreadCount.toString()

        }

        if (msg.profilePicture != null || msg.profilePicture != "") {

            Picasso.get().load(msg.profilePicture).placeholder(R.drawable.profile_placeholder)
                .into(viewHolder.view.imgMessagePic)
        }

        viewHolder.view.txtSenderName.text = msg.name
        //viewHolder.view.txtMsgTime.setText(msg.time)
        viewHolder.view.txtMsgBody.text = msg.lastMessage

        viewHolder.view.llMessageList.setOnClickListener {

            appPrefence.initAppPreferences(context)

            var receiverId = 0
            val senderId = msg.senderId


            var userId = appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString())


            if (senderId == userId.toInt()) {
                receiverId = msg.receiverId
            } else {
                receiverId = msg.senderId
            }

            listener.onMessageListClick(receiverId, position)
        }

        setTime(msg.time, viewHolder.view.txtMsgTime)
    }


    override fun getItemCount(): Int {
        return messages.size
    }

    /*
   Method to set time
    */
    fun setTime(time: String, timeTextView: TextView) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        sdf.timeZone = TimeZone.getDefault()
        try {
            val createdDate = sdf.parse(time)
            val currentTime = sdf.format(Date(System.currentTimeMillis()))
            val currentDate = sdf.parse(currentTime)
            val createdCalendar = Calendar.getInstance()
            createdCalendar.time = createdDate
            val currentCalendar = Calendar.getInstance()
            currentCalendar.time = currentDate
            val createdMilisecond = createdCalendar.timeInMillis
            val currentMiliSecond = currentCalendar.timeInMillis
            val seconds = TimeUnit.MILLISECONDS.toSeconds(currentMiliSecond - createdMilisecond)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(currentMiliSecond - createdMilisecond)
            val hours = TimeUnit.MILLISECONDS.toHours(currentMiliSecond - createdMilisecond)
            val days = TimeUnit.MILLISECONDS.toDays(currentMiliSecond - createdMilisecond)

            if (days < 1) {

                var time = parseDateToTime(time)
                timeTextView.text = time
            } else if (days.toInt() >= 1 && days <= 7) {

                var time = parseDateToDay(time)
                timeTextView.text = time
            } else {

                var time = parseDateToDate(time)
                timeTextView.text = time

            }


        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (e: java.text.ParseException) {
            e.printStackTrace()
        }

    }

    fun parseDateToTime(time: String): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "HH:mm a"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        var date: Date? = null
        var str: String? = null

        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return str
    }

    fun parseDateToDay(time: String): String? {

        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "EEE"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        var date: Date? = null
        var str: String? = null

        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return str

    }

    fun parseDateToDate(time: String): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "MM/dd/yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        var date: Date? = null
        var str: String? = null

        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return str
    }

}

