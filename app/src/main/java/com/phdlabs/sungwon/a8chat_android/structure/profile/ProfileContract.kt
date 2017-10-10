package com.phdlabs.sungwon.a8chat_android.structure.profile

import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.api.data.UserData
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity

/**
 * Created by SungWon on 10/2/2017.
 */
interface ProfileContract {

    interface View: BaseView<Controller>{
        val getProfileImageView: ImageView?

        val getUserData: UserData

        val getActivity: ProfileActivity
    }

    interface Controller: BaseController{
        fun onPictureResult(requestCode: Int, resultCode: Int, data: Intent?)

        fun showPicture(activity: CoreActivity)

        fun postProfile()

        fun circlePicture(pictureUrl: String)
        fun circlePicture(pictureUrl: Uri)
    }
}