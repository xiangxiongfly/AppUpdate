package com.example.lib_appupdate.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

object AppUtils {
    /**
     * 获取App版本号
     */
    fun getVersionCode(context: Context): Long {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return packageInfo.longVersionCode
        } else {
            return packageInfo.versionCode.toLong()
        }
    }

    /**
     * 文件生产MD5值
     */
    fun md5(file: File): String? {
        var digest: MessageDigest? = null
        var inputStream: FileInputStream? = null
        return try {
            digest = MessageDigest.getInstance("MD5")
            inputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            var len = 0
            while (inputStream.read(buffer).also { len = it } != -1) {
                digest.update(buffer, 0, len)
            }
            val result = digest.digest()
            //转为16进制
            BigInteger(result).toString(16)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                inputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 安装apk
     */
    fun installApk(activity: Activity, apkFile: File) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setAction(Intent.ACTION_VIEW)

        var uri: Uri?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(activity,
                "${activity.packageName}.fileprovider",
                apkFile)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(apkFile)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        activity.startActivity(intent)
    }
}