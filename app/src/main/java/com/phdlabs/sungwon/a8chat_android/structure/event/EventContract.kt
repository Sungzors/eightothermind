package com.phdlabs.sungwon.a8chat_android.structure.event

import android.content.Intent
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 1/2/2018.
 */
interface EventContract {
    interface Create {
        interface View: BaseView<Controller>{
            fun showPicture(url: String)

            val get8Application : Application
        }
        interface Controller: BaseController{
            fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)

            fun createEvent(name: String, lock: Boolean)
        }
    }
}