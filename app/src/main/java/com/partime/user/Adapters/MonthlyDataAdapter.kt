package com.partime.user.Adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.R
import com.partime.user.Responses.Work
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.expand_profile.*
import kotlinx.android.synthetic.main.monthly_data_layout.view.*

class MonthlyDataAdapter (var workList: ArrayList<Work>,var context: Context): RecyclerView.Adapter<MonthlyDataAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view: View

        init {
            this.view = view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.monthly_data_layout, parent, false)
        return MyViewHolder(view)    }


    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        var work = workList[position]

        viewHolder.view.txtDateMD.setText(work.date)
        viewHolder.view.txtActualRateMD.setText(work.schedule)
        viewHolder.view.txtPunchinMD.setText(work.punchIn)
        viewHolder.view.txtPunchOutMD.setText(work.punchOut)
        viewHolder.view.txtTimeCalculationMD.setText(work.workTime)
        viewHolder.view.txtTaskStatusMD.setText(work.enterpriseStatus)

        viewHolder.view.txtTaskTimeMD.setText(work.lateEarlyHour)
        if(work.status.equals("Early Hour")) {
            viewHolder.view.txtTaskStatus.setText(work.status)
            viewHolder.view.txtTaskStatus.setTextColor(context.resources.getColor(R.color.applied_color))
        }else{
            viewHolder.view.txtTaskStatus.setTextColor(context.resources.getColor(R.color.text_light))
            viewHolder.view.txtTaskStatus.setText(work.status)
        }

        if(work.enterpriseStatus.equals(context.getString(R.string.accepted))){

            viewHolder.view.txtTaskStatusMD.setTextColor(context.resources.getColor(R.color.profile_green))

        }else if(work.enterpriseStatus.equals(context.getString(R.string.rejected))){

            viewHolder.view.txtTaskStatusMD.setTextColor(context.resources.getColor(R.color.applied_color))

        }else if(work.enterpriseStatus.equals(context.getString(R.string.undecided))){

            viewHolder.view.txtTaskStatusMD.setTextColor(context.resources.getColor(R.color.profile_yellow))

        }
        if(!work.reason.isNullOrBlank()||!work.reason.isNullOrEmpty()){

            viewHolder.view.txtReasonMD.setText(work.reason)
            viewHolder.view.txtReason.visibility=View.VISIBLE
            viewHolder.view.txtReasonMD.visibility=View.VISIBLE

        }else{

            viewHolder.view.txtReason.visibility=View.GONE
            viewHolder.view.txtReasonMD.visibility=View.GONE
        }
        if(!work.reasonDoc.isNullOrEmpty()||!work.reasonDoc.isNullOrBlank()){
            viewHolder.view.imgReasonMD.visibility=View.VISIBLE
            viewHolder.view.txtReasonDocMD.visibility=View.VISIBLE
            Picasso.get().load(work.reason).placeholder(R.drawable.image_placeholder).into(viewHolder.view.imgReasonMD)
        }else{

            viewHolder.view.imgReasonMD.visibility=View.GONE
            viewHolder.view.txtReasonDocMD.visibility=View.GONE
        }

        viewHolder.view.imgReasonMD.setOnClickListener{
            if(!work.reasonDoc.isNullOrEmpty()||!work.reasonDoc.isNullOrBlank()) {
                previewReason(work.reasonDoc)
            }
        }
    }

    override fun getItemCount(): Int {
        return workList.size
    }

    private fun previewReason(reasonDoc: String) {

        var reasonDialog = Dialog(context)

        reasonDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        reasonDialog.setContentView(R.layout.expand_profile)
        reasonDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        reasonDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        Picasso.get().load(reasonDoc).into(reasonDialog.imgPicDialog)
        reasonDialog.btnCanclePicDialog.setOnClickListener {

            reasonDialog.dismiss()
        }

        reasonDialog.show()
    }
}