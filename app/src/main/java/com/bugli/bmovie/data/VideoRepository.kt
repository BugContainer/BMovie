package com.bugli.bmovie.data

import android.util.Log
import com.bugli.bmovie.data.db.VideoDao
import com.bugli.bmovie.data.model.Video
import com.bugli.bmovie.ui.download.DownloadListener
import com.bugli.bmovie.util.DownloadUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoRepository private constructor(
    private val videoDao: VideoDao
) {


    fun saveVideo(playList: MutableList<String>, downloadList: MutableList<String>, code: String) {
        val list: MutableList<Video> = ArrayList()
        for ((pos, e) in downloadList.withIndex()) {
            val video = Video(
                e.substring(e.lastIndexOf("/") + 1, e.indexOf(".mp4"))
            )
            if (pos < playList.size) {
                val s = playList[pos]
                video.playUrl = s
                video.downloadUrl = e
                video.movieCode = code
                video.localPath =
                    DownloadUtil.PATH_CHALLENGE_VIDEO + e.substring(e.lastIndexOf("/"))
                list.add(video)
            }
        }
        videoDao.saveVideo(list)
    }


    fun downloadVideo(url: String) {
        //
        DownloadUtil.downloadFile(url, object : DownloadListener {
            override fun onStart() {
                //更新到数据库  开始下载 进度 0
                videoDao.updateCacheProgress(0, url)
                Log.e("bugli", "onStart")
            }

            override fun onProgress(currentLength: Int) {
                //更新到数据库  开始下载 进度 0
                videoDao.updateCacheProgress(currentLength, url)
                Log.e("bugli", "onProgress$currentLength====$url")
            }

            override fun onFinish(localPath: String?) {
                //结束回调
                videoDao.updateCacheProgress(100, url)
                Log.e("bugli", "onFinish")
            }

            override fun onFailure(erroInfo: String?) {
                //错误回调
                videoDao.updateCacheProgress(-1, url)
                Log.e("bugli", "onFailure$erroInfo")
            }


        })
    }

    suspend fun getAllDownloadProgress() = withContext(Dispatchers.IO) {
        val list = videoDao.getCacheProgress()

        list
    }


    companion object {

        private var instance: VideoRepository? = null

        fun getInstance(videoDao: VideoDao): VideoRepository {
            if (instance == null) {
                synchronized(VideoRepository::class.java) {
                    if (instance == null) {
                        instance = VideoRepository(videoDao)
                    }
                }
            }
            return instance!!
        }

    }

}