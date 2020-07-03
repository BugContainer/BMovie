package com.bugli.bmovie.util

import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_MOVIES
import android.text.TextUtils
import android.util.Log
import androidx.annotation.NonNull
import com.bugli.bmovie.BMovieApplication.Companion.context
import com.bugli.bmovie.ui.download.ApiHelper.Companion.instance
import com.bugli.bmovie.ui.download.ApiInterface
import com.bugli.bmovie.ui.download.DownloadListener
import com.bugli.bmovie.util.FileUtils.createOrExistsDir
import com.bugli.bmovie.util.FileUtils.createOrExistsFile
import com.bugli.bmovie.util.FileUtils.isFileExists
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

/**
 * Description：下载文件工具类
 * Created by kang on 2018/3/9.
 */
object DownloadUtil {
    //视频下载相关
    private val TAG = "DownloadUtil"
    val PATH_CHALLENGE_VIDEO =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(DIRECTORY_MOVIES)
                .toString() + "/BMovie"
        } else {
            Environment.getExternalStorageDirectory().toString() + "/BMovie"
        }
    private var mApi: ApiInterface? = null
    private var mCall: Call<ResponseBody?>? = null
    private var mFile: File? = null
    lateinit var mThread: Thread
    private var mVideoPath //下载到本地的视频路径
            : String? = null

    fun downloadFile(
        url: String,
        downloadListener: DownloadListener
    ) {
        //通过Url得到保存到本地的文件名
        var name = url
        if (createOrExistsDir(
                PATH_CHALLENGE_VIDEO
            )
        ) {
            val i = name.lastIndexOf('/') //一定是找最后一个'/'出现的位置
            if (i != -1) {
                name = name.substring(i)
                mVideoPath = PATH_CHALLENGE_VIDEO +
                        name
            }
        }
        if (TextUtils.isEmpty(mVideoPath)) {
            Log.e(TAG, "downloadVideo: 存储路径为空了")
            return
        }
        //建立一个文件
        mFile = File(mVideoPath)
        if (!isFileExists(mFile) && createOrExistsFile(
                mFile
            )
        ) {

            if (mApi == null) {
                Log.e(TAG, "downloadVideo: 下载接口为空了")
                return
            }
            mCall = mApi!!.downloadFile(url)
            mCall!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    @NonNull call: Call<ResponseBody?>,
                    @NonNull response: Response<ResponseBody?>
                ) {
                    //下载文件放在子线程
                    mThread = object : Thread() {
                        override fun run() {
                            super.run()
                            //保存到本地
                            writeFile2Disk(
                                response,
                                mFile!!,
                                downloadListener
                            )
                        }
                    }
                    mThread.start()
                }

                override fun onFailure(
                    call: Call<ResponseBody?>,
                    t: Throwable
                ) {
                    downloadListener.onFailure("网络错误！")
                }
            })
        } else {
            downloadListener.onFinish(mVideoPath)
        }
    }

    private fun writeFile2Disk(
        response: Response<ResponseBody?>,
        file: File,
        downloadListener: DownloadListener
    ) {
        downloadListener.onStart()
        var currentLength: Long = 0
        var os: OutputStream? = null
        if (response.body() == null) {
            downloadListener.onFailure("资源错误！")
            return
        }
        val `is` = response.body()!!.byteStream()
        val totalLength = response.body()!!.contentLength()
        var s = 1
        try {
            os = FileOutputStream(file)
            var len: Int
            val buff = ByteArray(1024)
            while (`is`.read(buff).also { len = it } != -1) {
                os.write(buff, 0, len)
                currentLength += len.toLong()
                //设置每1MB 1024*1024 回调一次 可自行调整
                if (currentLength > s * (1024 * 1024) || currentLength == totalLength) {
                    s++
                    Log.e(TAG, "当前进度: $currentLength")
                    downloadListener.onProgress((100 * currentLength / totalLength).toInt())
                    if ((100 * currentLength / totalLength).toInt() == 100) {
                        downloadListener.onFinish(mVideoPath)
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            downloadListener.onFailure("未找到文件！")
            e.printStackTrace()
        } catch (e: IOException) {
            downloadListener.onFailure("IO错误！")
            e.printStackTrace()
        } finally {
            if (os != null) {
                try {
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    init {
        if (mApi == null) {
            mApi = instance!!.buildRetrofit("http://www.okzyw.com/")
                .createService(ApiInterface::class.java)
        }
    }
}