package com.bugli.bmovie.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import com.bugli.bmovie.R

@SuppressLint("AppCompatCustomView")
class ShineTextView(
    context: Context?,
    attrs: AttributeSet?
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    private var mLinearGradient: LinearGradient? = null
    private var mGradientMatrix: Matrix? = null
    private var mPaint: TextPaint? = null
    private var mViewWidth = 0
    private var mTranslate = 0
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//            Log.e("bugli","onSizeChanged");
        //准备画笔
        super.onSizeChanged(w, h, oldw, oldh)
        if (mViewWidth == 0) {
            mViewWidth = measuredWidth
            if (mViewWidth > 0) {
                mPaint = paint
                mLinearGradient = LinearGradient(
                    0f,
                    0f,
                    mViewWidth.toFloat(),
                    0f,
                    intArrayOf(
                        resources.getColor(R.color.colorGreen, null),
                        resources.getColor(R.color.colorPink, null),
                        resources.getColor(R.color.colorLightBlue, null),
                        resources.getColor(R.color.colorGreen, null),
                        resources.getColor(R.color.colorPink, null)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
                mPaint!!.shader = mLinearGradient
                mGradientMatrix = Matrix()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        //循环绘制
//        Log.e("bugli", "canvas")
        super.onDraw(canvas)
        if (mGradientMatrix != null) {
            mTranslate += mViewWidth / 5
            if (mTranslate > mViewWidth * 3 / 2) {
                mTranslate = -mViewWidth / 2
            }
            mGradientMatrix!!.setTranslate(mTranslate.toFloat(), 15f)
            mLinearGradient!!.setLocalMatrix(mGradientMatrix)
            //每100毫秒执行onDraw()
            postInvalidateDelayed(300)
        }
    }
}