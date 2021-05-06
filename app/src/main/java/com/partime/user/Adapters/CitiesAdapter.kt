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
import com.partime.user.Responses.CityNames

class CitiesAdapter(private val list: MutableList<CityNames>, internal var context: Context?) :
    RecyclerView.Adapter<CitiesAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtCity: TextView = view.findViewById(R.id.txtFilterData)
        var imgcheckBox: ImageView = view.findViewById(R.id.imgCheckBoxFilters)
        var txtCheckBox: RelativeLayout = view.findViewById(R.id.filterCheckBox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.filter_data_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val city = list[position]
        viewHolder.txtCity.text = city.city

        if (city.isClicked) {

            viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_blue)
        } else {

            viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_gray)

        }

        viewHolder.txtCheckBox.setOnClickListener {

            if (city.isClicked) {

                city.isClicked = false
                viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_gray)
            } else {

                city.isClicked = true
                viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_blue)
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}