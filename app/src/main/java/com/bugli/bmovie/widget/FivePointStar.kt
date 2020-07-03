package com.bugli.bmovie.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.bugli.bmovie.R

class FivePointStar : View {

    private var mRadius = 0
    private var bgColor = 0
    private var borderColor = 0


    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        initTypeArray(context, attrs)
    }

    private fun initTypeArray(context: Context?, attrs: AttributeSet?) {
        val typeArray = context!!.obtainStyledAttributes(attrs, R.styleable.FivePointStar)
        //分开设置
        mRadius =
            typeArray.getDimensionPixelSize(R.styleable.FivePointStar_fp_radius, 0)
        bgColor =
            typeArray.getColor(R.styleable.FivePointStar_fp_background_color, 0x000000)
        borderColor =
            typeArray.getColor(R.styleable.FivePointStar_fp_border_color, 0xffffff)
        typeArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        val p = Paint()
        p.color = Color.YELLOW
        canvas!!.drawLine(2f, 3f, 100f, 100f, p)
        super.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

}
