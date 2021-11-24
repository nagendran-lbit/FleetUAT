package com.lbit.fleetuat.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lbit.fleetuat.R
import com.lbit.fleetuat.adapter.VehicleDetailsAdapter
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.utils.Constants
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleTimelineDetails : AppCompatActivity(), View.OnClickListener {

    private var vpPager: ViewPager? = null

    lateinit var tvSize: MyTextViewPoppinsSemiBold
    lateinit var tvNext: MyTextViewPoppinsSemiBold
    lateinit var tvPrevious: MyTextViewPoppinsSemiBold

    private var mPageCount: String? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0
    var partsArray: JSONArray = (JSONArray())
    private var gson: Gson? = null

    private var mReqID: String = ""
    var mRegNo: String = ""
    var mDateFrom: String = ""
    var mDateTo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_timeline)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.header_color)

        mReqID = intent.getStringExtra("ReqID").toString()
        mDateFrom = intent.getStringExtra("DateFrom").toString()
        mDateTo = intent.getStringExtra("DateTo").toString()

        init()

        getVehicleTimelineDetails()

    }

    private fun getVehicleTimelineDetails() {

        val mProgressDialog = ProgressDialog(this@VehicleTimelineDetails)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getTimelines(
            "",
            "",
            "",
            mDateFrom,
            mDateTo,
            mReqID
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

                        val jsonArray = JSONArray(string)

                        if (jsonArray.length() > 0) {

                            val mList = gson!!.fromJson<List<DashboardData>>(
                                jsonArray.toString(),
                                object : TypeToken<List<DashboardData>>() {
                                }.type
                            )

                            vpPager!!.adapter = VehicleDetailsAdapter(
                                this@VehicleTimelineDetails,
                                mList
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
                            this@VehicleTimelineDetails,
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

    fun init() {
        vpPager = findViewById(R.id.vpPager)
        tvPrevious = findViewById(R.id.tvPrevious)
        tvNext = findViewById(R.id.tvNext)
        tvSize = findViewById(R.id.tvSize)

        gson = Gson()

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
                    if (mNext != "0") {
                        tvSize.text = mNext
                    }

                }
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
            }

        })

        val mList = gson!!.fromJson<List<DashboardData>>(
            partsArray.toString(),
            object : TypeToken<List<DashboardData>>() {
            }.type
        )

        vpPager!!.adapter = VehicleDetailsAdapter(
            this@VehicleTimelineDetails,
            mList
        )
        mPageCount = "5"
        if (mPageCount!!.isNotEmpty()) {
            tvSize.text = "1 of " + mPageCount
        }

    }

    override fun onClick(p0: View?) {

        val i = p0!!.id

        if (i == R.id.ll_back) {

            onBackPressed()
        } else if (i == R.id.tvPrevious) {
            vpPager!!.setCurrentItem(getInteroffice(-1), true)

            Log.e("TAG", "onClick_Previous: " + vpPager!!.currentItem)
            val mPrev = (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
            tvSize.text = mPrev


        } else if (i == R.id.tvNext) {

            vpPager!!.setCurrentItem(getInteroffice(+1), true)
            Log.e("TAG", "onClick_Next: " + vpPager!!.currentItem + 1)
            val mNext = (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
            tvSize.text = mNext

        }
    }

    private fun getInteroffice(i: Int): Int {
        return vpPager!!.currentItem + i
    }

    override fun onBackPressed() {

        val i = Intent(this@VehicleTimelineDetails, LandingPage::class.java)

        i.putExtra("tag","4")
        startActivity(i)
        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
        finish()
    }


}