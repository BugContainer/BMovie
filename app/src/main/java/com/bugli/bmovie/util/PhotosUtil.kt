package com.bugli.bmovie.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import java.util.*
import kotlin.collections.ArrayList

object PhotosUtil {
    private fun small(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postScale(0.25f, 0.25f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getPicturePixel(bitmap: Bitmap): ArrayList<Int> {
        val width = bitmap.width
        val height = bitmap.height

        // 保存所有的像素的数组，图片宽×高
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val rgb = ArrayList<Int>()
        for (i in pixels.indices) {
            val clr = pixels[i]
            val red = clr and 0x00ff0000 shr 16 // 取高两位
            val green = clr and 0x0000ff00 shr 8 // 取中两位
            val blue = clr and 0x000000ff // 取低两位
            //            Log.d("tag", "r=" + red + ",g=" + green + ",b=" + blue);
            val color = Color.rgb(red, green, blue)
            //除去白色和黑色
            if (color != Color.WHITE && color != Color.BLACK) {
                rgb.add(color)
            }
        }
        return rgb
    }

    fun getBigColor(bitmap: Bitmap): Int {
        val picturePixel =
            getPicturePixel(small(bitmap))
        //计数相同颜色数量并保存
        val color2 = HashMap<Int, Int?>()
        for (color in picturePixel) {
            if (color2.containsKey(color)) {
                var integer = color2[color]
                if (integer != null) {
                    integer += 1
                }
                color2.remove(color)
                color2[color] = integer
            } else {
                color2[color] = 1
            }
        }
        //挑选数量最多的颜色
        val iter: Iterator<*> = color2.entries.iterator()
        var count = 0
        var color = 0
        while (iter.hasNext()) {
            val entry =
                iter.next() as Map.Entry<*, *>
            val value = entry.value as Int
            if (count < value) {
                count = value
                color = entry.key as Int
            }
        }
        return color
    }

    //jiang photo fen wei shang zhong xia san kuai
    fun separatePhoto(h1: Int, h2: Int, h3: Int, bitmap: Bitmap): List<Bitmap> {
        val list: MutableList<Bitmap> = ArrayList()
        //shang
        list.add(
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height * h1 / h3,
                null,
                false
            )
        )
//xia
        list.add(
            Bitmap.createBitmap(
                bitmap,
                0,
                bitmap.height * (h3 - h2) / h3,
                bitmap.width,
                bitmap.height * h2 / h3,
                null,
                false
            )
        )

        list.add(
            Bitmap.createBitmap(
                bitmap,
                0,
                bitmap.height * h1 / h3,
                bitmap.width,
                bitmap.height * (h3 - h2 - h1) / h3,
                null,
                false
            )
        )

        return list
    }
}