package com.lbit.fleetuat.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.google.gson.reflect.TypeToken
import com.lbit.fleetuat.R
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.utils.Constants
import com.lbit.payroll.Singleton.UserSession
import kotlinx.android.synthetic.main.vehicle_inspection_damages.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class VehicleInspectionDamages : AppCompatActivity(), View.OnClickListener {

    private lateinit var path: String
    private lateinit var tvTitle: MyTextViewPoppinsSemiBold
    private lateinit var llBack: LinearLayout
    private lateinit var tvVehicle: MyTextViewPoppinsMedium
    private lateinit var tvSiteLocation: MyTextViewPoppinsMedium
    private lateinit var tvKMS: MyTextViewPoppinsMedium
    private lateinit var tvVehicleCondition: MyTextViewPoppinsMedium

    private lateinit var etRemarks: EditText
    private lateinit var tvImage: MyTextViewPoppinsMedium
    private lateinit var llPreviewImage: LinearLayout
    private lateinit var llSave: LinearLayout
    private lateinit var tvImage1: MyTextViewPoppinsMedium
    private lateinit var tvImageTxt: MyTextViewPoppinsMedium
    private lateinit var tvSave: MyTextViewPoppinsSemiBold


    private var mMobile: String = ""
    private var mVehicle: String = ""
    private var mSiteLocation: String = ""
    private var mKms: String = ""
    private var mVehicleCondition: String = ""
    private var mNid: String = ""
    private var mFuel: String = ""
    private var mCurrentDate: String = ""
    private var mReg: String = ""
    private var mRemarks: String = ""

    private var base64: String = ""
    private lateinit var mAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vehicle_inspection_damages)

        mNid = intent.getStringExtra("nid").toString()
        mVehicle = intent.getStringExtra("vehicle").toString()
        mSiteLocation = intent.getStringExtra("location").toString()
        mKms = intent.getStringExtra("kms").toString()
        mVehicleCondition = intent.getStringExtra("vehCond").toString()
        mReg = intent.getStringExtra("reg").toString()
        mFuel = intent.getStringExtra("fuel").toString()
        mCurrentDate = intent.getStringExtra("cDate").toString()

        mMobile = UserSession(this@VehicleInspectionDamages).getMobile()

        init()

        etRemarks.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                mRemarks = editable.toString()

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    fun init() {

        tvTitle = findViewById(R.id.tv_title)
        tvTitle.text = "Damages"

        llBack = findViewById(R.id.ll_back)

        tvVehicle = findViewById(R.id.tvVehicle)
        tvSiteLocation = findViewById(R.id.tvSiteLocation)
        tvKMS = findViewById(R.id.tvKMS)
        tvVehicleCondition = findViewById(R.id.tvVehicleCondition)
        etRemarks = findViewById(R.id.et_remarks)
        tvImage = findViewById(R.id.tv_Image)
        llPreviewImage = findViewById(R.id.llPreviewImage)
        llSave = findViewById(R.id.llSave)
        tvImageTxt = findViewById(R.id.tv_Image1_txt)
        tvImage1 = findViewById(R.id.tv_Image1)
        tvSave = findViewById(R.id.tvSave)

        etRemarks.imeOptions = EditorInfo.IME_ACTION_DONE

        tvVehicle.text = mVehicle
        tvSiteLocation.text = mSiteLocation
        tvKMS.text = mKms
        tvVehicleCondition.text = mVehicleCondition

    }

    override fun onClick(v: View?) {

        val i = v!!.id

        if (i == R.id.ll_back) {

            onBackPressed()
        } else if (i == R.id.tv_Image) {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 1)
        } else if (i == R.id.tv_Image1) {

            showPicture()
        } else if (i == R.id.tvSave) {

            if (base64.isNotEmpty()) {

                saveInspect()
            } else {

                Toast.makeText(this@VehicleInspectionDamages, "Please Upload the Damaged Pics", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveInspect() {

        val mProgressDialog = ProgressDialog(this@VehicleInspectionDamages)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.show()
        Constants.Fleet_URL.saveInspect(
            mMobile,
            mReg,
            mNid,
            mKms,
            mFuel,
            mSiteLocation,
            mVehicleCondition,
            mCurrentDate,
            mRemarks,
            base64
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                try {

                    if (response.code() == 200) {
                        val string = response.body()!!.string()
                        Log.e("save Inspect", string)

                        Toast.makeText(
                            this@VehicleInspectionDamages,
                            "Inspect Saved Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val i = Intent(this@VehicleInspectionDamages, LandingPage::class.java)
                        i.putExtra("tag", "3")
                        startActivity(i)
                        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
                        finish()

                        mProgressDialog.dismiss()
                    } else {

                        mProgressDialog.dismiss()
                        Toast.makeText(
                            this@VehicleInspectionDamages,
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

    private fun showPicture() {
        val mDialogView =
            LayoutInflater.from(this@VehicleInspectionDamages)
                .inflate(R.layout.show_picture_dialog, null)

        val btnBack = mDialogView.findViewById(R.id.btnBack) as MyTextViewPoppinsSemiBold
        val ivCaptureImage = mDialogView.findViewById(R.id.ivCaptureImage) as ImageView
        val tvMonth = mDialogView.findViewById(R.id.tv_month) as MyTextViewPoppinsSemiBold


        Glide.with(this)
            .load(path)
            .transform(CenterInside())
            .into(ivCaptureImage)

        Log.e("final Path", path)
        tvMonth.text = "Image1.png"

        val mBuilder = AlertDialog.Builder(this@VehicleInspectionDamages)
            .setView(mDialogView)
            .setTitle("")
        mBuilder.setCancelable(false)
        mAlertDialog = mBuilder.show()


        btnBack.setOnClickListener {

            mAlertDialog.dismiss()

        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {

            val contentURI = data!!.data
            try {
                val bitmap = data.extras!!.get("data") as Bitmap
                val permission = hasStoragePermission(requestCode)
                Log.e("TAG", "permission $permission")

                if (permission) {
                    path = saveImage(bitmap)
                    Log.e("TAG", "image_bitmap Path$path")

                    val bytes = File(path).readBytes()
                    base64 = android.util.Base64.encodeToString(bytes, 0)
                    llPreviewImage.visibility = View.VISIBLE
                    llSave.visibility = View.VISIBLE
                    Log.e("TAG", "image_bitmap $base64")

                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@VehicleInspectionDamages, "Failed!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)).toString() + "/payroll_upload/upload"
        )

        // have the object build the directory structure, if needed.
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())

            val f = File(
                wallpaperDirectory, Calendar.getInstance().timeInMillis.toString() + ".jpg"
            )

            tvImageTxt.text = "Image1.png"

            Log.e("FileName", "Image1.png")

            if (!f.exists()) {
                f.parentFile.mkdirs()

                f.createNewFile()
                Log.e("TAG", "File Created")
            }

            Log.d("File Name", f.name)

            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                this@VehicleInspectionDamages,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    private fun hasStoragePermission(requestCode: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this@VehicleInspectionDamages,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode
                )
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val i = Intent(this@VehicleInspectionDamages, LandingPage::class.java)
        i.putExtra("tag", "3")
        startActivity(i)
        overridePendingTransition(R.anim.move_left_enter, R.anim.move_left_exit)
        finish()
    }
}