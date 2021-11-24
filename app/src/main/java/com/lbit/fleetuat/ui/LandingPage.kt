package com.lbit.fleetuat.ui

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.lbit.fleetuat.LockableViewPager
import com.lbit.fleetuat.R
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsMedium
import com.lbit.fleetuat.customfonts.MyTextViewPoppinsSemiBold
import com.lbit.fleetuat.fragments.*
import com.lbit.payroll.Singleton.UserSession
import org.json.JSONException
import org.json.JSONObject
import android.util.Log
import java.util.*


class LandingPage : AppCompatActivity(), TabLayout.OnTabSelectedListener,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var iconDashboard: MyTextViewPoppinsSemiBold
    private lateinit var iconStock: MyTextViewPoppinsSemiBold
    private lateinit var iconRequest: MyTextViewPoppinsSemiBold
    private lateinit var iconNotification: MyTextViewPoppinsSemiBold
    private lateinit var iconHealthCheck: MyTextViewPoppinsSemiBold
    private lateinit var iconTimeline: MyTextViewPoppinsSemiBold

    private lateinit var tabLayout: TabLayout
    private lateinit var tab1: LinearLayout
    private lateinit var tab2: LinearLayout
    private lateinit var tab3: LinearLayout
    private lateinit var tab4: LinearLayout
    private lateinit var tab5: LinearLayout
    private var viewPager: LockableViewPager? = null

    private var drawer: DrawerLayout? = null
    private var llProfileLayout: LinearLayout? = null
    private lateinit var tvheadername: MyTextViewPoppinsSemiBold
    private lateinit var tvheaderMobile: MyTextViewPoppinsMedium
    private lateinit var tvheaderRole: MyTextViewPoppinsMedium

    var toolbarTitle: MyTextViewPoppinsSemiBold? = null
    var navigationView: NavigationView? = null

    private lateinit var dictData: JSONObject
    private var mRole: String = ""
    private var mProfileName: String = ""
    private var mMobile: String = ""

    var mTag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTag = intent.getStringExtra("tag").toString()

        Log.e("Tag", mTag)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(com.lbit.fleetuat.R.color.header_color)
        val tags: MutableList<Array<String>> =
            Arrays.asList(resources.getStringArray(com.lbit.fleetuat.R.array.hi))

        Log.e("List", tags.toString())

        val loginData = UserSession(this@LandingPage).getLoginDetails()

        dictData = JSONObject()
        try {
            dictData = JSONObject(loginData)
            mRole = dictData.optString("role")
            mProfileName = dictData.optString("name")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        mMobile = UserSession(this@LandingPage).getMobile()

        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar!!.title = ""

        navigationView = findViewById(R.id.nav_view)
        toolbarTitle = findViewById(R.id.toolbar_title)
        val headerview = navigationView!!.getHeaderView(0)
        val nav_Menu = navigationView!!.menu

        llProfileLayout = headerview.findViewById(R.id.llProfileLayout)
        tvheadername = headerview.findViewById(R.id.tvheadername)
        tvheaderMobile = headerview.findViewById(R.id.tvheaderMobile)
        tvheaderRole = headerview.findViewById(R.id.tvheaderRole)

        navigationView!!.setNavigationItemSelectedListener(this)

        tvheadername.text = mProfileName
        tvheaderRole.text = mRole
        tvheaderMobile.text = mMobile

        init()
    }

    private fun init() {

        tabLayout = findViewById(R.id.dashboard_tabs)
        viewPager = findViewById(R.id.dashboard_viewpager)

        setupViewPager(viewPager!!)
        viewPager!!.offscreenPageLimit = 1
        tabLayout.setupWithViewPager(viewPager!!)
        tabLayout.setOnTabSelectedListener(this)

        if (mRole == "Others") {

            tab1 = LayoutInflater.from(this@LandingPage)
                .inflate(R.layout.tab_dashboard, null) as LinearLayout
            tabLayout.getTabAt(0)!!.customView = tab1

            tab2 = LayoutInflater.from(this@LandingPage)
                .inflate(R.layout.tab_request, null) as LinearLayout
            tabLayout.getTabAt(1)!!.customView = tab2

            tab3 = LayoutInflater.from(this@LandingPage)
                .inflate(R.layout.tab_stock, null) as LinearLayout
            tabLayout.getTabAt(2)!!.customView = tab3

            tab4 = LayoutInflater.from(this@LandingPage)
                .inflate(R.layout.tab_healthcheck, null) as LinearLayout
            tabLayout.getTabAt(3)!!.customView = tab4

            tab5 = LayoutInflater.from(this@LandingPage)
                .inflate(R.layout.tab_timelines, null) as LinearLayout
            tabLayout.getTabAt(4)!!.customView = tab5

            iconDashboard = findViewById(R.id.icon_dashboard)
            iconRequest = findViewById(R.id.icon_request)
            iconStock = findViewById(R.id.icon_stock)
            iconHealthCheck = findViewById(R.id.icon_healthCheck)
            iconTimeline = findViewById(R.id.icon_timeline)

//            (tabLayout.getChildAt(0) as LinearLayout).getChildAt(3).isClickable = false
//        (tabLayout.getChildAt(0) as LinearLayout).getChildAt(2).isClickable = false

            supportActionBar!!.title = "Service Status"

            if (mTag.isNotEmpty()){

                if (mTag == "4"){

                    viewPager!!.currentItem = 4
                }else if (mTag == "3"){

                    viewPager!!.currentItem = 3
                }else{

                    viewPager!!.currentItem = 0
                }
            }else{

                viewPager!!.currentItem = 0
            }

        } else {

            tab1 = LayoutInflater.from(this@LandingPage)
                .inflate(R.layout.tab_dashboard, null) as LinearLayout
            tabLayout.getTabAt(0)!!.customView = tab1

            tab2 = LayoutInflater.from(this@LandingPage)
                .inflate(R.layout.tab_stock, null) as LinearLayout
            tabLayout.getTabAt(1)!!.customView = tab2

            tab3 = LayoutInflater.from(this@LandingPage)
                .inflate(R.layout.tab_notification, null) as LinearLayout
            tabLayout.getTabAt(2)!!.customView = tab3

            iconDashboard = findViewById(R.id.icon_dashboard)
            iconStock = findViewById(R.id.icon_stock)
            iconNotification = findViewById(R.id.icon_notification)
//            iconHealthCheck = findViewById(R.id.icon_healthCheck)

            (tabLayout.getChildAt(0) as LinearLayout).getChildAt(2).isClickable = false
//        (tabLayout.getChildAt(0) as LinearLayout).getChildAt(2).isClickable = false

            supportActionBar!!.title = "Store Boy"
        }
        getToolbarTextColor()

    }

    private fun setupViewPager(viewPager: ViewPager) {


        val adapter = ViewPagerAdapter(supportFragmentManager)

        if (mRole == "Others") {
            adapter.addFrag(FragmentDashboard(), "ONE")
            adapter.addFrag(FragmentServiceRequest(), "TWO")
            adapter.addFrag(FragmentStock(), "THREE")
            adapter.addFrag(FragmentHealthCheckPoints(), "FOUR")
            adapter.addFrag(FragmentTimelines(), "Five")
        } else {
            adapter.addFrag(FragmentDashboardSB(), "ONE")
            adapter.addFrag(FragmentStock(), "TWO")
            adapter.addFrag(FragmentNotification(), "THREE")
        }


        viewPager.adapter = adapter
    }

    internal inner class ViewPagerAdapter
        (manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)

        }

        override fun getPageTitle(position: Int): CharSequence? {

            return null
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        val position = tab!!.position

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        val position = tab!!.position
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val position = tab!!.position

        if (mRole == "Others") {

            if (position == 0) {

                supportActionBar!!.title = "Service Status"
                getToolbarTextColor()

                iconDashboard.visibility = View.VISIBLE
                iconStock.visibility = View.INVISIBLE
                iconRequest.visibility = View.INVISIBLE
                iconHealthCheck.visibility = View.INVISIBLE
                iconTimeline.visibility = View.INVISIBLE

            } else if (position == 1) {

                supportActionBar!!.title = "Request a Service"
                getToolbarTextColor()

                iconDashboard.visibility = View.INVISIBLE
                iconStock.visibility = View.INVISIBLE
                iconRequest.visibility = View.VISIBLE
                iconHealthCheck.visibility = View.INVISIBLE
                iconTimeline.visibility = View.INVISIBLE

            } else if (position == 2) {

                supportActionBar!!.title = "Stock Search"
                getToolbarTextColor()

                iconDashboard.visibility = View.INVISIBLE
                iconStock.visibility = View.VISIBLE
                iconRequest.visibility = View.INVISIBLE
                iconHealthCheck.visibility = View.INVISIBLE
                iconTimeline.visibility = View.INVISIBLE

            } else if (position == 3) {

                supportActionBar!!.title = "Health Check Points"
                getToolbarTextColor()

                iconDashboard.visibility = View.INVISIBLE
                iconStock.visibility = View.INVISIBLE
                iconRequest.visibility = View.INVISIBLE
                iconHealthCheck.visibility = View.VISIBLE
                iconTimeline.visibility = View.INVISIBLE

            }else if (position == 4) {

                supportActionBar!!.title = "Timelines"
                getToolbarTextColor()

                iconDashboard.visibility = View.INVISIBLE
                iconStock.visibility = View.INVISIBLE
                iconRequest.visibility = View.INVISIBLE
                iconHealthCheck.visibility = View.INVISIBLE
                iconTimeline.visibility = View.VISIBLE
            }
        } else {

            if (position == 0) {
                supportActionBar!!.title = "Store Boy"
                getToolbarTextColor()

                iconDashboard.visibility = View.VISIBLE
                iconStock.visibility = View.INVISIBLE
                iconNotification.visibility = View.INVISIBLE
            } else if (position == 1) {

                supportActionBar!!.title = "Stock Search"
                getToolbarTextColor()

                iconDashboard.visibility = View.INVISIBLE
                iconStock.visibility = View.VISIBLE
                iconNotification.visibility = View.INVISIBLE
            } else if (position == 2) {

                supportActionBar!!.title = "Notifications"
                getToolbarTextColor()

                iconDashboard.visibility = View.INVISIBLE
                iconStock.visibility = View.INVISIBLE
                iconNotification.visibility = View.VISIBLE
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_logout) {
            drawer!!.closeDrawer(GravityCompat.START)
            Logout()
        }

        drawer = findViewById(R.id.drawer_layout)
        drawer!!.closeDrawer(GravityCompat.START)
        return true

    }

    private fun getToolbarTextColor() {

        val mSpannableText =
            SpannableString(supportActionBar?.title)

        mSpannableText.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.header_color)),
            0,
            mSpannableText.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        supportActionBar?.title = mSpannableText
    }


    private fun Logout() {

        val builder1 = AlertDialog.Builder(this@LandingPage)
        builder1.setMessage("Are you sure to Logout")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Yes"
        ) { dialog, id ->


            UserSession(this@LandingPage).removeMobile()
            UserSession(this@LandingPage).removeLoginDetails()

            Toast.makeText(this@LandingPage, "LoggedOut Successfully", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this@LandingPage, LoginScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        builder1.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()
    }


}