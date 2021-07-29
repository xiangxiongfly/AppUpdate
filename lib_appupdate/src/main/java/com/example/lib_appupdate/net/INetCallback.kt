package com.example.lib_appupdate.net

import com.example.lib_appupdate.bean.AppInfo

/**
 * 版本接口请求回调
 */
public interface INetCallback {
    fun onSuccess(response: AppInfo?)

    fun onFailed(throwable: Throwable)
}