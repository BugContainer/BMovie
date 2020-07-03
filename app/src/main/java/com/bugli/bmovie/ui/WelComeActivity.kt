package com.bugli.bmovie.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bugli.bmovie.R
import com.bugli.bmovie.util.GetPoint
import com.bugli.bmovie.widget.ShineTextView
import kotlinx.android.synthetic.main.activity_welcome.*

class WelComeActivity : AppCompatActivity() {
    lateinit var mWelcomeTV1: ShineTextView
    lateinit var mWelcomeTV2: ShineTextView
    lateinit var mWelcomeTV3: ShineTextView
    lateinit var mWelcomeTV4: ShineTextView
    lateinit var mWelcomeTV5: ShineTextView
    lateinit var mWelcomeTV6: ShineTextView

    lateinit var textSign: TextView
    var currentHeight = 0f
    var currentWidth = 0
    var currentTV = 0
    var list: MutableList<ShineTextView> = ArrayList()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        hideStatusAndNav()
        setContentView(R.layout.activity_welcome)
        mWelcomeTV1 = welcomeTV1
        mWelcomeTV2 = welcomeTV2
        mWelcomeTV3 = welcomeTV3
        mWelcomeTV4 = welcomeTV4
        mWelcomeTV5 = welcomeTV5
        mWelcomeTV6 = welcomeTV6

        textSign = text_sign
        list.add(mWelcomeTV1)
        list.add(mWelcomeTV2)
        list.add(mWelcomeTV3)
        list.add(mWelcomeTV4)
        list.add(mWelcomeTV5)
        list.add(mWelcomeTV6)
        textSign.typeface = Typeface.createFromAsset(assets, "陈继世-硬笔行书.ttf")
        val tf: Typeface = Typeface.createFromAsset(assets, "YoungFolks.ttf")
        for (i in list) {
            i.typeface = tf
        }
        //获取位移y坐标
        currentHeight = mWelcomeTV1.y
        textInAnimator()
    }

    override fun onResume() {
        hideStatusAndNav()
        super.onResume()
    }

    /**
     * 隐藏状态栏和导航栏
     * */
    private fun hideStatusAndNav() {
        //设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        //设置全屏隐藏状态栏和导航栏
        window.decorView.systemUiVisibility =
            (View.INVISIBLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        val lp = window.attributes
        //        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        //        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode =
                    //适配挖孔屏
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.attributes = lp
    }

    /**
     *淡出文字动画
     * */
    private fun textOutAnimator() {

        //位移动画
        val translateAnimation: TranslateAnimation =
            TranslateAnimation(
                0f,
                0f - GetPoint.getWinWH(this).x,
                currentHeight,
                currentHeight
            )
        //表示动画结束装态为结束状态
//        translateAnimation.fillAfter = true
        //translateAnimation.fillBefore = true
//        translateAnimation.repeatMode = Animation.RESTART
//        translateAnimation.repeatCount = 0

        val alphaAnimation = AlphaAnimation(1f, 0f)
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(translateAnimation)
        animationSet.addAnimation(alphaAnimation)
        animationSet.fillAfter = true
        animationSet.duration = 400
        list[currentTV].startAnimation(animationSet)
        //循环下一个字
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                if (currentTV + 1 < list.size) {
                    currentTV++
                    textOutAnimator()
                } else if (currentTV == list.size - 1) {
                    //
                    textSign.visibility = View.VISIBLE
                    val signAnimation = AlphaAnimation(0f, 1f)
                    signAnimation.fillAfter = true
                    signAnimation.duration = 1800
                    textSign.startAnimation(signAnimation)

                    signAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            //淡出结束，跳转界面
                            val intent = Intent()
                            intent.setClass(this@WelComeActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        override fun onAnimationStart(animation: Animation?) {
                        }
                    })

                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })

    }

    /**
     *淡入文字动画
     * */
    private fun textInAnimator() {
        val scaleAnimation = ScaleAnimation(1f, 1.2f, 1f, 1.2f)
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(scaleAnimation)
        animationSet.fillAfter = true
        animationSet.duration = 400
        list[currentTV].startAnimation(animationSet)
        //循环下一个字
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                if (currentTV + 1 < list.size) {
                    currentTV++
                    textInAnimator()
                } else if (currentTV == list.size - 1) {
                    currentTV = 0
                    textOutAnimator()
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })

    }


}