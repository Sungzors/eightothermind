package com.phdlabs.sungwon.a8chat_android.structure.application

import android.app.Application
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.net.URISyntaxException

/**
 * Created by SungWon on 9/19/2017.
 */
class Application : Application() {

    private lateinit var mSocket: Socket

    override fun onCreate() {
        super.onCreate()

        try {
            mSocket = IO.socket("https://eight-backend.herokuapp.com/")
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("")
                .setFontAttrId(R.attr.fontPath)
                .build())

    }



    fun getSocket(): Socket = mSocket
}