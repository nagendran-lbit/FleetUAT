package com.lbit.fleetuat.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lbit.fleetuat.R
import com.lbit.fleetuat.utils.Constants
import com.lbit.payroll.Singleton.UserSession
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginScreen : AppCompatActivity() {

    lateinit var etPhone: EditText

    var mMobileNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        init()

        etPhone.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(textVal: Editable) {
                mMobileNumber = textVal.toString()

                if (mMobileNumber.length == 10) {

                    getLogin(mMobileNumber)
                }
            }
        })
    }


    private fun getLogin(mobile: String?) {
        val mProgressDialog = ProgressDialog(this@LoginScreen)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()

        Constants.Fleet_URL.getUserLogin(mobile.toString(), "No", "No", "", "")
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    try {

                        if (response.code() == 200) {

                            val string = response.body()!!.string()
                            Log.e("TAG", "Login $string")

                            if (string.equals("[\"User not found\"]")) {
                                Toast.makeText(
                                    this@LoginScreen,
                                    "User Not Found",
                                    Toast.LENGTH_LONG
                                ).show()
                                mProgressDialog.dismiss()

                            } else {
                                Log.e("TAG", "Login $string")

                                val parts = string.split(" ")
                                val lastWord = parts[parts.size - 1]
                                println(lastWord)
                                val otp = lastWord.substring(0, 4)
                                Log.e("TAG", "Login OTP $otp")

                                mProgressDialog.dismiss()

                                if (otp.isNotEmpty()) {

                                    UserSession(this@LoginScreen).setOTP(otp.toString())

                                    UserSession(this@LoginScreen).setMobile(mMobileNumber.toString())

                                    val i = Intent(this@LoginScreen, OTPScreen::class.java)
                                    startActivity(i)
                                    overridePendingTransition(
                                        R.anim.move_left_enter,
                                        R.anim.move_left_exit
                                    )
                                    finish()
                                }

                            }
                        } else {

                            mProgressDialog.dismiss()
                            Toast.makeText(
                                this@LoginScreen,
                                "There seems to be a network problem. Please contact support team",
                                Toast.LENGTH_LONG
                            ).show()

                            /*SendErrorCode(
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
        val mProgressDialog = ProgressDialog(this@LoginActivity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        payrolluat_URL.ApiTracking(
            mMobileNumber.toString(),
            app,
            api,
            code.toString(),
            message.toString(), System.currentTimeMillis().toString()
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
        etPhone = findViewById(R.id.et_Phone)

    }
}