package com.partime.user.Adapters

import android.app.Dialog
import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.partime.user.Adapters.ViewPagerDialogAdapter
import com.partime.user.R
import com.partime.user.Responses.JobDetailsImage
import com.partime.user.helpers.Utilities
import kotlinx.android.synthetic.main.expand_enterprise_pic.*


class FragPagerAdapter(context: Context, private val imageModelArrayList: List<JobDetailsImage>) : PagerAdapter() {
    private val inflater: LayoutInflater
    lateinit var mContext: Context
    lateinit var dailogAdapter: ViewPagerDialogAdapter

    init {
        inflater = LayoutInflater.from(context)
        mContext = context
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return imageModelArrayList.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.item_image_row, view, false)!!

        val imageView = imageLayout.findViewById(R.id.imgJobDesc) as ImageView

        Utilities.setImagePicasso(mContext, imageView, imageModelArrayList[position].jobImage)
        view.addView(imageLayout, 0)

        imageView.setOnClickListener {

            jDpicDialog(position)
        }
        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

    private fun jDpicDialog(position: Int) {

        var jDpicDialog = Dialog(mContext)

        jDpicDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        jDpicDialog.setContentView(R.layout.expand_enterprise_pic)
        jDpicDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        jDpicDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        //url of images .............
        dailogAdapter = ViewPagerDialogAdapter(mContext, imageModelArrayList)
        jDpicDialog.viewPagerDialog.adapter = dailogAdapter
        jDpicDialog.viewPagerDialog.currentItem = position


        jDpicDialog.btnCancleViewpagerDialog.setOnClickListener {

            jDpicDialog.dismiss()
        }

        jDpicDialog.show()
    }

}