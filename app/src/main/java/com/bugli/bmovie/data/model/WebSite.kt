package com.bugli.bmovie.data.model

import com.google.gson.annotations.SerializedName
import org.litepal.crud.LitePalSupport

class WebSite(@SerializedName("name") val siteName: String) : LitePalSupport() {
    @Transient
    val id = 0

    //网址
    @SerializedName("url")
    val siteUrl: String = "url"

}