package com.phdlabs.sungwon.a8chat_android.structure.main.lobby

/**
 * Created by SungWon on 10/17/2017.
 */
class LobbyController(val mView: LobbyContract.View): LobbyContract.Controller {

    //connects to LobbyFragment

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
}