package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.SelectedJobTitle
import com.partime.user.R
import com.partime.user.Responses.JobTitleMessage

class FilteredJobAdapter(
    private val jobTitleList: MutableList<JobTitleMessage>,
    internal var context: Context?,
    listener: SelectedJobTitle
) : RecyclerView.Adapter<FilteredJobAdapter.MyViewHolder>() {

    var listener: SelectedJobTitle

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

        val jobTitle = jobTitleList[position]
        viewHolder.txtData.text = jobTitle.jobTitle

        viewHolder.imgCross.setOnClickListener {

            jobTitleList.removeAt(position)
            listener.selectedJobTitleListener(jobTitleList.size)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return jobTitleList.size
    }
}
