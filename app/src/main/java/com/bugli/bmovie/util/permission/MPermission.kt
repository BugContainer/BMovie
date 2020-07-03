package com.bugli.bmovie.util.permission

import android.app.Activity
import android.app.Fragment
import android.content.pm.PackageManager
import java.lang.ref.WeakReference

/**
 * Description：Permission封装类
 * Created by kang on 2017/10/26.
 */
class MPermission private constructor() {
    //==================================以下为get、set方法================================================
    //    private Activity activity;
    //持有弱引用HandlerActivity,GC回收时会被回收掉.解决内存泄漏的问题
    var weakActivity: WeakReference<Activity?>? = null
    var requestCode = 0
    var listener: MPermissionListener? = null
    private var permissions: Array<String> = arrayOf()

    /**
     * 设置权限请求码
     *
     * @param requestCode
     * @return
     */
    fun requestCode(requestCode: Int): MPermission {
        codes.add(requestCode)
        instance.requestCode = requestCode
        return instance
    }

    /**
     * 设置请求回调
     *
     * @param listener
     * @return
     */
    fun callBack(listener: MPermissionListener): MPermission {
        instance.listener = listener
        return instance
    }

    /**
     * 请求项目
     *
     * @param permissions
     * @return
     */
    fun permission(permissions: Array<String>): MPermission {
        instance.setPermissions(permissions)
        return instance
    }

    /**
     * 开始请求
     */
    fun send() {
        if (instance == null || instance.weakActivity
            !!.get() == null || instance.listener == null || instance.getPermissions() == null
        ) {
            return
        }

        // 判断是否授权
        if (MPermissionUtils.instance!!.checkPermission(
                instance.weakActivity!!.get()!!,
                *instance.getPermissions()!!
            )
        ) {
            // 已经授权，执行授权回调
            instance.listener!!.onPermit(
                instance.requestCode,
                *instance.getPermissions()!!
            )
        } else {
            MPermissionUtils.instance!!.requestPermission(
                instance.weakActivity!!.get(),
                instance.requestCode,
                *instance.getPermissions()!!
            )
        }
    }


    fun setActivity(activity: Activity?) {
//        this.activity = activity;
        weakActivity = WeakReference(activity)
    }

    fun getPermissions(): Array<String>? {
        return permissions
    }

    fun setPermissions(permissions: Array<String>) {
        this.permissions = permissions
    }

    companion object {
        private val instance = MPermission()
        private val codes: MutableList<Int> = ArrayList()

        /**
         * 关联上下文
         *
         * @param activity
         * @return
         */
        fun with(activity: Activity): MPermission {
            instance.setActivity(activity)
            return instance
        }

        /**
         * 关联上下文
         *
         * @param fragment
         * @return
         */
        fun with(fragment: Fragment): MPermission {
            instance.setActivity(fragment.activity)
            return instance
        }

        /**
         * 关联上下文
         *
         * @param fragment
         * @return
         */
        fun with(fragment: androidx.fragment.app.Fragment): MPermission {
            instance.setActivity(fragment.activity)
            return instance
        }

        /**
         * @param requestCode
         * @param permissions
         * @param grantResults
         */
        fun onRequestPermissionResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
        ) {
            if (instance == null) {
                return
            }
            for (j in codes.indices) {
                if (requestCode == codes[j]) {
                    // 遍历请求时的所有权限
                    for (i in grantResults.indices) {
                        // 授权
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            instance.listener
                                ?.onPermit(codes[j], *permissions)
                        } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            instance.listener
                                ?.onCancel(codes[j], *permissions)
                        }
                    }
                    codes.remove(codes[j])
                }
            }
        }
    }
}