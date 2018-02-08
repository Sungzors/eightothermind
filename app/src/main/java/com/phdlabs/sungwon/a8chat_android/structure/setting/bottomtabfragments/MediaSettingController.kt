package com.phdlabs.sungwon.a8chat_android.structure.setting.bottomtabfragments

import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract

/**
 * Created by SungWon on 2/1/2018.
 */
class MediaSettingController(val mView: SettingContract.MediaFragment.View) : SettingContract.MediaFragment.Controller{



    init {
        mView.controller = this
    }

    override fun start() {

    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

//    override fun getMediaList(): MutableList<Media> = mMediaList
//    override fun getIVList(): MutableList<ImageView> = mIVList
}