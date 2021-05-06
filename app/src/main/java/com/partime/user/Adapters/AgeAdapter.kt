package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.partime.user.Responses.Age

class AgeAdapter(internal var AgeList: ArrayList<Age>, internal var context: Context) : BaseAdapter() {

    private val mInflator: LayoutInflater

    init {
        this.mInflator = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return AgeList.size
    }

    override fun getItem(position: Int): Any {

        return AgeList[position]
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

        viewHolder.age.text = AgeList[position].age
        return view

    }

    override fun getViewTypeCount(): Int {
        return AgeList.size
    }

    inner class ViewHolder internal constructor(view: View) {

        val age: TextView

        init {
            this.age = view.findViewById(com.partime.user.R.id.text) as TextView
        }

    }
}