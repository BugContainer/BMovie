package com.bugli.bmovie.ui.download

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Description：网络请求接口类
 * Created by kang on 2018/3/9.
 */
interface ApiInterface {
    /**
     * 下载视频
     *
     * @param fileUrl
     * @return
     */
    @Streaming //大文件时要加不然会OOM
    @GET
    fun downloadFile(@Url fileUrl: String?): Call<ResponseBody?>?
}