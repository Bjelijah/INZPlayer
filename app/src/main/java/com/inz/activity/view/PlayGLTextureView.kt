package com.inz.activity.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.howellsdk.api.player.GLESRendererImpl
import com.howellsdk.api.player.GLESTextureView
import com.howellsdk.api.player.ZoomableTextureView

class PlayGLTextureView(private val mContext:Context,attrs: AttributeSet):ZoomableTextureView(mContext,attrs){

    private var mRenderer = GLESRendererImpl(mContext,this,null)
    init {
        setRenderer(mRenderer)
        setRenderMode(GLESTextureView.RENDERMODE_WHEN_DIRTY)
    }
}