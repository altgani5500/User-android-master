package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.SelectedCity
import com.partime.user.R
import com.partime.user.Responses.CityNames

class FiltereCityAdapter(
    private val cityList: MutableList<CityNames>,
    internal var context: Context?,
    listener: SelectedCity
) : RecyclerView.Adapter<FiltereCityAdapter.MyViewHolder>() {

    var listener: SelectedCity

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

        val city = cityList[position]
        viewHolder.txtData.text = city.city
        viewHolder.imgCross.setOnClickListener {

            cityList.removeAt(position)
            listener.selectedCityListener(cityList.size)
            notifyDataSetChanged()
        }


    }

    override fun getItemCount(): Int {
        return cityList.size
    }

}
