package com.partime.user.Adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.R
import com.partime.user.Responses.ProfileUserDocument
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.expand_profile.*

class ResumeAdapter(private val cerificateList: List<ProfileUserDocument>, internal var context: Context?) :
    RecyclerView.Adapter<ResumeAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtData: TextView = view.findViewById(R.id.txtDocumentName)
        var imgViewDoc: ImageView = view.findViewById(R.id.imgShowDoc)
        var llDocument: LinearLayout = view.findViewById(R.id.llDocument)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.document_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val cerificates = cerificateList[position]

        viewHolder.txtData.text = cerificates.documentName

        viewHolder.imgViewDoc.setOnClickListener {

            if (cerificates.docUrl.contains(".pdf")) {

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(cerificates.docUrl), "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                context!!.startActivity(intent)

                viewHolder.txtData.text = cerificates.documentName + ".pdf"

            } else {

                certificateImage(cerificates.docUrl)
                viewHolder.txtData.text = cerificates.documentName + ".jpeg"

            }


        }
        viewHolder.llDocument.setOnClickListener {

            if (cerificates.docUrl.contains(".pdf")) {

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(cerificates.docUrl), "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                context!!.startActivity(intent)
            } else {

                certificateImage(cerificates.docUrl)
            }

        }

    }

    override fun getItemCount(): Int {
        return cerificateList.size
    }

    private fun certificateImage(url: String) {

        var cerificate = Dialog(context)

        cerificate.window!!.attributes.windowAnimations = R.style.DialogAnimation
        cerificate.setContentView(R.layout.expand_profile)
        cerificate.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        cerificate.window!!.setBackgroundDrawableResource(android.R.color.transparent)


        Picasso.get().load(url).into(cerificate.imgPicDialog)
        cerificate.btnCanclePicDialog.setOnClickListener {

            cerificate.dismiss()
        }

        cerificate.show()
    }
}

