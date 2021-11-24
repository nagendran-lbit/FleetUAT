package com.lbit.fleetuat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.lbit.fleetuat.R
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IRecords


class StockSearchAdapter(
    private var mContext: Context,
    var mData1:IRecords,
    private var mList: List<DashboardData>
    ) : PagerAdapter() {

    var container = mData1
    lateinit var mData: DashboardData

    internal var layoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private lateinit var tvPartDesc: MyTextViewPoppinsMedium
    private lateinit var tvPart: MyTextViewPoppinsMedium
    private lateinit var tvQty: MyTextViewPoppinsMedium
    private lateinit var tvLocation: MyTextViewPoppinsMedium

    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.stock_details_row, container, false)

        mData = mList[position]

        tvPart = itemView.findViewById(R.id.tvPart)
        tvPartDesc = itemView.findViewById(R.id.tvPartDesc)
        tvQty = itemView.findViewById(R.id.tvQty)
        tvLocation = itemView.findViewById(R.id.tvSiteLocation)

        tvPartDesc.text = mData.description
        tvPart.text = mData.oePart
        tvQty.text = mData.qty
        tvLocation.text = mData.location1

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}
