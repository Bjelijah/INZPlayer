package com.inz.model

import android.databinding.ViewDataBinding

interface ViewModelBinder {
    fun bind(bind: ViewDataBinding,vm:BaseViewModel?)
}