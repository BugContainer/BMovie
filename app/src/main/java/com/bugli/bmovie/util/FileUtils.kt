package com.bugli.bmovie.util

import android.util.Log
import java.io.File
import java.io.IOException

/**
 * Description：
 * Created by kang on 2018/3/9.
 */
object FileUtils {
    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    fun isFileExists(file: File?): Boolean {
        Log.e("bugli", "isFileExists" + file + "--" + file!!.exists())
        return file != null && file.exists()
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsFile(file: File?): Boolean {
        Log.e(
            "bugli",
            "createOrExistsFile" + file + "====" + file!!.exists() + "====" + file.isFile + "====" + file.parentFile
        )
        if (file == null) return false
        // 如果存在，是文件则返回 true，是目录则返回 false
        if (file.exists()) return file.isFile
        return if (!createOrExistsDir(file.parentFile)) false else try {
            Log.e("bugli", "createNewFile")
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("bugli", "fail"+e)
            false
        }
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsDir(file: File?): Boolean {
        // 如果存在，是目录则返回 true，是文件则返回 false，不存在则返回是否创建成功
        Log.e("bugli", "" + file + "===" + file!!.exists() + "----" + file.isDirectory + "0000")
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 目录路径
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsDir(dirPath: String?): Boolean {
        return createOrExistsDir(
            getFileByPath(
                dirPath
            )
        )
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    private fun getFileByPath(filePath: String?): File? {
        return if (isSpace(filePath)) null else File(
            filePath
        )
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }
}