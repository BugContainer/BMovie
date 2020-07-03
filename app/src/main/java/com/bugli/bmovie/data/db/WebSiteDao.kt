package com.bugli.bmovie.data.db

import com.bugli.bmovie.data.model.WebSite
import org.litepal.LitePal

class WebSiteDao {

    fun getAllWebSites(): MutableList<WebSite> = LitePal.findAll(WebSite::class.java)
    fun getWebSiteByName(name: String): MutableList<WebSite> =
        LitePal.where("siteName = ?", name).find(WebSite::class.java)

    fun saveWebSite(list: List<WebSite>) {
        if (list.isNotEmpty()) {
            LitePal.saveAll(list)
        }
    }
}