package com.lbit.fleetuat.fragments

import android.app.ProgressDialog
import android.content.Context
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
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lbit.fleetuat.R
import com.lbit.fleetuat.adapter.StockSearchAdapter
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IRecords
import com.lbit.fleetuat.utils.Constants
import com.lbit.fleetuat.utils.Utilities
import kotlinx.android.synthetic.main.fragment_stock.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentStock : Fragment(), View.OnClickListener, IRecords {

    lateinit var etSearchPart: AutoCompleteTextView
    private lateinit var svScroll: NestedScrollView
    private lateinit var llPartsList: LinearLayout
    private lateinit var llFooter: LinearLayout
    private lateinit var llSearchOptions: LinearLayout

    private lateinit var mViewPager: ViewPager
    private lateinit var spMake: Spinner
    private lateinit var spLocation: Spinner
    internal lateinit var list: ArrayList<String>
    internal lateinit var ivClose: ImageView

    private var gson: Gson? = null
    private var mPageCount: String? = null
    private lateinit var tvPagerCount: MyTextViewPoppinsMedium
    private lateinit var ivPrevious: MyTextViewPoppinsMedium
    private lateinit var ivNext: MyTextViewPoppinsMedium

    private var tvNoData: MyTextViewPoppinsMedium? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0
    private var mMakeList = java.util.ArrayList<String>()
    private var mLocList = java.util.ArrayList<String>()

    var mPartDesc: String = ""
    var mMake: String = ""
    var mLocation: String = ""

    private var isLoaded = false
    private var isVisibleToUser = false

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

        getMakeList()

        getLocationList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_stock, container, false)

        init(v)

        etSearchPart.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val mSearchPart = editable.toString()

                if (mSearchPart.length == 3) {
                    hideKeyboard()
                    etSearchPart.clearFocus()

                    searchPart(mSearchPart)

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        etSearchPart.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mPartDesc = parent.getItemAtPosition(position).toString()
                hideKeyboard()
                etSearchPart.clearFocus()
                etSearchPart.setText(mPartDesc)

                getStockDetails()

            }

        spMake.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {
                    if (position > 0) {

                        mMake = spMake.selectedItem.toString()

                        getStockDetails()
                    } else {
                        mMake = ""
                    }
                }
            }

        spLocation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {
                    if (position > 0) {

                        mLocation = spLocation.selectedItem.toString()

                        getStockDetails()
                    } else {
                        mLocation = ""
                    }
                }
            }

        return v
    }

    private fun getLocationList() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getRegNoList("location").enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {

                    if (response.code() == 200) {
                        val string = response.body()!!.string()
                        Log.e("Location List", string)

                        val list = ArrayList<String>()

                        mLocList = Utilities.getItemList(list, string)

                        spLocation.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, mLocList
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

    private fun getMakeList() {
        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getRegNoList("make").enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {

                    if (response.code() == 200) {
                        val string = response.body()!!.string()

                        Log.e("make List", string)
                        val list = ArrayList<String>()

                        mMakeList = Utilities.getItemList(list, string)

                        spMake.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, mMakeList
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

    fun init(v: View) {

        svScroll = v.findViewById(R.id.nest_scrollview)
        svScroll.isFillViewport = true

        llPartsList = v.findViewById(R.id.ll_Parts_list)
        llFooter = v.findViewById(R.id.ll_footer)
        llSearchOptions = v.findViewById(R.id.ll_search_options)

        etSearchPart = v.findViewById(R.id.et_search_part)
        mViewPager = v.findViewById(R.id.pager)
        spMake = v.findViewById(R.id.sp_make)
        spLocation = v.findViewById(R.id.sp_location)
        ivPrevious = v.findViewById(R.id.ivprevious)
        ivNext = v.findViewById(R.id.ivnext)
        tvPagerCount = v.findViewById(R.id.tvPagerCount)
        tvNoData = v.findViewById(R.id.tvNodata)

        ivNext.setOnClickListener(this)
        ivPrevious.setOnClickListener(this)
        list = ArrayList()

        gson = Gson()

        mViewPager.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {

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
//                    Log.e("TAG", "going left")


//                    Log.e("TAG", "onClick_Previous: " + (mViewPager.currentItem - 1))

                    val mNext =
                        (mViewPager.currentItem + 1).toString() + " of " + mPageCount
                    if (!(mViewPager.currentItem - 1).equals(mPageCount) && !mNext.equals(
                            "0"
                        )
                    ) {
                        tvPagerCount.text = mNext
                    }


                } else {

                    val mNext =
                        (mViewPager.currentItem + 1).toString() + " of " + mPageCount
//                    Log.e("TAG", mNext)
                    if (!mNext.equals("0")) {
                        tvPagerCount.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
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
                                etSearchPart.setAdapter(adapter)
                                etSearchPart.showDropDown()
                                etSearchPart.setTextColor(Color.BLACK)
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

    private fun getStockDetails() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getStockSearch(mPartDesc,mMake)
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

                                val jsonArray = JSONArray(string)

                                if (jsonArray.length() > 0) {

                                    etSearchPart.clearFocus()

                                    llSearchOptions.visibility = View.VISIBLE
                                    tvNoData!!.visibility = View.GONE
                                    llFooter.visibility = View.VISIBLE
                                    llPartsList.visibility = View.VISIBLE

                                    val getLoansList = gson!!.fromJson<List<DashboardData>>(
                                        jsonArray.toString(),
                                        object : TypeToken<List<DashboardData>>() {
                                        }.type
                                    )
                                    mViewPager.adapter =
                                        StockSearchAdapter(
                                            requireContext(),
                                            this@FragmentStock,
                                            getLoansList
                                        )
                                    mViewPager.overScrollMode = View.OVER_SCROLL_NEVER

                                    mPageCount = jsonArray.length().toString()

                                    if (mPageCount!!.isNotEmpty()) {
                                        tvPagerCount.text = mPageCount!!
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

    override fun onClick(p0: View?) {

        val i = p0!!.id
        if (i == R.id.ivprevious) {
            mViewPager.setCurrentItem(getItemOfViewpager(-1), true)

            Log.e("TAG", "onClick_Previous: " + mViewPager.currentItem)
            val mPrev = (mViewPager.currentItem + 1).toString() + " of " + mPageCount
            tvPagerCount.text = mPrev


        } else if (i == R.id.ivnext) {

            mViewPager.setCurrentItem(getItemOfViewpager(+1), true)
            Log.e("TAG", "onClick_Next: " + mViewPager.currentItem + 1)
            val mNext = (mViewPager.currentItem + 1).toString() + " of " + mPageCount
            tvPagerCount.text = mNext

        }
    }

    private fun getItemOfViewpager(i: Int): Int {
        return mViewPager.currentItem + i
    }

    override fun onNavigate(position: Int, dashboardData: DashboardData) {


    }
}