package com.example.lib_appupdate.bean

import android.os.Parcelable
import org.json.JSONObject
import java.io.Serializable
import java.lang.Exception

/*
{
    "title":"4.5.0更新啦！",
    "content":"1. 优化了阅读体验；\n2. 上线了 hyman 的课程；\n3. 修复了一些已知问题。",
    "url":"http://59.110.162.30/v450_imooc_updater.apk",
    "md5":"14480fc08932105d55b9217c6d2fb90b",
    "versionCode":"450"
}
 */
 data class AppInfo(
    var content: String?,
    var md5: String?,
    var title: String?,
    var url: String?,
    var versionCode: String?,
):Serializable {

    companion object {
        fun parse(json: String?): AppInfo? {
            if (json.isNullOrEmpty())
                return null

            return try {
                val jsonObject = JSONObject(json)
                val content = jsonObject.optString("content")
                val md5 = jsonObject.optString("md5")
                val title = jsonObject.optString("title")
                val url = jsonObject.optString("url")
                val versionCode = jsonObject.optString("versionCode")
                AppInfo(content, md5, title, url, versionCode)
            } catch (e: Exception) {
                null
            }
        }
    }



}