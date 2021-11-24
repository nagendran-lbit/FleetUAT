package com.lbit.fleetuat.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goodiebag.pinview.Pinview
import com.lbit.fleetuat.R
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.utils.Constants
import com.lbit.payroll.Singleton.UserSession
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPScreen : AppCompatActivity() {

    lateinit var pinview: Pinview
    lateinit var tv_otp: TextView
    lateinit var tv_resend_otp: MyTextViewPoppinsMedium
    lateinit var tv_resend_timer: MyTextViewPoppinsMedium
    var mCustomOTP: String = ""
    private var mOTP: String = ""
    private var mMobile: String = ""
    private var mURL: String = ""

    private lateinit var dict_data: JSONObject
    private var mNid: String = ""
    private var mUid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        mOTP = UserSession(this@OTPScreen).getOTP()
        mMobile = UserSession(this@OTPScreen).getMobile()

        init()

        tv_otp.text = "Your OTP is : " + mOTP

        val timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tv_resend_timer.visibility = View.VISIBLE
                tv_resend_otp.visibility = View.GONE
                tv_resend_timer.setText("Resend In : " + (millisUntilFinished / 1000).toString())
                tv_resend_timer.paintFlags = 0

            }

            override fun onFinish() {

                tv_resend_otp.visibility = View.VISIBLE
                tv_resend_timer.visibility = View.GONE
                tv_resend_otp.paintFlags = tv_resend_otp.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            }
        }
        timer.start()

        tv_resend_otp.setOnClickListener {

            if (mMobile.isNotEmpty()) {
                ResendOTP()

            }
        }

        pinview.setPinViewEventListener { pinview, fromUser ->
            val view: View? = currentFocus
            if (view != null) {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)

                mCustomOTP = pinview.value


                if (mCustomOTP.length == 4) {

                    getLoginOTP()
                }
            }
        }
        pinview.setCursorShape(R.drawable.example_cursor)
        pinview.setTextSize(18)
        pinview.setTextColor(Color.BLACK)
        pinview.showCursor(true)
    }

    private fun getLoginOTP() {
        val mProgressDialog = ProgressDialog(this@OTPScreen)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.getUserLogin(mMobile, "No","No", mCustomOTP,"")
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {

                            val string = response.body()!!.string()
                            Log.e("TAG", "OTP" + " " + string)

                            if (string.equals("[\"OTP invalid\"]")) {
                                Toast.makeText(
                                    this@OTPScreen,
                                    "OTP Invalid, Please Re-Enter OTP",
                                    Toast.LENGTH_LONG
                                ).show()
                                mProgressDialog.dismiss()
                            } else {

                                val jsonArray = JSONArray(string)
                                val jsonObject = jsonArray.getJSONObject(0)
                                dict_data = JSONObject()

                                try {
                                    dict_data.put("name", jsonObject.getString("user_name"))
                                    dict_data.put("uid", jsonObject.getString("uid"))
                                    dict_data.put("location", jsonObject.getString("location"))
                                    dict_data.put("role", jsonObject.getString("role"))

                                    Log.e("data", dict_data.toString())
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                                UserSession(this@OTPScreen).setLoginDetails(dict_data.toString())
                                UserSession(this@OTPScreen).removeOTP()

                                val i = Intent(this@OTPScreen, LandingPage::class.java)
                                startActivity(i)
                                overridePendingTransition(
                                    R.anim.move_left_enter,
                                    R.anim.move_left_exit
                                )
                                finish()

                            }
                            mProgressDialog.dismiss()

                        } else {

                            mProgressDialog.dismiss()
                            Toast.makeText(
                                this@OTPScreen,
                                "There seems to be a network problem. Please contact support team",
                                Toast.LENGTH_LONG
                            ).show()

                            /*SendErrorCode(
                                "payrolluat",
                                "userlogin_",
                                response.code(),
                                response.message()
                            )*/
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


    private fun ResendOTP() {
        val mProgressDialog = ProgressDialog(this@OTPScreen)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()

        Constants.Fleet_URL.getUserLogin(mMobile, "Yes", "Yes","","")
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {

                            val string = response.body()!!.string()
                            Log.e("TAG", "Login" + " " + string)

                            if (string.equals("[\"User not found\"]")) {
                                Toast.makeText(
                                    this@OTPScreen,
                                    "User Not Found",
                                    Toast.LENGTH_LONG
                                ).show()

                            } else {
                                Log.e("TAG", "Login" + " " + string)

                                val parts = string.split(" ")
                                val lastWord = parts[parts.size - 1]
                                println(lastWord)
                                val otp = lastWord.substring(0, 4)
                                tv_otp.text = "Your OTP is : " + otp

                                mProgressDialog.dismiss()
                            }
                        } else {

                            mProgressDialog.dismiss()
                            Toast.makeText(
                                this@OTPScreen,
                                "There seems to be a network problem. Please contact support team",
                                Toast.LENGTH_LONG
                            ).show()

                           /* SendErrorCode(
                                "payrolluat",
                                "user-login-mobile",
                                response.code(),
                                response.message()
                            )*/
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

/*
    private fun SendErrorCode(app: String, api: String, code: Int, message: String) {
        val mProgressDialog = ProgressDialog(this@OTPActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Payroll_URL.ApiTracking(
            mMobile,
            app,
            api,
            code.toString(),
            message, System.currentTimeMillis().toString()
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                try {
                    val string = response.body()!!.string()

                    Log.e("Api Tracking", string)

                    mProgressDialog.dismiss()

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
*/

    fun init() {
        pinview = findViewById(R.id.pinview)
        tv_otp = findViewById(R.id.tv_otp)
        tv_resend_timer = findViewById(R.id.tv_resend_timer)
        tv_resend_otp = findViewById(R.id.tv_resend_otp)
    }
}