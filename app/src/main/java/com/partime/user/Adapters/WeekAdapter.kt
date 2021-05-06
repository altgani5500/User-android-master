package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.WeekClickListener
import com.partime.user.Listeners.WeekIsClickedListener
import com.partime.user.R
import com.partime.user.Responses.WorkDetailsMessage
import kotlinx.android.synthetic.main.week_layout.view.*

class WeekAdapter (
    var weeks: ArrayList<WorkDetailsMessage> ,
    internal var context: Context?,
    private  var clickListener: View.OnClickListener):
    RecyclerView.Adapter<WeekAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.week_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        viewHolder.itemView.weekButton.tag = position
        viewHolder.itemView.weekButton.setOnClickListener(clickListener)

        val week = weeks[position]

        //set width according to screen

        val width= context!!.resources.displayMetrics.widthPixels

        viewHolder.itemView.llWeekButton.layoutParams.width=width/5

        if(week.isClicked){

            viewHolder.itemView.weekButton.background=context!!.getDrawable(R.drawable.button_background)
            viewHolder.itemView.weekButton.setTextColor(context!!.resources.getColor(R.color.white))


        }else{
            viewHolder.itemView.weekButton.background=context!!.getDrawable(R.drawable.unselected_week)
            viewHolder.itemView.weekButton.setTextColor(context!!.resources.getColor(R.color.text_bg))
        }

        week.weekName=context!!.getString(R.string.week)+" "+(position+1).toString()

        viewHolder.itemView.weekButton.setText(week.weekName)
    }

    override fun getItemCount(): Int {
        return weeks.size
    }

}
