package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.SelectedHourWeek
import com.partime.user.R
import com.partime.user.Responses.HoursPerWeek

class FilteredHoursPerWeek(
    private val hourPerWeeklist: MutableList<HoursPerWeek>,
    internal var context: Context?,
    listener: SelectedHourWeek
) : RecyclerView.Adapter<FilteredHoursPerWeek.MyViewHolder>() {

    var listener: SelectedHourWeek

    init {

        this.listener = listener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtData: TextView = view.findViewById(R.id.txtTagData)
        var imgCross: ImageView = view.findViewById(R.id.imgTagCross)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.tag_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val hoursWeek = hourPerWeeklist[position]
        viewHolder.txtData.text = hoursWeek.hoursPerWeek.toString().trim()

        viewHolder.imgCross.setOnClickListener {

            hourPerWeeklist.removeAt(position)
            listener.selectedHourWeekListener(hourPerWeeklist.size)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return hourPerWeeklist.size
    }

}