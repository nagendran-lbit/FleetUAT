package com.lbit.fleetuat.fragments

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lbit.fleetuat.R
import com.lbit.fleetuat.adapter.PartTimelineAdapter
import com.lbit.fleetuat.adapter.VehicleTimelineAdapter
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IRecords
import com.lbit.fleetuat.ui.VehicleTimelineDetails
import com.lbit.fleetuat.utils.Constants
import com.lbit.fleetuat.utils.Utilities
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FragmentTimelines : Fragment(), View.OnClickListener, IRecords {


    private lateinit var vpPager: ViewPager
    private var mPageCount: String? = null

    lateinit var tvSize: MyTextViewPoppinsSemiBold
    lateinit var tvNext: MyTextViewPoppinsSemiBold
    lateinit var tvPrevious: MyTextViewPoppinsSemiBold

    private lateinit var vpPager1: ViewPager
    private var mPageCount1: String? = null

    lateinit var tvSize1: MyTextViewPoppinsSemiBold
    lateinit var tvNext1: MyTextViewPoppinsSemiBold
    lateinit var tvPrevious1: MyTextViewPoppinsSemiBold

    lateinit var tvNoData: MyTextViewPoppinsSemiBold

    private lateinit var tvDateFrom: MyTextViewPoppinsMedium
    private lateinit var tvDateTo: MyTextViewPoppinsMedium
    private lateinit var tvInductionDate: MyTextViewPoppinsMedium
    private lateinit var tvConsumedDate: MyTextViewPoppinsMedium
    private lateinit var tvIndent: MyTextViewPoppinsSemiBold
    private lateinit var tvConsumption: MyTextViewPoppinsSemiBold

    private lateinit var rgOptions: RadioGroup
    private lateinit var rbOptions: RadioButton
    private lateinit var rbVehicle: RadioButton
    private lateinit var rbPart: RadioButton

    private lateinit var llVehicle: LinearLayout
    private lateinit var llPart: LinearLayout

    private lateinit var spVehicleNo: Spinner
    private lateinit var tvVehicle: MyTextViewPoppinsMedium
    private lateinit var llVehicleData: LinearLayout
    private lateinit var llVehicleName: LinearLayout
    private lateinit var llInductionDate: LinearLayout
    private lateinit var llConsumedDate: LinearLayout
    private lateinit var llPartDesc: LinearLayout
    private lateinit var llPartDetails: LinearLayout
    private lateinit var llPartConsumption: LinearLayout
    private lateinit var llPartIndent: LinearLayout
    private lateinit var llScrollview: ScrollView

    private lateinit var tvPartType: AutoCompleteTextView
    private lateinit var tvPartDesc: MyTextViewPoppinsMedium

    /*Part Indent*/
    private lateinit var tvInGRNDate: MyTextViewPoppinsMedium
    private lateinit var tvInGRNNo: MyTextViewPoppinsMedium
    private lateinit var tvInGRNQty: MyTextViewPoppinsMedium
    private lateinit var tvInGRNFreeQty: MyTextViewPoppinsMedium
    private lateinit var tvInGRNSupplier: MyTextViewPoppinsMedium


    /*Part Consumption*/
    private lateinit var tvConReqDate: MyTextViewPoppinsMedium
    private lateinit var tvConReqID: MyTextViewPoppinsMedium
    private lateinit var tvConQty: MyTextViewPoppinsMedium
    private lateinit var tvConVehicle: MyTextViewPoppinsMedium
    private lateinit var tvConPickupBy: MyTextViewPoppinsMedium
    private lateinit var tvConVehicleCondition: MyTextViewPoppinsMedium

    private var mRegList = java.util.ArrayList<String>()

    private var gson: Gson? = null
    private var dashboardArray: JSONArray = (JSONArray())
    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mMonthYear: String? = null

    private var mType: String = "Vehicle"

    private var isLoaded = false
    private var isVisibleToUser = false

    private var mDateFrom: String = ""
    private var mInductionDate: String = ""
    private var mConsumedDate: String = ""
    private var mDateTo: String = ""
    private var mRegNo: String = ""
    private var mPartType: String = ""

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

        val v = inflater.inflate(R.layout.fragment_timelines, container, false)

        init(v)

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

                        if (mDateFrom.isNotEmpty() && mDateTo.isNotEmpty()) {
                            getVehicleTimeline()
                        }
                    } else {
                        mRegNo = ""
                    }
                }
            }

        rgOptions.setOnCheckedChangeListener { group, checkedId ->
            rbOptions = requireView().findViewById(checkedId)
            mType = rbOptions.text.toString()

            if (mDateFrom.isNotEmpty() && mDateTo.isNotEmpty()) {

                if (mType == resources.getString(R.string.vehicle)) {

                    llVehicle.visibility = View.VISIBLE
                    llPart.visibility = View.GONE
                } else if (mType == resources.getString(R.string.part1)) {

                    llVehicle.visibility = View.GONE
                    llPart.visibility = View.VISIBLE
                }
            }
        }

        tvPartType.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val mSearchPart = editable.toString()

                if (mSearchPart.length == 3) {
                    hideKeyboard()
                    tvPartType.clearFocus()

                    searchPart(mSearchPart)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        tvPartType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mPartType = parent.getItemAtPosition(position).toString()
                hideKeyboard()
                tvPartType.clearFocus()
                tvPartType.setText(mPartType)

                if (mDateFrom.isNotEmpty() && mDateTo.isNotEmpty()) {
                    getPartTimeline("intent")
                }
            }

        return v
    }

    private fun getVehicleTimeline() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getTimelines(
            "vehicle",
            mRegNo,
            "",
            mDateFrom,
            mDateTo
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {

                    if (response.code() == 200) {
                        val string = response.body()!!.string()
                        Log.e("Vehicle Timeline List", string)

                        val jsonObject = JSONObject(string)

                        llInductionDate.visibility = View.VISIBLE
                        llVehicleName.visibility = View.VISIBLE

                        val dataObject = jsonObject.getJSONObject("data")

                        tvInductionDate.text = dataObject.getString("induction_date").toString()
                        tvVehicle.text = dataObject.getString("vehicle").toString()

                        val jsonArray = jsonObject.getJSONArray("timeline")

                        if (jsonArray.length() > 0) {

                            llVehicleData.visibility = View.VISIBLE

                            val mList = gson!!.fromJson<List<DashboardData>>(
                                jsonArray.toString(),
                                object : TypeToken<List<DashboardData>>() {
                                }.type
                            )

                            vpPager.adapter = VehicleTimelineAdapter(
                                requireContext(),
                                mList, this@FragmentTimelines
                            )

                            mPageCount = jsonArray.length().toString()
                            if (mPageCount!!.isNotEmpty()) {
                                tvSize.text = "1 of $mPageCount"
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

    private fun getPartTimeline(type: String) {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getTimelines(
            "part",
            "",
            "GRNCHECK",
            mDateFrom,
            mDateTo
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {

                    if (response.code() == 200) {
                        val string = response.body()!!.string()
                        Log.e("Part Timeline List", string)

                        val jsonObject = JSONObject(string)
                        val dataObject = jsonObject.getJSONObject("data")
                        val intentObject = jsonObject.getJSONObject("indent")
                        val jsonArray = jsonObject.getJSONArray("consumption")

                        val mPartDesc = dataObject.getString("part_description")
                        val mConsumedQty = dataObject.getString("consumed_qty")

                        llConsumedDate.visibility = View.VISIBLE
                        llPartDesc.visibility = View.VISIBLE
                        tvConsumedDate.text = mConsumedQty
                        tvPartDesc.text = mPartDesc

                        if (type == "intent") {
                            llPartDetails.visibility = View.VISIBLE
                            llPartIndent.visibility = View.VISIBLE
                            llPartConsumption.visibility = View.GONE

                            val mPid = intentObject.getString("procurement_pid")
                            val mPart = intentObject.getString("oepart")
                            val mPartDesc = intentObject.getString("description")
                            val mGrnNo = intentObject.getString("grn_number")
                            val mGrnQty = intentObject.getString("grn_qty")
                            val mAvailQty = intentObject.getString("available_qty")
                            val mSupplier = intentObject.getString("Supplier")
                            val mGrnDate = intentObject.getString("grn_date")

                            tvInGRNDate.text = mGrnDate
                            tvInGRNNo.text = mGrnNo
                            tvInGRNQty.text = mGrnQty
                            tvInGRNFreeQty.text = mAvailQty
                            tvInGRNSupplier.text = mSupplier
                        } else {

                            llPartIndent.visibility = View.GONE
                            llPartConsumption.visibility = View.VISIBLE

                            if (jsonArray.length() > 0) {

                                val mList = gson!!.fromJson<List<DashboardData>>(
                                    jsonArray.toString(),
                                    object : TypeToken<List<DashboardData>>() {
                                    }.type
                                )

                                vpPager1.adapter = PartTimelineAdapter(
                                    requireContext(),
                                    mList,this@FragmentTimelines
                                )

                                mPageCount1 = jsonArray.length().toString()
                                if (mPageCount1!!.isNotEmpty()) {
                                    tvSize1.text = "1 of $mPageCount1"
                                }
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

    private fun hideKeyboard() {
        val view = requireActivity().currentFocus
        view?.let { v ->
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
        }
    }

    fun init(v: View) {

        vpPager = v.findViewById(R.id.vp_pager)
        tvPrevious = v.findViewById(R.id.tvPrevious)
        tvNext = v.findViewById(R.id.tvNext)
        tvSize = v.findViewById(R.id.tvSize)

        vpPager1 = v.findViewById(R.id.vp_pager1)
        tvPrevious1 = v.findViewById(R.id.tvPrevious1)
        tvNext1 = v.findViewById(R.id.tvNext1)
        tvSize1 = v.findViewById(R.id.tvSize1)
//        tvNoData = v.findViewById(R.id.no_data)

        tvPrevious.setOnClickListener(this)
        tvNext.setOnClickListener(this)

        tvPrevious1.setOnClickListener(this)
        tvNext1.setOnClickListener(this)
        gson = Gson()

        tvDateFrom = v.findViewById(R.id.tvDateFrom)
        tvDateTo = v.findViewById(R.id.tvDateTo)
        tvInductionDate = v.findViewById(R.id.tvInductionDate)
        tvConsumedDate = v.findViewById(R.id.tvConsumedDate)
        tvIndent = v.findViewById(R.id.tvIndent)
        tvConsumption = v.findViewById(R.id.tvConsumption)

        tvDateFrom.setOnClickListener(this)
        tvDateTo.setOnClickListener(this)
        tvConsumedDate.setOnClickListener(this)
        tvIndent.setOnClickListener(this)
        tvConsumption.setOnClickListener(this)
        tvIndent.background = resources.getDrawable(R.drawable.underline_text_bg)
        tvIndent.setTextColor(resources.getColor(R.color.header_color))

        rgOptions = v.findViewById(R.id.rg_options)
        rbVehicle = v.findViewById(R.id.rb_vehicle)
        rbPart = v.findViewById(R.id.rb_part)

        llPart = v.findViewById(R.id.llPart)
        llVehicle = v.findViewById(R.id.llVehicle)

        spVehicleNo = v.findViewById(R.id.spVehicleNo)
        tvVehicle = v.findViewById(R.id.tvVehicle)
        llVehicleData = v.findViewById(R.id.llVehicleData)
        llVehicleName = v.findViewById(R.id.llVehicleName)
        llInductionDate = v.findViewById(R.id.llInductionDate)
        llConsumedDate = v.findViewById(R.id.llConsumedDate)
        llPartDesc = v.findViewById(R.id.llPartDesc)
        llPartDetails = v.findViewById(R.id.ll_part_details)
        llPartConsumption = v.findViewById(R.id.llPartConsumption)
        llPartIndent = v.findViewById(R.id.llPartIndent)
        llScrollview = v.findViewById(R.id.llScrollview)

        tvPartType = v.findViewById(R.id.tvPartType)
        tvPartDesc = v.findViewById(R.id.tvPartDesc)

        /*Part Indent*/
        tvInGRNDate = v.findViewById(R.id.tvInGRNDate)
        tvInGRNNo = v.findViewById(R.id.tvInGRNNo)
        tvInGRNQty = v.findViewById(R.id.tvInGRNQty)
        tvInGRNFreeQty = v.findViewById(R.id.tvInGRNFreeQty)
        tvInGRNSupplier = v.findViewById(R.id.tvInGRNSupplier)

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


                    Log.e("TAG", "onClick_Previous: " + (vpPager.currentItem - 1))

                    val mNext =
                        (vpPager.currentItem + 1).toString() + " of " + mPageCount
                    if (!(vpPager.currentItem - 1).equals(mPageCount) && mNext != "0") {
                        tvSize.text = mNext
                    }


                } else {

                    val mNext =
                        (vpPager.currentItem + 1).toString() + " of " + mPageCount
                    Log.e("TAG", mNext)
                    if (mNext != "0") {
                        tvSize.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
            }

        })

        vpPager1.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

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


                    Log.e("TAG", "onClick_Previous: " + (vpPager.currentItem - 1))

                    val mNext =
                        (vpPager1.currentItem + 1).toString() + " of " + mPageCount1
                    if (!(vpPager1.currentItem - 1).equals(mPageCount1) && mNext != "0") {
                        tvSize1.text = mNext
                    }
                } else {

                    val mNext =
                        (vpPager1.currentItem + 1).toString() + " of " + mPageCount1
                    Log.e("TAG", mNext)
                    if (mNext != "0") {
                        tvSize1.text = mNext
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

        if (i == R.id.tvConReqID) {

            val i = Intent(requireContext(), VehicleTimelineDetails::class.java)
            startActivity(i)

        } else if (i == R.id.tvIndent) {

            llPartConsumption.visibility = View.GONE
            llPartIndent.visibility = View.VISIBLE

            tvIndent.background = resources.getDrawable(R.drawable.underline_text_bg)
            tvIndent.setTextColor(resources.getColor(R.color.header_color))

            tvConsumption.setTextColor(resources.getColor(R.color.text_color))
            tvConsumption.setBackgroundColor(resources.getColor(R.color.white))

            getPartTimeline("intent")

        } else if (i == R.id.tvConsumption) {

            llPartIndent.visibility = View.GONE
            llPartConsumption.visibility = View.VISIBLE

            tvIndent.setTextColor(resources.getColor(R.color.text_color))
            tvIndent.setBackgroundColor(resources.getColor(R.color.white))

            tvConsumption.background = resources.getDrawable(R.drawable.underline_text_bg)
            tvConsumption.setTextColor(resources.getColor(R.color.header_color))

            getPartTimeline("consumption")

        } else if (i == R.id.tvPrevious) {
            vpPager.setCurrentItem(getInteroffice(-1), true)

            Log.e("TAG", "onClick_Previous: " + vpPager.currentItem)
            val mPrev = (vpPager.currentItem + 1).toString() + " of " + mPageCount
            tvSize.text = mPrev


        } else if (i == R.id.tvNext) {

            vpPager.setCurrentItem(getInteroffice(+1), true)
            Log.e("TAG", "onClick_Next: " + vpPager.currentItem + 1)
            val mNext = (vpPager.currentItem + 1).toString() + " of " + mPageCount
            tvSize.text = mNext

        } else if (i == R.id.tvPrevious1) {
            vpPager1.setCurrentItem(getInteroffice1(-1), true)

            Log.e("TAG", "onClick_Previous: " + vpPager1.currentItem)
            val mPrev = (vpPager1.currentItem + 1).toString() + " of " + mPageCount1
            tvSize1.text = mPrev


        } else if (i == R.id.tvNext1) {

            vpPager1.setCurrentItem(getInteroffice1(+1), true)
            Log.e("TAG", "onClick_Next: " + vpPager1.currentItem + 1)
            val mNext = (vpPager1.currentItem + 1).toString() + " of " + mPageCount1
            tvSize1.text = mNext

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

                    if (mDateFrom.isNotEmpty() && mDateTo.isNotEmpty()) {

                        if (mType == resources.getString(R.string.vehicle)) {

                            llVehicle.visibility = View.VISIBLE
                            llPart.visibility = View.GONE
                        } else if (mType == resources.getString(R.string.part1)) {

                            llVehicle.visibility = View.GONE
                            llPart.visibility = View.VISIBLE
                        }
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

                    if (mDateFrom.isNotEmpty() && mDateTo.isNotEmpty()) {

                        if (mType == resources.getString(R.string.vehicle)) {

                            llVehicle.visibility = View.VISIBLE
                            llPart.visibility = View.GONE
                        } else if (mType == resources.getString(R.string.part1)) {

                            llVehicle.visibility = View.GONE
                            llPart.visibility = View.VISIBLE
                        }
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

    private fun searchPart(mSearchPart: String) {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getPartSearch(mSearchPart)
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()
                            Log.e("Search Part", string)

                            if (!string.equals("{}")) {
                                val mStates = ArrayList<String>()
                                val stateList = Utilities.getItemListPart(mStates, string)

                                val adapter: ArrayAdapter<String> = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_list_item_1, stateList
                                )
                                tvPartType.setAdapter(adapter)
                                tvPartType.showDropDown()
                                tvPartType.setTextColor(Color.BLACK)

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

    private fun getInteroffice(i: Int): Int {
        return vpPager.currentItem + i
    }

    private fun getInteroffice1(i: Int): Int {
        return vpPager1.currentItem + i
    }


    override fun onNavigate(position: Int, dashboardData: DashboardData) {
        val mReqId = dashboardData.requestId

        val i = Intent(requireContext(), VehicleTimelineDetails::class.java)
        i.putExtra("ReqID", mReqId)
        i.putExtra("DateFrom", mDateFrom)
        i.putExtra("DateTo", mDateTo)
        startActivity(i)    }
}