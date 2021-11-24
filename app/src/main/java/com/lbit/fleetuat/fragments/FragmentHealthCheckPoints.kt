package com.lbit.fleetuat.fragments

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lbit.fleetuat.R
import com.lbit.fleetuat.adapter.HealthCheckHistoryAdapter
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IPartED
import com.lbit.fleetuat.ui.VehicleInspectionDamages
import com.lbit.fleetuat.utils.Constants
import com.lbit.fleetuat.utils.Utilities
import com.lbit.payroll.Singleton.UserSession
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class FragmentHealthCheckPoints : Fragment(), View.OnClickListener, IPartED {

    private lateinit var llInspect: LinearLayout
    private lateinit var llHistory: LinearLayout
    private lateinit var llInspectBG: LinearLayout
    private lateinit var llHistoryBG: LinearLayout
    private lateinit var tvInspect: MyTextViewPoppinsMedium
    private lateinit var tvHistory: MyTextViewPoppinsMedium
    private lateinit var tv_history_bg: MyTextViewPoppinsMedium
    private lateinit var tv_inspect_bg: MyTextViewPoppinsMedium

    private lateinit var llScrollview: ScrollView
    private lateinit var llInspectVehicle: LinearLayout
    private lateinit var llHistoryVehicle: LinearLayout
    private lateinit var llVehicleDetails: LinearLayout
    private lateinit var llVehicleEntryDetails: LinearLayout
    private lateinit var llVehicleEntryDetails1: LinearLayout
    private lateinit var llNoData: LinearLayout
    private lateinit var llHistoryData: LinearLayout
    private lateinit var llHistoryFooter: LinearLayout
    private lateinit var llSave: LinearLayout
    private lateinit var spVehicleNo: Spinner
    private lateinit var spVehicleNoHistory: Spinner
    private lateinit var spVehicleCondition: Spinner
    private lateinit var spDamages: Spinner
    private lateinit var etKMS: EditText
    private lateinit var etFuel: EditText
    private lateinit var tvVehicle: MyTextViewPoppinsMedium
    private lateinit var tvSiteLocation: MyTextViewPoppinsMedium
    private lateinit var tvKMS: MyTextViewPoppinsMedium
    private lateinit var tvVehicleCondition: MyTextViewPoppinsMedium
    private lateinit var tvSave: MyTextViewPoppinsSemiBold
    private lateinit var tvDateFrom: MyTextViewPoppinsMedium
    private lateinit var tvDateTo: MyTextViewPoppinsMedium

    private var mRegList = java.util.ArrayList<String>()

    private var mRegNo: String = ""
    private var mRegNoH: String = ""
    private var mVehicle: String = ""
    private var mSiteLocation: String = ""
    private var mKms: String = ""
    private var mVehicleLocation: String = ""
    private var mKms1: String = ""
    private var mFuel: String = ""
    private var mMake: String = ""
    private var mNid: String = ""
    private var mModel: String = ""
    private var mSupervisor: String = ""
    private var mVehicleCondition: String = ""
    private var mDamages: String = ""
    private var mDateFrom: String = ""
    private var mDateTo: String = ""
    private var mMobile: String = ""
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mMonthYear: String? = null
    private lateinit var mCurrentDate: String

    private var isLoaded = false
    private var isVisibleToUser = false

    private lateinit var vpPager: ViewPager
    private var mPageCount: String? = null

    lateinit var tvSize: MyTextViewPoppinsSemiBold
    lateinit var tvNext: MyTextViewPoppinsSemiBold
    lateinit var tvPrevious: MyTextViewPoppinsSemiBold
    lateinit var tvNoData: MyTextViewPoppinsSemiBold

    private var gson: Gson? = null
    var dashboardArray: JSONArray = (JSONArray())
    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        if (isVisibleToUser && isAdded) {

            loadData()
            isLoaded = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isVisibleToUser && (!isLoaded)) {

            loadData()
            isLoaded = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_healthcheckpoints, container, false)

        mMobile = UserSession(requireContext()).getMobile()

        init(v)

        getCurrentDate()

        spVehicleNo.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {
                    if (position > 0) {

                        mRegNo = spVehicleNo.selectedItem.toString()

                        getVehicleDetails(mRegNo)
                    } else {
                        mRegNo = ""
                    }
                }
            }

        spVehicleNoHistory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {
                    if (position > 0) {

                        mRegNoH = spVehicleNoHistory.selectedItem.toString()

                        if (mDateFrom.isNotEmpty() && mDateTo.isNotEmpty()) {

                            getVehicleDetailsHistory()
                        }
                    } else {
                        mRegNoH = ""
                    }
                }
            }

        spVehicleCondition.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {
                    mVehicleCondition = if (position > 0) {

                        spVehicleCondition.selectedItem.toString()

                    } else {
                        ""
                    }
                }
            }

        spDamages.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {
                    if (position > 0) {

                        mDamages = spDamages.selectedItem.toString()

                        if (mDamages == "No") {

                            llSave.visibility = View.VISIBLE
                            etFuel.clearFocus()
                            focusOnView()
                        } else {

                            llSave.visibility = View.GONE

                            val i = Intent(requireContext(), VehicleInspectionDamages::class.java)
                            i.putExtra("nid",mNid)
                            i.putExtra("vehicle",mVehicle)
                            i.putExtra("location",mSiteLocation)
                            i.putExtra("kms",mKms)
                            i.putExtra("vehCond",mVehicleCondition)
                            i.putExtra("reg",mRegNo)
                            i.putExtra("fuel",mFuel)
                            i.putExtra("cDate",mCurrentDate)
                            startActivity(i)
                        }
                    } else {
                        mDamages = ""
                    }
                }
            }

        etKMS.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mKms = editable.toString()

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        etFuel.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mFuel = editable.toString()

                llVehicleEntryDetails1.visibility = View.VISIBLE
                focusOnView1()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        return v

    }


    private fun getCurrentDate() {

        val c = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)

        mMonthYear = if (mMonth < 9) {
            "0" + (mMonth + 1).toString()
        } else {
            (mMonth + 1).toString()
        }
        mCurrentDate = "$mYear-$mMonthYear-$mDay"
    }

    private fun focusOnView() {
        Handler().post(Runnable {
            llScrollview.scrollTo(0, llSave.top)
        })
    }

    private fun focusOnView1() {
        llScrollview.scrollTo(0, llVehicleEntryDetails1.top)
    }

    private fun focusOnView2() {
        llScrollview.scrollTo(0, llHistoryFooter.top)
    }

    fun init(v: View) {

        llInspect = v.findViewById(R.id.llInspect)
        llHistory = v.findViewById(R.id.llHistory)

        llInspectBG = v.findViewById(R.id.llInspectBG)
        llHistoryBG = v.findViewById(R.id.llHistoryBG)

        tvInspect = v.findViewById(R.id.tvInspect)
        tvHistory = v.findViewById(R.id.tvHistory)

        tv_inspect_bg = v.findViewById(R.id.tv_inspect_bg)
        tv_history_bg = v.findViewById(R.id.tv_history_bg)

        llInspectVehicle = v.findViewById(R.id.llInspectVehicle)
        llHistoryVehicle = v.findViewById(R.id.llHistoryVehicle)
        spVehicleNo = v.findViewById(R.id.sp_vehicle_no)
        spVehicleNoHistory = v.findViewById(R.id.sp_vehicle_no_h)

        llScrollview = v.findViewById(R.id.llScrollview)
        llVehicleDetails = v.findViewById(R.id.llVehicleDetails)
        llVehicleEntryDetails = v.findViewById(R.id.llVehicleEntryDetails)
        llVehicleEntryDetails1 = v.findViewById(R.id.llVehicleEntryDetails1)
        llSave = v.findViewById(R.id.llSave)
        tvVehicle = v.findViewById(R.id.tvVehicle)
        tvSiteLocation = v.findViewById(R.id.tvSiteLocation)
        tvKMS = v.findViewById(R.id.tvKMS)
        tvVehicleCondition = v.findViewById(R.id.tvVehicleCondition)
        etKMS = v.findViewById(R.id.etKMS)
        etFuel = v.findViewById(R.id.etFuel)
        spVehicleCondition = v.findViewById(R.id.spVehicleCondition)
        spDamages = v.findViewById(R.id.spDamages)
        tvSave = v.findViewById(R.id.tvSave)

        tvDateFrom = v.findViewById(R.id.tvDateFrom)
        tvDateTo = v.findViewById(R.id.tvDateTo)

        llInspect.setOnClickListener(this)
        llHistory.setOnClickListener(this)
        tvDateFrom.setOnClickListener(this)
        tvDateTo.setOnClickListener(this)
        tvSave.setOnClickListener(this)

        vpPager = v.findViewById(R.id.vp_pager)
        tvPrevious = v.findViewById(R.id.tvPrevious)
        tvNext = v.findViewById(R.id.tvnext)
        tvSize = v.findViewById(R.id.tv_size)
        tvNoData = v.findViewById(R.id.no_data)
        llNoData = v.findViewById(R.id.ll_no_data_found)
        llHistoryData = v.findViewById(R.id.llHistoryData)
        llHistoryFooter = v.findViewById(R.id.llHistoryFooter)

        tvPrevious.setOnClickListener(this)
        tvNext.setOnClickListener(this)
        gson = Gson()

        vpPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
                if (!scrollStarted && state == ViewPager.SCROLLBAR_POSITION_DEFAULT) {
                    scrollStarted = true
                    checkDirection = true
                } else {
                    scrollStarted = false
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                if (thresholdOffset > positionOffset && positionOffsetPixels > thresholdOffsetPixels) {
                    Log.e("TAG", "going left")


                    Log.e("TAG", "onClick_Previous: " + (vpPager!!.currentItem - 1))

                    val mNext =
                        (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
                    if (!(vpPager!!.currentItem - 1).equals(mPageCount) && !mNext.equals("0")) {
                        tvSize.text = mNext
                    }


                } else {

                    val mNext =
                        (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
                    Log.e("TAG", mNext)
                    if (!mNext.equals("0")) {
                        tvSize.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
            }

        })
    }

    private fun loadData() {

        getRegList()

    }

    override fun onClick(v: View?) {
        val i = v!!.id

        if (i == R.id.tvSave) {

            saveInspect()
        } else if (i == R.id.llInspect) {
            llInspectBG.background = resources.getDrawable(R.drawable.search_bg)
            llHistoryBG.background = resources.getDrawable(R.drawable.submit_bg)

            tv_inspect_bg.background = resources.getDrawable(R.drawable.icon_inspect_active)
            tv_history_bg.background = resources.getDrawable(R.drawable.icon_history_noactive)

            tvInspect.setTextColor(resources.getColor(R.color.header_color))
            tvHistory.setTextColor(resources.getColor(R.color.text_color))

            llInspectVehicle.visibility = View.VISIBLE
            llHistoryVehicle.visibility = View.GONE

            llVehicleDetails.visibility = View.GONE
            llVehicleEntryDetails.visibility = View.GONE
            llVehicleEntryDetails1.visibility = View.GONE
            llSave.visibility = View.GONE
            llHistoryData.visibility = View.GONE
            spVehicleNo.setSelection(0)

        } else if (i == R.id.llHistory) {

            llInspectBG.background = resources.getDrawable(R.drawable.submit_bg)
            llHistoryBG.background = resources.getDrawable(R.drawable.search_bg)

            tv_inspect_bg.background = resources.getDrawable(R.drawable.icon_inspect_noactive)
            tv_history_bg.background = resources.getDrawable(R.drawable.icon_history_active)

            tvInspect.setTextColor(resources.getColor(R.color.text_color))
            tvHistory.setTextColor(resources.getColor(R.color.header_color))

            spVehicleNoHistory.setSelection(0)

            llInspectVehicle.visibility = View.GONE
            llHistoryVehicle.visibility = View.VISIBLE
            llHistoryData.visibility = View.GONE

            mDateTo = ""
            mDateFrom = ""

            tvDateFrom.text = mDateFrom
            tvDateTo.text = mDateTo
        } else if (i == R.id.tvPrevious) {
            vpPager.setCurrentItem(getInteroffice(-1), true)

            Log.e("TAG", "onClick_Previous: " + vpPager.currentItem)
            val mPrev = (vpPager.currentItem + 1).toString() + " of " + mPageCount
            tvSize.text = mPrev


        } else if (i == R.id.tvnext) {

            vpPager.setCurrentItem(getInteroffice(+1), true)
            Log.e("TAG", "onClick_Next: " + vpPager.currentItem + 1)
            val mNext = (vpPager.currentItem + 1).toString() + " of " + mPageCount
            tvSize.text = mNext

        } else if (i == R.id.tvDateFrom) {

            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(), R.style.DialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    mMonthYear = if (monthOfYear < 9) {
                        "0" + (monthOfYear + 1).toString()
                    } else {
                        (monthOfYear + 1).toString()
                    }
                    if (dayOfMonth < 9) {
                        mDateFrom =
                            "$year-$mMonthYear-0$dayOfMonth"

                        tvDateFrom.text =
                            "0$dayOfMonth-$mMonthYear-$year"
                    } else {
                        mDateFrom =
                            "$year-$mMonthYear-$dayOfMonth"

                        tvDateFrom.text =
                            "$dayOfMonth-$mMonthYear-$year"
                    }

                    if (mDateFrom.isNotEmpty() && mDateTo.isNotEmpty() && mRegNoH.isNotEmpty()) {

                        getVehicleDetailsHistory()
                    }
                },
                mYear,
                mMonth,
                mDay
            )

            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()

        } else if (i == R.id.tvDateTo) {

            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(), R.style.DialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    mMonthYear = if (monthOfYear < 9) {
                        "0" + (monthOfYear + 1).toString()
                    } else {
                        (monthOfYear + 1).toString()
                    }
                    if (dayOfMonth < 9) {
                        mDateTo =
                            "$year-$mMonthYear-0$dayOfMonth"

                        tvDateTo.text =
                            "0$dayOfMonth-$mMonthYear-$year"
                    } else {
                        mDateTo =
                            "$year-$mMonthYear-$dayOfMonth"

                        tvDateTo.text =
                            "$dayOfMonth-$mMonthYear-$year"
                    }

                    if (mDateFrom.isNotEmpty() && mDateTo.isNotEmpty() && mRegNoH.isNotEmpty()) {

                        getVehicleDetailsHistory()
                    }

                },
                mYear,
                mMonth,
                mDay
            )

            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()

        }
    }

    private fun saveInspect() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.saveInspect(
            mMobile,
            mRegNo,
            mNid,
            mKms,
            mFuel,
            mSiteLocation,
            mVehicleCondition,
            mCurrentDate,
            "",
            ""
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {

                    if (response.code() == 200) {
                        val string = response.body()!!.string()
                        Log.e("save Inspect", string)

                        Toast.makeText(
                            requireContext(),
                            "Inspect Saved Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        llInspectBG.background = resources.getDrawable(R.drawable.submit_bg)
                        llHistoryBG.background = resources.getDrawable(R.drawable.submit_bg)

                        tv_inspect_bg.background =
                            resources.getDrawable(R.drawable.icon_inspect_noactive)
                        tv_history_bg.background =
                            resources.getDrawable(R.drawable.icon_history_noactive)

                        tvInspect.setTextColor(resources.getColor(R.color.text_color))
                        tvHistory.setTextColor(resources.getColor(R.color.text_color))

                        llInspectVehicle.visibility = View.GONE
                        llHistoryVehicle.visibility = View.GONE

                        spVehicleNo.setSelection(0)
                        llVehicleDetails.visibility = View.GONE
                        llVehicleEntryDetails.visibility = View.GONE
                        llVehicleEntryDetails1.visibility = View.GONE
                        llSave.visibility = View.GONE

                        mNid = ""
                        mRegNo = ""
                        mMake = ""
                        mSiteLocation = ""
                        mKms = ""
                        mFuel = ""
                        mVehicleCondition = ""
                        mProgressDialog.dismiss()
                    } else {

                        mProgressDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "There seems to be a network problem. Please contact support team",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(
                    "TAG",
                    "onFailure() called with: call = [" + call.request()
                        .url() + "], t = [" + t + "]",
                    t
                )

                if (mProgressDialog.isShowing)
                    mProgressDialog.dismiss()
            }
        })

    }

    private fun getInteroffice(i: Int): Int {
        return vpPager.currentItem + i
    }

    private fun getRegList() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getRegNoList("reg").enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {

                    if (response.code() == 200) {
                        val string = response.body()!!.string()
                        Log.e("Reg List", string)

                        val list = ArrayList<String>()

                        mRegList = Utilities.getItemList(list, string)

                        spVehicleNo.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, mRegList
                        )

                        spVehicleNoHistory.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, mRegList
                        )
                        mProgressDialog.dismiss()
                    } else {

                        mProgressDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "There seems to be a network problem. Please contact support team",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(
                    "TAG",
                    "onFailure() called with: call = [" + call.request()
                        .url() + "], t = [" + t + "]",
                    t
                )

                if (mProgressDialog.isShowing)
                    mProgressDialog.dismiss()
            }
        })

    }

    private fun getVehicleDetails(mReg: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getVehicleDetails(mReg)
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()
                            llVehicleDetails.visibility = View.VISIBLE
                            llVehicleEntryDetails.visibility = View.VISIBLE

                            val jsonObject = JSONObject(string)

                            mNid = jsonObject.getString("entity_id")
                            mMake = jsonObject.getString("make")
                            mModel = jsonObject.getString("model")
                            mSupervisor = jsonObject.getString("supervisor")
                            mRegNo = jsonObject.getString("reg")
                            mFuel = jsonObject.getString("fuel_type")
                            mVehicleCondition = jsonObject.getString("vehicle_condition")
                            mSiteLocation = jsonObject.getString("site_location")
                            mKms = jsonObject.getString("kms_driven")

                            mVehicle = "$mMake"

                            tvVehicle.text = mVehicle
                            if (mSiteLocation != "null") {
                                tvSiteLocation.text = mSiteLocation
                            }
                            if (mKms != "null") {
                                tvKMS.text = mKms
                            }
                            tvVehicleCondition.text = mVehicleCondition
                            Log.e("Veh Details", string)

                            mProgressDialog.dismiss()
                        } else {

                            mProgressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "There seems to be a network problem. Please contact support team",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(
                        "TAG",
                        "onFailure() called with: call = [" + call.request()
                            .url() + "], t = [" + t + "]",
                        t
                    )

                    if (mProgressDialog.isShowing)
                        mProgressDialog.dismiss()
                }
            })

    }

    private fun getVehicleDetailsHistory() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getVehicleDetailsHistory(mRegNoH, mDateFrom, mDateTo)
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            Log.e("Veh Details History", string)

                            dashboardArray = JSONArray(string)

                            if (dashboardArray.length() > 0) {

                                llHistoryData.visibility = View.VISIBLE
                                focusOnView2()

                                val mList = gson!!.fromJson<List<DashboardData>>(
                                    dashboardArray.toString(),
                                    object : TypeToken<List<DashboardData>>() {
                                    }.type
                                )

                                vpPager.adapter = HealthCheckHistoryAdapter(
                                    requireContext(),
                                    mList, this@FragmentHealthCheckPoints
                                )

                                mPageCount = dashboardArray.length().toString()
                                if (mPageCount!!.isNotEmpty()) {
                                    tvSize.text = "1 of " + mPageCount!!
                                }
                            }

                            mProgressDialog.dismiss()
                        } else {

                            mProgressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "There seems to be a network problem. Please contact support team",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(
                        "TAG",
                        "onFailure() called with: call = [" + call.request()
                            .url() + "], t = [" + t + "]",
                        t
                    )

                    if (mProgressDialog.isShowing)
                        mProgressDialog.dismiss()
                }
            })

    }

    override fun onNavigate(position: Int, dashboardData: DashboardData, status: String) {

    }
}