package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.HoursRateListener
import com.partime.user.R
import com.partime.user.Responses.HoursRate

class HoursRateAdapter(private val list: List<HoursRate>, internal var context: Context?, listener: HoursRateListener) :
    RecyclerView.Adapter<HoursRateAdapter.MyViewHolder>() {

    var listener: HoursRateListener

    init {
        this.listener = listener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtRate: TextView = view.findViewById(R.id.txtFilterData)
        var imgcheckBox: ImageView = view.findViewById(R.id.imgCheckBoxFilters)
        var txtCheckBox: RelativeLayout = view.findViewById(R.id.filterCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.filter_data_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val hours = list[position]

        viewHolder.txtRate.text = hours.hoursRate.toString()

        if (hours.isClicked) {

            viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_blue)

        } else {

            viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_gray)

        }


        viewHolder.txtCheckBox.setOnClickListener {

            listener.onSelectNone(position, viewHolder.imgcheckBox, list.size)

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}