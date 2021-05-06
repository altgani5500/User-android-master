package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.R
import com.partime.user.Responses.StateMessage

class StateAdapter(private val stateList: MutableList<StateMessage>, internal var context: Context?) :
    RecyclerView.Adapter<StateAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtData: TextView = view.findViewById(R.id.txtFilterData)
        var imgcheckBox: ImageView = view.findViewById(R.id.imgCheckBoxFilters)
        var txtCheckBox: RelativeLayout = view.findViewById(R.id.filterCheckBox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.filter_data_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val state = stateList[position]
        viewHolder.txtData.text = state.state

        if (state.isClicked) {

            viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_blue)
        } else {

            viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_gray)

        }

        viewHolder.txtCheckBox.setOnClickListener {

            if (state.isClicked) {

                state.isClicked = false
                viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_gray)
            } else {

                state.isClicked = true
                viewHolder.imgcheckBox.setImageResource(R.drawable.checkbox_blue)
            }

        }

    }

    override fun getItemCount(): Int {
        return stateList.size
    }

}