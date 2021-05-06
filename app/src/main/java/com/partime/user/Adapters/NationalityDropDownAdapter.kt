package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.partime.user.Responses.NationalityMessage


class NationalityDropDownAdapter(
    internal var nationalityList: ArrayList<NationalityMessage>,
    internal var context: Context
) : BaseAdapter() {

    private val mInflator: LayoutInflater

    init {
        this.mInflator = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return nationalityList.size
    }

    override fun getItem(position: Int): Any {

        return nationalityList[position]
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

        viewHolder.txtNationality.text = nationalityList[position].nationality
        return view

    }

    override fun getViewTypeCount(): Int {
        return nationalityList.size
    }

    inner class ViewHolder internal constructor(view: View) {

        val txtNationality: TextView

        init {
            this.txtNationality = view.findViewById(com.partime.user.R.id.text) as TextView
        }

    }
}
