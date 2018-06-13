package com.inz.model.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.ObservableField
import android.os.Build
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.inz.inzplayer.BasePlayer
import com.inz.inzplayer.R
import com.inz.model.BaseViewModel
import com.inz.model.ModelMgr
import io.reactivex.functions.Action

class MainViewModel(private var mContext:Context): BaseViewModel {

    val mGestureDetector = GestureDetector(mContext,object :GestureDetector.SimpleOnGestureListener(){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            //todo  ctrl bar visibility
            return super.onSingleTapUp(e)
        }
    })
    val mWaitVisibility  = ObservableField<Int>(View.VISIBLE)
    val mCtrlVisibility  = ObservableField<Int>(View.VISIBLE)
    val mTitleVisibility = ObservableField<Int>(View.VISIBLE)
    val mStateVisibility = ObservableField<Int>(View.GONE)
    val mCtrlBeg         = ObservableField<String>("00:00:00")
    val mCtrlEnd         = ObservableField<String>("00:00:00")
    val mTitleText       = ObservableField<String>(mContext.getString(R.string.title_file_no))
    val mPlayState       = ObservableField<Boolean>(true)
    var mActivity :Activity ?= null
    var mPlayer:BasePlayer  ?= null


    fun setContext(c:Context){
        mContext = c
    }
    fun setActivity(activity:Activity){
        mActivity = activity
    }

    override fun onCreate() {
    }

    override fun onDestory() {
    }

    fun setUriAndPlay(path:String){
        var title = path.split("/")
        mTitleText.set(title[title.lastIndex])
        mPlayer = ModelMgr.getLocalPlayerInstance()
                .setURI(path)
                .registListener({bInit->

                },{bDeinit->

                },{bPlay->

                },{bStop->

                })
                .setURI(path)
                .init(-1,path)



    }


    fun onGlTouch(v: View,event:MotionEvent ):Boolean{
        return mGestureDetector.onTouchEvent(event)
    }

    val onFileOpenClick = Action{
        Log.i("123","onFileOpenClick")
        if (isGrantExternalRW(mActivity)) {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            mActivity?.startActivityForResult(intent,1)
        }
    }



    private fun isGrantExternalRW(activity:Activity?):Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity?.checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            activity?.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
            return true
        }
        return true
    }
}