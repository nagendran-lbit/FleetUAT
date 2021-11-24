package com.lbit.fleetuat.interfaces

import com.lbit.fleetuat.data.DashboardData

interface IRecords {

    fun onNavigate(
        position: Int,
        dashboardData: DashboardData
    )
}