package com.phdlabs.sungwon.a8chat_android.api.utility

import com.google.gson.Gson

/**
 * Created by JPAM on 12/20/17.
 * Gson Singleton
 */
class GsonHolder {

    /*Singleton*/
    companion object {
        var instance: GsonHolder = GsonHolder()
            private set
    }

    /*Properties*/
    private var gson: Gson? = null

    /*Initialization*/
    init {
        gson = Gson()
    }

    /*Getter*/
    fun get(): Gson? = gson
}