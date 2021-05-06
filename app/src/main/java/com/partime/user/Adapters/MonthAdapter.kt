package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.MonthClickListener
import com.partime.user.R
import kotlinx.android.synthetic.main.months_layout.view.*

class MonthAdapter (var months: ArrayList<String>,var year:String, internal var context: Context?, listener : MonthClickListener) :
    RecyclerView.Adapter<MonthAdapter.MyViewHolder>() {

    var listener: MonthClickListener

    init {
        this.listener = listener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view: View

        init {
            this.view = view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.months_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val monthName = months[position]

        viewHolder.view.txtMonthNameML.setText(monthName)
        viewHolder.view.txtYearML.setText(year)

        viewHolder.view.cardLayoutML.setOnClickListener {

            listener.onMonthClick(monthName,year,position+1)

        }

    }

    override fun getItemCount(): Int {
        return months.size
    }
}
