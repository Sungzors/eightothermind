package com.phdlabs.sungwon.a8chat_android.utility

import android.os.Build

/**
 * Created by JPAM on 1/16/18.
 * Device Management for Android
 */
class DeviceInfo {

    /*Properties*/
    private var warningDevices = arrayOf(nexus5X, xiaomiMix2)

    /*Companion*/
    companion object {
        val INSTANCE: DeviceInfo by lazy { Holder.INSTANCE }
        /*Warning DeviceInfo*/
        val nexus5X = "Nexus 5X"
        val xiaomiMix2 = "Mi MIX 2"
    }

    /*Object holder for singleton pattern*/
    private object Holder {
        val INSTANCE = DeviceInfo()
    }

    /*Initialization*/
    init {
        println("DeviceInfo (\$this) is a Singleton")
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

    /**
     * [deviceName] get the Device's manufacturer name
     * @return
     * */
    fun deviceName():String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        if(model.startsWith(manufacturer)){
            return capitalize(model)
        }else {
            return capitalize(manufacturer)
        }
    }

    /**
     * [isEmulator] Check if the device is an Android Emulator
     * @return [Boolean]
     * */
    fun isEmulator(): Boolean {
        val brand = Build.BRAND
        return brand.equals("generic")
    }

    /**
     * [capitalize] helper to capitalize the string properly
     * @param [String]
     * @return
     * */
    fun capitalize(s:String?):String {
        if(s == null || s.length == 0) {
            return ""
        }
        var first = s.toCharArray().first()
        if(first.isUpperCase()){
            return s
        }else {
            return Character.toUpperCase(first) + s.substring(1)
        }
    }



}