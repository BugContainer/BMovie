package com.bugli.bmovie.ui.recommend

import com.bugli.bmovie.BMovieApplication.Companion.context
import com.bugli.bmovie.util.GetPoint

object CardConfig {
    //显示的最大item数
    const val SHOW_MAX_COUNT = 4

    //Y轴偏移

    val TRANSLATION_Y = GetPoint.getWinWH(context).x / 18f

    //缩放
    const val SCALE = 0.05f
}