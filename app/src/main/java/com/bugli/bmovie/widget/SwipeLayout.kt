package com.bugli.bmovie.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bugli.bmovie.R
import kotlinx.android.synthetic.main.view_search.view.*
import kotlin.math.abs

class SwipeLayout : RelativeLayout {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBtn: RoundView
    private lateinit var editText: EditText
    private var lastY = 0f

    constructor(context: Context) : super(context) {
        initView()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }


/*    //控制滑动
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.rawY
//                Log.e("bugli", "按下" + event.y)
            }
            MotionEvent.ACTION_UP -> {
//                Log.e("bugli", "抬起" + event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                val y = event.rawY
                if (abs(y - lastY) > 5) {
                    setShowItem(true)
                    if (alpha >= 0 && alpha < 1) {
                        if (y - lastY < 0) {
                            alpha += abs(y - lastY) / 500
                        } else if (y - lastY > 0) {
                            alpha -= abs(y - lastY) / 500
                        }
                    } else if (alpha >= 1) {
                        editText.isFocusable = true
                        searchBtn.isClickable = true
                        //自动滑出
                    }
                }
//                Log.e("bugli", "移动" + event.y)
            }
        }
        return true
    }*/

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_search, this, true)
        recyclerView = result_rv
        searchBtn = search_btn
        editText = search_et
    }


    fun setSearchBtnOnClickListener(listener: OnClickListener) {
        searchBtn.setOnClickListener(listener)
    }


/*    fun setShowItem(show: Boolean) {
        if (show) {
            recyclerView.visibility = View.VISIBLE
            searchBtn.visibility = View.VISIBLE
            editText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.INVISIBLE
            searchBtn.visibility = View.INVISIBLE
            editText.visibility = View.INVISIBLE

            editText.isFocusable = false
            searchBtn.isClickable = false
        }

    }*/
}