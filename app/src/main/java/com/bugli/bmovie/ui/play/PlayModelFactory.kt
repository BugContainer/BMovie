package com.bugli.bmovie.ui.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bugli.bmovie.data.MovieRepository
import com.bugli.bmovie.data.VideoRepository

class PlayModelFactory(
    private val movieRepository: MovieRepository,
    private val videoRepository: VideoRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayViewModel(movieRepository, videoRepository) as T
    }
}