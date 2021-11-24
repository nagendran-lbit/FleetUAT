package com.lbit.fleetuat.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lbit.fleetuat.R
import com.lbit.fleetuat.adapter.DashboardPartsAdapter
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.utils.Constants
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardParts : AppCompatActivity(), View.OnClickListener {

    private var mNid: String = ""
    private var mStatus: String = ""
    private var vpPager: ViewPager? = null
    private lateinit var tvTitle: MyTextViewPoppinsSemiBold

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_parts)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.cust_name)
        }

        mNid = intent.getStringExtra("nid").toString()
        mStatus = intent.getStringExtra("status").toString()

        init()

        getDashboardPartList(mNid)

    }

    private fun getDashboardPartList(mNid: String) {
        val mProgressDialog = ProgressDialog(this@DashboardParts)
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

                            Log.e("Dashboard Data", string)

                            val jsonArray = JSONArray(string)
                            val mList = gson!!.fromJson<List<DashboardData>>(
                                jsonArray.toString(),
                                object : TypeToken<List<DashboardData>>() {
                                }.type
                            )

                            vpPager!!.adapter = DashboardPartsAdapter(
                                this@DashboardParts,
                                mList
                            )

                            mPageCount = jsonArray.length().toString()
                            if (mPageCount!!.isNotEmpty()) {
                                tvSize.text = "1 of " + mPageCount!!
                            }

                            mProgressDialog.dismiss()
                        } else {

                            mProgressDialog.dismiss()
                            Toast.makeText(
                                this@DashboardParts,
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
        tvTitle = findViewById(R.id.tv_title)
        tvTitle.text = mStatus

        vpPager = findViewById(R.id.vp_pager)
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
    }

    private fun getItemofviewpager(i: Int): Int {
        return vpPager!!.currentItem + i
    }

    override fun onClick(p0: View?) {
        val i = p0!!.id

        if (i == R.id.ll_back){

            onBackPressed()
        } else if (i == R.id.tvPrevious) {
            vpPager!!.setCurrentItem(getItemofviewpager(-1), true)

            Log.e("TAG", "onClick_Previous: " + vpPager!!.currentItem)
            val mPrev = (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
            tvSize.text = mPrev


        } else if (i == R.id.tvNext) {

            vpPager!!.setCurrentItem(getItemofviewpager(+1), true)
            Log.e("TAG", "onClick_Next: " + vpPager!!.currentItem + 1)
            val mNext = (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
            tvSize.text = mNext

        }
    }

    override fun onBackPressed() {

        val i = Intent(this@DashboardParts, LandingPage::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
        finish()
    }
}