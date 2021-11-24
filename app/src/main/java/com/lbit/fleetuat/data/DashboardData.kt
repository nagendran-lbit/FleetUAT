package com.lbit.fleetuat.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DashboardData {

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("nid")
    @Expose
    var nid: String? = null

    @SerializedName("pid")
    @Expose
    var pid: String? = null

    @SerializedName("loc")
    @Expose
    var location: String? = null

    @SerializedName("qty")
    @Expose
    var qty: String? = null

    @SerializedName("entity_id")
    @Expose
    var entityId: String? = null

    @SerializedName("reg")
    @Expose
    var reg: String? = null

    @SerializedName("make")
    @Expose
    var make: String? = null

    @SerializedName("model")
    @Expose
    var model: String? = null

    @SerializedName("variant")
    @Expose
    var variant: String? = null

    @SerializedName("request_date")
    @Expose
    var requestDate: String? = null

    @SerializedName("request_id")
    @Expose
    var requestId: String? = null

    @SerializedName("veh_status")
    @Expose
    var vehStatus: String? = null

    @SerializedName("veh_condition")
    @Expose
    var vehCondition: String? = null

    @SerializedName("oepart")
    @Expose
    var oePart: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("mrp")
    @Expose
    var mrp: String? = null

    @SerializedName("storelocation")
    @Expose
    var storeLocation: String? = null

    @SerializedName("requestedby")
    @Expose
    var requestedBy: String? = null

    @SerializedName("sitelocation")
    @Expose
    var siteLocation: String? = null

    @SerializedName("site_location")
    @Expose
    var siteLocation1: String? = null

    @SerializedName("pickup_person")
    @Expose
    var pickupPerson: String? = null

    @SerializedName("Pickup_person")
    @Expose
    var pickupPerson1: String? = null

    @SerializedName("contact")
    @Expose
    var contact: String? = null

    @SerializedName("location")
    @Expose
    var location1: String? = null

    @SerializedName("jobcard_id")
    @Expose
    var jobcardId: String? = null

    @SerializedName("jobcard_date")
    @Expose
    var jobcardDate: String? = null

    @SerializedName("supervisor")
    @Expose
    var supervisor: String? = null

    @SerializedName("Supervisor")
    @Expose
    var supervisor1: String? = null

    @SerializedName("sp_contact")
    @Expose
    var spContact: String? = null

    @SerializedName("pick_contact")
    @Expose
    var pickContact: String? = null

    @SerializedName("oe_part_no")
    @Expose
    var oePartNo: String? = null

    @SerializedName("part_description")
    @Expose
    var partDescription: String? = null

    @SerializedName("storage_bin")
    @Expose
    var storageBin: String? = null

    @SerializedName("parts")
    @Expose
    var parts: String? = null

    @SerializedName("grn_number")
    @Expose
    var grnNumber: String? = null

    @SerializedName("procurement_pid")
    @Expose
    var procurementPid: String? = null

    @SerializedName("man_qr_no")
    @Expose
    var manQrNo: String? = null

    @SerializedName("vehicle")
    @Expose
    var vehicle: String? = null

    @SerializedName("kms")
    @Expose
    var kms: String? = null

    @SerializedName("fuel_in_litres")
    @Expose
    var fuel: String? = null

    @SerializedName("vehicle_condition")
    @Expose
    var vehicleCondition: String? = null

    @SerializedName("inspection_date")
    @Expose
    var inspectionDate: String? = null

    @SerializedName("inspection_remarks")
    @Expose
    var inspectionRemarks: String? = null

    @SerializedName("service_date")
    @Expose
    var serviceStartDate: String? = null

    @SerializedName("service_end")
    @Expose
    var serviceEndDate: String? = null

    @SerializedName("bin")
    @Expose
    var bin: String? = null

    @SerializedName("jobcard_status")
    @Expose
    var jobcardStatus: String? = null

    @SerializedName("no_of_parts")
    @Expose
    var no_of_parts: String? = null
}