package cn.roy.permission.core.aspect

import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import cn.roy.permission.core.ContextHolder
import cn.roy.permission.core.annotations.PermissionApply
import cn.roy.permission.core.component.PermissionApplyParams
import cn.roy.permission.core.component.RequestPermissionActivity
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.lang.reflect.Method

/**
 * @Description: 权限申请切面
 * @Author: Roy
 * @Date: 2022/01/06
 * @Version: v1.0
 */
@Aspect
class PermissionApplyAspect {

    @Pointcut("execution(@cn.roy.permission.core.annotations.PermissionApply * *(..))")
    fun basePointcut() {
    }

    @Around("basePointcut()")
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        Log.d("Roy", "切面执行开始")
        val signature = joinPoint.signature
        var result: Any? = null
        if (signature is MethodSignature) {
            // this must be a call or execution join point
            val method: Method = signature.method
            val requestPermission = method.getAnnotation(PermissionApply::class.java)
            requestPermission?.let {
                val permissions = requestPermission.permissions
                val lackPermissionTip = requestPermission.lackPermissionTip
                val applyPermissionTip = requestPermission.applyPermissionTip
                val functionDescription = requestPermission.functionDescription
                val permissionDeniedPattern = requestPermission.permissionDeniedPattern
                // 检查权限是否被授予
                var hasAllPermission = true
                var lackPermissionList = arrayOf<String>()
                permissions?.let {
                    for (permission in it) {
                        if (ContextCompat.checkSelfPermission(
                                ContextHolder.context,
                                permission
                            ) == PackageManager.PERMISSION_DENIED
                        ) {
                            hasAllPermission = false
                            lackPermissionList = lackPermissionList.plus(permission)
                        }
                    }
                }
                if (hasAllPermission) {
                    // 已有权限，直接执行方法
                    result = joinPoint.proceed(joinPoint.args)
                } else {
                    // 无权限，自动进行权限申请，此时需要将执行参数进行包装
                    ContextHolder.setApplyPermissionCallback {
                        if (it) {
                            joinPoint.proceed(joinPoint.args)
                        } else {
                            // 无权限，什么也不做
                            Toast.makeText(
                                ContextHolder.context,
                                lackPermissionTip,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    val permissionApplyParams = PermissionApplyParams(
                        lackPermissionList,
                        lackPermissionTip,
                        applyPermissionTip,
                        functionDescription,
                        permissionDeniedPattern
                    )
                    RequestPermissionActivity.jump2PermissionGrantActivity(
                        ContextHolder.context,
                        permissionApplyParams
                    )
                    result = null
                }
            }
        } else {
            result = joinPoint.proceed(joinPoint.args)
        }
        Log.d("Roy", "切面执行完毕")
        return result
    }

}