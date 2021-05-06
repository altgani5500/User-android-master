package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.StateListener
import com.partime.user.R


class StateListAdapter(
    private val stateList: ArrayList<String>,
    internal var context: Context?,
    listener: StateListener
) : RecyclerView.Adapter<StateListAdapter.MyViewHolder>() {

    var listener: StateListener

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

        val states = stateList[position]
        viewHolder.txtData.text = states
        viewHolder.imgCross.setOnClickListener {

            stateList.removeAt(position)

            listener.state(stateList.size, states)

            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return stateList.size
    }

}