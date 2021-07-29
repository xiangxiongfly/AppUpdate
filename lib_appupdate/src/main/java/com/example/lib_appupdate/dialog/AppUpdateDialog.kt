package com.example.lib_appupdate.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.lib_appupdate.AppUpdateHelper
import com.example.lib_appupdate.R
import com.example.lib_appupdate.bean.AppInfo
import com.example.lib_appupdate.net.INetDownloadCallback
import com.example.lib_appupdate.utils.AppUtils
import java.io.File

class AppUpdateDialog : DialogFragment() {

    private lateinit var title: TextView
    private lateinit var content: TextView
    private lateinit var update: Button
    private lateinit var cancel: Button

    private lateinit var appInfo: AppInfo

    companion object {
        fun show(activity: AppCompatActivity, appInfo: AppInfo) {
            val dialog = AppUpdateDialog()
            val bundle = Bundle()
            bundle.putSerializable("appInfo", appInfo)
            dialog.arguments = bundle
            dialog.show(activity.supportFragmentManager, "appUpdate")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appInfo = arguments?.getSerializable("appInfo") as AppInfo
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.dialog_app_update, null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = view.findViewById<TextView>(R.id.title)
        content = view.findViewById<TextView>(R.id.content)
        update = view.findViewById<Button>(R.id.update)
        cancel = view.findViewById<Button>(R.id.cancel)

        update.setOnClickListener {
            download()
        }

        cancel.setOnClickListener {
            dismiss()
        }

        init()
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val attributes = window?.attributes
        attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        window?.attributes = attributes
    }

    private fun init() {
        appInfo.let {
            title.text = it.title
            content.text = it.content
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        AppUpdateHelper.getNetManager().cancel(this@AppUpdateDialog)
    }

    private fun download() {
        update.isEnabled = false

        if (activity != null) {

            val file = File(activity!!.cacheDir, "download.apk")
            if (!file.exists()) {
                file.createNewFile()
            }

            AppUpdateHelper.getNetManager()
                .download(appInfo.url ?: "", file, object : INetDownloadCallback {
                    override fun onSuccess(apkFile: File) {
                        dismiss()
                        if (appInfo.md5.equals(AppUtils.md5(apkFile))) {
                            AppUtils.installApk(activity!!, apkFile)
                        } else {
                            Toast.makeText(activity, "下载失败", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onProgress(progress: Int) {
                        update.text = "进度：${progress}%"
                    }

                    override fun onFailed(throwable: Throwable) {
                        dismiss()
                        Toast.makeText(activity, "下载失败", Toast.LENGTH_SHORT).show()
                    }
                }, this@AppUpdateDialog)
        }
    }

}