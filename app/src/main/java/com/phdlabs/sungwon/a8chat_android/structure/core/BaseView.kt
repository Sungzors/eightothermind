package com.phdlabs.sungwon.a8chat_android.structure.core

import android.content.Context

/**
 * Created by SungWon on 9/28/2017.
 */
interface BaseView<T> {
    var controller: T

    fun getContext(): Context?
    fun showError(errorname: String)
    fun showProgress()
    fun hideProgress()
    fun close()
}