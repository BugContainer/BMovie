package com.bugli.bmovie.ui

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bugli.bmovie.BMovieApplication
import com.bugli.bmovie.data.MovieRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MovieRepository) : ViewModel() {
    var dataChanged = MutableLiveData<Int>()
    fun isLocalCached() = repository.isLocalCached()

    fun getMovies() {
        //异步执行
        GlobalScope.launch {
            val job = launch {
                //获取最新20页，如果没找到再查询下面20，直到找到或者超时（30s）
                for (i in 1..20) {
                    launch {
                        repository.getAllMovies("vod-index-pg-$i.html")
                        Log.e("bugli", "这是第$i==、次=================")
                    }
                }

            }
            job.join()
            Log.e("bugli", "我在外面")
        }
    }


    suspend fun getRecommendMovies()  = repository.getRecommendMovies()


    suspend fun getMovieIssues(s:String) = repository.getMovieIssues(s)

    suspend fun getMovieByName(name: String) =
        repository.getMovieByName("vod-search-pg-1-wd-$name.html")


    //
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
}