package com.bugli.bmovie.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.bugli.bmovie.R

class RoundView : View {

    private lateinit var roundedPath: Path
    private var mViewWidth = 0
    private var mViewHeight = 0
    private var topRightRadius = 0
    private var bottomRightRadius = 0
    private var bottomLeftRadius = 0
    private var topLeftRadius = 0

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typeArray = context!!.obtainStyledAttributes(attrs, R.styleable.RoundView)
        if (typeArray.hasValue(R.styleable.RoundView_radius)) {
            val radius = typeArray.getDimensionPixelSize(R.styleable.RoundView_radius, 0)
            if (radius >= 0) {
                //一键设置
                topLeftRadius = radius
                topRightRadius = radius
                bottomLeftRadius = radius
                bottomRightRadius = radius
            }
            typeArray.recycle()
            return
        }
        //分开设置
        topLeftRadius =
            typeArray.getDimensionPixelSize(R.styleable.RoundView_topLeftRadius, 0)
        topRightRadius =
            typeArray.getDimensionPixelSize(R.styleable.RoundView_topRightRadius, 0)
        bottomLeftRadius =
            typeArray.getDimensionPixelSize(R.styleable.RoundView_bottomLeftRadius, 0)
        bottomRightRadius =
            typeArray.getDimensionPixelSize(R.styleable.RoundView_bottomRightRadius, 0)
        typeArray.recycle()
    }

    /*
    * 设置圆角
    * */
    fun setRadius(radius: Int) {
        if (radius >= 0) {
            topLeftRadius = radius
            topRightRadius = radius
            bottomLeftRadius = radius
            bottomRightRadius = radius
            invalidate()
        }
    }

    fun setRadius(
        topLeftRadius: Int,
        topRightRadius: Int,
        bottomLeftRadius: Int,
        bottomRightRadius: Int
    ) {
        if (topLeftRadius >= 0) {
            this.topLeftRadius = topLeftRadius
        }
        if (topRightRadius >= 0) {
            this.topRightRadius = topRightRadius
        }
        if (bottomLeftRadius >= 0) {
            this.bottomLeftRadius = bottomLeftRadius
        }
        if (bottomRightRadius >= 0) {
            this.bottomRightRadius = bottomRightRadius
        }
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mViewWidth = width
        mViewHeight = height
        Log.e("bugli", "$bottomLeftRadius====$topLeftRadius----$mViewHeight=====$mViewWidth")
        updateRoundedPath()
    }

    private fun updateRoundedPath() {

        roundedPath = Path()
        roundedPath.addRoundRect(
            RectF(0f, 0f, mViewWidth + 0f, mViewHeight + 0f), floatArrayOf(
                topLeftRadius.toFloat(),
                topLeftRadius.toFloat(),
                topRightRadius.toFloat(),
                topRightRadius.toFloat(),
                bottomRightRadius.toFloat(),
                bottomRightRadius.toFloat(),
                bottomLeftRadius.toFloat(),
                bottomLeftRadius.toFloat()
            ),
            Path.Direction.CW
        )
    }

    override fun onDraw(canvas: Canvas?) {
        canvas!!.clipPath(roundedPath)
        super.onDraw(canvas)
    }
}