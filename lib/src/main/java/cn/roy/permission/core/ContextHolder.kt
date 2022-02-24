package cn.roy.permission.core

import android.annotation.SuppressLint
import android.content.Context

/**
 * @Description:
 * @Author: Roy
 * @Date: 2022/01/14
 * @Version: v1.0
 */
@SuppressLint("StaticFieldLeak")
internal object ContextHolder {

    lateinit var context: Context
    private var callback: ((allPermissionGranted: Boolean) -> Unit)? = null

    internal fun setApplyPermissionCallback(callback: ((allPermissionGranted: Boolean) -> Unit)?) {
        ContextHolder.callback = callback
    }

    internal fun invokeApplyPermissionCallback(granted: Boolean) {
        callback?.invoke(granted)
        callback = null
    }

}