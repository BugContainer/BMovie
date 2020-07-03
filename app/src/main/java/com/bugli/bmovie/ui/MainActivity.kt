package com.bugli.bmovie.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.bugli.bmovie.R
import com.bugli.bmovie.data.model.Movie
import com.bugli.bmovie.ui.local.LocalFragment
import com.bugli.bmovie.ui.play.PlayFragment
import com.bugli.bmovie.ui.recommend.RecommendFragment
import com.bugli.bmovie.util.GetPoint
import com.bugli.bmovie.util.InjectorUtil
import com.bugli.bmovie.util.PhotosUtil
import com.bugli.bmovie.widget.ManagePopupWindow
import com.bugli.bmovie.widget.SearchFullScreenPopupWindow
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable


/**
 * 每日推荐
 * */
class MainActivity : AppCompatActivity(), Serializable {
    private var managePopupWindow: ManagePopupWindow? = null
    private lateinit var topView: LinearLayout
    private lateinit var bottomView: LinearLayout
    private lateinit var topNameTV: TextView
    private lateinit var bitmaps: List<Bitmap>
    private lateinit var viewModel: MainViewModel
    private lateinit var mMovie: Movie
    private lateinit var playFragment: PlayFragment
    private lateinit var topShape: LinearLayout
    private lateinit var bottomShape: LinearLayout
    private var mPlay = 1
    private var cVolume = 0.8f
    private var isLand = false
    private var isTrans = false
    private var canShow = true
    private var canShowManage = true
    private var currentFragment = 0 //0 推荐页面 1 play 页面 2 local 页面
    private var lastFragment = 0
    private var screenHeight = 0


    // 否则会自动根据xml文件名+Binding生成class
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        isLand = requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        //必须最先初始化
        super.onCreate(savedInstanceState)
        //强制竖屏
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, InjectorUtil.getMainModelFactory())
            .get(MainViewModel::class.java)
        screenHeight = GetPoint.getWinWH(this).y
        //bg
        bitmaps = PhotosUtil.separatePhoto(
            screenHeight / 6, screenHeight / 12, screenHeight,
            ResourcesCompat.getDrawable(resources, R.drawable.main_bg, null)!!.toBitmap()
        )
        initView()
        if (savedInstanceState != null) {
            if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                topView.visibility = View.GONE
                bottomView.visibility = View.GONE
                topShape.visibility = View.GONE
                bottomShape.visibility = View.GONE
            }
            mPlay = savedInstanceState.getInt("mPlay")
            cVolume = savedInstanceState.getFloat("cVolume")
            mMovie = savedInstanceState.getSerializable("movie") as Movie
            playFragment = savedInstanceState.getSerializable("playFragment") as PlayFragment
