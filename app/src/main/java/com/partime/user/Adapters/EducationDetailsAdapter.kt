package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.partime.user.Responses.EducationDetail

class EducationDetailsAdapter(
    internal var educationDetailsList: ArrayList<EducationDetail>,
    internal var context: Context
) : BaseAdapter() {

    private val mInflator: LayoutInflater

    init {
        this.mInflator = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return educationDetailsList.size
    }

    override fun getItem(position: Int): Any {

        return educationDetailsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {

            view = this.mInflator.inflate(com.partime.user.R.layout.nationality_list, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder

        } else {

            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.txtEducationDetail.text = educationDetailsList[position].educationDetail
        return view

    }

    override fun getViewTypeCount(): Int {
        return educationDetailsList.size
    }

    inner class ViewHolder internal constructor(view: View) {

        val txtEducationDetail: TextView

        init {
            this.txtEducationDetail = view.findViewById(com.partime.user.R.id.text) as TextView
        }

    }
}