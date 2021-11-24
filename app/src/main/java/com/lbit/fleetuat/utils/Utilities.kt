package com.lbit.fleetuat.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.lbit.fleetuat.R
import java.io.IOException
import java.util.*

object Utilities {


    /*
        Internet Availability check

         */


    /**
     * Method to hide keyboard start
     *
     * @param activity
     */
    fun hideSoftKeyboard(activity: Activity) {

        val inputMethodManager = activity
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            try {
                inputMethodManager.hideSoftInputFromWindow(
                    activity
                        .currentFocus!!.windowToken, 0
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }


    fun getItemList(getItemAsList: ArrayList<String>, jsonResponse: String): ArrayList<String> {

        val list = ArrayList<String>()

        val subStringValue = 2
        var name: String

        var response = jsonResponse.replace("\\u0022", "")
        response = response.substring(subStringValue, response.length - subStringValue)
        val responseSplitList =
            response.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        if (responseSplitList.size > 0) {
            list.add("Select")
            for (listItem in responseSplitList) {
                try{
                    val itemNameList =
                        listItem.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    name = itemNameList[1]
                    name = name.replace("}", "")
                    name = name.replace("\"", "")
                    list.add(name)
                }catch (e:java.lang.Exception){
                    e.printStackTrace()
                }
            }
        }

        return list


    }

    fun getItemListPart(getItemAsList: ArrayList<String>, jsonResponse: String): ArrayList<String> {

        val list = ArrayList<String>()

        val subStringValue = 2
        var name: String

        var response = jsonResponse.replace("\\u0022", "")
        response = response.substring(subStringValue, response.length - subStringValue)
        val responseSplitList =
            response.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        if (responseSplitList.size > 0) {
            for (listItem in responseSplitList) {
                try{
                    val itemNameList =
                        listItem.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    name = itemNameList[1]
                    name = name.replace("}", "")
                    name = name.replace("\"", "")
                    list.add(name)
                }catch (e:java.lang.Exception){
                    e.printStackTrace()
                }
            }
        }

        return list
}


fun showSoftKeyboard(activity: Activity, editText: EditText) {
        val inputMethodManager = activity
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }


    fun hideStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            /*window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.transparent));*/
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            // Show status bar
            //            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        }
    }

    fun settingLocale(context: Activity, language: String) {
        val locale: Locale
        val resources = context.resources
        val config = Configuration(resources.configuration)
        if (language != "eng")
            locale = Locale(language, "IN")
        else
            locale = Locale(language)

        Locale.setDefault(locale)
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun displayToast(ctxt: Context, toastMessage: String) {
        val toast = Toast.makeText(ctxt, toastMessage, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)

        val group = toast.view as ViewGroup
        val messageTextView = group.getChildAt(0) as TextView
        messageTextView.textSize = 22f

        toast.show()
    }

    /**
     * Displaying snackbar
     * SUJITH DASARI
     * 04-12-2018
     */

    fun showMessage(view: View, msg: String) {
        try {
            val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)

            val snack_view = snackbar.view
            //            android.support.design.R.id
            val tv =
                snack_view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            tv.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                view.resources.getDimension(R.dimen.snackbar_textsize)
            )
            snackbar.show()
        } catch (e: Exception) {
            Log.v("Exception", "Exception$e")
        }

    }



    /*Call Intent*/
    fun shoeDialer(view: Context, number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        view.startActivity(intent)
    }

    /**
     * SpannableString
     * SUJITH DASARI
     * 04-12-2018
     */

    fun getCustomString(name: String): SpannableString {
        val ss1 = SpannableString(name)
        ss1.setSpan(RelativeSizeSpan(1.5f), 0, name.length, 0)
        return ss1
    }

    /**
     * imageRotate
     * SUJITH DASARI
     * 04-12-2018
     */

    fun imageRotate(bitmapImage: Bitmap, path: String): Bitmap {
        var bitmapImage = bitmapImage
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (exif != null) {
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            val m = Matrix()
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                m.postRotate(180f)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90f)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270f)
            }
            bitmapImage = Bitmap.createBitmap(
                bitmapImage,
                0,
                0,
                bitmapImage.width,
                bitmapImage.height,
                m,
                true
            )
        }
        return bitmapImage
    }


    /*Calcualte Time*/
    fun formatSeconds(timeInSeconds: Long): String {
        val hours = (timeInSeconds / 3600).toInt()
        val secondsLeft = (timeInSeconds - hours * 3600).toInt()
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60

        var formattedTime = ""
        /*if (hours < 10)
                formattedTime += "0";
            formattedTime += hours + ":";*/

        if (hours > 0)
            formattedTime += hours.toString() + "h "

        formattedTime += minutes.toString() + "m "
        formattedTime += seconds.toString() + "s"

        /*if (minutes < 10)
                formattedTime += "0";
            formattedTime += minutes + ":";

            if (seconds < 10)
                formattedTime += "0";
            formattedTime += seconds ;*/

        return formattedTime
    }




}
