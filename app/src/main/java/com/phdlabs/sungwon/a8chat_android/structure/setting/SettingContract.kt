package com.phdlabs.sungwon.a8chat_android.structure.setting

import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.model.media.Media
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

    interface MediaFragment {
        interface View: BaseView<Controller>{

        }
        interface Controller: BaseController{
            fun getMediaList(): MutableList<Media>
            fun getIVList(): MutableList<ImageView>
        }
    }
}