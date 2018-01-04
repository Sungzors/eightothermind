package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal

import android.hardware.Camera
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView

/**
 * Created by paix on 1/1/18.
 * Camera Normal feature interface contract used for NormalController
 */
interface NormalContract {

    interface View: BaseView<Controller> {

    }

    interface Controller: BaseController  {

    }

}