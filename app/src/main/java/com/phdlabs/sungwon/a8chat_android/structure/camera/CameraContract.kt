package com.phdlabs.sungwon.a8chat_android.structure.camera

import android.support.annotation.IdRes
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControls.CameraCloseView
import com.phdlabs.sungwon.a8chat_android.structure.camera.cameraControls.CameraControlView
import com.phdlabs.sungwon.a8chat_android.structure.camera.share.ShareCameraMediaActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseController
import com.phdlabs.sungwon.a8chat_android.structure.core.BaseView
import net.alhazmy13.imagefilter.ImageFilter
import net.protyposis.android.mediaplayer.MediaSource

/**
 * Created by JPAM on 12/28/17.
 */
interface CameraContract {

    /*Camera Functionality*/
    interface Camera {

        interface View : BaseView<Controller> {
            /*Camera Control View*/
            fun getCameraControl(): CameraControlView

            /*Camera Close View Control*/
            fun getCameraCloseControl(): CameraCloseView

            /*Flash UI*/
            fun flashFeedback(isFLashOn: Boolean)

            /*Activity*/
            var activity: CameraActivity
        }

        interface Controller : BaseController {

            /*Tab selection*/
            fun onTabReselected(tab: TabLayout.Tab?)

            fun onTabUnselected(tab: TabLayout.Tab?)
            fun onTabSelected(tab: TabLayout.Tab?, viewPager: ViewPager)
            /*Camera Actions*/
            fun takePhoto(viewPager: ViewPager)

            /*Camera Flip*/
            fun cameraFlip(viewPager: ViewPager)

            /*Manual Flash*/
            fun manualFlash(viewPager: ViewPager)

            /*Start Preview Activity*/
            fun startPreviewActivity(imageFilePath: String?)

        }
    }

    /*Sharing Functionality*/
    interface Share {

        interface View : BaseView<Controller> {
            //Context
            val getActivity: ShareCameraMediaActivity
            val get8Application: Application
            //Fragment Management
            fun swapContainer(@IdRes contentContainer: Int): Int

            //Content
            fun showMyChannels()

            fun showMyEvents()
            fun showMyContacts()

            fun shareCompletion()

        }

        interface Controller : BaseController {
            //LifeCycle
            fun onCreate()

            //Pull Data
            fun loadMyChannels()

            fun loadMyEvents()
            fun loadMyContacts()

            //Validation
            fun validatedSelection(channels: List<Channel>?, events: List<EventsEight>?, contacts: List<Contact>?): Boolean

            fun infoValidation(filePath: String?, message: String?): ShareCameraMediaActivity.ShareType?

            //Push Data
            fun pushToChannel(channels: List<Channel>?, shareType: ShareCameraMediaActivity.ShareType?)

            fun pushToEvent(events: List<EventsEight>?, shareType: ShareCameraMediaActivity.ShareType?)
            fun pushToContact(contacts: List<Contact>?, shareType: ShareCameraMediaActivity.ShareType?)

        }
    }

    /*Filters*/
    interface Filters {

        interface View : BaseView<Controller> {
            fun processFilter(filter: ImageFilter.Filter)
        }

        interface Controller : BaseController {
            fun savePhoto()
            fun sendPhoto()
        }
    }

}