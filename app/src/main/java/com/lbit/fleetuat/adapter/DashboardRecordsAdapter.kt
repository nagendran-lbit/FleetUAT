package com.lbit.fleetuat.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.lbit.fleetuat.R
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IPartED
import com.lbit.fleetuat.interfaces.IRecords


class DashboardRecordsAdapter(
    private val mContext: Context,
    var mDataList: List<DashboardData>,
    var iDetails: IPartED,
    var value: String
) : RecyclerView.Adapter<DashboardRecordsAdapter.MyViewHolder>() {

    private val listener = iDetails
    lateinit var mData: DashboardData

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvReqID: MyTextViewPoppinsMedium = view.findViewById(R.id.tvReqID)
        var tvRegNo: MyTextViewPoppinsMedium = view.findViewById(R.id.tvRegNo)
        var tvReqDate: MyTextViewPoppinsMedium = view.findViewById(R.id.tvReqDate)
        var tvJobcardStatus: MyTextViewPoppinsMedium = view.findViewById(R.id.tvJobcardStatus)
        var tvCondition: MyTextViewPoppinsMedium = view.findViewById(R.id.tvCondition)
        var llDashBoardDetails: LinearLayout = view.findViewById(R.id.llDashBoardDetails)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_details_row, parent, false)


        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        mData = mDataList[position]


        Log.e("value", value)

        if (value == "pending") {

            holder.llDashBoardDetails.background = mContext.resources.getDrawable(R.drawable.table_bg)
        }else if (value == "live") {

            holder.llDashBoardDetails.background = mContext.resources.getDrawable(R.drawable.table_bg)
        }else if (value == "completed") {

            holder.llDashBoardDetails.background = mContext.resources.getDrawable(R.drawable.completed_bg)
        }
        holder.tvRegNo.text = mData.reg
        holder.tvReqID.text = mData.requestId
        holder.tvReqDate.text = mData.requestDate
        holder.tvCondition.text = mData.vehCondition
        holder.tvJobcardStatus.text = mData.jobcardStatus

        holder.llDashBoardDetails.setOnClickListener {

            mData = mDataList[position]

            listener.onNavigate(position, mData,value)

        }

    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

}
