package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gems.app.utilities.AppValidator
import com.partime.user.R
import com.partime.user.Responses.GroupChatListReponse
import com.partime.user.prefences.AppPrefence
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.item_chat.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GroupChatAdapter (var chatList: ArrayList<GroupChatListReponse>, internal var context: Context?) : RecyclerView.Adapter<GroupChatAdapter.MyViewHolder>() {

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

        appPrefence.initAppPreferences(context)
        val chat = chatList[position]

        val senderId = chat.userId

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
            viewHolder.view.txtSenderName.visibility=View.VISIBLE

            viewHolder.view.txtSenderMsg.text = chat.message
            viewHolder.view.txtSenderName.text=chat.name

            if (!chat.userPic.equals("")) {
                Picasso.get().load(chat.userPic).placeholder(R.drawable.profile_placeholder)
                    .into(viewHolder.view.imgSenderImage)

            }
            var time = parseDateToTime(chat.created_at)
            viewHolder.view.txtSenderTime.text = time

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
}
