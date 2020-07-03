package com.bugli.bmovie.util

import com.bugli.bmovie.data.model.Movie
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

object StringParseUtil {


    /**
     * ok资源网
     * */
    fun okParse(data: String): MutableList<Movie> {
        val movies: MutableList<Movie> = ArrayList()
        //取一条记录
        val pattern = "</span><span class=\"xing_vb4\">[\\s\\S]*?</span></li>"
        //取播放地址
        val urlPattern = "<a href=\"[\\s\\S]*?\" target"
        //取名称
        val namePattern = "_blank\">[\\s\\S]*?</a>"
        //取类别
        val typePattern = "class=\"xing_vb5\">[\\s\\S]*?</span> <span"
        //取时间
        val timePattern = "class=\"xing_vb[67]\">[\\s\\S]*?</span></li>"

        // 创建 Pattern 对象
        val r: Pattern = Pattern.compile(pattern)
        val nameR: Pattern = Pattern.compile(namePattern)
        val urlR: Pattern = Pattern.compile(urlPattern)
        val typeR: Pattern = Pattern.compile(typePattern)
        val timeR: Pattern = Pattern.compile(timePattern)
        // 现在创建 matcher 对象
        val m: Matcher = r.matcher(data)
        while (m.find()) {
            val s = m.group()
            val nameM = nameR.matcher(s)
            val urlM = urlR.matcher(s)
            val typeM = typeR.matcher(s)
            val timeM = timeR.matcher(s)
            if (nameM.find()) {
                val movie = Movie(
                    nameM.group()
                        .substring(nameM.group().indexOf(">") + 1, nameM.group().indexOf("<"))
                        .replace("\n", "")
                )
                if (urlM.find()) {
                    movie.playUrl = urlM.group()
                        .substring(urlM.group().indexOf("\"") + 2, urlM.group().lastIndexOf("\""))
                        .replace("\n", "").substring(3)
                }
                if (typeM.find()) {
                    movie.type = typeM.group()
                        .substring(typeM.group().indexOf(">") + 1, typeM.group().indexOf("<"))
                        .replace("\n", "")
                }
                if (timeM.find()) {
                    movie.date = timeM.group()
                        .substring(timeM.group().indexOf(">") + 1, timeM.group().indexOf("<"))
                        .replace("\n", "")
                }
                movies.add(movie)
//                Log.e("bugli ", "movieName${movie.movieName}")
//                Log.e("bugli ", "playUrl${movie.playUrl}")
//                Log.e("bugli ", "type${movie.type}")
//                Log.e("bugli ", "date${movie.date}")
            }

        }
        return movies
    }

    fun okParsePlayList(data: String): MutableList<String> {
        val list: MutableList<String> = ArrayList()
        val m3u8Pattern = "m3u8\" checked=\"\" />[\\s\\S]*?.m3u8</li>"
        val downloadPattern = "mp4\" checked=\"\" />[\\s\\S]*?.mp4</li>"
        // 创建 Pattern 对象
        val m3u8PatternR: Pattern = Pattern.compile(m3u8Pattern)
        val downloadPatternR: Pattern = Pattern.compile(downloadPattern)
        // 现在创建 matcher 对象
        val m3u8PatternM: Matcher = m3u8PatternR.matcher(data)
        while (m3u8PatternM.find()) {
            val s = m3u8PatternM.group()
            list.add(s.substring(s.indexOf("$") + 1, s.indexOf("<")))
        }
        list.add("download")
        val downloadPatternM: Matcher = downloadPatternR.matcher(data)
        while (downloadPatternM.find()) {
            val s = downloadPatternM.group()
            list.add(s.substring(s.indexOf("$") + 1, s.indexOf("<")))
        }
        return list
    }


    /**
     * 获取详细信息
     * */
    fun okParseIssues(data: String): MutableList<Movie> {
        val list: MutableList<Movie> = ArrayList()
        val imgPattern = "lazy\" src=[\\s\\S]*?.jpg"
        val actPattern = "主演：<span>[\\s\\S]*?</span><"
        val areaPattern = "地区：<span>[\\s\\S]*?</span><"
        val signPattern = ""

        return list
    }


}