package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.TaskButtonListener
import com.partime.user.Listeners.TaskClickListener
import com.partime.user.R
import com.partime.user.Responses.TaskListMessage
import kotlinx.android.synthetic.main.task_layout.view.*

class TaskListAdapter(
    private val tasklist: ArrayList<TaskListMessage>,
    internal var context: Context?,
    listener: TaskClickListener,
    buttonLisetner: TaskButtonListener
) : RecyclerView.Adapter<TaskListAdapter.MyViewHolder>() {

    var listener: TaskClickListener
    var buttonLisetner: TaskButtonListener

    init {
        this.listener = listener
        this.buttonLisetner = buttonLisetner

    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view: View

        init {
            this.view = view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val tasks = tasklist[position]


        viewHolder.view.txtEnterpriseNameTL.text = tasks.enterpriseName
        viewHolder.view.txtTaskStatusTL.text = tasks.taskStatus

        if (tasks.taskStatus.equals("Pending")) {
            viewHolder.view.btnStartTL.visibility = View.VISIBLE
            viewHolder.view.txtRatingTL.visibility = View.GONE
            viewHolder.view.txtTaskStatusTL.setTextColor(context!!.resources.getColor(R.color.profile_yellow))
            viewHolder.view.viewTaskStatusTL.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.yellow_circle))
            if(tasks.taskType.equals("Time bound")){
                viewHolder.view.btnRescheduleTL.visibility = View.VISIBLE
            }else{
                viewHolder.view.btnRescheduleTL.visibility = View.GONE
            }

        } else if (tasks.taskStatus.equals("ReEvaluation")) {

            viewHolder.view.txtRatingTL.visibility = View.VISIBLE
            viewHolder.view.btnRescheduleTL.visibility = View.GONE
            viewHolder.view.btnStartTL.visibility = View.GONE

            viewHolder.view.txtTaskStatusTL.setTextColor(context!!.resources.getColor(R.color.applied_color))
            viewHolder.view.viewTaskStatusTL.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.red_circle))

            viewHolder.view.txtRatingTL.text = tasks.taskRating
        } else if (tasks.taskStatus.equals("Completed")) {

            viewHolder.view.txtRatingTL.visibility = View.VISIBLE
            viewHolder.view.txtRatingTL.text = tasks.taskRating

            viewHolder.view.btnStartTL.visibility = View.GONE
            viewHolder.view.btnRescheduleTL.visibility = View.GONE

            if (tasks.evaluation != null || tasks.reevaluation != null) {

                if(tasks.evaluation.toInt() == 0){
                    viewHolder.view.txtTaskStatusTL.text=context!!.getString(R.string.pending_evaluation)
                    viewHolder.view.txtTaskStatusTL.setTextColor(context!!.resources.getColor(R.color.pending_eval))
                    viewHolder.view.viewTaskStatusTL.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.pending_eval_circle))


                }else{
                    viewHolder.view.txtTaskStatusTL.text=context!!.getString(R.string.completed)
                    viewHolder.view.txtTaskStatusTL.setTextColor(context!!.resources.getColor(R.color.blue))
                    viewHolder.view.viewTaskStatusTL.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.blue_circle))

                }

                if (tasks.evaluation.toInt() == 0 || tasks.reevaluation.toInt() == 1) {
                    viewHolder.view.btnRescheduleTL.visibility = View.GONE

                } else {
                    viewHolder.view.btnRescheduleTL.visibility = View.VISIBLE
                    viewHolder.view.btnRescheduleTL.text = context!!.getString(R.string.revaluate)
                }
            }

        } else if (tasks.taskStatus.equals("In Process")) {



            viewHolder.view.btnRescheduleTL.visibility = View.VISIBLE
            viewHolder.view.btnRescheduleTL.text = context!!.getString(R.string.done)
            viewHolder.view.txtRatingTL.visibility = View.GONE
            viewHolder.view.btnStartTL.visibility = View.GONE

            viewHolder.view.txtTaskStatusTL.setTextColor(context!!.resources.getColor(R.color.profile_green))
            viewHolder.view.viewTaskStatusTL.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.green_circle))


        } else {
            viewHolder.view.btnRescheduleTL.visibility = View.GONE
            viewHolder.view.txtRatingTL.visibility = View.GONE
            viewHolder.view.btnStartTL.visibility = View.GONE

            viewHolder.view.txtTaskStatusTL.setTextColor(context!!.resources.getColor(R.color.applied_color))
            viewHolder.view.viewTaskStatusTL.setBackgroundDrawable(context!!.resources.getDrawable(R.drawable.red_circle))

        }

        viewHolder.view.txtTaskNameTL.text = tasks.taskName
        viewHolder.view.txtTaskTypeTL.text = tasks.taskType
        viewHolder.view.txtScheddulerTL.text = tasks.evaluators

        if (!tasks.reason.isNullOrEmpty() || !tasks.reason.isNullOrBlank()) {

            viewHolder.view.txtClarifyText.visibility = View.VISIBLE
            viewHolder.view.txtClarificationTL.visibility = View.VISIBLE
            viewHolder.view.txtClarificationTL.text = tasks.reason
        } else {
            viewHolder.view.txtClarifyText.visibility = View.GONE
            viewHolder.view.txtClarificationTL.visibility = View.GONE
        }

        viewHolder.view.cardTaskList.setOnClickListener {
            listener.onTaskCikcListener(tasks.taskId)
        }

        viewHolder.view.btnRescheduleTL.setOnClickListener {

            var status: String? = null
            if (viewHolder.view.btnRescheduleTL.text.equals(context!!.getString(R.string.reschedule_request))) {
                status = "Rescheduled"
            } else if (viewHolder.view.btnRescheduleTL.text.equals(context!!.getString(R.string.revaluate))) {
                status = "ReEvaluation"
            } else if (viewHolder.view.btnRescheduleTL.text.equals(context!!.getString(R.string.done))) {
                status = "Completed"
            }

            if (status != null) {
                buttonLisetner.onTaskButtonClick(tasks.taskId, status, position)
            }
        }

        viewHolder.view.btnStartTL.setOnClickListener {
            buttonLisetner.onTaskButtonClick(tasks.taskId, "In Process", position)
        }
    }

    override fun getItemCount(): Int {
        return tasklist.size
    }

}