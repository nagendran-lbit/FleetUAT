package com.lbit.fleetuat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.lbit.fleetuat.R
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IPartED


class PartsSummaryAdapter(
    mContext: Context,
    private var mPartsList: List<DashboardData>,
    var mPartED: IPartED
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var mPartsData: DashboardData
    private var listener = mPartED

    private lateinit var tvVehNumber: MyTextViewPoppinsMedium
    private lateinit var tvVehCondition: MyTextViewPoppinsMedium
    private lateinit var tvPartDesc: MyTextViewPoppinsMedium
    private lateinit var tvPart: MyTextViewPoppinsMedium
    private lateinit var tvQuantity: MyTextViewPoppinsMedium
    private lateinit var tvCostPrice: MyTextViewPoppinsMedium
    private lateinit var tvStoreLocation: MyTextViewPoppinsMedium
    private lateinit var tvPickupPerson: MyTextViewPoppinsMedium
    private lateinit var tvContact: MyTextViewPoppinsMedium
    private lateinit var tvSiteLocation: MyTextViewPoppinsMedium
    private lateinit var tvEdit: MyTextViewPoppinsSemiBold
    private lateinit var tvDelete: MyTextViewPoppinsSemiBold

    override fun getCount(): Int {
        return mPartsList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.parts_summary_records_row, container, false)

        mPartsData = mPartsList[position]

        tvVehNumber = itemView.findViewById(R.id.tv_veh_number)
        tvVehCondition = itemView.findViewById(R.id.tv_veh_condition)
        tvPartDesc = itemView.findViewById(R.id.tv_part_desc)
        tvPart = itemView.findViewById(R.id.tv_part)
        tvQuantity = itemView.findViewById(R.id.tv_quantity)
        tvCostPrice = itemView.findViewById(R.id.tv_cost_price)
        tvStoreLocation = itemView.findViewById(R.id.tv_store_location)
        tvPickupPerson = itemView.findViewById(R.id.tv_pickup_person)
        tvContact = itemView.findViewById(R.id.tv_contact)
        tvSiteLocation = itemView.findViewById(R.id.tv_site_location)
        tvEdit = itemView.findViewById(R.id.tvEdit)
        tvDelete = itemView.findViewById(R.id.tvDelete)

        tvVehNumber.text = mPartsData.reg
        tvVehCondition.text = mPartsData.vehCondition
        tvPartDesc.text = mPartsData.description
        tvPart.text = mPartsData.oePart
        tvQuantity.text = mPartsData.qty
        tvCostPrice.text = mPartsData.mrp
        tvSiteLocation.text = mPartsData.siteLocation
        tvStoreLocation.text = mPartsData.storeLocation
        tvPickupPerson.text = mPartsData.pickupPerson
        tvContact.text = mPartsData.contact

        container.addView(itemView)

        tvEdit.setOnClickListener {
            mPartsData = mPartsList[position]

            listener.onNavigate(position,mPartsData,"edit")
        }

        tvDelete.setOnClickListener {
            mPartsData = mPartsList[position]

            listener.onNavigate(position,mPartsData,"delete")
        }
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
