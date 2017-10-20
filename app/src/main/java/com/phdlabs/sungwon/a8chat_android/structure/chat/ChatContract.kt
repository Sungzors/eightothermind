package com.phdlabs.sungwon.a8chat_android.structure.chat

import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 10/18/2017.
 */
interface ChatContract {
    interface View: BaseView<Controller>{

    }

    interface Controller: BaseController{
        fun destroy()
    }
}