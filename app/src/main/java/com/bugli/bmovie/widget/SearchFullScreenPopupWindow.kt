package com.bugli.bmovie.widget

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bugli.bmovie.R
import com.bugli.bmovie.data.model.Movie
import com.bugli.bmovie.ui.MainActivity
import com.bugli.bmovie.ui.MainViewModel
import com.bugli.bmovie.ui.SearchResultsAdapter
import com.bugli.bmovie.util.GetPoint
import com.bugli.bmovie.util.InjectorUtil
import kotlinx.android.synthetic.main.view_search.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SearchFullScreenPopupWindow(
    private var context: Context,
    private var parentLayout: Int,
    private var animation: Int,
    private var topY: Int,
    private var bottomY: Int
) {

    //绑定软键盘
    interface OnSoftKeyboardStateChangedListener {
        fun OnSoftKeyboardStateChanged(
            isKeyBoardShow: Boolean,
            keyboardHeight: Int
        )
    }

    //注册软键盘状态变化监听
    fun addSoftKeyboardChangedListener(listener: OnSoftKeyboardStateChangedListener?) {
        if (listener != null) {
            mKeyboardStateListeners!!.add(listener)
        }
    }

    //取消软键盘状态变化监听
    fun removeSoftKeyboardChangedListener(listener: OnSoftKeyboardStateChangedListener?) {
        if (listener != null) {
            mKeyboardStateListeners!!.remove(listener)
        }
    }

    private var mKeyboardStateListeners //软键盘状态监听列表
            : ArrayList<OnSoftKeyboardStateChangedListener>? = null
    private var mLayoutChangeListener: OnGlobalLayoutListener? = null
    private var mIsSoftKeyboardShowing = false


    private var childView: View

    private lateinit var editText: EditText
    private lateinit var searchBtn: RoundView

    private lateinit var recyclerView: RecyclerView

    private lateinit var popupWindow: PopupWindow
    private var viewModel: MainViewModel
    private lateinit var adapter: SearchResultsAdapter

    private lateinit var results: MutableList<Movie>

    fun show() {
        val point = GetPoint.getWinWH(context)
        val topMargin = topY
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

        Log.e("bugli", "==" + point.y)
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
        //监听et文本变化
        editText = childView.search_et
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                Log.e("bugli", "afterTextChanged" + editText.text)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                Log.e("bugli", "beforeTextChanged" + editText.text)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                Log.e("bugli", "onTextChanged" + editText.text)

            }


        })

        //
        recyclerView = childView.result_rv
        //注册搜索按钮点击事件
        searchBtn = childView.search_btn
        searchBtn.setOnClickListener {
            //
            GlobalScope.launch {
                //de dao sou suo jie guo
                results = viewModel.getMovieByName(editText.text.toString())
                //she zhi rv
                val msg = Message()
                msg.what = 0
                mHandler.sendMessage(msg)

            }
        }
    }


    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    val lm = LinearLayoutManager(context)
                    lm.orientation = LinearLayoutManager.VERTICAL
                    recyclerView.layoutManager = lm
                    adapter = SearchResultsAdapter(context, results)
                    recyclerView.adapter = adapter
                    adapter.setGetListener(object : SearchResultsAdapter.GetListener {
                        override fun onClick(position: Int) {
                            (GetPoint.findActivity(context) as MainActivity).goPlayFragment(results[position])
                            popupWindow.dismiss()
                        }

                    })
                }
            }
        }
    }


    fun showSoftInput() {
        editText = childView.search_et
        editText.requestFocus()
        //使用延迟加载解决直接加载不显示的问题
        editText.postDelayed({ //获取系统软件盘服务
            val imm: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, 0)
        }, 100)
    }


    /*
     *
     * 注入软件盘监听
     * */
    private fun initSoftInputListener() {
        val point: Point = GetPoint.getWinWH(context)
        mKeyboardStateListeners = ArrayList()
        mLayoutChangeListener = OnGlobalLayoutListener { //判断窗口可见区域大小
            val r = Rect()
            GetPoint.findActivity(context)!!.window.decorView
                .getWindowVisibleDisplayFrame(r)
            //如果屏幕高度和Window可见区域高度差值大于整个屏幕高度的1/3，则表示软键盘显示中，否则软键盘为隐藏状态。
            val heightDifference: Int = point.y - (r.bottom - r.top)
            val isKeyboardShowing: Boolean = heightDifference > point.y / 3
            //如果之前软键盘状态为显示，现在为关闭，或者之前为关闭，现在为显示，则表示软键盘的状态发生了改变
            if (mIsSoftKeyboardShowing && !isKeyboardShowing || !mIsSoftKeyboardShowing && isKeyboardShowing) {
                mIsSoftKeyboardShowing = isKeyboardShowing
                for (i in mKeyboardStateListeners!!.indices) {
                    val listener =
                        mKeyboardStateListeners!![i]
                    listener.OnSoftKeyboardStateChanged(
                        mIsSoftKeyboardShowing,
                        heightDifference
                    )
                }
            }
        }
        //注册布局变化监听
        GetPoint.findActivity(context)!!.window.decorView.viewTreeObserver
            .addOnGlobalLayoutListener(mLayoutChangeListener)
    }

    fun onDestroy() {
        //移除布局变化监听
        GetPoint.findActivity(context)!!.window.decorView
            .viewTreeObserver.removeOnGlobalLayoutListener(mLayoutChangeListener)
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        childView = inflater.inflate(R.layout.view_search, null, false)
        viewModel = ViewModelProvider(
            GetPoint.findActivity(context) as FragmentActivity,
            InjectorUtil.getMainModelFactory()
        )
            .get(MainViewModel::class.java)
        initSoftInputListener()
    }
}