package cn.roy.permission.core.aspect

import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import cn.roy.permission.core.ContextHolder
import cn.roy.permission.core.annotations.PermissionCheck
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.lang.reflect.Method

/**
 * @Description: 权限检查切面
 * @Author: Roy
 * @Date: 2022/01/06
 * @Version: v1.0
 */
@Aspect
class PermissionCheckAspect {

    @Pointcut("execution(@cn.roy.permission.core.annotations.PermissionCheck * *(..))")
    fun basePointcut() {
    }

    @Around("basePointcut()")
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        Log.d("Roy", "切面执行开始")
        val signature = joinPoint.signature
        var result: Any? = null
        if (signature is MethodSignature) {
            val method: Method = signature.method
            val requestPermission = method.getAnnotation(PermissionCheck::class.java)
            val permissions = requestPermission?.permissions
            val lackPermissionTip = requestPermission?.lackPermissionTip
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
                result = joinPoint.proceed(joinPoint.args)
            } else {
                result = null
                if (!TextUtils.isEmpty(lackPermissionTip)) {
                    Toast.makeText(
                        ContextHolder.context,
                        lackPermissionTip,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            result = joinPoint.proceed(joinPoint.args)
        }
        Log.d("Roy", "切面执行完毕")
        return result
    }

}