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
import com.lbit.fleetuat.interfaces.IPartED


class HealthCheckHistoryAdapter(
    private val mContext: Context,
    private var mList: List<DashboardData>,
    private var iRecord: IPartED,
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var mData: DashboardData
    private val listener = iRecord

    lateinit var tvVehicleH: MyTextViewPoppinsMedium
    lateinit var tvSiteLocationH: MyTextViewPoppinsMedium
    private lateinit var tvKMSH: MyTextViewPoppinsMedium
    lateinit var tvFuelH: MyTextViewPoppinsMedium
    lateinit var tvVehicleConditionH: MyTextViewPoppinsMedium
    lateinit var tvInspectionDateH: MyTextViewPoppinsMedium

    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.health_check_history_row, container, false)

        mData = mList[position]

        tvVehicleH = itemView.findViewById(R.id.tvVehicleH)
        tvSiteLocationH = itemView.findViewById(R.id.tvSiteLocationH)
        tvKMSH = itemView.findViewById(R.id.tvKMSH)
        tvFuelH = itemView.findViewById(R.id.tvFuelH)
        tvVehicleConditionH = itemView.findViewById(R.id.tvVehicleConditionH)
        tvInspectionDateH = itemView.findViewById(R.id.tvInspectionDateH)


        tvVehicleH.text = mData.vehicle

        if (mData.siteLocation1 != "null"){
            tvSiteLocationH.text = mData.siteLocation1

        }
        tvKMSH.text = mData.kms
        tvFuelH.text = mData.fuel
        tvVehicleConditionH.text = mData.vehicleCondition
        tvInspectionDateH.text = mData.inspectionDate

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
