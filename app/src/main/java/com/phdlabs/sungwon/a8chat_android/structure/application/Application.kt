package com.phdlabs.sungwon.a8chat_android.structure.application

import android.app.Application
import com.phdlabs.sungwon.a8chat_android.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created by SungWon on 9/19/2017.
 */
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("")
                .setFontAttrId(R.attr.fontPath)
                .build())
    }
}