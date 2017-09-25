package com.phdlabs.sungwon.a8chat_android.db

import org.greenrobot.eventbus.EventBus

/**
 * Created by SungWon on 9/22/2017.
 */
class EventBusManager(){

    companion object {
        fun instance(): EventBusManager = EventBusManager()
    }

    var mUiEventBus: EventBus = EventBus()
    var mDataEventBus: EventBus = EventBus()
}