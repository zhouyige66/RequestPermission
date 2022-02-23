package cn.roy.permission.core.helper

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import cn.roy.permission.core.ContextHolder
import cn.roy.permission.core.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * @Description: 请求权限Activity
 * @Author: Roy
 * @Date: 2022/01/14
 * @Version: v1.0
 */
class RequestPermissionActivity : Activity() {
    companion object {
        const val PERMISSION_REQUEST_CODE = 5678// 系统权限管理页面请求code
        const val PERMISSION_APPLY_PARAMS = "permissionsApplyParams"// 请求权限传递参数

        /**
         * 启动授权页面
         *
         * @param context Context
         * @param permissionApplyParams PermissionApplyParams
         */
        fun jump2PermissionGrantActivity(
            context: Context,
            permissionApplyParams: PermissionApplyParams,
        ) {
            val intent = Intent(context, RequestPermissionActivity::class.java).apply {
                this.putExtra(PERMISSION_APPLY_PARAMS, permissionApplyParams)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var permissionApplyParams: PermissionApplyParams
    private var isNeedCheckPermission = false// 是否需要检查权限状态
    private var tipDialog: BottomSheetDialog? = null// 提示对话框

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionApplyParams = intent.getParcelableExtra(PERMISSION_APPLY_PARAMS)!!
        if (permissionApplyParams.permissions.isNullOrEmpty()) {
            applyPermissionFinish(true)
        } else {
            if (permissionApplyParams.applyPermissionTip.isNullOrBlank()) {
                applyPermissions()
            } else {
                showApplyPermissionTip()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 从设置页面返回后，需要检查权限是否已被授予
        if (isNeedCheckPermission) {
            if (PermissionUtil.allPermissionGranted(this, permissionApplyParams.permissions)) {
                applyPermissionFinish(true)
            } else {
                // 此时用户已从设置页面返回，但是权限仍未被禁止
                applyPermissionFinish(false)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            checkPermissions()
        }
    }

    /**
     * 申请授权结束
     *
     * @param success Boolean
     */
    private fun applyPermissionFinish(success: Boolean) {
        ContextHolder.invokeApplyPermissionCallback(success)
        finish()
    }

    /**
     * 检查权限状态
     */
    private fun checkPermissions() {
        var hasPermissionDeniedAndDotAskAgain = false
        val lackPermissionList = arrayListOf<String>()
        permissionApplyParams.permissions.forEach { permission ->
            val status = PermissionUtil.checkPermissionStatus(this, permission)

            /**
             * 1.应用第一次安装，并且权限被禁用时，返回true
             * 2.权限被禁用时，返回true
             * 3.权限被禁用且不再提示时，返回false
             * 4.已授权时返回false
             */
            val shouldShowRequestPermissionRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
            if (status != PackageManager.PERMISSION_GRANTED) {
                lackPermissionList.add(PermissionUtil.getPermissionName(permission))
                if (!shouldShowRequestPermissionRationale) {
                    hasPermissionDeniedAndDotAskAgain = true
                }
            }
        }
        if (lackPermissionList.isNotEmpty()) {
            showLackPermissionTip(lackPermissionList)
        } else {
            applyPermissionFinish(true)
        }
    }

    /**
     * 申请授权
     */
    private fun applyPermissions() {
        ActivityCompat.requestPermissions(
            this,
            permissionApplyParams.permissions,
            PERMISSION_REQUEST_CODE
        )
    }

    /**
     * 初始化提示对话框
     */
    private fun initTipDialog() {
        if (tipDialog == null) {
            val view = LayoutInflater.from(this).inflate(R.layout.activity_request_permission, null)
            tipDialog = BottomSheetDialog(this, R.style.BottomSheetDialogExt).apply {
                this.setContentView(view)
                val parentView = this.delegate.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
                parentView?.setBackgroundColor(Color.TRANSPARENT)
                val mDialogBehavior = BottomSheetBehavior.from(view.parent as View)
                mDialogBehavior.isHideable = true
                this.setCancelable(false)// 点击外部不可消失
            }
        }
    }

    /**
     * 显示申请权限提示
     */
    private fun showApplyPermissionTip() {
        if (tipDialog == null) {
            initTipDialog()
        }
        tipDialog!!.let {
            val tvTitle: TextView = it.findViewById(R.id.tvTitle)!!
            val tvMsg: TextView = it.findViewById(R.id.tvMsg)!!
            val btnLeft: Button = it.findViewById(R.id.btn_left)!!
            val btnRight: Button = it.findViewById(R.id.btn_right)!!
            tvTitle.text = "重要提示"
            tvMsg.text = permissionApplyParams.applyPermissionTip
            btnLeft.text = "去授权"
            btnRight.text = "退出"
            btnLeft.setOnClickListener {
                tipDialog?.hide()
                applyPermissions()
            }
            btnRight.setOnClickListener {
                tipDialog?.dismiss()
                applyPermissionFinish(false)
            }
            it.show()
        }
    }

    /**
     * 显示权限被禁止提示
     *
     * @param lackPermissionList List<String>
     */
    private fun showLackPermissionTip(lackPermissionList: List<String>) {
        if (tipDialog == null) {
            initTipDialog()
        }
        tipDialog!!.let {
            val tvTitle: TextView = it.findViewById(R.id.tvTitle)!!
            val tvMsg: TextView = it.findViewById(R.id.tvMsg)!!
            val tvMsg2: TextView = it.findViewById(R.id.tvMsg2)!!
            tvTitle.text = "温馨提示"
            if (permissionApplyParams.permissionDeniedPattern.isNotBlank()) {
                tvMsg.text = String.format(
                    permissionApplyParams.permissionDeniedPattern,
                    lackPermissionList.joinToString("、")
                )
            } else {
                if (permissionApplyParams.functionDescription.isNotBlank()) {
                    tvMsg.text =
                        "${permissionApplyParams.functionDescription}功能可能无法正常工作，由于以下访问权限：${
                            lackPermissionList.joinToString("、")
                        }权限被禁止。"
                } else {
                    tvMsg.text = "以下访问权限：${lackPermissionList.joinToString("、")}权限被禁止，相应功能可能不能正常工作。"
                }
            }
            tvMsg2.isVisible = true
            val btnLeft: Button = it.findViewById(R.id.btn_left)!!
            val btnRight: Button = it.findViewById(R.id.btn_right)!!
            btnLeft.text = "去开启"
            btnRight.text = "返回"
            btnLeft.setOnClickListener {
                tipDialog?.hide()
                jumpToAppSettings()
            }
            btnRight.setOnClickListener {
                tipDialog?.dismiss()
                applyPermissionFinish(false)
            }
            it.show()
        }
    }

    /******************************REMARK 功能：跳转应用详情设置页面******************************/
    /**
     * 进入应用设置页面
     */
    private fun jumpToAppSettings() {
        isNeedCheckPermission = true
        val sdk = Build.VERSION.SDK// SDK号
        val model = Build.MODEL// 手机型号
        val release = Build.VERSION.RELEASE// android系统版本号
        val brand = Build.BRAND// 手机厂商
        if (TextUtils.equals(brand.toLowerCase(), "redmi")
            || TextUtils.equals(brand.toLowerCase(), "xiaomi")
        ) {
            gotoMiuiPermission()
        } else if (TextUtils.equals(brand.toLowerCase(), "meizu")) {
            gotoMeizuPermission()
        } else if (TextUtils.equals(brand.toLowerCase(), "huawei")
            || TextUtils.equals(brand.toLowerCase(), "honor")
        ) {
            gotoHuaweiPermission()
        } else {
            gotoAppDetailSettingPage()
        }
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private fun gotoMiuiPermission() {
        try { // MIUI 8
            val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
            localIntent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            localIntent.putExtra("extra_pkgname", packageName)
            startActivity(localIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            try { // MIUI 5/6/7
                val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                localIntent.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
                )
                localIntent.putExtra("extra_pkgname", packageName)
                startActivity(localIntent)
            } catch (e1: Exception) {
                e1.printStackTrace()
                gotoAppDetailSettingPage()
            }
        }
    }

    /**
     * 跳转到魅族的权限管理页面
     */
    private fun gotoMeizuPermission() {
        try {
            val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra("packageName", packageName)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            gotoAppDetailSettingPage()
        }
    }

    /**
     * 华为的权限管理页面
     */
    private fun gotoHuaweiPermission() {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val comp = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.permissionmanager.ui.MainActivity"
            ) //华为权限管理
            intent.component = comp
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            gotoAppDetailSettingPage()
        }
    }

    /**
     * 进入应用详情页面
     */
    private fun gotoAppDetailSettingPage() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}