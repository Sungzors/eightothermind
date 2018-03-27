package com.phdlabs.sungwon.a8chat_android.structure.camera.share

import android.os.Bundle
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract
import com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments.ShareToChannelFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments.PostFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments.ShareToContactFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments.ShareToEventFragment
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_share_camera.*

/**
 * Created by JPAM on 3/26/18.
 * Used for sharing camera media to Channels, Events & Private Conversations.
 */
class ShareCameraMedia : CoreActivity(), CameraContract.Share.View {

    //TODO: on Sent completion setResult OK to finish Editing Activity & go back to Camera App

    /*Controller*/
    override lateinit var controller: CameraContract.Share.Controller

    /*Properties*/
    private var currentContainer: Int = 0

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_share_camera

    override fun contentContainerId(): Int = currentContainer

    override fun swapContainer(contentContainer: Int): Int {
        currentContainer = contentContainer
        return currentContainer
    }

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ShareCameraMediaController(this)
        controller.onCreate()
        processIntent()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
        controller.loadMyChannels()
        controller.loadMyEvents()
        controller.loadMyContacts()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }

    /*Process Intent Information*/
    private fun processIntent() {
        //Thumbnail
        intent.getStringExtra(Constants.CameraIntents.IMAGE_FILE_PATH)?.let {
            currentContainer = asc_thumbnail_container.id
            println("Image File Path: $it")
            //Post Fragment
            addFragment(contentContainerId(), PostFragment.create(it), false)
            currentContainer = 0
        }
    }

    /*Show My Channels*/
    override fun showMyChannels() {
        currentContainer = asc_channels_container.id
        addFragment(contentContainerId(), ShareToChannelFragment(), false)
        currentContainer = 0
    }

    /*Show My Events*/
    override fun showMyEvents() {
        currentContainer = asc_events_container.id
        addFragment(contentContainerId(), ShareToEventFragment(), false)
        currentContainer = 0
    }

    /*Show My Contacts*/
    override fun showMyContacts() {
        currentContainer = asc_contacts_container.id
        addFragment(contentContainerId(), ShareToContactFragment(), false)
        currentContainer = 0
    }

    //TODO: Setup SearchView

}