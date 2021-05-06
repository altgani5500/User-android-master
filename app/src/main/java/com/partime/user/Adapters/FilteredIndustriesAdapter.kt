package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.SelectedIndustry
import com.partime.user.R
import com.partime.user.Responses.IndustryMessage


class FilteredIndutriesAdapter(
    private val industryList: MutableList<IndustryMessage>,
    internal var context: Context?,
    listener: SelectedIndustry
) : RecyclerView.Adapter<FilteredIndutriesAdapter.MyViewHolder>() {

    var listener: SelectedIndustry

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

        val industry = industryList[position]
        viewHolder.txtData.text = industry.industry

        viewHolder.imgCross.setOnClickListener {

            industryList.removeAt(position)
            listener.selectedIndustryListener(industryList.size)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return industryList.size
    }

}