/*旋转时，activity 会 从mFragments恢复fragment ，所以此处不用重复添加 ，以免 fragment的 onActivityCreated方法被调用两次*/
/*            supportFragmentManager.beginTransaction()
                .replace(R.id.fg_container, playFragment)
                .commit()*/
            currentFragment = 1
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            initFragment()
        }
    }

    private fun initFragment() {
        if (!viewModel.isLocalCached()) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fg_container, RecommendFragment(bitmaps[2]))
                .commit()
            lastFragment = currentFragment
            currentFragment = 0
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fg_container, LocalFragment())
                .commit()
            lastFragment = currentFragment
            currentFragment = 2
        }

    }

    override fun onResume() {
        super.onResume()
        if (isLand) {
            hideStatusAndNav()

        } else {
            initStatusAndNav()
        }

    }

    private fun initStatusAndNav() {
        val bigBtmColor = PhotosUtil.getBigColor(
            (bottomView.background as BitmapDrawable).bitmap
        )
        val bigTopColor = PhotosUtil.getBigColor(
            (topView.background as BitmapDrawable).bitmap
        )
        /*  透明状态栏*/
        //tiaose
        if (ColorUtils.calculateLuminance(bigBtmColor) > 0.5) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        window.statusBarColor = bigTopColor
        window.navigationBarColor = bigBtmColor
    }


    /**
     * 隐藏状态栏和导航栏
     * */
    fun hideStatusAndNav() {
        //设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        //设置全屏隐藏状态栏和导航栏
        window.decorView.systemUiVisibility =
            (View.INVISIBLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

/*        val lp = window.attributes
        //        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        //        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode =
                    //适配挖孔屏
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.attributes = lp*/
    }

    /**
     * playFragment
     * */
    fun goPlayFragment(movie: Movie) {
        mMovie = movie
        playFragment = PlayFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fg_container, playFragment)
            .commit()
        lastFragment = currentFragment
        currentFragment = 1
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        /*初始化上下View*/
        topShape = top_shape
        bottomShape = bottom_shape
        topView = top_view

        bottomView = bottom_view
        topNameTV = top_name
        topNameTV.typeface = Typeface.createFromAsset(
            assets, "YoungFolks.ttf"
        )
        //
        val lp: RelativeLayout.LayoutParams =
            topView.layoutParams as RelativeLayout.LayoutParams
        lp.height = screenHeight / 6
        topView.layoutParams = lp

        val lp1: RelativeLayout.LayoutParams =
            bottomView.layoutParams as RelativeLayout.LayoutParams
        lp1.height = screenHeight / 12
        bottomView.layoutParams = lp1


        topView.background = BitmapDrawable(resources, bitmaps[0])
        bottomView.background = BitmapDrawable(resources, bitmaps[1])

        val searchPopupWindow = SearchFullScreenPopupWindow(
            this,
            R.layout.activity_main,
            R.style.PopupWindowSearchAnimation
            , GetPoint.getLoc(topView)[1] / 2,
            GetPoint.getWinWH(this).y + GetPoint.getNavigationBarHeight(this) - GetPoint.getLoc(
                bottomView
            )[1] / 2
        )

        managePopupWindow = ManagePopupWindow(
            this,
            R.layout.activity_main,
            R.style.PopupWindowManageAnimation
            ,
            GetPoint.getLoc(topView)[1] / 2,
            GetPoint.getWinWH(this).y + GetPoint.getNavigationBarHeight(this) - GetPoint.getLoc(
                bottomView
            )[1] / 2
        )
        topView.setOnClickListener {
            if (canShow) {
                canShow = false
                searchPopupWindow.show()
                //200ms  ×2
                GlobalScope.launch {
                    delay(400)
                    canShow = true
                }
            }

        }
        bottomView.setOnClickListener {
            if (canShowManage) {
                canShowManage = false
                managePopupWindow!!.show()
                //200ms  ×2
                GlobalScope.launch {
                    delay(400)
                    canShowManage = true
                }
            }
        }
    }


    @SuppressLint("SourceLockedOrientationActivity")
    fun setOrientation(orientation: Int) {
        if (requestedOrientation != orientation) {
            isTrans = true
            requestedOrientation = orientation
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        if (currentFragment == 1 && isTrans) {
            outState.putSerializable("movie", mMovie)
            outState.putSerializable("playFragment", playFragment)
            isTrans = false
        }
        super.onSaveInstanceState(outState)
    }

    fun getMMovie(): Movie {
        return mMovie
    }

    fun backFragment() {
        when (lastFragment) {
            0 -> {
                //推荐
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fg_container, RecommendFragment(bitmaps[2]))
                    .commit()
                lastFragment = currentFragment
                currentFragment = 0
            }
            2 -> {
                //本地
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fg_container, LocalFragment())
                    .commit()
                lastFragment = currentFragment
                currentFragment = 2
            }
            else -> {

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fg_container, LocalFragment())
                    .commit()
                lastFragment = currentFragment
                currentFragment = 2
            }
        }
    }


    //重写activity 的 findViewById方法 以便 在PopupWindow中获取到viewPager控件
    override fun <T : View?> findViewById(id: Int): T {
        if (id == R.id.pager && managePopupWindow != null) {
            return managePopupWindow!!.childView.findViewById<T>(R.id.pager)
        }
        return super.findViewById<T>(id)

    }
}
