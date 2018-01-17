package com.phdlabs.sungwon.a8chat_android.utility

import android.os.Build

/**
 * Created by paix on 1/16/18.
 * Device Management for Android
 */
class Devices {

    /*Properties*/
    private var warningDevices = arrayOf(nexus5X)

    /*Companion*/
    companion object {
        val instance: Devices by lazy { Holder.INSTANCE }
        /*Warning Devices*/
        val nexus5X = "Nexus 5X"
    }

    /*Object holder for singleton pattern*/
    private object Holder {
        val INSTANCE = Devices()
    }

    /*Initialization*/
    init {
        println("Devices (\$this) is a Singleton")
    }

    /**
     * Return [Boolean] value indicating if the current device is considered within the current
     * list of devices to be warned about.
     * The listed devices rotate camera images 180 degrees when retrieved from gallery
     * */
    fun isWarningDevice(model: String): Boolean {
        if (warningDevices.size > 0) {
            if (warningDevices.contains(model)) {
                return true
            }
        }
        return false
    }


}