package com.inz.utils

import android.util.Log

object DebugLog {
    private val TAG = "123"
    var bDubug:Boolean = true
    fun init(isDebug:Boolean){
        bDubug = isDebug
    }

    fun LogI(msg:String){
        if (!bDubug)return
        Log.i(TAG,msg)
    }

    fun LogE(msg:String){
        if (!bDubug)return
        Log.e(TAG,msg)
    }

    fun LogW(msg:String){
        if (!bDubug)return
        Log.w(TAG,msg)
    }

    fun LogD(msg:String){
        if (!bDubug)return
        Log.d(TAG,msg)
    }

    fun LogV(msg:String){
        if (!bDubug)return
        Log.v(TAG,msg)
    }
}