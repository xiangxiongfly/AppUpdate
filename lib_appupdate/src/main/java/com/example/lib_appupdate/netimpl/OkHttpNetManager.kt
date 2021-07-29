package com.example.lib_appupdate.netimpl

import android.os.Handler
import android.os.Looper
import com.example.lib_appupdate.bean.AppInfo
import com.example.lib_appupdate.net.INetCallback
import com.example.lib_appupdate.net.INetDownloadCallback
import com.example.lib_appupdate.net.INetManager
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class OkHttpNetManager : INetManager {

    private val handler = Handler(Looper.getMainLooper())

    private val okHttpClient by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
        client
    }

    override fun get(url: String, callback: INetCallback, tag: Any) {

        val request = Request.Builder()
            .url(url)
            .tag(tag)
            .build()
        val call = okHttpClient.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                handler.post {
                    callback.onFailed(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val appInfo = AppInfo.parse(result)
                handler.post {
                    callback.onSuccess(appInfo)
                }
            }
        })
    }

    override fun download(
        url: String,
        targetFile: File,
        downloadCallback: INetDownloadCallback,
        tag: Any,
    ) {

        val request = Request.Builder()
            .url(url)
            .tag(tag)
            .build()
        val call = okHttpClient.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                handler.post {
                    downloadCallback.onFailed(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val inputStream: InputStream? = response.body?.byteStream()
                val fileOutputStream = FileOutputStream(targetFile)
                val totalSize = response.body?.contentLength() ?: 0
                var downloadSize = 0L

                var len = 0
                val buffer = ByteArray(4 * 1024)

                try {
                    while (!call.isCanceled() &&
                        ((inputStream?.read(buffer) ?: -1).also { len = it }) != -1
                    ) {
                        fileOutputStream.write(buffer, 0, len)
                        fileOutputStream.flush()
                        downloadSize += len
                        handler.post {
                            downloadCallback.onProgress((downloadSize * 1F / totalSize * 100).toInt())
                        }
                    }

                    if (call.isCanceled()) {
                        return
                    }

                    targetFile.setExecutable(true)
                    targetFile.setReadable(true)
                    targetFile.setWritable(true)
                    handler.post {
                        downloadCallback.onSuccess(targetFile)
                    }
                } catch (e: Exception) {
                    if (call.isCanceled()) {
                        return
                    }
                    downloadCallback.onFailed(e)
                } finally {
                    inputStream?.close()
                    fileOutputStream.close()
                }
            }

        })
    }

    override fun cancel(tag: Any) {
        val queuedCalls = okHttpClient.dispatcher.queuedCalls()
        for (call in queuedCalls) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
        val runningCalls = okHttpClient.dispatcher.runningCalls()
        for (call in runningCalls) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
    }
}