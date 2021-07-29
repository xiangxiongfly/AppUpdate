package com.example.myapplication

import android.app.Application

class BaseApp : Application() {

    companion object {
        private lateinit var instance: BaseApp

        fun getIntance(): BaseApp {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}