package com.bugli.bmovie.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Point
import android.view.Display
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import androidx.annotation.Nullable


object GetPoint {


    //获取屏幕宽高
    fun getWinWH(context: Context): Point {
        val defaultDisplay: Display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        return point
    }


    //获取View宽高
    fun getLoc(v: View): IntArray {
        val loc = intArrayOf(0,0)
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(w, h)
        loc[0] = v.measuredWidth
        loc[1] = v.measuredHeight
        return loc
    }


    fun dp2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun dp2px(context: Context, dipValue: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    //获取View在屏幕中的位置
    fun getLocOnScreen(v: View): IntArray {
        val loc = IntArray(2)
        v.getLocationOnScreen(loc)
        return loc
    }

    @Nullable
    fun findActivity(context: Context?): Activity? {
        if (context is Activity) {
            return context
        }
        return if (context is ContextWrapper) {
            findActivity(context.baseContext)
        } else {
            null
        }
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        val resources: Resources = context.resources
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }


    fun getNavigationBarHeight(var0: Context): Int {
        val var1 = ViewConfiguration.get(var0).hasPermanentMenuKey()
        var var2: Int
        return if (var0.resources.getIdentifier("navigation_bar_height", "dimen", "android")
                .also { var2 = it } > 0 && !var1
        ) var0.resources.getDimensionPixelSize(var2) else 0
    }

}