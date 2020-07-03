package com.bugli.bmovie.data.model

import com.google.gson.annotations.SerializedName
import org.litepal.crud.LitePalSupport

class MovieList(@SerializedName("name") val listName: String):LitePalSupport() {
    @Transient
    val id = 0

    //影单编号
    @SerializedName("code")
    val listCode: String = "-1"

    //标签
    @SerializedName("sign")
    val listSign: String = "sign"

    //清单
    @SerializedName("movie")
    val movies: String = "movie"

    //图片地址
    @SerializedName("img")
    val imgUrl: String = "imgUrl"

}