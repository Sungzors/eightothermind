package com.phdlabs.sungwon.a8chat_android.structure.setting

import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 1/22/2018.
 */
interface SettingContract {
    interface Chat {
        interface View: BaseView<Controller> {
            fun finishActivity()
        }
        interface Controller: BaseController {
            fun favoriteChat()

            fun setRoomId(roomId: Int)
            fun setFavorite(isFave: Boolean)
        }
    }
}