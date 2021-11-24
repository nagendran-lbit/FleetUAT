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


class DashboardPartsAdapter(
    mContext: Context,
    private var mPartsList: List<DashboardData>
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var mPartsData :DashboardData

    private lateinit var tvMake: MyTextViewPoppinsMedium
    private lateinit var tvModel: MyTextViewPoppinsMedium
    private lateinit var tvVehCondition: MyTextViewPoppinsMedium
    private lateinit var tvVehNumber: MyTextViewPoppinsMedium
    private lateinit var tvQuantity: MyTextViewPoppinsMedium
    private lateinit var tvPartDesc: MyTextViewPoppinsMedium
    private lateinit var tvPart: MyTextViewPoppinsMedium
    private lateinit var tvPickupPerson: MyTextViewPoppinsMedium
    private lateinit var tvContact: MyTextViewPoppinsMedium
    private lateinit var tvStoreLocation: MyTextViewPoppinsMedium
    private lateinit var tvDate: MyTextViewPoppinsMedium
    private lateinit var tvRequestedBy: MyTextViewPoppinsMedium
    private lateinit var tvCostPrice: MyTextViewPoppinsMedium

    override fun getCount(): Int {
        return mPartsList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.dashboard_parts_row, container, false)

        mPartsData = mPartsList[position]

        tvMake = itemView.findViewById(R.id.tv_make)
        tvModel = itemView.findViewById(R.id.tv_model)
        tvVehNumber = itemView.findViewById(R.id.tv_veh_number)
        tvVehCondition = itemView.findViewById(R.id.tv_veh_condition)
        tvDate = itemView.findViewById(R.id.tv_date)
        tvRequestedBy = itemView.findViewById(R.id.tv_requestedBy)
        tvStoreLocation = itemView.findViewById(R.id.tv_store_location)
        tvPartDesc = itemView.findViewById(R.id.tv_part_desc)
        tvPart = itemView.findViewById(R.id.tv_part)
        tvQuantity = itemView.findViewById(R.id.tv_quantity)
        tvCostPrice = itemView.findViewById(R.id.tv_cost_price)
        tvPickupPerson = itemView.findViewById(R.id.tv_pickup_person)
        tvContact = itemView.findViewById(R.id.tv_contact)

        tvMake.text = mPartsData.make
        tvModel.text = mPartsData.model
        tvVehNumber.text = mPartsData.reg
        tvVehCondition.text = mPartsData.vehCondition
        tvDate.text = mPartsData.requestDate
        tvRequestedBy.text = mPartsData.requestedBy
        tvStoreLocation.text = mPartsData.storeLocation
        tvPartDesc.text = mPartsData.description
        tvPart.text = mPartsData.oePart
        tvQuantity.text = mPartsData.qty
        tvCostPrice.text = mPartsData.mrp
        tvPickupPerson.text = mPartsData.pickupPerson
        tvContact.text = mPartsData.contact

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
