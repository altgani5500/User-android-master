package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.SetCityAdpater
import com.partime.user.R
import com.partime.user.Responses.CityMessage

class CityAdapter(private val cityList: List<CityMessage>, internal var context: Context?, listener: SetCityAdpater) :
    RecyclerView.Adapter<CityAdapter.MyViewHolder>() {

    var listener: SetCityAdpater

    init {
        this.listener = listener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtState: TextView = view.findViewById(R.id.txtStateCityFilter)
        var recyclerCity: RecyclerView = view.findViewById(R.id.recyclerCity)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.city_filter_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val city = cityList[position]
        viewHolder.txtState.text = city.stateName

        listener.setCity(viewHolder.recyclerCity, position)

    }

    override fun getItemCount(): Int {
        return cityList.size
    }

}