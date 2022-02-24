package cn.roy.permission.core.component

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import androidx.core.content.PermissionChecker

/**
 * @Description:
 * @Author: Roy
 * @Date: 2022/01/17
 * @Version: v1.0
 */
object PermissionUtil {

    private val runtimePermissionMap = mapOf(
        Manifest.permission.READ_CALENDAR to "日历（读取日历数据）",
        Manifest.permission.WRITE_CALENDAR to "日历（写入日历数据）",
        Manifest.permission.CAMERA to "相机",
        Manifest.permission.READ_CONTACTS to "通讯录（读取联系人数据）",
        Manifest.permission.WRITE_CONTACTS to "通讯录（添加联系人数据）",
        Manifest.permission.GET_ACCOUNTS to "通讯录（访问账户服务）",
        Manifest.permission.ACCESS_FINE_LOCATION to "位置信息",
        Manifest.permission.ACCESS_COARSE_LOCATION to "位置信息",
        Manifest.permission.RECORD_AUDIO to "麦克风",
        Manifest.permission.READ_PHONE_STATE to "电话（获取设备信息）",
        Manifest.permission.CALL_PHONE to "电话（拨打电话）",
        Manifest.permission.READ_CALL_LOG to "电话（读取通话记录）",
        Manifest.permission.WRITE_CALL_LOG to "电话（写入通话记录）",
        Manifest.permission.ADD_VOICEMAIL to "电话（将语音添加邮件中）",
        Manifest.permission.USE_SIP to "电话（使用SIP服务）",
        Manifest.permission.PROCESS_OUTGOING_CALLS to "电话（查看正在通话号码）",
        Manifest.permission.BODY_SENSORS to "传感器",
        Manifest.permission.SEND_SMS to "短信（发送短信）",
        Manifest.permission.RECEIVE_SMS to "短信（接收短信）",
        Manifest.permission.READ_SMS to "短信（读取短信）",
        Manifest.permission.RECEIVE_WAP_PUSH to "短信（接收WAP推送消息）",
        Manifest.permission.RECEIVE_MMS to "短信（监控彩信信息）",
        Manifest.permission.READ_EXTERNAL_STORAGE to "存储（读写设备的照片、文件等）",
        Manifest.permission.WRITE_EXTERNAL_STORAGE to "存储（读写设备的照片、文件等）",
        Manifest.permission.ACTIVITY_RECOGNITION to "身体活动识别",
        Manifest.permission.ACCESS_BACKGROUND_LOCATION to "位置信息",
        Manifest.permission.READ_PHONE_NUMBERS to "电话（获取电话号码）",
        Manifest.permission.ANSWER_PHONE_CALLS to "电话（接通电话）",
        Manifest.permission.ACCESS_MEDIA_LOCATION to "位置信息",
        Manifest.permission.ACCEPT_HANDOVER to "呼叫转移",
//        Manifest.permission.UWB_RANGING to "使用超宽带的设备",
//        Manifest.permission.BLUETOOTH_ADVERTISE to "蓝牙",
//        Manifest.permission.BLUETOOTH_CONNECT to "蓝牙",
//        Manifest.permission.BLUETOOTH_SCAN to "蓝牙",
    )

    /**
     * 根据权限查询权限功能描述
     *
     * @param permission String
     * @return String
     */
    fun getPermissionName(permission: String): String {
        return runtimePermissionMap[permission] ?: "其他"
    }

    /**
     * 判断权限集合
     *
     * @param context
     * @param permissions
     * @return true-已授权，false-未授权
     */
    fun allPermissionGranted(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (checkPermissionStatus(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun checkPermissionStatus(context: Context, permission: String): Int {
        /**
         * 1.Context.checkSelfPermission(String permission)
         *  自调用Context.checkPermission(@NonNull String permission, int pid, int uid)
         * 2.PermissionChecker.checkSelfPermission(@NonNull Context context,@NonNull String permission)
         *  首先Context.checkPermission(@NonNull String permission, int pid, int uid)，如果权限没被拒绝，
         *  则会调用AppOpsManagerCompat.permissionToOp(@NonNull String permission)，要求VERSION>=23
         * 3.ContextCompat.checkSelfPermission(String permission)
         *  自调用Context.checkPermission(@NonNull String permission, int pid, int uid)
         */
        // 以23版本为界（Build.VERSION_CODES.M）
        val targetSDKVersion = getTargetSDKVersion(context)
        var permissionStatus = PackageManager.PERMISSION_GRANTED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// Android 6.0及以上
            permissionStatus = if (targetSDKVersion >= 23) {// 动态授权，检测方案不同
                context.checkSelfPermission(permission)
            } else {// 使用兼容类PermissionChecker
                PermissionChecker.checkSelfPermission(context, permission)
            }
            // 设置页面开启或关闭权限后，不是立刻就能读到权限授权情况
            if (permissionStatus == PackageManager.PERMISSION_DENIED) {
                permissionStatus = checkPermissionStatus2(context, permission)
            }
        }
        return permissionStatus
    }

    private fun checkPermissionStatus2(context: Context, permission: String): Int {
        val aom: AppOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        var status = PackageManager.PERMISSION_GRANTED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppOpsManager.permissionToOp(permission)?.let {
                val result = aom.checkOp(it, Binder.getCallingUid(), context.packageName)
                status = if (result == AppOpsManager.MODE_ALLOWED
                    || result == AppOpsManager.MODE_FOREGROUND
                ) {
                    PackageManager.PERMISSION_GRANTED
                } else {
                    PackageManager.PERMISSION_DENIED
                }
            }
        }
        return status
    }

    private fun getTargetSDKVersion(context: Context): Int {
        var targetSDKVersion = 0
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName, 0
            )
            targetSDKVersion = info.applicationInfo.targetSdkVersion
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return targetSDKVersion
    }
}