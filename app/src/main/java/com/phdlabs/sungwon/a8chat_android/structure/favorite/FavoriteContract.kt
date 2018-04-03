package com.phdlabs.sungwon.a8chat_android.structure.favorite

import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 3/20/2018.
 */
interface FavoriteContract {

    interface Message {

        interface View: BaseView<Controller>{
            fun setUpRecycler(messages: MutableList<com.phdlabs.sungwon.a8chat_android.model.message.Message>)
        }
        interface Controller: BaseController{
            fun getUserId(callback: (Int?) -> Unit)
            fun getFavorites(roomId: Int, isSelf: Boolean)
            /*Like Post*/
            fun likePost(messageId: Int, unlike: Boolean)
        }
    }
}