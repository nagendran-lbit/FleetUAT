package com.lbit.fleetuat.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator
import com.lbit.fleetuat.R
import com.lbit.fleetuat.adapter.PartScanAdapter
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IPartED
import com.lbit.fleetuat.interfaces.IRecords
import com.lbit.fleetuat.utils.Constants
import com.lbit.payroll.Singleton.UserSession
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class PartsScanningScreen : AppCompatActivity(), View.OnClickListener, IRecords, IPartED {

    private var vpPager: ViewPager? = null

    lateinit var tv_size: MyTextViewPoppinsSemiBold
    lateinit var tvnext: MyTextViewPoppinsSemiBold
    lateinit var tvPrevious: MyTextViewPoppinsSemiBold

    private var mPageCount: String? = null

    private var scrollStarted: Boolean = false
    private var checkDirection: Boolean = false
    private val thresholdOffset = 0.5f
    private val thresholdOffsetPixels = 1
    private var mCurrentFragmentPosition = 0
    var partsArray: JSONArray = (JSONArray())
    private var gson: Gson? = null

    private var mScanValue: String = ""
    private var mScanValidationCount: String = ""
    private var mScanValidation: String = ""
    private var mMobile: String = ""
    private var mNid: String = ""
    private var mPid: String = ""
    private var mSGRN: String = ""
    private var mSPartD: String = ""
    private var mSPartN: String = ""
    private var mManualQR: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_part_scan)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.header_color)

        init()

        mNid = intent.getStringExtra("nid").toString()

        mMobile = UserSession(this@PartsScanningScreen).getMobile()

        getPartsData()

    }

    private fun getPartsData() {

        val mProgressDialog = ProgressDialog(this@PartsScanningScreen)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getDashboardDataListSB(mMobile, mNid)
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            Log.e("Part Scan Data", string)

                            val jsonObject = JSONObject(string)

                            partsArray = jsonObject.getJSONArray("data")

                            if (partsArray.length() > 0) {

                                val mList = gson!!.fromJson<List<DashboardData>>(
                                    partsArray.toString(),
                                    object : TypeToken<List<DashboardData>>() {
                                    }.type
                                )

                                vpPager!!.adapter = PartScanAdapter(
                                    this@PartsScanningScreen,
                                    mList,
                                    this@PartsScanningScreen,
                                    this@PartsScanningScreen
                                )

                                mPageCount = partsArray.length().toString()
                                if (mPageCount!!.isNotEmpty()) {
                                    tv_size.text = "1 of " + mPageCount!!
                                }
                            }
                            mProgressDialog.dismiss()
                        } else {

                            mProgressDialog.dismiss()
                            Toast.makeText(
                                this@PartsScanningScreen,
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
        vpPager = findViewById(R.id.vp_pager)
        tvPrevious = findViewById(R.id.tvPrevious)
        tvnext = findViewById(R.id.tvnext)
        tv_size = findViewById(R.id.tv_size)

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
                        tv_size.text = mNext
                    }


                } else {

                    val mNext =
                        (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
                    Log.e("TAG", mNext)
                    if (!mNext.equals("0")) {
                        tv_size.text = mNext
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

        if (i == R.id.ll_back) {

            onBackPressed()
        } else if (i == R.id.tvPrevious) {
            vpPager!!.setCurrentItem(getInteroffice(-1), true)

            Log.e("TAG", "onClick_Previous: " + vpPager!!.currentItem)
            val mPrev = (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
            tv_size.text = mPrev


        } else if (i == R.id.tvnext) {

            vpPager!!.setCurrentItem(getInteroffice(+1), true)
            Log.e("TAG", "onClick_Next: " + vpPager!!.currentItem + 1)
            val mNext = (vpPager!!.currentItem + 1).toString() + " of " + mPageCount
            tv_size.text = mNext

        }
    }

    private fun getInteroffice(i: Int): Int {
        return vpPager!!.currentItem + i
    }

    override fun onBackPressed() {

        val i = Intent(this@PartsScanningScreen, LandingPage::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
        finish()
    }

    override fun onNavigate(position: Int, dashboardData: DashboardData) {

        mPid = dashboardData.pid.toString()
        mSGRN = dashboardData.grnNumber.toString()
        mSPartD = dashboardData.partDescription.toString()
        mSPartN = dashboardData.oePartNo.toString()

        val integrator = IntentIntegrator(this@PartsScanningScreen)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()

    }

    override fun onNavigate(position: Int, dashboardData: DashboardData, status: String) {


        mPid = dashboardData.pid.toString()
        mSGRN = dashboardData.grnNumber.toString()
        mSPartD = dashboardData.partDescription.toString()
        mSPartN = dashboardData.oePartNo.toString()
        mManualQR = dashboardData.manQrNo.toString()

        val pattern =
            Pattern.compile("[A-Z]{2}[_]{1}[A-Z]{2}[0-9]{2}[_]{1}[0-9]{6}")
        val matcher = pattern.matcher(status)
        if (matcher.matches()) {

            if (status.contains(mManualQR)) {
                mScanValidationCount = "1"
                mScanValidation = "Verified"
            } else {
                mScanValidationCount = "0"
                mScanValidation = "Rejected"
            }

            showDialog()
        } else {
            Toast.makeText(this@PartsScanningScreen, "Invalid Qr Code", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Log.e("Scan*******", "Cancelled scan")
            } else {
                mScanValue = result.contents
                Log.e("Scan", mScanValue)

                Toast.makeText(this@PartsScanningScreen, "GRN - $mSGRN",Toast.LENGTH_LONG).show()

                if (mScanValue.contains(mSGRN + "_" + mSPartN)) {
                    mScanValidationCount = "1"
                    mScanValidation = "Verified"
                } else {
                    mScanValidationCount = "0"
                    mScanValidation = "Rejected"
                }

                showDialog()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showDialog() {

        val mDialogView =
            LayoutInflater.from(this@PartsScanningScreen)
                .inflate(R.layout.qr_confirm_dialog, null)

        val btnBack = mDialogView.findViewById(R.id.btnBack) as MyTextViewPoppinsSemiBold
        val tvText =
            mDialogView.findViewById(R.id.tv_submitted_text) as MyTextViewPoppinsSemiBold
        val tvConfirm =
            mDialogView.findViewById(R.id.tvConfirm) as MyTextViewPoppinsSemiBold
        val tvVerIcon =
            mDialogView.findViewById(R.id.tvVerIcon) as MyTextViewPoppinsSemiBold


        if (mScanValidation == "Verified") {

            tvVerIcon.background = resources.getDrawable(R.drawable.icon_tick)
            tvText.text =
                "Successfully Scanned." + System.lineSeparator() + "Part ID:" + "FMSREF002189"

        } else {

            tvVerIcon.background = resources.getDrawable(R.drawable.icon_stop)

            tvText.text =
                "Wrong Part Added." + System.lineSeparator() + "Please enter the correct QR Code "

            tvConfirm.visibility = View.GONE
        }
        val mBuilder = AlertDialog.Builder(this@PartsScanningScreen)
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)

        val mAlertDialog = mBuilder.show()

        btnBack.setOnClickListener {

            mAlertDialog.dismiss()
        }

        tvConfirm.setOnClickListener {

            mAlertDialog.dismiss()

            saveQRVerification()

        }
    }

    private fun saveQRVerification() {

        val mProgressDialog = ProgressDialog(this@PartsScanningScreen)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.saveQrVerification(mMobile, mPid, mScanValidation, "")
            .enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {
                            val string = response.body()!!.string()

                            Log.e("Save Part Scan", string)

                            mProgressDialog.dismiss()

                            val i = Intent(this@PartsScanningScreen, LandingPage::class.java)
                            startActivity(i)
                            overridePendingTransition(
                                R.anim.move_left_enter,
                                R.anim.move_left_exit
                            )
                            finish()

                        } else {

                            mProgressDialog.dismiss()
                            Toast.makeText(
                                this@PartsScanningScreen,
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
}