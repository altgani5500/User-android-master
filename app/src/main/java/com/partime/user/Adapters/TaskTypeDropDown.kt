package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class TaskTypeAdapter(internal var taskTypeList: ArrayList<String>, internal var context: Context) : BaseAdapter() {

    private val mInflator: LayoutInflater

    init {
        this.mInflator = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return taskTypeList.size
    }

    override fun getItem(position: Int): Any {

        return taskTypeList[position]
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

        viewHolder.txtTaskType.text = taskTypeList.get(position)
        return view

    }

    override fun getViewTypeCount(): Int {
        return taskTypeList.size
    }

    inner class ViewHolder internal constructor(view: View) {

        val txtTaskType: TextView

        init {
            this.txtTaskType = view.findViewById(com.partime.user.R.id.text) as TextView
        }

    }
}
