package com.bugli.bmovie.widget

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bugli.bmovie.R
import com.bugli.bmovie.ui.MainActivity
import com.bugli.bmovie.ui.MainViewModel
import com.bugli.bmovie.ui.MyViewPagerAdapter
import com.bugli.bmovie.ui.download.DownloadFragment
import com.bugli.bmovie.ui.manage.ManageFragment
import com.bugli.bmovie.util.GetPoint
import com.bugli.bmovie.util.InjectorUtil
import com.google.android.material.tabs.TabLayout


class ManagePopupWindow// FragmentAdapter传入的是Activity的FragmentManger，
// 所以默认是在Activity的布局xml中寻找ViewPager的定义，但是实际上它是在弹出的View里定义的。
    (
    private var context: Context,
    private var parentLayout: Int,
    private var animation: Int,
    private var topY: Int,
    private var bottomY: Int
) {

    var childView: View
    private lateinit var popupWindow: PopupWindow
    private val titles = arrayListOf("设置管理", "下载管理")
    private val fragments = arrayListOf(ManageFragment(), DownloadFragment())

    private var tabLayout: TabLayout
    private var viewPager: ViewPager

    private var viewModel: MainViewModel

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        childView = inflater.inflate(R.layout.view_manage, null, false)
        viewModel = ViewModelProvider(
            GetPoint.findActivity(context) as FragmentActivity,
            InjectorUtil.getMainModelFactory()
        )
            .get(MainViewModel::class.java)
        tabLayout = childView.findViewById(R.id.tab_layout)
        viewPager = childView.findViewById(R.id.pager)
        val myViewPagerAdapter =
        // FragmentAdapter传入的是Activity的FragmentManger，
            // 所以默认是在Activity的布局xml中寻找ViewPager的定义，但是实际上它是在弹出的View里定义的。
            MyViewPagerAdapter((context as MainActivity).supportFragmentManager, titles, fragments)
        viewPager.adapter = myViewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }


    fun show() {
        val point = GetPoint.getWinWH(context)
        val topMargin = topY
        //创建popupWindow
        popupWindow =
            PopupWindow(
                childView,
                ActionBar.LayoutParams.MATCH_PARENT,
                bottomY - topY,
                true
            )
//
//   设置动画要在引入布局之前
        //设置动画
        popupWindow.animationStyle = animation

        //引入依附的布局
        val parentView = LayoutInflater.from(context).inflate(parentLayout, null)
        popupWindow.showAtLocation(
            parentView,
            Gravity.TOP,
            0,
            topMargin
        )


        //注册监听
        initListener()

    }

    private fun initListener() {

    }

}