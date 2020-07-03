package com.bugli.bmovie.data.model

import com.google.gson.annotations.SerializedName
import org.litepal.crud.LitePalSupport
import java.io.Serializable

class Video(@SerializedName("name") var videoName: String) : LitePalSupport(), Serializable {
    @Transient
    var id = 0

    //编号
    @SerializedName("code")
    var movieCode: String = "-1"

    //历史记录
    @SerializedName("history")
    var isHistory = 0

    //缓存进度
    @SerializedName("cacheProgress")
    var cacheProgress = -100

    //本地路径
    @SerializedName("localPath")
    var localPath: String = "localPath"

    //播放链接
    @SerializedName("playUrl")
    var playUrl: String = "playUrl"

    //下载链接
    @SerializedName("downloadUrl")
    var downloadUrl: String = "downloadUrl"
}