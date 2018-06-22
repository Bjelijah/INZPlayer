package com.inz.inzplayer


abstract class BasePlayer {
    interface PlayListener{
        fun onInit(b:Boolean)
        fun onDeinit(b:Boolean)
        fun onPlay(b:Boolean)
        fun onStop(b:Boolean)
    }

    protected var mListener: PlayListener?=null

    fun registListener(l: PlayListener):BasePlayer{
        mListener = l
        return this
    }

    fun registListener(onInit:(Boolean)->Unit,onDeinit:(Boolean)->Unit,onPlay:(Boolean)->Unit,onStop:(Boolean)->Unit):BasePlayer{
        mListener = object : PlayListener {
            override fun onInit(b: Boolean) = onInit(b)
            override fun onDeinit(b: Boolean) = onDeinit(b)
            override fun onPlay(b: Boolean) = onPlay(b)
            override fun onStop(b: Boolean) = onStop(b)
        }
        return this
    }


    fun unregistListener():BasePlayer{
        mListener=null
        return this
    }

    open fun setURI(uri:String):BasePlayer{return this}

    abstract fun init(crypto:Int,uri:String):BasePlayer
    abstract fun deinit():BasePlayer
    abstract fun play(isSub:Boolean):BasePlayer
    abstract fun pause():BasePlayer
    abstract fun isPause():Boolean
    abstract fun stop():BasePlayer

    open fun getTotalFrame():Int = 0
    open fun getTotalMsec() :Int = 0
    open fun setPos(pos:Int){}

}