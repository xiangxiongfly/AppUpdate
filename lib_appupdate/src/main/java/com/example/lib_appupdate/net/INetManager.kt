package com.example.lib_appupdate.net

import java.io.File

/**
 * 网络框架管理
 */
public interface INetManager {
    /**
     * 检查App版本
     */
    fun get(url: String, callback: INetCallback, tag: Any)

    /**
     * apk文件下载
     */
    fun download(url: String, targetFile: File, downloadCallback: INetDownloadCallback, tag: Any)

    /**
     * 取消下载
     */
    fun cancel(tag: Any)

}