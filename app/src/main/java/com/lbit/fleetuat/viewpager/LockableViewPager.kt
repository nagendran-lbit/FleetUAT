package com.lbit.fleetuat

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class LockableViewPager(myContext: Context,
                        attrs: AttributeSet? = null
) : ViewPager(myContext, attrs) {

    private var swipeLocked: Boolean = true

    constructor(context: Context) : this(context, null)

    fun getSwipeLocked(): Boolean {
        return swipeLocked
    }

    fun setSwipeLocked(swipeLocked: Boolean) {
        this.swipeLocked = swipeLocked
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return !swipeLocked && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return !swipeLocked && super.onInterceptTouchEvent(event)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return !swipeLocked && super.canScrollHorizontally(direction)
    }
}