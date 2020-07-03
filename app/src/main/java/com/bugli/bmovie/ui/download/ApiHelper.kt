package com.bugli.bmovie.ui.download

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * ApiHelper 网络请求工具类
 * Created by kang on 2018/3/9.
 */
class ApiHelper(connTimeout: Int, readTimeout: Int, writeTimeout: Int) {
    private var mRetrofit: Retrofit? = null
    private val mHttpClient: OkHttpClient

    private constructor() : this(30, 30, 30) {}

    fun buildRetrofit(baseUrl: String?): ApiHelper {
        mRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(mHttpClient)
            .build()
        return this
    }

    fun <T> createService(serviceClass: Class<T>?): T {
        return mRetrofit!!.create(serviceClass)
    }

    companion object {
        private const val TAG = "ApiHelper"
        private var mInstance: ApiHelper? = null
        @JvmStatic
        val instance: ApiHelper?
            get() {
                if (mInstance == null) {
                    mInstance = ApiHelper()
                }
                return mInstance
            }
    }

    init {
        val builder = OkHttpClient.Builder()
            .connectTimeout(connTimeout.toLong(), TimeUnit.SECONDS)
            .readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)
            .writeTimeout(writeTimeout.toLong(), TimeUnit.SECONDS)
        mHttpClient = builder.build()
    }
}