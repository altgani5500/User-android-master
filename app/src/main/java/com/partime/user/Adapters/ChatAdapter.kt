package com.partime.user.Adapters

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.R
import com.partime.user.Responses.ChatListReponse
import com.partime.user.prefences.AppPrefence
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ChatAdapter(var chatList: ArrayList<ChatListReponse>, internal var context: Context?, internal var profilePicUr: String) : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    val appPrefence = AppPrefence.INSTANCE

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view: View

        init {
            this.view = view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        //hide sender name in chat.............
        viewHolder.view.txtSenderName.visibility=View.GONE

        appPrefence.initAppPreferences(context)
        val chat = chatList[position]

        val senderId = chat.sender_id

        var userId = appPrefence.getString(AppPrefence.SharedPreferncesKeys.USER_ID.toString())

        AppValidator.rotateChat(
            viewHolder.view.llRecieverBackGround,
            context!!,
            context!!.getDrawable(R.drawable.reciever_ar_chat_bg),
            context!!.getDrawable(R.drawable.reciever_chat_bg)
        )
        AppValidator.rotateChat(
            viewHolder.view.llSenderBackground,
            context!!,
            context!!.getDrawable(R.drawable.sender_ar_chat_bg),
            context!!.getDrawable(R.drawable.sender_chat_bg)
        )

        if (senderId == userId.toInt()) {
            viewHolder.view.llReciever.visibility = View.VISIBLE
            viewHolder.view.llSender.visibility = View.GONE

            viewHolder.view.txtRecieverMsg.text = chat.message

            var time = parseDateToTime(chat.created_at)
            viewHolder.view.txtRecieverTime.text = time


        } else {

            viewHolder.view.llReciever.visibility = View.GONE
            viewHolder.view.llSender.visibility = View.VISIBLE

            var time = parseDateToTime(chat.created_at)
            viewHolder.view.txtSenderTime.text = time

            if (!chat.message_type.isNullOrEmpty() || !chat.message_type.isNullOrBlank()) {

                if (chat.message_type.toInt() == 1) {

                    viewHolder.view.txtSenderMsg.text =
                        chat.message + "\n" + context!!.getString(R.string.interview_date) + " " + chat.date + "\n" + context!!.getString(
                            R.string.interview_time
                        ) + " " + chat.start_time + " - " + chat.end_time

                    viewHolder.view.txtSaveToCalendar.visibility = View.VISIBLE

                } else {
                    viewHolder.view.txtSenderMsg.text = chat.message
                    viewHolder.view.txtSaveToCalendar.visibility = View.GONE

                }
            }
        }
        viewHolder.view.txtSaveToCalendar.setOnClickListener {

            // var calDate=parseStringToDate(chat.date)
            openCalendar(chat.date, chat.start_time, chat.end_time)

        }
        if (!profilePicUr.isNullOrEmpty() || !profilePicUr.isNullOrBlank()) {

            Picasso.get().load(profilePicUr).placeholder(R.drawable.profile_placeholder)
                .into(viewHolder.view.imgSenderImage)

        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun parseDateToTime(time: String): String? {
        val inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val outputPattern = "hh:mm a"
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

    /*
    method to save data to calendar
     */
    private fun openCalendar(calDate: String, sTime: String, eTime: String) {

        var date = calDate.toString().split("/")
        var calStartTime = sTime.split(":")
        var calEndTIme = eTime.split(":")

        val startTime = Calendar.getInstance()
        startTime.set(
            date[2].toInt(),
            date[1].toInt(),
            date[0].toInt(),
            calStartTime[0].toInt(),
            calStartTime[1].toInt()
        )
        val endTime = Calendar.getInstance()
        endTime.set(date[2].toInt(), date[1].toInt(), date[0].toInt(), calEndTIme[0].toInt(), calEndTIme[1].toInt())

        var intent = Intent(Intent.ACTION_EDIT)
        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.timeInMillis)
        intent.putExtra("allDay", false)
        intent.putExtra("rrule", "FREQ=YEARLY")
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
        intent.putExtra("title", context!!.resources.getString(R.string.interview))
        context!!.startActivity(intent)
    }

}

