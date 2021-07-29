package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.lib_appupdate.AppUpdateHelper
import com.example.lib_appupdate.bean.AppInfo
import com.example.lib_appupdate.dialog.AppUpdateDialog
import com.example.lib_appupdate.net.INetCallback
import com.example.lib_appupdate.net.INetDownloadCallback
import com.example.lib_appupdate.utils.AppUtils
import com.example.lib_appupdate.utils.toast
import java.io.File

class MainActivity : AppCompatActivity() {

    private val URL = "http://59.110.162.30/app_updater_version.json"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        AppUpdateHelper.getNetManager().get(URL, object : INetCallback {

            override fun onSuccess(response: AppInfo?) {
                if (response != null) {
                    if (response.versionCode?.toLong() ?: 0 > AppUtils.getVersionCode(this@MainActivity)) {
                        AppUpdateDialog.show(this@MainActivity, response)
                    } else {
                        toast("已经最新版本")
                    }
                } else {
                    toast("更新版本失败")
                }
            }

            override fun onFailed(throwable: Throwable) {
                toast("更新版本失败")
            }

        }, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppUpdateHelper.getNetManager().cancel(this)
    }
}