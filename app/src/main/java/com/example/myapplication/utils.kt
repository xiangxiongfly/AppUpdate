package com.example.lib_appupdate.utils

import android.util.Log
import android.widget.Toast
import com.example.myapplication.BaseApp

fun log(msg: String) {
    Log.e("net", msg)
}

fun toast(msg: String) {
    Toast.makeText(BaseApp.getIntance(), msg, Toast.LENGTH_SHORT).show()
}