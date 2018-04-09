package com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.model.EngineConfig
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.model.MyEngineEventHandler
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.model.WorkerThread
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import io.agora.rtc.RtcEngine
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by JPAM on 4/9/18.
 * Agora.io Live Video Broadcast
 * This activity should be instantiated with Client Role (Audience || Broadcaster)
 * Only if the Eight-Channel owner starts this activity it can be based in a Broadcaster
 */
open class BroadcastActivity : CoreActivity() {
    //Dev
    private val log = LoggerFactory.getLogger(BroadcastActivity::class.java)

    /*Layout*/
    override fun layoutId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*Container*/
    override fun contentContainerId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*Properties*/


    /*LifeCycle*/


    protected fun rtcEngine(): RtcEngine =
            (application as Application).getWorkerThread()?.getRtcEngine()!!

    protected fun worker(): WorkerThread = (application as Application).getWorkerThread()!!

    protected fun config(): EngineConfig =
            (application as Application).getWorkerThread()?.getEngineConfig()!!

    protected fun event(): MyEngineEventHandler =
            (application as Application).getWorkerThread()?.eventHandler()!!

}