package com.bugli.bmovie.util

object TimeUtil {
    fun formatTime(time: Long): String {
        var min = (time / (1000 * 60)).toString()
        var second = (time % (1000 * 60) / 1000).toString()
        if (min.length < 2) {
            min = "0$min"
        }
        if (second.length < 2) {
            second = "0$second"
        }
        return "$min:$second"
    }
}