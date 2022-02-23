package cn.roy.permission.core.aspect

import android.util.Log
import cn.roy.permission.core.annotations.ClickBuryPoint
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method

/**
 * @Description: 点击事件埋点切面
 * @Author: Roy
 * @Date: 2022/01/06
 * @Version: v1.0
 */
@Aspect
class ClickEventAspect {

    /**
     * Note:
     * 1.注解使用全路径名称
     * 2.指定Pointcut必须为execution或call，因为直接使用@annotation，会调用不止一次
     */
    @Pointcut("execution(@cn.roy.permission.core.annotations.ClickBuryPoint * *(..))")
    fun clickMethodPointcut() {
    }

    @Before("clickMethodPointcut()")
    fun clickMethodBefore(joinPoint: JoinPoint) {
        val signature = joinPoint.signature
        val key: String = signature.toString()
        Log.d("Roy", "clickMethodBefore: $key")
        val declaringType: AnnotatedElement = signature.declaringType
        Log.d("Roy", "declaringType=${declaringType}")
        if (signature is MethodSignature) {
            // this must be a call or execution join point
            val method: Method = signature.method
            val clickBuryPoint = method.getAnnotation(ClickBuryPoint::class.java)
            val pageName = clickBuryPoint?.pageName
            val pageChannel = clickBuryPoint?.pageChannel
            val buttonName = clickBuryPoint?.buttonName
            Log.d(
                "Roy", "埋点参数，pageName=${pageName}，pageChannel=${pageChannel}，" +
                        "buttonName=$buttonName"
            )
        }
    }

}