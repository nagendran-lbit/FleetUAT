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
import android.widget.ScrollView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lbit.fleetuat.R
import com.lbit.fleetuat.adapter.DashboardRecordsAdapter
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IPartED
import com.lbit.fleetuat.interfaces.IRecords
import com.lbit.fleetuat.ui.DashboardParts
import com.lbit.fleetuat.utils.Constants
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentDashboard : Fragment(), View.OnClickListener, IPartED {


    private var srlSwipeRefresh: SwipeRefreshLayout? = null

    /* Headers */
    private lateinit var tvLive: MyTextViewPoppinsSemiBold
    private lateinit var tvCompleted: MyTextViewPoppinsSemiBold
    private lateinit var etSearch: EditText

    /*Dashboard Table*/
    private lateinit var llScrollview: ScrollView
    private lateinit var llNoData: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var dashboardRecordsAdapter: DashboardRecordsAdapter
    var dashboardArray: JSONArray = (JSONArray())
    private var gson: Gson? = null

    private var isLoaded = false
    private var isVisibleToUser = false

    var mRecordsType: String = "live"

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

        getDashboardData(mRecordsType)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_dashboard, container, false)

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

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val newText = editable.toString().trim()

                if (newText.isNotEmpty()) {

                    getFilter(newText)

                } else {

                    if (dashboardArray.length() > 0) {
                        val mList = gson!!.fromJson<List<DashboardData>>(
                            dashboardArray.toString(),
                            object : TypeToken<List<DashboardData>>() {
                            }.type
                        )

                        dashboardRecordsAdapter =
                            DashboardRecordsAdapter(
                                requireContext(),
                                mList,
                                this@FragmentDashboard, mRecordsType
                            )
                        recyclerView.adapter = dashboardRecordsAdapter
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        return v
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

            val mList = gson!!.fromJson<List<DashboardData>>(
                filteredList.toString(),
                object : TypeToken<List<DashboardData>>() {
                }.type
            )

            dashboardRecordsAdapter =
                DashboardRecordsAdapter(
                    requireContext(),
                    mList,
                    this@FragmentDashboard, mRecordsType
                )
            recyclerView.adapter = dashboardRecordsAdapter
        } else {

            val mList = gson!!.fromJson<List<DashboardData>>(
                dashboardArray.toString(),
                object : TypeToken<List<DashboardData>>() {
                }.type
            )

            dashboardRecordsAdapter =
                DashboardRecordsAdapter(
                    requireContext(),
                    mList,
                    this@FragmentDashboard, mRecordsType
                )
            recyclerView.adapter = dashboardRecordsAdapter
        }

    }

    private fun getDashboardData(value: String) {

        val mProgressDialog = ProgressDialog(requireContext())
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getDashboardDataList(value)
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            Log.e("Dashboard Data", string)

                            val jsonObject = JSONObject(string)
                            val mLive = jsonObject.getString("live")
                            val mCompleted = jsonObject.getString("completed")

                            tvLive.text = resources.getString(R.string.live) + "[" + mLive + "]"
                            tvCompleted.text =
                                resources.getString(R.string.completed) + "[" + mCompleted + "]"

                            dashboardArray = jsonObject.getJSONArray("data")

                            if (dashboardArray.length() > 0) {

                                val mList = gson!!.fromJson<List<DashboardData>>(
                                    dashboardArray.toString(),
                                    object : TypeToken<List<DashboardData>>() {
                                    }.type
                                )

                                dashboardRecordsAdapter =
                                    DashboardRecordsAdapter(
                                        requireContext(),
                                        mList,
                                        this@FragmentDashboard, value
                                    )
                                recyclerView.adapter = dashboardRecordsAdapter
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

    /*ID Initialization*/
    fun init(v: View) {

        srlSwipeRefresh = v.findViewById(R.id.srlSwipeRefresh)

        tvLive = v.findViewById(R.id.tv_live)
        tvCompleted = v.findViewById(R.id.tv_completed)

        llScrollview = v.findViewById(R.id.ll_scrollview)
        llNoData = v.findViewById(R.id.ll_no_data_found)
        etSearch = v.findViewById(R.id.et_search)

        recyclerView = v.findViewById(R.id.recyclerView)

        tvLive.setOnClickListener(this)
        tvCompleted.setOnClickListener(this)

        tvLive.background = resources.getDrawable(R.drawable.underline_text_bg)
        tvCompleted.setBackgroundColor(resources.getColor(R.color.white))

        gson = Gson()

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
    }

    /* Onclick Buttons */
    override fun onClick(v: View?) {

        val i = v!!.id

        if (i == R.id.tv_live) {

            tvLive.setTextColor(resources.getColor(R.color.header_color))
            tvCompleted.setTextColor(resources.getColor(R.color.text_color))

            tvLive.background = resources.getDrawable(R.drawable.underline_text_bg)
            tvCompleted.setBackgroundColor(resources.getColor(R.color.white))

            mRecordsType = "live"
            getDashboardData(mRecordsType)

        } else if (i == R.id.tv_completed) {

            tvLive.setTextColor(resources.getColor(R.color.text_color))
            tvCompleted.setTextColor(resources.getColor(R.color.header_color))

            tvCompleted.background = resources.getDrawable(R.drawable.underline_text_bg)
            tvLive.setBackgroundColor(resources.getColor(R.color.white))

            mRecordsType = "completed"
            getDashboardData(mRecordsType)

        }
    }

    override fun onNavigate(position: Int, dashboardData: DashboardData, status: String) {
        val i = Intent(requireContext(), DashboardParts::class.java)

        if (status == "live") {
            i.putExtra("status", "Live")
        } else {
            i.putExtra("status", "Completed")
        }

        i.putExtra("nid", dashboardData.entityId)
        startActivity(i)
    }


}