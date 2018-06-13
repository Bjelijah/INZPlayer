package com.inz.model

import android.content.Context
import com.inz.inzplayer.BasePlayer
import com.inz.inzplayer.LocalPlayer
import com.inz.model.main.MainViewModel

object ModelMgr {
    var mContext:Context               ?=null
    var mMainViewModel: MainViewModel  ?=null
    var mLocalPlayer:LocalPlayer       ?=null
    fun init(c:Context){
        mContext = c
    }

    fun getMainViewModelInstance(c:Context):MainViewModel{
        if (mMainViewModel==null){
            mMainViewModel = MainViewModel(c)
        }else{
            mMainViewModel?.setContext(c)
        }
        return mMainViewModel!!
    }

    fun getLocalPlayerInstance():LocalPlayer{
        if (mLocalPlayer==null){
            mLocalPlayer = LocalPlayer()
        }
        return mLocalPlayer!!
    }
}