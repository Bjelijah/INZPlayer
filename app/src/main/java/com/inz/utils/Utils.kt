package com.inz.utils

object Utils {

    fun formatMsec(mss:Long):String{
        var hours = (mss % (1000*60*60*24))/(1000*60*60)
        var min = (mss % (1000*60*60))/(1000*60)
        var sec = (mss % (1000*60))/1000
        return  String.format("%02d:%02d",min,sec)
    }
}