package com.phdlabs.sungwon.a8chat_android.structure.camera.share

import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.events.EventsManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
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
    private var mFilePath: String? = null
    private var mMessage: String? = null

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

    override fun validatedSelection(channels: List<Channel>?, events: List<EventsEight>?, contacts: List<Contact>?): Boolean {
        var availableChannels = false
        var availabelEvents = false
        var availableContacts = false
        //Validate channels
        channels?.let {
            if (it.count() > 0) {
                availableChannels = true
            }
        }
        //Validate Events
        events?.let {
            if (it.count() > 0) {
                availabelEvents = true
            }
        }
        //Validate Contacts
        contacts?.let {
            if (it.count() > 0) {
                availableContacts = true
            }
        }
        //Validation
        return availableChannels || availabelEvents || availableContacts
    }

    /**
     * Validate Information to share Only Media || Media + Message
     * */
    override fun infoValidation(filePath: String?, message: String?): ShareCameraMediaActivity.ShareType? {
        var shareType: ShareCameraMediaActivity.ShareType? = null
        //Media + Message
        if (message.isNullOrBlank()) {
            //Only Media
            filePath?.let {
                mFilePath = it
                shareType = ShareCameraMediaActivity.ShareType.Media
            }
        } else {
            //Only Media
            mMessage = message
            filePath?.let {
                mFilePath = it
                shareType = ShareCameraMediaActivity.ShareType.Post
            }
        }
        return shareType
    }


    /**
     * Push media & message to Channel, Event & Chat
     * */
    override fun pushToChannel(channels: List<Channel>?, shareType: ShareCameraMediaActivity.ShareType?) {
        println("Push to Channels: " + channels)
    }

    override fun pushToEvent(events: List<EventsEight>?, shareType: ShareCameraMediaActivity.ShareType?) {
        println("Push to Events: " + events)
    }

    override fun pushToContact(contacts: List<Contact>?, shareType: ShareCameraMediaActivity.ShareType?) {
        println("Push to Contact: " + contacts)
    }

}