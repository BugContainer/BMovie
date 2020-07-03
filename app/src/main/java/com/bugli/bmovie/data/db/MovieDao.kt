package com.bugli.bmovie.data.db

import android.util.Log
import com.bugli.bmovie.data.model.Movie
import org.litepal.LitePal
import java.util.*

class MovieDao {
    fun getLocalUserMovie(): MutableList<Movie> =
        LitePal.where("imgUrl != ?", "imgUrl").find(Movie::class.java)

    fun getAllLocalMovie(): MutableList<Movie> = LitePal.findAll(Movie::class.java)
    fun getLocalMovieByName(name: String): MutableList<Movie> =
        LitePal.where("movieName like ?", "%$name%").find(Movie::class.java)

    fun getLocalMovieByActor(actor: String): MutableList<Movie> =
        LitePal.where("actors = ?", actor).find(Movie::class.java)

    fun getLocalMovieByType(type: String): MutableList<Movie> =
        LitePal.where("type = ?", type).find(Movie::class.java)

    fun saveMovies(movies: List<Movie>) {
        if (movies.isNotEmpty()) {
            try {
                //生成唯一code
                for (elem in movies) {
                    elem.movieCode = UUID.randomUUID().toString()
                }
                LitePal.saveAll(movies)
            } catch (e: Exception) {
                if (!e.toString().contains("LitePalSupportException")) {
                    //如果不是由于数据库唯一性约束产生的异常则打印
                    Log.e("bugli", "$e")
                }
            }
        }
    }
}