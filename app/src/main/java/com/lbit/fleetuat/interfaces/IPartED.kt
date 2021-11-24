package com.lbit.fleetuat.interfaces

import com.lbit.fleetuat.data.DashboardData

interface IPartED {

    fun onNavigate(
        position: Int,
        dashboardData: DashboardData,
        status:String
    )
}