package com.inz.inzplayer

import com.howellsdk.api.ApiManager
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LocalPlayer:BasePlayer() {





    override fun setURI(uri: String): BasePlayer {
        ApiManager.getInstance().localService.setUri(uri)
        return super.setURI(uri)
    }


    override fun init(crypto:Int,uri: String): BasePlayer {
        Observable.create(ObservableOnSubscribe<Boolean> {
            ApiManager.getInstance().getLocalService(crypto,uri)
                    .bindCam()
            it.onNext(true)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mListener?.onInit(it)
                },{e->e.printStackTrace()})
        return this
    }

    override fun deinit(): BasePlayer {
        Observable.create(ObservableOnSubscribe<Boolean> {
            ApiManager.getInstance().localService.unBindCam()
            it.onNext(true)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mListener?.onDeinit(it)
                },{e->e.printStackTrace()})
        return this
    }
    override fun play(isSub: Boolean): BasePlayer {
        Observable.create(ObservableOnSubscribe<Boolean> {
            ApiManager.getInstance().localService.play(isSub)
            it.onNext(true)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mListener?.onPlay(it)
                },{e->e.printStackTrace()})
        return this
    }

    override fun pause(): BasePlayer {
        ApiManager.getInstance().localService.playPause()
        return this
    }

    override fun stop(): BasePlayer {
        Observable.create(ObservableOnSubscribe<Boolean> {
            ApiManager.getInstance().localService.stop()
            it.onNext(true)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mListener?.onStop(it)
                },{e->e.printStackTrace()})
        return this
    }



}