package com.bugli.bmovie.data.db

import android.util.Log
import androidx.core.content.contentValuesOf
import com.bugli.bmovie.data.model.Video
import org.litepal.LitePal

class VideoDao {
    fun updateCacheProgress(progress: Int, downloadUrl: String) {
        LitePal.updateAll(
            Video::class.java,
            contentValuesOf(Pair("cacheProgress", progress)),
            "downloadUrl = ?", downloadUrl

        )
    }

    fun getCacheProgress(): MutableList<Video> =
        LitePal.where("cacheProgress != ?", "-100").find(Video::class.java)

    fun saveVideo(videos: List<Video>) {
        if (videos.isNotEmpty()) {
            try {
                LitePal.saveAll(videos)
            } catch (e: Exception) {
                if (!e.toString().contains("LitePalSupportException")) {
                    //如果不是由于数据库唯一性约束产生的异常则打印
                    Log.e("bugli", "$e")
                }
            }
        }
    }
}