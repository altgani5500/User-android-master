package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.SelectedCompany
import com.partime.user.R
import com.partime.user.Responses.ComapnyMessage

class FilteredCompanyAdapter(
    private val companyList: MutableList<ComapnyMessage>,
    internal var context: Context?,
    listener: SelectedCompany
) : RecyclerView.Adapter<FilteredCompanyAdapter.MyViewHolder>() {

    var listener: SelectedCompany

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

        val company = companyList[position]
        viewHolder.txtData.text = company.companyName

        viewHolder.imgCross.setOnClickListener {

            companyList.removeAt(position)
            listener.selectedCompanyListener(companyList.size)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return companyList.size
    }

}