package com.phdlabs.sungwon.a8chat_android.structure.main

import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 10/13/2017.
 */
interface MainContract {

    interface View: BaseView<Controller>{

    }

    interface Controller: BaseController{
        fun showHome()
        fun showCamera()
        fun showProfile()
    }
}