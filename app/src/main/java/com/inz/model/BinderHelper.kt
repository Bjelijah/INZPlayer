package com.inz.model

import android.databinding.BindingAdapter
import android.databinding.BindingConversion
import android.databinding.ViewDataBinding
import android.view.View
import com.howellsdk.api.player.ZoomableTextureView
import com.inz.activity.view.PlayGLTextureView
import com.inz.inzplayer.BR
import com.inz.inzplayer.R
import com.inz.utils.DebugLog
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_main.view.*

object BinderHelper {
    val defaultBinder = object :ViewModelBinder{
        override fun bind(bind: ViewDataBinding, vm: BaseViewModel?) {
            bind.setVariable(BR.vm,vm)
        }
    }

    @BindingConversion
    @JvmStatic
    fun onClickListen(listener:Action?)= View.OnClickListener {
        if (listener==null)DebugLog.LogE("listener==null")
        listener?.run()
    }

    @BindingAdapter("onTouchListener")
    @JvmStatic
    fun setOnTouchListener(v:View,b:Boolean){
        v.setOnTouchListener { v, event->
            when(v.id){
                R.id.play_gl->ModelMgr.getMainViewModelInstance(ModelMgr.mContext!!).onGlTouch(v,event)
                else->false
            }
        }
    }

    @BindingAdapter("onZoomTouchListener")
    @JvmStatic
    fun setOnZoomTouchListener(v: PlayGLTextureView,cb: ZoomableTextureView.OnTouchCb?){
        v.setOnTouchCallback(cb)
    }


    @BindingAdapter("play_state_src")
    @JvmStatic
    fun setPlayStateSrc(v:View,bPause:Boolean){
        if (bPause){
            v.setBackgroundResource(R.drawable.ic_pause_circle_outline)
        }else{
            v.setBackgroundResource(R.drawable.ic_play_circle_outline)
        }
    }

}