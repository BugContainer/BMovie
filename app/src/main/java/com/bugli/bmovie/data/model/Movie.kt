package com.bugli.bmovie.data.model

import com.google.gson.annotations.SerializedName
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

class Movie(@SerializedName("name") var movieName: String) : LitePalSupport(), Serializable {
    @Transient
    var id = 0

    //编号
    @SerializedName("code")
    var movieCode: String = "-1"

    //演员
    @SerializedName("actors")
    var actors: String = "testAct"

    //地区
    @SerializedName("area")
    var area: String = "area"

    //播放列表
    @Column(unique = true)
    @SerializedName("play")
    var playUrl = "playUrl"

    //标签
    @SerializedName("sign")
    var sign: String = "sign"

    //类型
    @SerializedName("type")
    var type: String = "type"

    //更新日期
    @SerializedName("date")
    var date: String = "date"

    //简介
    @SerializedName("intro")
    var intro: String = "intro"

    //图片地址
    @SerializedName("img")
    var imgUrl: String = "imgUrl"

    //所属影单
    @SerializedName("list")
    var belongList: String = "noneList"


    //来源网站
    @SerializedName("site")
    var webSiteUrl: String = "defaultSiteUrl"
}