package com.phdlabs.sungwon.a8chat_android.structure.camera.share

import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.events.EventsManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract

/**
 * Created by JPAM on 3/26/18.
 * [ShareCameraMediaActivity] Controller
 */
class ShareCameraMediaController(val mView: CameraContract.Share.View) : CameraContract.Share.Controller {

    init {
        mView.controller = this
    }

    /*Properties*/
    private var mUser: User? = null

    /*LifeCycle*/
    override fun onCreate() {
        //User access
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.let {
                    mUser = it
                }
            }
        }
    }

    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    override fun loadMyChannels() {
        mUser?.id?.let {
            ChannelsManager.instance.getUserChannels(it, true, {
                it.second?.let {
                    //Error
                    //Todo(comment)
                    mView.showError(it)
                } ?: run {
                    it.first?.let {
                        mView.showMyChannels()
                    }
                }
            })
        }
    }

    override fun loadMyEvents() {
        mUser?.id?.let {
            EventsManager.instance.getEvents(true, {
                it.second?.let {
                    //Error
                    //TODO(comment)
                    mView.showError(it)
                } ?: run {
                    it.first?.let {
                        mView.showMyEvents()
                    }
                }
            })
        }
    }

    override fun loadMyContacts() {
        mView.showMyContacts()
    }

    /**
     * Validate Information to share Only Media || Media + Message
     * */
    override fun infoValidation(filePath: String?, message: String?): ShareCameraMediaActivity.SHARE_TYPE? {
        var shareType: ShareCameraMediaActivity.SHARE_TYPE? = null
        //Media + Message
        message?.let {
            filePath?.let {
                shareType = ShareCameraMediaActivity.SHARE_TYPE.Post
            }
        } ?: run {
            //Only Media
            filePath?.let {
                shareType = ShareCameraMediaActivity.SHARE_TYPE.Media
            }
        }
        return shareType
    }

}