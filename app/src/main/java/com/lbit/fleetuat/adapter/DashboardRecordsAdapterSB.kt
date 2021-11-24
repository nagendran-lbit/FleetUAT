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


class DashboardRecordsAdapterSB(
    private val mContext: Context,
    private var mPartsList: List<DashboardData>,
    private var iRecord: IPartED,
    var mType: String
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var mPartsData: DashboardData
    private val listener = iRecord

    lateinit var llDashboardRecordSb: LinearLayout
    lateinit var tvReqId: MyTextViewPoppinsMedium
    lateinit var tvReqDate: MyTextViewPoppinsMedium
    lateinit var tvReg: MyTextViewPoppinsMedium
    lateinit var tvVehicle: MyTextViewPoppinsMedium
    lateinit var tvNoOfParts: MyTextViewPoppinsMedium
    lateinit var tvSupervisor: MyTextViewPoppinsMedium
    lateinit var tvSupervisorContact: MyTextViewPoppinsMedium
    lateinit var tvPickupPerson: MyTextViewPoppinsMedium
    lateinit var tvPickupPersonContact: MyTextViewPoppinsMedium
    lateinit var tvSiteLocation: MyTextViewPoppinsMedium

    override fun getCount(): Int {
        return mPartsList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.dashboard_records_row_sb, container, false)

        mPartsData = mPartsList[position]

        llDashboardRecordSb = itemView.findViewById(R.id.ll_dashboard_record_sb)
        tvReqId = itemView.findViewById(R.id.tv_req_id)
        tvReqDate = itemView.findViewById(R.id.tv_req_date)
        tvReg = itemView.findViewById(R.id.tv_reg)
        tvVehicle = itemView.findViewById(R.id.tv_vehicle)
        tvNoOfParts = itemView.findViewById(R.id.tv_no_of_parts)
        tvSiteLocation = itemView.findViewById(R.id.tv_site_location)
        tvSupervisor = itemView.findViewById(R.id.tv_supervisor)
        tvSupervisorContact = itemView.findViewById(R.id.tv_supervisor_contact)
        tvPickupPerson = itemView.findViewById(R.id.tv_pickup_person)
        tvPickupPersonContact = itemView.findViewById(R.id.tv_pickup_person_contact)

        tvReqId.text = mPartsData.jobcardId
        tvReqDate.text = mPartsData.jobcardDate
        tvReg.text = mPartsData.reg
        tvVehicle.text = mPartsData.make
        tvNoOfParts.text = mPartsData.parts
        tvSiteLocation.text = mPartsData.siteLocation1
        tvSupervisor.text = mPartsData.supervisor
        tvSupervisorContact.text = mPartsData.spContact
        tvPickupPerson.text = mPartsData.pickupPerson
        tvPickupPersonContact.text = mPartsData.pickContact

        container.addView(itemView)

        llDashboardRecordSb.setOnClickListener {

            mPartsData = mPartsList[position]

            listener.onNavigate(position, mPartsData, mType)

        }

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
