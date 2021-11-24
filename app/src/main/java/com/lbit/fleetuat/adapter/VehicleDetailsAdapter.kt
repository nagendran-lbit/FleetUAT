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


class VehicleDetailsAdapter(
    private val mContext: Context,
    private var mList: List<DashboardData>
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var mData: DashboardData

    private lateinit var tvPart: MyTextViewPoppinsMedium
    private lateinit var tvReqDate: MyTextViewPoppinsMedium
    lateinit var tvReg: MyTextViewPoppinsMedium
    private lateinit var tvVehicle: MyTextViewPoppinsMedium
    lateinit var tvQuantity: MyTextViewPoppinsMedium
    lateinit var tvSupervisor: MyTextViewPoppinsMedium
    lateinit var tvPartDesc: MyTextViewPoppinsMedium
    lateinit var tvBinLocation: MyTextViewPoppinsMedium
    lateinit var tvVehicleCondition: MyTextViewPoppinsMedium

    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.vehicle_details_row, container, false)

        mData = mList[position]

        tvVehicle = itemView.findViewById(R.id.tv_vehicle)
        tvReg = itemView.findViewById(R.id.tv_reg)
        tvSupervisor = itemView.findViewById(R.id.tv_supervisor)
        tvVehicleCondition = itemView.findViewById(R.id.tv_vehicle_condition)
        tvReqDate = itemView.findViewById(R.id.tv_req_date)
        tvPart = itemView.findViewById(R.id.tv_part)
        tvPartDesc = itemView.findViewById(R.id.tv_part_desc)
        tvQuantity = itemView.findViewById(R.id.tv_qty)
        tvBinLocation = itemView.findViewById(R.id.tv_bin_location)

        tvVehicle.text = mData.make
        tvReg.text = mData.reg
        tvSupervisor.text = mData.supervisor1
        tvVehicleCondition.text = mData.vehCondition
        tvReqDate.text = mData.requestDate
        tvPart.text = mData.oePart
        tvPartDesc.text = mData.description
        tvQuantity.text = mData.qty
        tvBinLocation.text = mData.bin

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
