package cn.roy.permission.core.component

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Description: 权限申请参数
 * @Author: Roy
 * @Date: 2022/01/20
 * @Version: v1.0
 */
@Parcelize
data class PermissionApplyParams(
    var permissions: Array<String>,
    var lackPermissionTip: String,
    var applyPermissionTip: String,
    var functionDescription: String,
    var permissionDeniedPattern: String
):Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionApplyParams

        if (!permissions.contentEquals(other.permissions)) return false
        if (lackPermissionTip != other.lackPermissionTip) return false
        if (applyPermissionTip != other.applyPermissionTip) return false
        if (functionDescription != other.functionDescription) return false
        if (permissionDeniedPattern != other.permissionDeniedPattern) return false

        return true
    }

    override fun hashCode(): Int {
        var result = permissions.contentHashCode()
        result = 31 * result + lackPermissionTip.hashCode()
        result = 31 * result + applyPermissionTip.hashCode()
        result = 31 * result + functionDescription.hashCode()
        result = 31 * result + permissionDeniedPattern.hashCode()
        return result
    }
}