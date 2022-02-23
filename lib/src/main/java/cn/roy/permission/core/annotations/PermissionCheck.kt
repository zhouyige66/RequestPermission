package cn.roy.permission.core.annotations

/**
 * @Description: 权限检查
 * @Author: Roy
 * @Date: 2022/01/10
 * @Version: v1.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PermissionCheck(
    /**
     * 权限集合
     */
    val permissions: Array<String> = [],
    /**
     * 权限被拒绝后的提示语，如果为空或空字符串，则表示不提示
     */
    val lackPermissionTip: String = "",
)
