package com.phdlabs.sungwon.a8chat_android.structure.application

import android.app.Application
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.model.WorkerThread
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.rx.RealmObservableFactory
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.net.URISyntaxException

/**
 * Created by SungWon on 9/19/2017.
 * Updated by JPAM on 03/15/2018
 * Application Init
 */
class Application : Application() {

    /*Properties*/
    lateinit var realmConfig: RealmConfiguration
    private lateinit var mSocket: Socket

    /*LifeCycle*/
    override fun onCreate() {
        super.onCreate()

        /*Realm*/
        Realm.init(this)
        realmConfig = RealmConfiguration.Builder().rxFactory(RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfig)
        //DEV
        print("REALM PATH: " + realmConfig.path) //Path for realm browser


        /*Sockets*/
        try {
            mSocket = IO.socket("https://eight-backend.herokuapp.com/")
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
        mSocket.connect()

        /*Fonts*/
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Avenir-Black-webfont.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build())

    }

    /*Get Socket*/
    fun getSocket(): Socket = mSocket

    /*LIVE VIDEO BROADCAST*/
    private var mWorkerThread: WorkerThread? = null

    @Synchronized
    fun initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = WorkerThread(applicationContext)
            mWorkerThread?.start()

            mWorkerThread?.waitForReady()
        }
    }

    @Synchronized
    fun getWorkerThread(): WorkerThread? = mWorkerThread

    @Synchronized
    fun deInitWorkerThread() {
        mWorkerThread?.exit()
        try {
            mWorkerThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        mWorkerThread = null
    }

}
