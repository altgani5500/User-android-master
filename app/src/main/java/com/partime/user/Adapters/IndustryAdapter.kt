package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.R
import com.partime.user.Responses.IndustryMessage


class IndustryAdapter(var industryList: MutableList<IndustryMessage>, internal var context: Context?) :
    RecyclerView.Adapter<IndustryAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtData: TextView = view.findViewById(R.id.txtFilterData)
        var imgcheckBox: ImageView = view.findViewById(R.id.imgCheckBoxFilters)
        var txtCheckBox: RelativeLayout = view.findViewById(R.id.filterCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.filter_data_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val industry = industryList[position]
        viewHolder.txtData.text = industry.industry

        if (industry.isClicked) {

            viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_blue)
        } else {

            viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_gray)

        }

        viewHolder.txtCheckBox.setOnClickListener {

            if (industry.isClicked) {

                industry.isClicked = false
                viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_gray)
            } else {

                industry.isClicked = true
                viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_blue)
            }

        }

    }

    override fun getItemCount(): Int {
        return industryList.size
    }

}

