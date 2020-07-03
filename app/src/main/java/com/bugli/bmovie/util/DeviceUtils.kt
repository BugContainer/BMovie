package com.bugli.bmovie.util

import android.os.Build

/**
 * Description：
 * Created by kang on 2018/3/9.
 */
object DeviceUtils {
    /**
     * 获取设备厂商
     *
     * 如 Xiaomi
     *
     * @return 设备厂商
     */
    val manufacturer: String
        get() = Build.MANUFACTURER
}