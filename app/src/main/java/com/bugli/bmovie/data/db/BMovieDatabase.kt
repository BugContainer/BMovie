package com.bugli.bmovie.data.db

object BMovieDatabase {
    private var movieDao: MovieDao? = null
    private var videoDao: VideoDao? = null
    private var movieListDao: MovieListDao? = null
    private var webSiteDao: WebSiteDao? = null


    fun getMovieDao(): MovieDao {
        if (movieDao == null)
            movieDao = MovieDao()
        return movieDao!!
    }

    fun getMovieListDao(): MovieListDao {
        if (movieListDao == null)
            movieListDao = MovieListDao()
        return movieListDao!!
    }

    fun getWebSiteDao(): WebSiteDao {
        if (webSiteDao == null)
            webSiteDao = WebSiteDao()
        return webSiteDao!!

    }

    fun getVideoDao(): VideoDao {
        if (videoDao == null)
            videoDao = VideoDao()
        return videoDao!!
    }


}