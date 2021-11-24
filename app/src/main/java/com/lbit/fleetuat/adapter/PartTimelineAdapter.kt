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


class PartTimelineAdapter(
    private val mContext: Context,
    private var mList: List<DashboardData>,
    private var iRecord: IRecords,
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var mData: DashboardData
    private val listener = iRecord

    private lateinit var tvConReqDate: MyTextViewPoppinsMedium
    private lateinit var tvConReqID: MyTextViewPoppinsMedium
    lateinit var tvConVehicleCondition: MyTextViewPoppinsMedium
    private lateinit var tvConPickupBy: MyTextViewPoppinsMedium
    lateinit var tvConVehicle: MyTextViewPoppinsMedium
    lateinit var tvConQty: MyTextViewPoppinsMedium

    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.part_consumption_data_row, container, false)

        mData = mList[position]

        tvConReqDate = itemView.findViewById(R.id.tvConReqDate)
        tvConReqID = itemView.findViewById(R.id.tvConReqID)
        tvConQty = itemView.findViewById(R.id.tvConQty)
        tvConVehicle = itemView.findViewById(R.id.tvConVehicle)
        tvConPickupBy = itemView.findViewById(R.id.tvConPickupBy)
        tvConVehicleCondition = itemView.findViewById(R.id.tvConVehicleCondition)

        tvConReqDate.text = mData.requestDate
        tvConReqID.text = mData.requestId
        tvConQty.text = mData.qty
        tvConVehicle.text = mData.make
        tvConPickupBy.text = mData.pickupPerson1
        tvConVehicleCondition.text = mData.vehCondition

        tvConReqID.setTextColor(mContext.resources.getColor(R.color.text_color))
        tvConReqID.paintFlags = tvConReqID.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        container.addView(itemView)

        tvConReqID.setOnClickListener {

            mData = mList[position]

            listener.onNavigate(position, mData)

        }

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
