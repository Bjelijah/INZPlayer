package com.inz.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.inz.inzplayer.R
import com.inz.model.BaseViewModel
import com.inz.model.ModelMgr
import com.inz.model.main.MainViewModel

class MainActivity:BaseActivity() {
    override fun getLayout(): Int  = R.layout.activity_main

    override fun getViewModel(): BaseViewModel = ModelMgr.getMainViewModelInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (getViewModel() as MainViewModel).setActivity(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var uri = data?.data
        var filePath = getRealPathFromUri(uri!!)
        ModelMgr.getMainViewModelInstance(this).setUriAndPlay(filePath)
    }


    private fun getRealPathFromUri(contentUri: Uri):String{
        var cursor = contentResolver.query(contentUri,null,null,null,null)
        var res = ""
        if (cursor.moveToFirst()){
            var index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(index)
        }
        cursor.close()
        return res
    }
}