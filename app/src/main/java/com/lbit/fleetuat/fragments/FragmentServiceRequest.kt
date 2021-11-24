package com.lbit.fleetuat.fragments

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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lbit.fleetuat.R
import com.lbit.fleetuat.adapter.PartsDataAdapter
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.data.DashboardData
import org.json.JSONArray

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.lbit.fleetuat.adapter.PartsSummaryAdapter
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.interfaces.IPartED
import com.lbit.fleetuat.interfaces.IRecords
import com.lbit.fleetuat.ui.LandingPage
import com.lbit.fleetuat.utils.Constants
import com.lbit.fleetuat.utils.Utilities
import com.lbit.payroll.Singleton.UserSession
import kotlinx.android.synthetic.main.fragment_service_request.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentServiceRequest : Fragment(), View.OnClickListener, IRecords, IPartED {

    private lateinit var recyclerView: RecyclerView
    private var vpPager: ViewPager? = null

    private lateinit var llVehicleDetails: LinearLayout
    private lateinit var llVehicleCondition: LinearLayout
    private lateinit var llPartDetails: LinearLayout
    private lateinit var llSearchPart: LinearLayout
    private lateinit var llSearchPartLoc: LinearLayout
    private lateinit var llSearchPartOtherDetails: LinearLayout
    private lateinit var spVehicleNo: Spinner
    private lateinit var rgVehicleCondition: RadioGroup
    private lateinit var rbVehicleCondition: RadioButton
    private lateinit var rbRunning: RadioButton
    private lateinit var rbBreakdown: RadioButton

    private lateinit var tvMake: MyTextViewPoppinsMedium
    private lateinit var tvModel: MyTextViewPoppinsMedium
    private lateinit var tvFuel: MyTextViewPoppinsMedium
    private lateinit var tvSuperior: MyTextViewPoppinsMedium

    private lateinit var etSearchPartDesc: AutoCompleteTextView
    private lateinit var tvOEPart: MyTextViewPoppinsMedium
    private lateinit var tvStoreLocation: MyTextViewPoppinsMedium
    private lateinit var tvStoreQty: EditText
    private lateinit var spPickupPerson: Spinner
    private lateinit var etContact: EditText

    private lateinit var tvAvailQuantity: MyTextViewPoppinsMedium
    private lateinit var llAddParts: LinearLayout
    private lateinit var llPartsSummary: LinearLayout
    private lateinit var tvAddParts: MyTextViewPoppinsSemiBold
    private lateinit var tvAdd: MyTextViewPoppinsSemiBold
    private lateinit var tvPartConfirm: MyTextViewPoppinsSemiBold
    private lateinit var tvAddSummary: MyTextViewPoppinsSemiBold

    lateinit var tvsize: MyTextViewPoppinsSemiBold
    lateinit var tvnext: MyTextViewPoppinsSemiBold
    lateinit var tvPrevious: MyTextViewPoppinsSemiBold

    private var mPageCount: String? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0

    private lateinit var partsDataAdapter: PartsDataAdapter
    var partsArray: JSONArray = (JSONArray())
    private var gson: Gson? = null

    private var mRegNo: String = ""
    private var mVehicleCondition: String = ""
    private var mSearchPart: String = ""

    private var mRegList = java.util.ArrayList<String>()
    private var mList = java.util.ArrayList<String>()
    private var mQty: String = ""
    private var mSelectedQuantity: String = ""
    private var mPickupPerson: String = ""
    private var mContact: String = ""
    private var mStoreLocation: String = ""
    private var mGRN: String = ""
    private var mProcurementID: String = ""

    var mMobile: String = ""
    private var mOePart: String = ""
    private var mDescription: String = ""
    private var mNid: String = ""
    private var mEntityID: String = ""
    private var mMake: String = ""
    private var mModel: String = ""

    private var isLoaded = false
    private var isVisibleToUser = false

    var mUpdateType = ""

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

    private fun loadData() {

        llVehicleCondition.visibility = View.GONE
        llVehicleDetails.visibility = View.GONE
        llPartDetails.visibility = View.GONE
        llAddParts.visibility = View.GONE
        llPartsSummary.visibility = View.GONE

        mOePart = ""
        mNid = ""
        mMake = ""
        mModel = ""
        mQty = ""
        mSelectedQuantity = ""
        mPickupPerson = ""
        mContact = ""
        mStoreLocation = ""
        mRegNo = ""
        mVehicleCondition = ""
        mSearchPart = ""
        mSelectedQuantity = ""

        getRegList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_service_request, container, false)

        mMobile = UserSession(requireContext()).getMobile()

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

                        getVehicleDetails(mRegNo)
                    } else {
                        mRegNo = ""
                    }
                }
            }

        spPickupPerson.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {
                    if (position > 0) {

                        mPickupPerson = spPickupPerson.selectedItem.toString()

                        if (mPickupPerson.isNotEmpty()){

                            getContactDetails(mPickupPerson)
                        }
                    } else {
                        mPickupPerson = ""
                    }
                }
            }

        rgVehicleCondition.setOnCheckedChangeListener { group, checkedId ->
            rbVehicleCondition = requireView().findViewById(checkedId)
            mVehicleCondition = rbVehicleCondition.text.toString()

            llPartDetails.visibility = View.VISIBLE

            llAddParts.visibility = View.VISIBLE

        }

        etSearchPartDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val mSearchPart = editable.toString()

                if (mSearchPart.length == 3) {
                    hideKeyboard()
                    etSearchPartDesc.clearFocus()

                    searchPart(mSearchPart)

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        etContact.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mContact = editable.toString()

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        tvStoreQty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mSelectedQuantity = editable.toString()

                if (mSelectedQuantity.isNotEmpty()) {

                    if (mSelectedQuantity.toInt() > 0) {
                        if (mSelectedQuantity.toInt() > mQty.toInt()) {

                            tvAdd.visibility = View.GONE

                            Toast.makeText(
                                requireContext(),
                                "Qty Should not be greater than Available Quantity",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            tvAdd.visibility = View.VISIBLE
                        }
                    } else {
                        tvAdd.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Qty Should not be Zero",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        etSearchPartDesc.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mSearchPart = parent.getItemAtPosition(position).toString()
                hideKeyboard()
                etSearchPartDesc.clearFocus()
                etSearchPartDesc.setText(mSearchPart)

                getStockDetails()
            }

        /* etSearchPartDesc.setOnEditorActionListener { _, actionId, _ ->

             if (actionId == EditorInfo.IME_ACTION_DONE) {

                 searchPart(mSearchPart)
             }
             false
         }*/
        return v
    }

    private fun getContactDetails(mPickupPerson: String) {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getContact("person_contact",mPickupPerson)
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()
                            Log.e("Contact Details", string)

                            val jsonObject = JSONObject(string)

                            mContact = jsonObject.getString("contact")

                            etContact.setText(mContact)

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

    private fun getStockDetails() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getStockDetails(mSearchPart, "")
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()
                            Log.e("Stock Details", string)

                            if (!string.equals("{}")) {

                                val jsonObject = JSONObject(string)

                                mOePart = jsonObject.getString("oepart")
                                mDescription = jsonObject.getString("description")
                                mQty = jsonObject.getString("qty")
                                mGRN = jsonObject.getString("grn_number")
                                mProcurementID = jsonObject.getString("procurement_pid")

                                llSearchPart.visibility = View.VISIBLE
                                tvOEPart.text = mDescription
                                tvAvailQuantity.text = mQty

                                getStockLocations()
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

    private fun getStockLocations() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getStockDetails(mSearchPart, mQty)
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()
                            Log.e("Stock Location", string)

                            if (!string.equals("{}")) {

                                llSearchPartLoc.visibility = View.VISIBLE

                                val jsonArray = JSONArray(string)

                                if (jsonArray.length() > 0) {

                                    val mList = gson!!.fromJson<List<DashboardData>>(
                                        jsonArray.toString(),
                                        object : TypeToken<List<DashboardData>>() {
                                        }.type
                                    )

                                    partsDataAdapter =
                                        PartsDataAdapter(
                                            requireContext(),
                                            mList,
                                            this@FragmentServiceRequest
                                        )

                                    recyclerView.adapter = partsDataAdapter
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
                                etSearchPartDesc.setAdapter(adapter)
                                etSearchPartDesc.showDropDown()
                                etSearchPartDesc.setTextColor(Color.BLACK)
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

    private fun getVehicleDetails(mRegNo: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getVehicleDetails(mRegNo)
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            val jsonObject = JSONObject(string)
                            mMake = jsonObject.getString("make")
                            mModel = jsonObject.getString("model")
                            val mFuel = jsonObject.getString("fuel_type")
                            val mSupervisor = jsonObject.getString("supervisor")
                            val mReg = jsonObject.getString("reg")

                            tvMake.text = mMake
                            tvModel.text = mModel
                            tvFuel.text = mFuel
                            tvSuperior.text = mSupervisor

                            Log.e("Veh Details", string)
                            llVehicleCondition.visibility = View.VISIBLE
                            llVehicleDetails.visibility = View.VISIBLE

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

        llVehicleDetails = v.findViewById(R.id.llVehicleDetails)
        llVehicleCondition = v.findViewById(R.id.ll_vehicleCondition)
        llPartDetails = v.findViewById(R.id.ll_part_details)
        llSearchPart = v.findViewById(R.id.ll_search_part)
        llSearchPartLoc = v.findViewById(R.id.ll_search_part_location)
        llSearchPartOtherDetails = v.findViewById(R.id.ll_search_part_otherDetails)
        spVehicleNo = v.findViewById(R.id.sp_vehicle_no)

        rgVehicleCondition = v.findViewById(R.id.rg_vehicle_condition)
        rbRunning = v.findViewById(R.id.rb_running)
        rbBreakdown = v.findViewById(R.id.rb_breakdown)

        etSearchPartDesc = v.findViewById(R.id.et_search_part_desc)
        tvOEPart = v.findViewById(R.id.tv_oe_part_no)
        tvStoreLocation = v.findViewById(R.id.tv_store_location)
        tvStoreQty = v.findViewById(R.id.tv_store_qty)
        spPickupPerson = v.findViewById(R.id.sp_pickup_person)
        etContact = v.findViewById(R.id.et_contact)
        tvAdd = v.findViewById(R.id.tv_add)

        recyclerView = v.findViewById(R.id.recyclerView)
        vpPager = v.findViewById(R.id.vp_pager)
        tvPrevious = v.findViewById(R.id.tvPrevious)
        tvnext = v.findViewById(R.id.tvnext)
        tvsize = v.findViewById(R.id.tv_size)

        tvMake = v.findViewById(R.id.tvMake)
        tvModel = v.findViewById(R.id.tvModel)
        tvFuel = v.findViewById(R.id.tvFuel)
        tvSuperior = v.findViewById(R.id.tvSuperior)

        tvAvailQuantity = v.findViewById(R.id.tv_availQuantity)
        llAddParts = v.findViewById(R.id.ll_add_parts)
        llPartsSummary = v.findViewById(R.id.ll_parts_summary)
        tvAddParts = v.findViewById(R.id.tv_add_parts)
        tvAdd = v.findViewById(R.id.tv_add)
        tvPartConfirm = v.findViewById(R.id.tvPartConfirm)
        tvAddSummary = v.findViewById(R.id.tv_add_summary)

        tvAddParts.setOnClickListener(this)
        tvPartConfirm.setOnClickListener(this)
        tvAddSummary.setOnClickListener(this)
        tvAddParts.setBackground(resources.getDrawable(R.drawable.underline_text_bg))

        gson = Gson()

        tvnext.setOnClickListener(this)
        tvPrevious.setOnClickListener(this)
        tvAdd.setOnClickListener(this)
        tvAddSummary.setOnClickListener(this)

        vpPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

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
                        tvsize.text = mNext
                    }


                } else {

                    val mNext =
                        (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
                    Log.e("TAG", mNext)
                    if (!mNext.equals("0")) {
                        tvsize.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
            }

        })

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

    }

    override fun onClick(v: View?) {

        val i = v!!.id

        if (i == R.id.tv_add) {

            if (mUpdateType.isEmpty()) {
                addPart()

            } else {

                updatePart()
            }

        } else if (i == R.id.tv_add_parts) {

            llAddParts.visibility = View.VISIBLE
            llPartsSummary.visibility = View.GONE

            tvAddParts.setTextColor(resources.getColor(R.color.header_color))
            tvAddSummary.setTextColor(resources.getColor(R.color.text_color))

            tvAddParts.setBackground(resources.getDrawable(R.drawable.underline_text_bg))
            tvAddSummary.setBackgroundColor(resources.getColor(R.color.white))

            mUpdateType = ""
        } else if (i == R.id.tv_add_summary) {

            llAddParts.visibility = View.GONE
            llPartsSummary.visibility = View.VISIBLE

            tvAddSummary.setTextColor(resources.getColor(R.color.header_color))
            tvAddParts.setTextColor(resources.getColor(R.color.text_color))

            tvAddSummary.setBackground(resources.getDrawable(R.drawable.underline_text_bg))
            tvAddParts.setBackgroundColor(resources.getColor(R.color.white))

            getSummaryRecords(mNid)
        } else if (i == R.id.tvPrevious) {
            vpPager!!.setCurrentItem(getItemofviewpager(-1), true)

            Log.e("TAG", "onClick_Previous: " + vpPager!!.currentItem)
            val mPrev = (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
            tvsize.text = mPrev


        } else if (i == R.id.tvnext) {

            vpPager!!.setCurrentItem(getItemofviewpager(+1), true)
            Log.e("TAG", "onClick_Next: " + vpPager!!.currentItem + 1)
            val mNext = (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
            tvsize.text = mNext

        } else if (i == R.id.tvPartConfirm) {

            saveServiceRequest()
        }
    }

    private fun saveServiceRequest() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.saveServiceRequest(mNid)
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            val jsonObject = JSONObject(string)
                            val mRequestId = jsonObject.getString("request_id")
                            val mSiteLocation = jsonObject.getString("site_location")
                            val mStoreLocation = jsonObject.getString("store_location")

                            Log.e("Save", string)

                            mProgressDialog.dismiss()

                            showDialog(mRequestId, mStoreLocation)
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

    private fun showDialog(mRequestId: String, mStoreLocation: String) {

        val mDialogView =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_info, null)

        val btnBack = mDialogView.findViewById(R.id.btnBack) as MyTextViewPoppinsSemiBold
        val tvText =
            mDialogView.findViewById(R.id.tv_submitted_text) as MyTextViewPoppinsSemiBold
        val tvText1 =
            mDialogView.findViewById(R.id.tv_submitted_text1) as MyTextViewPoppinsSemiBold


        tvText.text =
            "Your Request has been generated." + System.lineSeparator() + "Request ID:" + mRequestId
        tvText1.text = "Contact $mStoreLocation for any Other Queries"

        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)

        val mAlertDialog = mBuilder.show()

        btnBack.setOnClickListener {

            mAlertDialog.dismiss()

            val i = Intent(requireContext(), LandingPage::class.java)
            startActivity(i)
        }
    }

    private fun showDialogInfo(mRequestId: String) {

        val mDialogView =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_info, null)

        val btnBack = mDialogView.findViewById(R.id.btnBack) as MyTextViewPoppinsSemiBold
        val tvText =
            mDialogView.findViewById(R.id.tv_submitted_text) as MyTextViewPoppinsSemiBold
        val tvText1 =
            mDialogView.findViewById(R.id.tv_submitted_text1) as MyTextViewPoppinsSemiBold

        tvText.text =
            "Part has been generated successfully." + System.lineSeparator()

        tvText1.text = "To edit/delete the added part. Please click on Summary."
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)

        val mAlertDialog = mBuilder.show()

        btnBack.setOnClickListener {

            mAlertDialog.dismiss()
        }
    }

    private fun getSummaryRecords(mNid: String) {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getDashboardPartsDataList(mNid)
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            Log.e("Summary Data", string)

                            if (!string.equals("{}")) {

                                val jsonArray = JSONArray(string)
                                if (jsonArray.length() > 0) {
                                    val mList = gson!!.fromJson<List<DashboardData>>(
                                        jsonArray.toString(),
                                        object : TypeToken<List<DashboardData>>() {
                                        }.type
                                    )

                                    vpPager!!.adapter = PartsSummaryAdapter(
                                        requireContext(),
                                        mList,
                                        this@FragmentServiceRequest
                                    )

                                    mPageCount = jsonArray.length().toString()
                                    if (mPageCount!!.isNotEmpty()) {
                                        tvsize.text = "1 of " + mPageCount!!
                                    }
                                } else {

                                    llPartsSummary.visibility = View.GONE

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

    private fun addPart() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.addPart(
            mMobile,
            mRegNo,
            mNid,
            mVehicleCondition,
            mOePart,
            mSearchPart,
            "",
            mSelectedQuantity.toString(),
            mMake,
            mModel,
            mStoreLocation,
            "",
            mPickupPerson,
            mContact,
            mGRN,
            mProcurementID
        )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            Log.e("add", string)

                            val jsonObject = JSONObject(string)
                            mNid = jsonObject.getString("nid")

                            showDialogInfo("")

                            mStoreLocation = ""
                            mPickupPerson = ""
                            mContact = ""
                            mQty = ""
                            tvStoreLocation.text = mStoreLocation
                            tvAvailQuantity.text = mQty
                            spPickupPerson.adapter = null
                            etContact.setText(mContact)
                            etContact.clearFocus()
                            mOePart = ""
                            tvOEPart.text = mOePart


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

    private fun updatePart() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.updatePart(
            mNid,
            mEntityID,
            mUpdateType,
            mSelectedQuantity
        )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            Log.e("update", string)

                            /*  val jsonObject = JSONObject(string)
                              mNid = jsonObject.getString("nid")*/

                            mStoreLocation = ""
                            mPickupPerson = ""
                            mContact = ""
                            mQty = ""
                            tvStoreLocation.text = mStoreLocation
                            tvAvailQuantity.text = mQty
                            spPickupPerson.adapter = null
                            etContact.setText(mContact)
                            etContact.clearFocus()
                            mOePart = ""
                            tvOEPart.text = mOePart

                            mProgressDialog.dismiss()

                            if (mUpdateType == "delete") {

                                getSummaryRecords(mNid)
                            } else {

                                showDialogInfo("")
                            }
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

    private fun getItemofviewpager(i: Int): Int {
        return vpPager!!.currentItem + i
    }

    /* private fun increaseInteger() {

         minteger += 1
         display(minteger)

         getStockLocations()
     }*/

    /*private fun decreaseInteger() {

        if (minteger.toInt() > 0) {

            minteger = (minteger.toInt() - 1).toString()
            display(minteger)

            getStockLocations()
        }
    }*/

    /*private fun display(number: String) {
        integernumber.setText(number)
    }*/

    override fun onNavigate(position: Int, dashboardData: DashboardData) {

        llSearchPartLoc.visibility = View.GONE
        llSearchPartOtherDetails.visibility = View.VISIBLE

        mStoreLocation = dashboardData.location.toString()
        mSelectedQuantity = dashboardData.qty!!.toString()

        tvStoreLocation.text = mStoreLocation
        tvStoreQty.setText(mSelectedQuantity)

        getPickupPersonList()

        mPickupPerson = ""
    }

    private fun getPickupPersonList() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getRegNoList("pickup_person").enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {

                    if (response.code() == 200) {
                        val string = response.body()!!.string()

                        Log.e("Pickup Person List", string)

                        val list = ArrayList<String>()

                        mList = Utilities.getItemList(list, string)

                        if (mPickupPerson.isNotEmpty()) {

                            if (mList.indexOf(mPickupPerson) > -1) {

                                spPickupPerson.adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    mList
                                )
                                spPickupPerson.setSelection(mList.indexOf(mPickupPerson))
                            }
                        } else {

                            spPickupPerson.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item, mList
                            )
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

        if (status == "edit") {

            mUpdateType = "edit"

            llAddParts.visibility = View.VISIBLE
            llPartsSummary.visibility = View.GONE

            tvAddParts.setTextColor(resources.getColor(R.color.header_color))
            tvAddSummary.setTextColor(resources.getColor(R.color.text_color))

            tvAddParts.background = resources.getDrawable(R.drawable.underline_text_bg)
            tvAddSummary.setBackgroundColor(resources.getColor(R.color.white))

            llVehicleCondition.visibility = View.VISIBLE
            llVehicleDetails.visibility = View.VISIBLE
            llPartDetails.visibility = View.VISIBLE
            llAddParts.visibility = View.VISIBLE
            llPartsSummary.visibility = View.GONE

            mEntityID = dashboardData.entityId.toString()
            mContact = dashboardData.contact.toString()
            mStoreLocation = dashboardData.storeLocation.toString()
            mPickupPerson = dashboardData.pickupPerson.toString()
            mOePart = dashboardData.oePart.toString()
            mQty = dashboardData.qty!!.toString()

            if (mContact != "null") {

                etContact.setText(mContact)
            }

            if (mStoreLocation != "null") {

                tvStoreLocation.text = mStoreLocation
            }

            if (mOePart.isNotEmpty()) {

                tvOEPart.text = mOePart
            }

            if (mQty.isNotEmpty()) {

                tvAvailQuantity.setText(mQty)
            }
            if (mPickupPerson.isNotEmpty()) {

                getPickupPersonList()
            }
        } else if (status == "delete") {

            mUpdateType = "delete"
            mEntityID = dashboardData.entityId.toString()

            updatePart()
        }
    }
}