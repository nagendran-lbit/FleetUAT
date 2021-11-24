package com.lbit.fleetuat.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lbit.fleetuat.R
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IRecords


class PartsDataAdapter(
    private val mContext: Context,
    var mDataList: List<DashboardData>,
    var mDData:IRecords
    ) : RecyclerView.Adapter<PartsDataAdapter.MyViewHolder>() {

    lateinit var mData: DashboardData
    private val listener = mDData

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvSite: MyTextViewPoppinsMedium = view.findViewById(R.id.tv_site)
        var tvQty: MyTextViewPoppinsMedium = view.findViewById(R.id.tv_qty)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.parts_data_row, parent, false)


        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        mData = mDataList[position]

        holder.tvQty.text = mData.qty
        holder.tvSite.text = mData.location

        holder.tvSite.setPaintFlags(holder.tvSite.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)


        holder.tvSite.setOnClickListener {

            mData = mDataList[position]

            listener.onNavigate(position,mData)
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

}
