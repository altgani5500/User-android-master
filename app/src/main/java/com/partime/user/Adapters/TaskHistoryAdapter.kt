package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.R
import com.partime.user.Responses.TaskHistoryMessage
import kotlinx.android.synthetic.main.task_history_layout.view.*
import kotlinx.android.synthetic.main.task_layout.view.*
import kotlinx.android.synthetic.main.task_layout.view.txtTaskNameTL
import kotlinx.android.synthetic.main.task_layout.view.txtTaskStatusTL
import kotlinx.android.synthetic.main.task_layout.view.txtTaskTypeTL
import kotlinx.android.synthetic.main.task_layout.view.viewTaskStatusTL

class TaskHistoryAdapter(private val tasklist: ArrayList<TaskHistoryMessage>, internal var context: Context?) : RecyclerView.Adapter<TaskHistoryAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view: View

        init {
            this.view = view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.task_history_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val tasks = tasklist[position]

        viewHolder.view.txtJobTitleTaskHLayout.text = tasks.enterpriseName
        viewHolder.view.txtTaskStatusTHL.text = tasks.taskStatus


        if (tasks.taskStatus.equals(context!!.getString(R.string.pending))) {

            viewHolder.view.txtTaskStatusTHL.setTextColor(context!!.resources.getColor(R.color.profile_yellow))
            viewHolder.view.viewTaskStatusTHL.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.yellow_circle))


        } else if (tasks.taskStatus.equals(context!!.getString(R.string.completed))) {

            if(!tasks.evaluation.isNullOrBlank()||!tasks.evaluation.isNullOrEmpty()){

                if(tasks.evaluation.toInt()==0){
                    viewHolder.view.txtTaskStatusTHL.text=context!!.resources.getString(R.string.pending_evaluation)
                    viewHolder.view.txtTaskStatusTHL.setTextColor(context!!.resources.getColor(R.color.pending_eval))
                    viewHolder.view.viewTaskStatusTHL.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.pending_eval_circle))

                }else{

                    viewHolder.view.txtTaskStatusTHL.text = tasks.taskStatus
                    viewHolder.view.txtTaskStatusTHL.setTextColor(context!!.resources.getColor(R.color.blue))
                    viewHolder.view.viewTaskStatusTHL.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.blue_circle))

                }
            }

        } else if (tasks.taskStatus.equals(context!!.getString(R.string.in_process))) {

            viewHolder.view.txtTaskStatusTHL.setTextColor(context!!.resources.getColor(R.color.profile_green))
            viewHolder.view.viewTaskStatusTHL.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.green_circle))

        }

        viewHolder.view.txtTaskNameTHL.text = tasks.taskName
        viewHolder.view.txtTaskTypeTHL.text = tasks.taskType

    }

    override fun getItemCount(): Int {
        return tasklist.size
    }

}