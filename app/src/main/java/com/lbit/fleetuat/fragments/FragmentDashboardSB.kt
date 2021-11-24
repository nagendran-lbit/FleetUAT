package com.lbit.fleetuat.fragments

import android.app.ProgressDialog
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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lbit.fleetuat.R
import com.lbit.fleetuat.adapter.DashboardRecordsAdapterSB
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IPartED
import com.lbit.fleetuat.ui.PartsScanningScreen
import com.lbit.fleetuat.utils.Constants
import com.lbit.payroll.Singleton.UserSession
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentDashboardSB : Fragment(), View.OnClickListener, IPartED {

    private var srlSwipeRefresh: SwipeRefreshLayout? = null

    private lateinit var tvRequest: MyTextViewPoppinsSemiBold
    private lateinit var tvHistory: MyTextViewPoppinsSemiBold

    private var vpPager: ViewPager? = null

    lateinit var tvSize: MyTextViewPoppinsSemiBold
    lateinit var tvNext: MyTextViewPoppinsSemiBold
    lateinit var tvPrevious: MyTextViewPoppinsSemiBold
    lateinit var tvNodata: MyTextViewPoppinsSemiBold
    lateinit var ll_dashboard: LinearLayout

    lateinit var etSearch: EditText

    private var mPageCount: String? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0

    private var gson: Gson? = null

    private var isLoaded = false
    private var isVisibleToUser = false
    var mMobile: String = ""
    var dashboardArray: JSONArray = (JSONArray())
    private var mType: String = ""

    var mRecordsType: String = "req"

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
        mMobile = UserSession(requireContext()).getMobile()

        if (mRecordsType == "req") {

            getDashboardData()
        } else {

            getDashboardHistoryData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_dashboard_sb, container, false)

        init(v)

        srlSwipeRefresh!!.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.header_color
            )
        )
        srlSwipeRefresh!!.setColorSchemeColors(Color.WHITE)

        srlSwipeRefresh!!.setOnRefreshListener {

            loadData()
            srlSwipeRefresh!!.isRefreshing = false
        }

        mMobile = UserSession(requireContext()).getMobile()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val newText = editable.toString().trim()

                if (newText.isNotEmpty()) {

                    getFilter(newText)

                } else {

                    ll_dashboard.visibility = View.VISIBLE
                    tvNodata.visibility = View.GONE

                    if (dashboardArray.length() > 0) {
                        val mList = gson!!.fromJson<List<DashboardData>>(
                            dashboardArray.toString(),
                            object : TypeToken<List<DashboardData>>() {
                            }.type
                        )

                        vpPager!!.adapter = DashboardRecordsAdapterSB(
                            requireContext(),
                            mList, this@FragmentDashboardSB, mType
                        )

                        mPageCount = dashboardArray.length().toString()
                        if (mPageCount!!.isNotEmpty()) {
                            tvSize.text = "1 of " + mPageCount!!
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        return v
    }

    private fun getDashboardData() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getDashboardDataListSB(mMobile, "")
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            Log.e("Dashboard Data SB", string)

                            val jsonObject = JSONObject(string)
                            val mReqCount = jsonObject.getString("requests_count")
                            val mHisCount = jsonObject.getString("history_count")

                            tvRequest.text =
                                resources.getString(R.string.request) + "[" + mReqCount + "]"
                            tvHistory.text =
                                resources.getString(R.string.history) + "[" + mHisCount + "]"

                            dashboardArray = jsonObject.getJSONArray("data")

                            if (dashboardArray.length() > 0) {

                                ll_dashboard.visibility = View.VISIBLE
                                tvNodata.visibility = View.GONE

                                val mList = gson!!.fromJson<List<DashboardData>>(
                                    dashboardArray.toString(),
                                    object : TypeToken<List<DashboardData>>() {
                                    }.type
                                )

                                vpPager!!.adapter = DashboardRecordsAdapterSB(
                                    requireContext(),
                                    mList,
                                    this@FragmentDashboardSB, "req"
                                )

                                mPageCount = dashboardArray.length().toString()
                                if (mPageCount!!.isNotEmpty()) {
                                    tvSize.text = "1 of " + mPageCount!!
                                }
                            } else {
                                ll_dashboard.visibility = View.GONE
                                tvNodata.visibility = View.VISIBLE
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

    private fun getDashboardHistoryData() {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getDashboardDataHistoryListSB(mMobile, "")
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            Log.e("Dashboard Data SB His", string)

                            val jsonObject = JSONObject(string)
                            val mReqCount = jsonObject.getString("requests_count")
                            val mHisCount = jsonObject.getString("history_count")

                            tvRequest.text =
                                resources.getString(R.string.request) + "[" + mReqCount + "]"
                            tvHistory.text =
                                resources.getString(R.string.history) + "[" + mHisCount + "]"

                            dashboardArray = jsonObject.getJSONArray("data")

                            if (dashboardArray.length() > 0) {

                                val mList = gson!!.fromJson<List<DashboardData>>(
                                    dashboardArray.toString(),
                                    object : TypeToken<List<DashboardData>>() {
                                    }.type
                                )

                                vpPager!!.adapter = DashboardRecordsAdapterSB(
                                    requireContext(),
                                    mList,
                                    this@FragmentDashboardSB, "history"
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

    private fun getFilter(newText: String) {

        val filteredList: JSONArray = JSONArray()

        for (i in 0 until dashboardArray.length()) {

            val jsonObject = dashboardArray.getJSONObject(i)
            val mSearchText = jsonObject.getString("reg")

            if (mSearchText.toLowerCase().contains(newText.toLowerCase())) {

                filteredList.put(jsonObject)
            }
        }

        Log.e("filtered List", filteredList.toString())

        if (filteredList.length() > 0) {

            ll_dashboard.visibility = View.VISIBLE
            tvNodata.visibility = View.GONE

            val mList = gson!!.fromJson<List<DashboardData>>(
                filteredList.toString(),
                object : TypeToken<List<DashboardData>>() {
                }.type
            )

            vpPager!!.adapter = DashboardRecordsAdapterSB(
                requireContext(),
                mList, this@FragmentDashboardSB, mType
            )

            mPageCount = filteredList.length().toString()
            if (mPageCount!!.isNotEmpty()) {
                tvSize.text = "1 of " + mPageCount!!
            }
        } else {

            ll_dashboard.visibility = View.GONE
            tvNodata.visibility = View.VISIBLE
        }

    }

    fun init(v: View) {

        srlSwipeRefresh = v.findViewById(R.id.srlSwipeRefresh)

        tvRequest = v.findViewById(R.id.tv_requests)
        tvHistory = v.findViewById(R.id.tv_history)
        etSearch = v.findViewById(R.id.et_search_mobile)

        tvRequest.setOnClickListener(this)
        tvHistory.setOnClickListener(this)

        vpPager = v.findViewById(R.id.vp_pager)
        tvPrevious = v.findViewById(R.id.tvPrevious)
        tvNext = v.findViewById(R.id.tvnext)
        tvSize = v.findViewById(R.id.tv_size)
        ll_dashboard = v.findViewById(R.id.ll_dashboard)
        tvNodata = v.findViewById(R.id.tvNodata)

        gson = Gson()

        tvNext.setOnClickListener(this)
        tvPrevious.setOnClickListener(this)

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

    override fun onClick(p0: View?) {

        val i = p0!!.id

        if (i == R.id.tv_requests) {

            tvRequest.setTextColor(resources.getColor(R.color.header_color))
            tvHistory.setTextColor(resources.getColor(R.color.text_color))

            tvRequest.background = resources.getDrawable(R.drawable.underline_text_bg)
            tvHistory.setBackgroundColor(resources.getColor(R.color.white))

            getDashboardData()
            mRecordsType = "req"
        } else if (i == R.id.tv_history) {

            tvRequest.setTextColor(resources.getColor(R.color.text_color))
            tvHistory.setTextColor(resources.getColor(R.color.header_color))

            tvHistory.background = resources.getDrawable(R.drawable.underline_text_bg)
            tvRequest.setBackgroundColor(resources.getColor(R.color.white))

            getDashboardHistoryData()
            mRecordsType = "his"

        } else if (i == R.id.tvPrevious) {
            vpPager!!.setCurrentItem(getInteroffice(-1), true)

            Log.e("TAG", "onClick_Previous: " + vpPager!!.currentItem)
            val mPrev = (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
            tvSize.text = mPrev


        } else if (i == R.id.tvnext) {

            vpPager!!.setCurrentItem(getInteroffice(+1), true)
            Log.e("TAG", "onClick_Next: " + vpPager!!.currentItem + 1)
            val mNext = (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
            tvSize.text = mNext

        }
    }

    private fun getInteroffice(i: Int): Int {
        return vpPager!!.currentItem + i
    }


    override fun onNavigate(position: Int, dashboardData: DashboardData, status: String) {

        mType = status
        if (mType == "req") {

            val i = Intent(requireContext(), PartsScanningScreen::class.java)
            i.putExtra("nid", dashboardData.nid)
            startActivity(i)
        }
    }
}