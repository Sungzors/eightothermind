package com.phdlabs.sungwon.a8chat_android.structure.myProfile

import android.content.Intent
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.api.data.UserData
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.myProfile.update.MyProfileUpdateActivity

/**
 * Created by SungWon on 10/2/2017.
 */
interface ProfileContract {

    /*UPDATE*/

    interface Update {

        interface View: BaseView<Controller>{

            fun setProfileImageView(pictureUrl: String)

            val getProfileImageView: ImageView?

            val getUserData: UserData

            val getUpdateActivityMy: MyProfileUpdateActivity

            fun startApp()


            /* true if either first or last name is null*/
            fun nullChecker() : Boolean
        }

        interface Controller: BaseController{
            fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)

            fun showPicture(activity: CoreActivity)

            fun postProfile()

        }

    }

    /*MY PROFILE*/

    interface MyProfile {

        interface View: BaseView<Controller> {

        }

        interface Controller: BaseController {

        }

    }
}