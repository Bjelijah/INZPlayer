package com.inz.model.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.ObservableField
import android.databinding.adapters.SeekBarBindingAdapter
import android.os.Build
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.howellsdk.api.ApiManager
import com.howellsdk.api.HWPlayApi
import com.howellsdk.api.player.ZoomableTextureView
import com.howellsdk.utils.RxUtil
import com.howellsdk.utils.ThreadUtil
import com.inz.action.Config
import com.inz.inzplayer.BasePlayer
import com.inz.inzplayer.R
import com.inz.model.BaseViewModel
import com.inz.model.ModelMgr
import com.inz.utils.Utils
import io.reactivex.Observable
import io.reactivex.functions.Action
import java.util.concurrent.TimeUnit

class MainViewModel(private var mContext:Context): BaseViewModel {
    val PLAY_STATE_PLAY = 0x01
    val PLAY_STATE_STOP = 0x02
    private var mLastClickTime = 0L
    val MIN_CLICK_DELAY_TIME = 200
    val mGestureDetector = GestureDetector(mContext,object :GestureDetector.SimpleOnGestureListener(){

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            Log.e("123","on single tap")
            return super.onSingleTapUp(e)
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            Log.e("123","on Double tap")
            if (mState == PLAY_STATE_PLAY) {
                pauseView()
                mTitleVisibility.set(if (mPlayer?.isPause() == true) View.VISIBLE else View.GONE)
            }
            return super.onDoubleTap(e)
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }
    })
    val mWaitVisibility  = ObservableField<Int>(View.VISIBLE)
    val mCtrlVisibility  = ObservableField<Int>(View.VISIBLE)
    val mTitleVisibility = ObservableField<Int>(View.VISIBLE)
    val mStateVisibility = ObservableField<Int>(View.GONE)
    val mCtrlBeg         = ObservableField<String>("00:00")
    val mCtrlEnd         = ObservableField<String>("00:00")
    val mTitleText       = ObservableField<String>(mContext.getString(R.string.title_file_no))
    val mPlayState       = ObservableField<Boolean>(true)
    val mProcess         = ObservableField<Int>(0)
    val mProcessMax      = ObservableField<Int>(0)

    var mIsUser          = false
    var mTotalMSec       = 0L
    var mState           = PLAY_STATE_STOP
    val onProgressChanged = SeekBarBindingAdapter.OnProgressChanged{
        seekBar, progress, fromUser ->
        if(fromUser && seekBar.max!=0){
            mCtrlBeg.set(Utils.formatMsec(progress*mTotalMSec / seekBar.max))
        }
    }

    val onStartTrackingTouch = SeekBarBindingAdapter.OnStartTrackingTouch {
        mIsUser = true
        if (mPlayer?.isPause()==false) {
            pauseView()
        }
    }

    val onStopTrackingTouch = SeekBarBindingAdapter.OnStopTrackingTouch {sb->
        if (mPlayer?.isPause()==true) {
            pauseView()
        }
        var pos = if (sb.max!=0) sb.progress*100/sb.max else 0
        mPlayer?.setPos(pos)
        Observable.timer(400,TimeUnit.MILLISECONDS).subscribe { mIsUser = false }
    }

    val onZoomTouchListener = ZoomableTextureView.OnTouchCb{view,event->
        if (isDoubleClick() && mState ==  PLAY_STATE_PLAY){
            pauseView()
            mTitleVisibility.set(if (mPlayer?.isPause() == true) View.VISIBLE else View.GONE)
        }
        false
    }

    var mActivity :Activity ?= null
    var mPlayer:BasePlayer  ?= null
    val F_TIME = 1L//刷新率  s
    var mWaiteNum = 0
    fun setContext(c:Context){
        mContext = c
    }
    fun setActivity(activity:Activity){
        mActivity = activity
    }

    override fun onCreate() {
        initUi()


    }

    override fun onDestory() {
        Log.e("123","on destory")
        stopView()
        mPlayer?.deinit()
    }

    fun setUriAndPlay(path:String){
        stopView()
        Log.i("123","path=$path")
        var title = path.split("/")
        mTitleText.set(title[title.lastIndex])
        Log.i("123","path=$path")
        mPlayer = ModelMgr.getLocalPlayerInstance()
                .registListener({ bInit ->
                    if (bInit) playView()
                }, { bDeinit ->

                }, { bPlay ->
                    if (bPlay) {
                        mState = PLAY_STATE_PLAY
                        initInfo()
                        stopProcessTask()
                        stopTimeTask()
                        startProcessTask(ApiManager.getInstance().localService)
                        startTimeTask(ApiManager.getInstance().localService)
                    }
                }, { bStop ->
                    if (bStop) {
                        mState = PLAY_STATE_STOP
                        stopTimeTask()
                        stopProcessTask()
                    }
                })
                .init(-1, path)


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
//            intent.setDataAndType()
            mActivity?.startActivityForResult(intent,1)
        }
    }

    fun playView(){
        mPlayer?.play(Config.CAM_IS_SUB)
    }

    fun pauseView(){
        mPlayer?.pause()
    }

    fun stopView(){
        mPlayer?.stop()
    }


    private fun initUi(){
        mCtrlBeg.set("00:00")
        mCtrlEnd.set("00:00")
        mProcess.set(0)
        mProcessMax.set(0)
        mIsUser = false
        mWaitVisibility.set(View.VISIBLE)
        mTitleVisibility.set(View.VISIBLE)
        mTitleText.set(mContext.getString(R.string.title_file_no))
    }

    private fun initInfo(){
        RxUtil.doRxTask(object :RxUtil.CommonTask<Long>(1000){
            var mTotalFrame = 0
            var mEndTime = ""
            override fun doInIOThread() {
                Thread.sleep(t)
                mTotalFrame = mPlayer?.getTotalFrame()?:0
                mTotalMSec = mPlayer?.getTotalMsec()?.toLong()?:0L
                mEndTime = Utils.formatMsec(mTotalMSec)
                Log.e("123","totalFrame=$mTotalFrame    endTIme=$mEndTime")

            }

            override fun doInUIThread() {
                mProcessMax.set(mTotalFrame)
                mCtrlEnd.set(mEndTime)
            }


        })
    }

    private fun isDoubleClick():Boolean{
        val curClickTime = System.currentTimeMillis()
        var flag = false
        if (curClickTime - mLastClickTime <= MIN_CLICK_DELAY_TIME){
            flag = true
        }
        mLastClickTime = curClickTime
        return flag
    }

    private fun onTime(speed:Long,bWait:Boolean){
        RxUtil.doInUIThread(object :RxUtil.RxSimpleTask<Void>(){
            override fun doTask() {
                if (bWait){

                }else{
                    //just progress,title  vanish
                    if(mPlayer?.isPause()==false) {
                        mWaitVisibility.set(View.GONE)
                        mTitleVisibility.set(View.GONE)
                    }
                }
            }
        })
    }

    private fun startTimeTask(server:HWPlayApi){
        ThreadUtil.scheduledSingleThreadStart({
            var bWaite        = true
            var streamLen = server.streamLen
            var speed   = streamLen*8/F_TIME
            if (streamLen!=0){
                bWaite = false
                mWaiteNum = 0
            }else{
                mWaiteNum++
                if (mWaiteNum==3){
                    bWaite = true
                    mWaiteNum = 0
                }
            }
            onTime(speed,bWaite)
        },0,F_TIME,TimeUnit.SECONDS)
    }

    private fun stopTimeTask(){
        ThreadUtil.scheduledSingleThreadShutDown()
    }

    private fun stopProcessTask(){
        ThreadUtil.scheduledThreadShutDown()
    }

    private fun onScheduled(curFrame:Int,curTime:String){
        RxUtil.doInUIThread(object :RxUtil.RxSimpleTask<Boolean>(){
            override fun doTask() {
                mProcess.set(curFrame)
                mCtrlBeg.set(curTime)
            }
        })
    }

    private fun startProcessTask(server: HWPlayApi){
        ThreadUtil.scheduledThreadStart({
            if (!mIsUser) {
                onScheduled(server.curFrame, Utils.formatMsec(server.playedMsec.toLong()))
            }
        },0,200,TimeUnit.MILLISECONDS)
    }

    private fun isGrantExternalRW(activity:Activity?):Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity?.checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            activity?.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        }
        return true
    }
}