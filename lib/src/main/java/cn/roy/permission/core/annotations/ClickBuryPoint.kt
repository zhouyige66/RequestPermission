package cn.roy.permission.core.annotations

/**
 * @Description: 埋点
 * @Author: Roy
 * @Date: 2022/01/06
 * @Version: v1.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ClickBuryPoint(
    val pageName: String = "",
    val pageChannel: String = "",
    val buttonName: String = "",
)