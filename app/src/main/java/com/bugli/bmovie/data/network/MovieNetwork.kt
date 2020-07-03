package com.bugli.bmovie.data.network

import android.util.Log
import com.bugli.bmovie.data.network.api.MovieService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MovieNetwork {


    private val movieService = ServiceCreator.create(MovieService::class.java)


    suspend fun fetchMovies(url: String) = movieService.getMovies(url).await()

    suspend fun fetchMovieName(name: String) = movieService.getMovieByName(name).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.e("bugli", "failure" + t)
                    continuation.resumeWithException(t)

                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    Log.e("bugli", "onResponse")
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }
            })
        }
    }

    suspend fun fetchPlayList(playUrl: String) = movieService.getPlayList(playUrl).await()

    companion object {

        private var network: MovieNetwork? = null

        fun getInstance(): MovieNetwork {
            if (network == null) {
                synchronized(MovieNetwork::class.java) {
                    if (network == null) {
                        network = MovieNetwork()
                    }
                }
            }
            return network!!
        }

    }
}