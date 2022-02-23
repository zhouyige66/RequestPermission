package cn.roy.permission

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.roy.permission.core.ContextHolder
import cn.roy.permission.core.annotations.ClickBuryPoint
import cn.roy.permission.core.annotations.PermissionApply
import cn.roy.permission.core.annotations.PermissionCheck

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ContextHolder.context = this
    }

    @ClickBuryPoint("首页", "测试", "埋点测试")
    fun buryPoint(view: View) {
        val name = view.context.javaClass.name
        val id = view.id
        Log.d("Roy", "点击view context:$name,id:$id")
        Toast.makeText(this, "埋点测试", Toast.LENGTH_SHORT).show()
    }

    @PermissionCheck(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
    )
    fun permissionCheck01(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

    @PermissionCheck(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
        lackPermissionTip = "由于相应权限被禁止，视频通话将不可用",
    )
    fun permissionCheck02(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

    @PermissionApply(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
        lackPermissionTip = "由于相应权限被禁止，视频通话将不可用",
    )
    fun permissionApply01(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

    @PermissionApply(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
        lackPermissionTip = "由于相应权限被禁止，视频通话将不可用",
        applyPermissionTip = "由于视频通话需要使用相机、拨号盘，所以需要申请相机、电话权限。",
    )
    fun permissionApply02(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

    @PermissionApply(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
        lackPermissionTip = "由于相应权限被禁止，视频通话将不可用",
        applyPermissionTip = "由于视频通话需要使用相机、拨号盘，所以需要申请相机、电话权限。",
        functionDescription = "视频通话",
    )
    fun permissionApply03(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

    @PermissionApply(
        permissions = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ],
        lackPermissionTip = "由于相应权限被禁止，视频通话将不可用",
        applyPermissionTip = "由于视频通话需要使用相机、拨号盘，所以需要申请相机、电话权限。",
        functionDescription = "视频通话",
        permissionDeniedPattern = "显示自定义提示：视频通话功能由于%s权限被禁止，无法使用",
    )
    fun permissionApply04(view: View) {
        Toast.makeText(this, "执行业务逻辑", Toast.LENGTH_SHORT).show()
    }

}