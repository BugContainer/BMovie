package com.bugli.bmovie.util.permission

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.app.AppOpsManagerCompat
import androidx.core.content.ContextCompat
import com.bugli.bmovie.util.DeviceUtils

/**
 * Description：
 * Created by kang on 2017/10/26.
 */
class MPermissionUtils private constructor() {
    /**
     * 检查权限检查
     *
     * @param context
     * @param permissions
     * @return
     */
    fun checkPermission(
        context: Context,
        vararg permissions: String?
    ): Boolean {
        for (permission in permissions) {
            // 判断当前该权限是否允许
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        for (permission in permissions) {
            val op = AppOpsManagerCompat.permissionToOp(permission!!)
            if (TextUtils.isEmpty(op)) continue
            var result = AppOpsManagerCompat.noteProxyOp(context, op!!, context.packageName)
            if (result == AppOpsManagerCompat.MODE_IGNORED) {
                return false
            }
            result = ContextCompat.checkSelfPermission(context, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 权限请求方法
     *
     * @param activity
     * @param code
     * @param permissions
     */
    fun requestPermission(
        activity: Activity?,
        code: Int,
        vararg permissions: String?
    ) {
        ActivityCompat.requestPermissions(activity!!, permissions, code)
    }

    companion object {
        @JvmStatic
        @Volatile
        var instance: MPermissionUtils? = null
            get() {
                if (field == null) {
                    synchronized(
                        MPermissionUtils::class.java
                    ) {
                        if (field == null) field =
                            MPermissionUtils()
                    }
                }
                return field
            }
            private set

        /**
         * 判断是否需要动态申请权限
         *
         * @return
         */
        fun needRequestPermission(): Boolean {
            return Build.VERSION.SDK_INT >= 23
        }

        /**
         * 去设置界面
         */
        fun goSetting(context: Context) {
            val dialog = AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("您已禁止了权限，是否要去开启？")
                .setPositiveButton("立即前往") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    getAppDetailSettingIntent(context)
                }
                .setNegativeButton("以后再说") { dialogInterface, i -> dialogInterface.dismiss() }
                .create()
            dialog.setCancelable(false)
            dialog.show()
        }

        /**
         * 跳转到权限设置界面
         */
        private fun getAppDetailSettingIntent(context: Context) {
            var intent: Intent? = null
            if (DeviceUtils.manufacturer == "huawei") {
                intent = huaweiApi(context)
            } else if (DeviceUtils.manufacturer == "xiaomi") {
                intent = xiaomiApi(context)
            } else if (DeviceUtils.manufacturer == "oppo") {
                intent = oppoApi(context)
            } else if (DeviceUtils.manufacturer == "vivo") {
                intent = vivoApi(context)
            } else if (DeviceUtils.manufacturer == "samsung") {
                intent = samsungApi(context)
            } else if (DeviceUtils.manufacturer == "meizu") {
                intent = meizuApi(context)
            } else if (DeviceUtils.manufacturer == "smartisan") {
                intent = smartisanApi(context)
            }
            intent = defaultApi(context)
            context.startActivity(intent)
        }

        /**
         * App details page.
         */
        private fun defaultApi(context: Context): Intent {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.fromParts("package", context.packageName, null)
            return intent
        }

        /**
         * Huawei cell phone Api23 the following method.
         */
        private fun huaweiApi(context: Context): Intent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return defaultApi(context)
            }
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.component = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.permissionmanager.ui.MainActivity"
            )
            return intent
        }

        /**
         * Xiaomi phone to achieve the method.
         */
        private fun xiaomiApi(context: Context): Intent {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("extra_pkgname", context.packageName)
            return intent
        }

        /**
         * Vivo phone to achieve the method.
         */
        private fun vivoApi(context: Context): Intent {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("packagename", context.packageName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                intent.component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"
                )
            } else {
                intent.component = ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"
                )
            }
            return intent
        }

        /**
         * Oppo phone to achieve the method.
         */
        private fun oppoApi(context: Context): Intent {
            return defaultApi(context)
        }

        /**
         * Meizu phone to achieve the method.
         */
        private fun meizuApi(context: Context): Intent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                return defaultApi(context)
            }
            val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("packageName", context.packageName)
            intent.component = ComponentName(
                "com.meizu.safe",
                "com.meizu.safe.security.AppSecActivity"
            )
            return intent
        }

        /**
         * Smartisan phone to achieve the method.
         */
        private fun smartisanApi(context: Context): Intent {
            return defaultApi(context)
        }

        /**
         * Samsung phone to achieve the method.
         */
        private fun samsungApi(context: Context): Intent {
            return defaultApi(context)
        }
    }
}