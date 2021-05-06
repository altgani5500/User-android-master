package com.partime.user.Adapters

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.partime.user.R
import com.partime.user.Responses.JobDetailsImage
import com.partime.user.helpers.TouchImageView
import com.partime.user.helpers.Utilities

class ViewPagerDialogAdapter(context: Context, private val imageModelArrayList: List<JobDetailsImage>) :
    PagerAdapter() {
    private val inflater: LayoutInflater
    lateinit var mContext: Context


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
        val imageLayout = inflater.inflate(R.layout.enterprise_dialog_item, view, false)!!

        val imageView = imageLayout.findViewById(R.id.imgJobDescDialog) as TouchImageView
        Utilities.setImagePicasso(mContext, imageView, imageModelArrayList[position].jobImage)

        view.addView(imageLayout, 0)
        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

}