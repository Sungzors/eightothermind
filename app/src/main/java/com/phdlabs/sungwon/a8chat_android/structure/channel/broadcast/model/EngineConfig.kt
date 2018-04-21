package com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.model

/**
 * Created by JPAM on 4/6/18.
 * RTC Engine Configuration
 */
class EngineConfig {

    /*Singleton Model*/
    private object Holder {
        val INSTANCE = EngineConfig()
    }

    companion object {
        val instance: EngineConfig by lazy { Holder.INSTANCE }
    }

    /*Properties*/
    var mClientRole: Int = 0

    var mVideoProfile: Int = 0

    var mUid: Int = 0

    var mChannel: String? = null

    /*Methods*/
    fun reset() {
        mChannel = null
    }

    fun setUid(uid: Int) {
        mUid = uid
    }

}