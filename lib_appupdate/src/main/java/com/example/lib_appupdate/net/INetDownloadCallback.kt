package com.example.lib_appupdate.net

import java.io.File

/**
 * 文件下载回调
 */
public interface INetDownloadCallback {
    fun onSuccess(apkFile: File)

    fun onProgress(progress: Int)

    fun onFailed(throwable: Throwable)
}