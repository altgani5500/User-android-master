package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.R
import com.partime.user.Responses.NotificationMessage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.notication_layout.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class NotificationAdapter(var notifications: ArrayList<NotificationMessage>, internal var context: Context?) :
    RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view: View

        init {
            this.view = view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notication_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val notification = notifications[position]


        if (notification.profilePicture != null || notification.profilePicture != "") {

            Picasso.get().load(notification.profilePicture).placeholder(R.drawable.profile_placeholder)
                .into(viewHolder.view.imgNotificationPic)
        }

        viewHolder.view.txtNotificationBody.setText(notification.message)
        viewHolder.view.txtNotifictionSenderName.setText(notification.name)

        setTime(notification.time,viewHolder.view.txtNotificationTime)

    }

    override fun getItemCount(): Int {
        return notifications.size
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

            if(days<1){

                var time=parseDateToTime(time)
                timeTextView.setText(time)
            }
            else if(days.toInt()>=1 && days<=7){

                var time=parseDateToDay(time)
                timeTextView.setText(time)
            }
            else{

                var time=parseDateToDate(time)
                timeTextView.setText(time)

            }


        } catch (e: ParseException) {
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

