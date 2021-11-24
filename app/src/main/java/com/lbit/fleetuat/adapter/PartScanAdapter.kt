package com.lbit.fleetuat.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import com.lbit.fleetuat.R
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.data.DashboardData
import com.lbit.fleetuat.interfaces.IPartED
import com.lbit.fleetuat.interfaces.IRecords
import java.util.regex.Pattern


class PartScanAdapter(
    private val mContext: Context,
    private var mPartsList: List<DashboardData>,
    private var iRecord: IRecords,
    var iPartED: IPartED,

    ) : PagerAdapter() {

    private var layoutInflater: LayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var mPartsData: DashboardData
    private val listener = iRecord
    private val listener1 = iPartED

    private lateinit var tvPart: MyTextViewPoppinsMedium
    private lateinit var tvReqDate: MyTextViewPoppinsMedium
    lateinit var tvReg: MyTextViewPoppinsMedium
    private lateinit var tvVehicle: MyTextViewPoppinsMedium
    lateinit var tvQuantity: MyTextViewPoppinsMedium
    lateinit var tvSupervisor: MyTextViewPoppinsMedium
    lateinit var tvPartDesc: MyTextViewPoppinsMedium
    lateinit var tvBinLocation: MyTextViewPoppinsMedium
    lateinit var tvVehicleCondition: MyTextViewPoppinsMedium
    private lateinit var tvScan: MyTextViewPoppinsSemiBold
    private lateinit var tvConfirm: MyTextViewPoppinsSemiBold
    private lateinit var tvManualEntry: MyTextViewPoppinsMedium
    private lateinit var etManual: EditText
    private lateinit var llManualEntry: LinearLayout
    private lateinit var llScan: LinearLayout
    var myString = "Click Here"
    private var mPosition: Int = 0
    private var mManualQrCode: String = ""


    override fun getCount(): Int {
        return mPartsList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout

    }

    private fun makeTextLink(
        tvText: TextView,
        myString: String,
        underlined: Boolean,
        color: Int,
        action: () -> Int
    ) {
        val spannableString = SpannableString(tvText.text)
        val textColor = color ?: tvText.currentTextColor
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {

                mPartsData = mPartsList[mPosition]

                llManualEntry.visibility = View.VISIBLE
                llScan.visibility = View.GONE

            }

            override fun updateDrawState(drawState: TextPaint) {
                super.updateDrawState(drawState)
                drawState.isUnderlineText = underlined
                drawState.color = textColor
            }
        }
        val index = spannableString.indexOf(myString)
        spannableString.setSpan(
            clickableSpan,
            index,
            index + myString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvText.text = spannableString
        tvText.movementMethod = LinkMovementMethod.getInstance()
        tvText.highlightColor = Color.TRANSPARENT

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.part_sacn_records_row, container, false)

        mPosition = position
        mPartsData = mPartsList[position]

        tvVehicle = itemView.findViewById(R.id.tv_vehicle)
        tvReg = itemView.findViewById(R.id.tv_reg)
        tvSupervisor = itemView.findViewById(R.id.tv_supervisor)
        tvVehicleCondition = itemView.findViewById(R.id.tv_vehicle_condition)
        tvReqDate = itemView.findViewById(R.id.tv_req_date)
        tvPart = itemView.findViewById(R.id.tv_part)
        tvPartDesc = itemView.findViewById(R.id.tv_part_desc)
        tvQuantity = itemView.findViewById(R.id.tv_qty)
        tvBinLocation = itemView.findViewById(R.id.tv_bin_location)
        tvScan = itemView.findViewById(R.id.tvScan)
        tvConfirm = itemView.findViewById(R.id.tvConfirm)
        llScan = itemView.findViewById(R.id.llScan)
        tvManualEntry = itemView.findViewById(R.id.tvManualEntry)
        etManual = itemView.findViewById(R.id.etManual)
        llManualEntry = itemView.findViewById(R.id.llManualEntry)

        makeTextLink(
            tvManualEntry,
            myString,
            true,
            mContext.resources.getColor(R.color.text_color),
            action = { Log.d("onClick", "link") })


        tvVehicle.text = mPartsData.make
        tvReg.text = mPartsData.reg
        tvSupervisor.text = mPartsData.supervisor
        tvVehicleCondition.text = mPartsData.vehCondition
        tvReqDate.text = mPartsData.jobcardDate
        tvPart.text = mPartsData.oePartNo
        tvPartDesc.text = mPartsData.partDescription
        tvQuantity.text = mPartsData.qty
        tvBinLocation.text = mPartsData.storageBin

        etManual.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(textVal: Editable) {
                mManualQrCode = textVal.toString()

                if (mManualQrCode.length == 14) {
                    val pattern =
                        Pattern.compile("[A-Z]{2}[_]{1}[A-Z]{2}[0-9]{2}[_]{1}[0-9]{6}")
                    val matcher = pattern.matcher(mManualQrCode)
                    if (matcher.matches()) {
//                        Snackbar.make(rlHomeLayout, "Valid", Snackbar.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(
                            mContext,
                            "Invalid Qr Code",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

            }
        })

        container.addView(itemView)

        tvScan.setOnClickListener {

            mPartsData = mPartsList[position]

            listener.onNavigate(position, mPartsData)
        }

        tvConfirm.setOnClickListener {

            mPartsData = mPartsList[position]

            if (mManualQrCode.isNotEmpty()) {

                listener1.onNavigate(position, mPartsData, mManualQrCode)

            } else {
                Toast.makeText(
                    mContext,
                    "Please enter Qrcode manually before click on confirm button",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
