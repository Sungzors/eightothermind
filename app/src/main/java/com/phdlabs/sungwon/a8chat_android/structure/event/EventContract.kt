package com.phdlabs.sungwon.a8chat_android.structure.event

import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 1/2/2018.
 */
interface EventContract {
    interface Create {
        interface View: BaseView<Controller>{
            val get8Application : Application
        }
        interface Controller: BaseController{

        }
    }
}