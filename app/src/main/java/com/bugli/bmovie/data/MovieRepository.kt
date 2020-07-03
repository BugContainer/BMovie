package com.bugli.bmovie.data

import android.util.Log
import com.bugli.bmovie.data.db.MovieDao
import com.bugli.bmovie.data.model.Movie
import com.bugli.bmovie.data.network.MovieNetwork
import com.bugli.bmovie.util.StringParseUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository private constructor(
    private val movieDao: MovieDao,
    private val network: MovieNetwork
) {


    suspend fun getAllMovies(url: String) = withContext(Dispatchers.IO) {
        var list = movieDao.getAllLocalMovie()
        //如果本地没有数据则从网络加载
        if (list.isEmpty()) {
            Log.e("bugli", "开始从网络加载")
            val s = network.fetchMovies(url)
            list = StringParseUtil.okParse(s)
            movieDao.saveMovies(list)
        }
        list
    }


    suspend fun getMovieByName(name: String) = withContext(Dispatchers.IO) {
        var list = movieDao.getLocalMovieByName(
            name.substring(
                name.indexOf("wd-") + 3,
                name.indexOf(".html")
            )
        )
        Log.e("bugli", "本地数据库已经有${list.size}部影片")
        //如果本地没有数据则从网络加载
        if (list.size < 20) {
            Log.e("bugli", "开始从网络查找资源")
            val s = network.fetchMovieName(name)
            val l = StringParseUtil.okParse(s)
            //去重
            var iterator = l.iterator()
            while (iterator.hasNext()) {
                val movie = iterator.next()
                for (i in 0 until list.size) {
                    if (movie.playUrl == list[i].playUrl) {
                        iterator.remove()
                    }
                }
            }
            //存下剩余的movie
            movieDao.saveMovies(l)
            //合并
            list.addAll(l)
        }
        //首次数据取决于网站索引方式
        list
    }

    fun isLocalCached(): Boolean {
        return movieDao.getLocalUserMovie().isNotEmpty()
    }


    suspend fun getPlayList(playUrl: String) = withContext(Dispatchers.IO) {
        val list: MutableList<String>
        Log.e("playUrl", playUrl)
        val s = network.fetchPlayList(playUrl)
        list = StringParseUtil.okParsePlayList(s)
        list
    }


    suspend fun getMovieIssues(s:String) = withContext(Dispatchers.IO) {
        val list: MutableList<Movie>
        val data = network.fetchPlayList(s)
        list = StringParseUtil.okParseIssues(data)
        list
    }

    suspend fun getRecommendMovies() = withContext(Dispatchers.IO) {
        val list: MutableList<Movie>
        val s = network.fetchMovies("vod-index-pg-1.html")
        list = StringParseUtil.okParse(s)
        list
    }


    companion object {

        private var instance: MovieRepository? = null

        fun getInstance(movieDao: MovieDao, network: MovieNetwork): MovieRepository {
            if (instance == null) {
                synchronized(MovieRepository::class.java) {
                    if (instance == null) {
                        instance = MovieRepository(movieDao, network)
                    }
                }
            }
            return instance!!
        }

    }

}