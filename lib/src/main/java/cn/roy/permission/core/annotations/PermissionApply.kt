package cn.roy.permission.core.annotations

/**
 * @Description: 权限申请
 * @Author: Roy
 * @Date: 2022/01/10
 * @Version: v1.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PermissionApply(
    /**
     * 权限集合
     */
    val permissions: Array<String> = [],
    /**
     * 权限被拒绝后的提示语
     * note:如果为空或空字符串，则表示不提示
     */
    val lackPermissionTip: String = "",
    /**
     * 权限申请提示信息
     * note:如果不为空（空字符串），则先显示提示语，否则直接申请授权
     */
    val applyPermissionTip: String = "",
    /**
     * 功能描述
     * note:当权限被拒绝后，显示的提示语"***功能可能无法正常工作，由于以下访问权限：xxx权限被禁止"
     */
    val functionDescription: String = "",
    /**
     * 权限被拒绝后显示的提示语，优先级高于[functionDescription]组装
     * note:使用案例"自定义头像功能可能无法正常工作，由于以下权限：%s被禁止"，其中%s会被被禁止的权限填充
     */
    val permissionDeniedPattern: String = ""

)
