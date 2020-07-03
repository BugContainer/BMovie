package com.bugli.bmovie.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.bugli.bmovie.R
import com.bugli.bmovie.util.GetPoint
import kotlin.math.abs

class HorizontalProgressBar : View {
    private var max = 0
    private var progress = 0
    private var bgColor = 0
    private var progressColor = 0
    private var padding = 0
    private var progressPaint: Paint? = null
    private var bgPaint: Paint? = null


    private var enableCircle = true
    private var enableTouch = true

    private var isMove = false
    private var lastX = 0f

    //圆点指示器的画笔
    private var circlePaint: Paint? = null

    //圆点指示器的半径
    private val mCircleRadius: Int = GetPoint.dp2px(context, 5)

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        initAttrs(context, attrs)
        initPaths()
        if (enableCircle) {
            initCircle()
        }
    }

    private fun initAttrs(
        context: Context,
        attrs: AttributeSet?
    ) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar)
        max = a.getInteger(R.styleable.HorizontalProgressBar_pb_max, 100)
        progress = a.getInteger(R.styleable.HorizontalProgressBar_pb_progress, 0)
        bgColor = a.getColor(R.styleable.HorizontalProgressBar_pb_bg_color, -0xc0ae4b)
        progressColor = a.getColor(R.styleable.HorizontalProgressBar_pb_pb_color, -0xbf7f)
        padding = a.getDimensionPixelSize(R.styleable.HorizontalProgressBar_pb_padding, 2)
        a.recycle()
    }

    private fun initPaths() {
        progressPaint = Paint()
        progressPaint!!.color = progressColor
        progressPaint!!.style = Paint.Style.FILL
        progressPaint!!.isAntiAlias = true
        bgPaint = Paint()
        bgPaint!!.color = bgColor
        bgPaint!!.style = Paint.Style.FILL
        bgPaint!!.isAntiAlias = true
    }


    private fun initCircle() {
        //圆点指示器
        //圆点指示器
        circlePaint = Paint()
        circlePaint!!.isAntiAlias = true //设置抗锯齿
        circlePaint!!.color = Color.parseColor("#fafafa") //颜色
        circlePaint!!.setShadowLayer(
            GetPoint.dp2px(context, 2).toFloat(),
            0f,
            0f,
            Color.parseColor("#38000000")
        ) //外阴影颜色
        circlePaint!!.style = Paint.Style.FILL //填充

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawProgress(canvas)
    }

    private fun drawProgress(canvas: Canvas) {
        var width = width

        if (width % 2 != 0) {
            //Fix Me
            width -= 1
        }
        var percent = 0f
        if (max != 0) {
            percent = progress * 1.0f / max
        }
        var progressHeight = height - padding * 2
        if (progressHeight % 2 != 0) {
            progressHeight -= 1
        }
        val progressWidth = if (enableCircle) {
            width - padding * 2 - progressHeight
        } else width
        val dx = progressWidth * percent
        //left circle
        canvas.drawCircle(
            padding + progressHeight / 2.toFloat(),
            padding + progressHeight / 2.toFloat(),
            progressHeight / 2.toFloat(),
            progressPaint!!
        )
        //right circle
        canvas.drawCircle(
            padding + progressHeight / 2 + dx,
            (padding + progressHeight / 2 - mCircleRadius / 2).toFloat(),
            progressHeight / 2.toFloat(),
            progressPaint!!
        )
        //middle line
        val midRecf = RectF(
            (padding + progressHeight / 2).toFloat(),
            padding.toFloat(),
            padding + progressHeight / 2 - mCircleRadius / 2 + dx,
            (padding + progressHeight).toFloat()
        )
        canvas.drawRect(midRecf, progressPaint!!)

        if (enableCircle) {
            //绘制圆点控制
            canvas.drawCircle(
                progressHeight / 2 + dx + padding,
                (padding + progressHeight / 2).toFloat(),
                mCircleRadius.toFloat(),
                circlePaint!!
            )
        }

    }

    private fun drawBackground(canvas: Canvas) {
        var bgHeight = height - padding * 2
        if (bgHeight % 2 != 0) {
            bgHeight -= 1
        }
        var width = width
        //Fix Me
        if (width % 2 != 0) width -= 1

        //left circle
        canvas.drawCircle(
            (padding + bgHeight / 2).toFloat(),
            (padding + bgHeight / 2).toFloat(),
            bgHeight / 2.toFloat(),
            bgPaint!!
        )
        //right circle
        canvas.drawCircle(
            (padding + width - bgHeight / 2).toFloat(),
            (padding + bgHeight / 2 - mCircleRadius / 2).toFloat(),
            bgHeight / 2.toFloat(),
            bgPaint!!
        )
        //middle line
        val midRecf =
            RectF(
                (padding + bgHeight / 2).toFloat(),
                padding.toFloat(),
                (width - bgHeight / 2 - mCircleRadius / 2).toFloat(),
                padding + bgHeight.toFloat()
            )
        canvas.drawRect(midRecf, bgPaint!!)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun getMax(): Int {
        return max
    }

    fun setMax(max: Int) {
        this.max = max
        invalidate()
    }

    fun getProgress(): Int {
        return progress
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun getBgColor(): Int {
        return bgColor
    }

    fun setBgColor(bgColor: Int) {
        this.bgColor = bgColor
        invalidate()
    }

    fun getProgressColor(): Int {
        return progressColor
    }


    fun setEnableCircle(boolean: Boolean) {
        this.enableCircle = boolean
        invalidate()
    }

    fun setEnableTouch(boolean: Boolean) {
        this.enableTouch = boolean
        invalidate()
    }


    fun setProgressColor(progressColor: Int) {
        this.progressColor = progressColor
        progressPaint!!.color = progressColor
        invalidate()
    }

    fun getPadding(): Int {
        return padding
    }

    fun setPadding(padding: Int) {
        this.padding = padding
        invalidate()
    }

    /**
     * get the percentage value of progress and max.
     *
     * @return percentage value
     */
    val percentage: Int
        get() = if (max == 0) {
            0
        } else (progress * 100.0 / max).toInt()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                //x,y 是控件的内部坐标
                lastX = event.x
                Log.e("bugli", "ACTION_Down")
            }

            MotionEvent.ACTION_MOVE -> {
                if (abs(event.x - lastX) > 4) {
                    isMove = true
                    progress += ((event.x - lastX) * max / width).toInt()
                    lastX = event.x
                    if (progress < 0) {
                        progress = 0
                    } else if (progress > max) {
                        progress = max
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.e("bugli", "ACTION_UP$isMove")

                if (!isMove) {
                    //点击切换进度
                    progress = (lastX * max / width).toInt()
                    if (progress < 0) {
                        progress = 0
                    } else if (progress > max) {
                        progress = max
                    }
                    Log.e("bugli", "width$width")
                    invalidate()
                }
                lastX = 0f
                isMove = false
            }
        }

        return enableTouch
    }

}