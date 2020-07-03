package com.bugli.bmovie

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import org.litepal.LitePal

@SuppressLint("Registered")
class BMovieApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
        context = this
    }

    companion object {
        lateinit var context: Context
    }
}