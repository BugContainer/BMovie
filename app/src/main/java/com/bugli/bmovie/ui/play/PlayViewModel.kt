package com.bugli.bmovie.ui.play

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bugli.bmovie.BMovieApplication
import com.bugli.bmovie.data.MovieRepository
import com.bugli.bmovie.data.VideoRepository
import kotlinx.coroutines.launch

class PlayViewModel(private val movieRepository: MovieRepository,private val videoRepository: VideoRepository) : ViewModel() {
    var dataChanged = MutableLiveData<Int>()

    private fun launch(block: suspend () -> Unit) = viewModelScope.launch {
        try {
            block()
            dataChanged.value = dataChanged.value?.plus(1)
        } catch (t: Throwable) {
            t.printStackTrace()
            dataChanged.value = dataChanged.value?.plus(1)
            Toast.makeText(BMovieApplication.context, t.message, Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun getPlayList(playUrl: String) = movieRepository.getPlayList(playUrl)



    fun saveVideo(playList:MutableList<String>,downloadList:MutableList<String>,code:String) = videoRepository.saveVideo(playList,downloadList,code)


    fun downloadVideo(url: String) = videoRepository.downloadVideo(url)


    suspend fun getAllDownloadProgress() = videoRepository.getAllDownloadProgress()

}