package com.phdlabs.sungwon.a8chat_android.structure.channel

import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 11/30/2017.
 */
interface ChannelContract {
    interface Create {
        interface View: BaseView<Controller>{

        }
        interface Controller: BaseController{

        }
    }
}