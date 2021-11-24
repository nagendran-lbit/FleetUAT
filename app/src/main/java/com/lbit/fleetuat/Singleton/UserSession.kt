package com.lbit.payroll.Singleton

import android.content.Context
import android.content.SharedPreferences


class UserSession(// Context


    private val _context: Context
) {
    // Shared Preferences reference
    internal var pref: SharedPreferences

    // Editor reference for Shared preferences
    private val editor: SharedPreferences.Editor

    // Shared preferences mode
    internal var PRIVATE_MODE = 0

    init {
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun setLoginDetails(value: String) {
        editor.putString(LOGINDETAILS, value)
        editor.apply()
    }

    fun getLoginDetails(): String {
        return pref.getString(LOGINDETAILS, "")!!
    }

    fun removeLoginDetails() {
        editor.remove(LOGINDETAILS)
        editor.apply()
    }

    fun setAttValue(value: String) {
        editor.putString(ATTValue, value)
        editor.apply()
    }

    fun getAttValue(): String {
        return pref.getString(ATTValue, "")!!
    }

    fun removeAttValue() {
        editor.remove(ATTValue)
        editor.apply()
    }


    fun setOTP(value: String) {
        editor.putString(OTP_VALUE, value)
        editor.apply()
    }

    fun getOTP(): String {
        return pref.getString(OTP_VALUE, "")!!
    }

    fun removeOTP() {
        editor.remove(OTP_VALUE)
        editor.apply()
    }

    fun setTaxDetails(value: String) {
        editor.putString(Tax_DATA, value)
        editor.apply()
    }

    fun getTaxDetails(): String {
        return pref.getString(Tax_DATA, "")!!
    }

    fun removeTaxProfile() {
        editor.remove(Tax_DATA)
        editor.apply()
    }

    fun setMobile(value: String) {
        editor.putString(Mobile_Name, value)
        editor.apply()
    }

    fun getMobile(): String {
        return pref.getString(Mobile_Name, "")!!
    }

    fun removeMobile() {
        editor.remove(Mobile_Name)
        editor.apply()
    }

    fun setURL(value: String) {
        editor.putString(URL, value)
        editor.apply()
    }

    fun getURL(): String {
        return pref.getString(URL, "")!!
    }

    fun removeURL() {
        editor.remove(URL)
        editor.apply()
    }


    companion object {

        // Shared preferences file location_name
        private val PREFER_NAME = "VAS"

        private val OTP_VALUE = "otp"
        private val Mobile_Name = "mobile"
        private val URL = "url"


        private val LOGINDETAILS = "login_details"
        private val ATTValue = "att_details"
        private val EVFROMDATA = "ev_form_data"
        private val Tax_DATA = "tax_data"

    }


}
