package cn.roy.permission.core.annotations

/**
 * @Description: 埋点
 * @Author: Roy
 * @Date: 2022/01/06
 * @Version: v1.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BuryPoint(
    val pageName: String = "",
    val pageChannel: String = "",
    val pageChannel2: String = "",
    val buttonName: String = "",
    val operationPosition: String = "",
    val operationPositionId: String = "",
    val productType: String = "",
    val productNumber: String = "",
    val recommendMethod: String = "",
    val custGroupTag: String = "",
    val if_success: String = "",
    val resultMessage: String = "",
    val keyWord: String = "",
    val searchType: String = "",
    val successOrNotBool: Boolean = false,
    val phone: String = "",
    val input_item: String = "",
)