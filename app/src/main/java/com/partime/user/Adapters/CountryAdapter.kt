package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.R
import com.partime.user.Responses.CountryMessage

class CountryAdapter(private val countryList: MutableList<CountryMessage>, internal var context: Context?) :
    RecyclerView.Adapter<CountryAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtData: TextView = view.findViewById(R.id.txtCountryFilterData)
        var txtCheckBox: RadioButton = view.findViewById(R.id.countryRadioBtn)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.country_filter_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val country = countryList[position]
        viewHolder.txtData.text = country.country

        viewHolder.txtCheckBox.isChecked = country.isClicked


        viewHolder.txtCheckBox.setOnClickListener {

            countryList[position].isClicked = true

            for (i in 0..countryList.size - 1) {

                if (i != position) {

                    countryList[i].isClicked = false
                }

            }

            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return countryList.size
    }
}
