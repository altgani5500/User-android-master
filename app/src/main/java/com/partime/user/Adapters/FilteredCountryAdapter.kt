package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.SelectedCountry
import com.partime.user.R
import com.partime.user.Responses.CountryMessage
import com.partime.user.prefences.AppPrefence

class FilteredCountryAdapter(
    private val countryList: MutableList<CountryMessage>,
    internal var context: Context?,
    listener: SelectedCountry
) : RecyclerView.Adapter<FilteredCountryAdapter.MyViewHolder>() {

    val appPrefence = AppPrefence.INSTANCE
    var listener: SelectedCountry

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

        appPrefence.initAppPreferences(context)


        val country = countryList[position]
        viewHolder.txtData.text = country.country


        viewHolder.imgCross.setOnClickListener {

            countryList.removeAt(position)
            listener.selectedCountryListener(countryList.size)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

}