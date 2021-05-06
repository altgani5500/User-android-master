package com.partime.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.R
import com.partime.user.Responses.WorkHistoryListMessage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.work_history.view.*

class WorkHistoryAdapter (var workHistoryList: ArrayList<WorkHistoryListMessage> , var context: Context?,private  var clickListener: View.OnClickListener): RecyclerView.Adapter<WorkHistoryAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.work_history, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        viewHolder.itemView.btnResignationTimeLog.tag = position
        viewHolder.itemView.btnResignationTimeLog.setOnClickListener(clickListener)

        val work = workHistoryList[position]

        if(!work.profilePicture.isNullOrBlank()||!work.profilePicture.isNullOrEmpty()){

            Picasso.get().load(work.profilePicture).into(viewHolder.itemView.imgEnterpriseLogoTimeLog)
        }

        if(work.isResignRequest==1){

            viewHolder.itemView.btnResignationTimeLog.background=context!!.getDrawable(R.drawable.applied_button)
            viewHolder.itemView.btnResignationTimeLog.setText(context!!.getString(R.string.resignation_sent))
           viewHolder.itemView.btnResignationTimeLog.isEnabled = false
        }

        viewHolder.itemView.txtDesignationTimeLog.text=work.jobTitle
        viewHolder.itemView.txtEnterpriseTimeLog.text=work.enterpriseName
        viewHolder.itemView.txtActualScheduleData.text=work.schedule
        viewHolder.itemView.txtDateTimeLog.text=work.jobJoining
        viewHolder.itemView.txtHoursTimeLog.text=work.totalWorks.toString()+" "+context!!.resources.getString(R.string.hours)

        if(work.isWorking!=0){

            viewHolder.itemView.btnResignationTimeLog.visibility=View.VISIBLE
        }else{
            viewHolder.itemView.btnResignationTimeLog.visibility=View.GONE

        }

    }

    override fun getItemCount(): Int {
        return workHistoryList.size
    }

}
