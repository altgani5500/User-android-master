package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.SelectedHourRate
import com.partime.user.R
import com.partime.user.Responses.HoursRate

class FilteredHourRateAdapter(
    private val hourRateList: MutableList<HoursRate>,
    internal var context: Context?,
    listener: SelectedHourRate
) : RecyclerView.Adapter<FilteredHourRateAdapter.MyViewHolder>() {

    var listener: SelectedHourRate

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

        val hourRate = hourRateList[position]
        viewHolder.txtData.text = hourRate.hoursRate

        viewHolder.imgCross.setOnClickListener {

            hourRateList.removeAt(position)
            listener.selectedHourRateListener(hourRateList.size)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return hourRateList.size
    }

}