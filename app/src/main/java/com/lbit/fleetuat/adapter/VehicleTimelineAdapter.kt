package com.lbit.fleetuat.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.lbit.fleetuat.R
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IDetails
import com.lbit.fleetuat.interfaces.IPartED
import com.lbit.fleetuat.interfaces.IRecords


class VehicleTimelineAdapter(
    private val mContext: Context,
    private var mList: List<DashboardData>,
    private var iRecord: IRecords,
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var mData: DashboardData
    private val listener = iRecord

    lateinit var tvReqDate: MyTextViewPoppinsMedium
    lateinit var tvReqID: MyTextViewPoppinsMedium
    private lateinit var tvPartsReqtd: MyTextViewPoppinsMedium
    lateinit var tvVehicleCondition: MyTextViewPoppinsMedium
    private lateinit var tvServiceStart: MyTextViewPoppinsMedium
    lateinit var tvServiceEnd: MyTextViewPoppinsMedium

    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.vehicle_timeline_row, container, false)

        mData = mList[position]

        tvReqDate = itemView.findViewById(R.id.tvReqDate)
        tvReqID = itemView.findViewById(R.id.tvReqID)
        tvPartsReqtd = itemView.findViewById(R.id.tvPartsReqtd)
        tvVehicleCondition = itemView.findViewById(R.id.tvVehicleCondition)
        tvServiceEnd = itemView.findViewById(R.id.tvServiceEnd)
        tvServiceStart = itemView.findViewById(R.id.tvServiceStart)


        tvReqID.setTextColor(mContext.resources.getColor(R.color.text_color))
        tvReqID.paintFlags = tvReqID.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        tvReqDate.text = mData.requestDate
        tvReqID.text = mData.requestId
        tvPartsReqtd.text = mData.qty
        tvVehicleCondition.text = mData.vehCondition
        tvServiceEnd.text = mData.serviceEndDate
        tvServiceStart.text = mData.serviceStartDate

        container.addView(itemView)

        tvReqID.setOnClickListener {

            mData = mList[position]

            listener.onNavigate(position, mData)

        }
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
