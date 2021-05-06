package com.partime.user.Adapters

import com.partime.user.Responses.ViewScheduleMessage
import kotlinx.android.synthetic.main.view_schedule_layout.view.*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.R
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class ViewScheduleAdapter(var schedule: ArrayList<ViewScheduleMessage>, internal var context: Context?) :
    RecyclerView.Adapter<ViewScheduleAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view: View

        init {
            this.view = view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_schedule_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val event = schedule[position]


        if (!event.prifilePicture.isNullOrEmpty() || !event.prifilePicture.isNullOrBlank()) {

            Picasso.get().load(event.prifilePicture).placeholder(R.drawable.profile_placeholder)
                .into(viewHolder.view.imgEnterpriseViewSchedule)
        }

        viewHolder.view.txtTimeViewSchedule.setText(event.time)
        viewHolder.view.txtUserViewSchedule.setText(event.name)
        viewHolder.view.txtJobViewSchedule.setText(event.jobTitle)

    }

    override fun getItemCount(): Int {
        return schedule.size
    }

}

