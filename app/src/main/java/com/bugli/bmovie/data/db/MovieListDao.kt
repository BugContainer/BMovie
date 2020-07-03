package com.bugli.bmovie.data.db

import com.bugli.bmovie.data.model.MovieList
import org.litepal.LitePal

class MovieListDao {

    fun getAllLocalMovieList(): MutableList<MovieList> = LitePal.findAll(MovieList::class.java)
    fun getLocalMovieListByName(name: String): MutableList<MovieList> =
        LitePal.where("listName = ?", name).find(MovieList::class.java)


    fun getLocalMovieListBySign(sign: String): MutableList<MovieList> =
        LitePal.where("listSign = ?", sign).find(MovieList::class.java)


    fun saveMovieLists(list: List<MovieList>) {
        if (list.isNotEmpty()) {
            LitePal.saveAll(list)
        }
    }
}