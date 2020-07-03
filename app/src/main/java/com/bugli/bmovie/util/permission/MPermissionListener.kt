package com.bugli.bmovie.util.permission

/**
 * Description：
 * Created by kang on 2017/10/26.
 */
interface MPermissionListener {
    /**
     * 授权
     */
    fun onPermit(requestCode: Int, vararg permission: String?)

    /**
     * 未授权
     */
    fun onCancel(requestCode: Int, vararg permission: String?)
}