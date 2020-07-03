package com.bugli.bmovie.ui.download

/**
 * Description：下载相关的接口
 * Created by kang on 2018/3/9.
 */
interface DownloadListener {
    fun onStart()
    fun onProgress(currentLength: Int)
    fun onFinish(localPath: String?)
    fun onFailure(erroInfo: String?)
}