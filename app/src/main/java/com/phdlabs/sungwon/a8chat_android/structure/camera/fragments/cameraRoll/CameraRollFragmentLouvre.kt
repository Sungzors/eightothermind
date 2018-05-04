package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.cameraRoll

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.andremion.louvre.Louvre
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.CameraBaseFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by paix on 5/4/18.
 * USE this API for handling video and images
 * https://www.programcreek.com/java-api-examples/?code=WeiXinqiao/Recognize-it/Recognize-it-master/album/src/main/java/com/yanzhenjie/album/ui/AlbumFragment.java
 */
class CameraRollFragmentLouvre : CameraBaseFragment() {

    /*Layout*/
    override fun cameraLayoutId(): Int = R.layout.fragment_cameraroll

    override fun inOnCreateView(root: View?, container: ViewGroup?, savedInstanceState: Bundle?) {
        //To change body of created functions use File | Settings | File Templates.
    }

    /*Companion*/
    companion object {
        fun create(): CameraRollFragmentLouvre = CameraRollFragmentLouvre()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Setup Louvre library
        activity?.let {
            Louvre.init(it)
                    .setRequestCode(Constants.CameraIntents.OPEN_MEDIA_PICKER)
                    .setMaxSelection(1)
                    .setMediaTypeFilter(Louvre.IMAGE_TYPE_JPEG, Louvre.IMAGE_TYPE_PNG)
        }

    }
}