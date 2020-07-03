package com.bugli.bmovie.util

import com.bugli.bmovie.data.MovieRepository
import com.bugli.bmovie.data.VideoRepository
import com.bugli.bmovie.data.db.BMovieDatabase
import com.bugli.bmovie.data.network.MovieNetwork
import com.bugli.bmovie.ui.MainModelFactory
import com.bugli.bmovie.ui.play.PlayModelFactory


object InjectorUtil {

    private fun getMovieRepository() =
        MovieRepository.getInstance(BMovieDatabase.getMovieDao(), MovieNetwork.getInstance())

    private fun getVideoRepository() =
        VideoRepository.getInstance(BMovieDatabase.getVideoDao())

    fun getMainModelFactory() = MainModelFactory(getMovieRepository())


    fun getPlayModelFactory() = PlayModelFactory(getMovieRepository(), getVideoRepository())

}