package com.example.lib_appupdate

import com.example.lib_appupdate.net.INetManager
import com.example.lib_appupdate.netimpl.OkHttpNetManager

object AppUpdateHelper {

    private var netManager: INetManager = getDefaultNetManager()

    fun getDefaultNetManager() = OkHttpNetManager()

    fun getNetManager(): INetManager {
        return netManager
    }

    fun setNetManager(netManager: INetManager) {
        this.netManager = netManager
    }

}