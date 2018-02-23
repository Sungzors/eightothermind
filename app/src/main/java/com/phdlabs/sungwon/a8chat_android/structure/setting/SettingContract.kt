package com.phdlabs.sungwon.a8chat_android.structure.setting

import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by SungWon on 1/22/2018.
 * Updated by JPAM on 02/22/2018
 */
interface SettingContract {
    interface Chat {
        interface View : BaseView<Controller> {
            fun finishActivity()

            /*Could not favorite contact*/
            fun couldNotFavoriteContact()
        }

        interface Controller : BaseController {
            /*Update room to favorite*/
            fun favoriteRoom(room: Room?, favorite: Boolean)

            /*Retrieve cached contact information*/
            fun getContactInfo(id: Int): Contact

            /*Retrieve room Info*/
            fun getRoomInfo(id: Int): Room
        }
    }

    interface MediaFragment {
        interface View : BaseView<Controller> {

        }

        interface Controller : BaseController {
//            fun getMediaList(): MutableList<Media>
//            fun getIVList(): MutableList<ImageView>
        }
    }
}