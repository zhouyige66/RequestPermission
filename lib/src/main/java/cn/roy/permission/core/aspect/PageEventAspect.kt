package cn.roy.permission.core.aspect

import android.util.Log
import cn.roy.permission.core.annotations.PageBuryPoint
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.Signature
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method

/**
 * @Description: 页面埋点切面
 * @Author: Roy
 * @Date: 2022/01/06
 * @Version: v1.0
 */
@Aspect
class PageEventAspect {

    @Pointcut("@annotation(cn.roy.permission.core.annotations.PageBuryPoint)")
    fun pageEnterPointcut() {
    }

    @Before("pageEnterPointcut()")
    fun pageEnteredBefore(joinPoint: JoinPoint) {
        val signature: Signature = joinPoint.signature
        val key: String = signature.toString()
        Log.d("Roy", "pageEnter: $key")
        val declaringType: AnnotatedElement = signature.declaringType
        Log.d("Roy","declaringType=${declaringType.toString()}")
        if (signature is MethodSignature) {
            // this must be a call or execution join point
            val method: Method = signature.method
            val pageBuryPoint = method.getAnnotation(PageBuryPoint::class.java)
            val pageName = pageBuryPoint?.pageName
            val pageChannel = pageBuryPoint?.pageChannel
            Log.d("Roy", "埋点参数，pageName=${pageName}，pageChannel=${pageChannel}")
        }
    }

}