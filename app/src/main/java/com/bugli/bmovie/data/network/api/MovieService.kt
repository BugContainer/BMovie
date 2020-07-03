package com.bugli.bmovie.data.network.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {


    @GET("index.php")
    fun getMovies(@Query("m") url: String): Call<String>

    @GET("index.php")
    fun getMovieByName(@Query("m") url: String): Call<String>

    @GET("/")
    fun getPlayList(@Query("m") playUrl: String): Call<String>
}