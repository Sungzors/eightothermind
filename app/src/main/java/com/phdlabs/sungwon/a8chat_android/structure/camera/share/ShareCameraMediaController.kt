package com.phdlabs.sungwon.a8chat_android.structure.camera.share

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.phdlabs.sungwon.a8chat_android.api.data.SendMessageStringData
import com.phdlabs.sungwon.a8chat_android.api.rest.Rest
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.events.EventsManager
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
import com.phdlabs.sungwon.a8chat_android.model.user.User
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

/**
 * Created by JPAM on 3/26/18.
 * [ShareCameraMediaActivity] Controller
 */
class ShareCameraMediaController(val mView: CameraContract.Share.View) : CameraContract.Share.Controller {

    /*Properties*/
    private var mUser: User? = null
    private var mFilePath: String? = null
    private var mMessage: String? = null
    private var mSocket: Socket

    init {
        mView.controller = this
        mSocket = mView.get8Application.getSocket()
    }

    /*LifeCycle*/
    override fun onCreate() {
        //User access
        UserManager.instance.getCurrentUser { success, user, _ ->
            if (success) {
                user?.let {
                    mUser = it
                    //Connect to Socket i/o for sharing content
                    mSocket.emit(Constants.SocketKeys.CONNECT_ROOMS, user.id, Constants.SocketTypes.CHANNEL)
                    mSocket.emit(Constants.SocketKeys.CONNECT_ROOMS, it.id, Constants.SocketTypes.EVENT)
                    mSocket.emit(Constants.SocketKeys.CONNECT_ROOMS, it.id, Constants.SocketTypes.PRIVATE_CHAT)
                }
            }
        }
    }

    override fun start() {
        //Channel
        mSocket.on(Constants.SocketKeys.UPDATE_ROOM, onUpdateRoom)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_STRING, onNewMessage)
        mSocket.on(Constants.SocketKeys.UPDATE_CHAT_MEDIA, onNewMessage)
        mSocket.on(Constants.SocketKeys.ON_ERROR, onError)
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
        //Channel
        mSocket.off(Constants.SocketKeys.UPDATE_ROOM)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_STRING)
        mSocket.off(Constants.SocketKeys.UPDATE_CHAT_MEDIA)
        mSocket.off(Constants.SocketKeys.ON_ERROR)
    }

    override fun loadMyChannels() {
        mUser?.id?.let {
            ChannelsManager.instance.getUserChannels(it, true, {
                it.second?.let {
                    //Error
                    //Todo(comment) -> Create user error
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
    //Todo fix events
//        mUser?.id?.let {
//            EventsManager.instance.getEvents(true, 0.0, 0.0, { response ->
//                response.second?.let {
//                    mView.showError("Failed to get events")
//                } ?: run {
//                    response.first?.let {
//                        mView.showMyEvents()
//                    }
//                }
//            })
//        }
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
    //TODO: Translate to a general method for a Media Manager
    override fun pushToChannel(channels: List<Channel>?, shareType: ShareCameraMediaActivity.ShareType?) {
        //Dev
        println("Push to Channels: $channels")
        //Push Post to channels
        if (shareType == ShareCameraMediaActivity.ShareType.Post) {
            //POST
            UserManager.instance.getCurrentUser { isSuccess, user, token ->
                if (isSuccess) {
                    user?.let {
                        token?.token?.let {
                            channels?.let {
                                for (channel in channels) {
                                    val formBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                                            .addFormDataPart("message", mMessage ?: "")
                                            .addFormDataPart("userId", user.id!!.toString())
                                            .addFormDataPart("roomId", channel.room_id.toString())
                                    //Create Media-Post BodyPart & Call
                                    mFilePath?.let {
                                        MediaStore.Images.Media.getBitmap(mView.getActivity.contentResolver, Uri.parse("file:///$it"))?.let {
                                            val bos = ByteArrayOutputStream()
                                            it.compress(Bitmap.CompressFormat.PNG, 0, bos)
                                            val bitmapData = bos.toByteArray()
                                            formBodyBuilder.addFormDataPart("file[0]",//Single Image sharing
                                                    "8_" + System.currentTimeMillis(),
                                                    RequestBody.create(MediaType.parse("image/*"), bitmapData))
                                        }
                                        val formBody = formBodyBuilder.build()
                                        val call = Rest.getInstance().getmCallerRx().postChannelMediaPost(token.token!!, formBody, true)
                                        call.subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe({ response ->
                                                    if (response.isSuccess) {
                                                        println("Message: " + response.messageInfo?.message)
                                                    } else if (response.isError) {
                                                        mView.showError("Could not post")
                                                    }
                                                }, { throwable ->
                                                    mView.showError(throwable.localizedMessage)
                                                    println(throwable.stackTrace)
                                                })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        mView.shareCompletion()
    }

    /*Share to Events*/
    //TODO: Translate to a general method for a Media Manager
    override fun pushToEvent(events: List<EventsEight>?, shareType: ShareCameraMediaActivity.ShareType?) {
        println("Push to Events: $events") //TODO: Push to API
        if (shareType == ShareCameraMediaActivity.ShareType.Post) {
            //Share Media

            //Share Message

        } else {
            //MESSAGE


        }
        //TODO: User feedback of sharing success!
        mView.shareCompletion()
    }

    /*Share to Private Chat*/
    //TODO: Translate to a general method for a Media Manager
    override fun pushToContact(contacts: List<Contact>?, shareType: ShareCameraMediaActivity.ShareType?) {
        println("Push to Contact: $contacts") //TODO: Push to API
        if (shareType == ShareCameraMediaActivity.ShareType.Post) {
            //Share Media
            UserManager.instance.getCurrentUser { isSuccess, user, token ->
                if (isSuccess) {
                    user?.let {
                        token?.token?.let {
                            contacts?.let {
                                //Will collect variables
                                for (contact in it) {
                                    //Find Private Chat Room
                                    RoomManager.instance.getRoomWithPrticipantsIds(user.id!!, contact.id, { room ->
                                        room?.let {
                                            //Start Content packaging for media
                                            mFilePath?.let {
                                                MediaStore.Images.Media.getBitmap(mView.getActivity.contentResolver, Uri.parse("file:///$it"))?.let {
                                                    val formBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                                                            .addFormDataPart("userId", user.id!!.toString())
                                                            .addFormDataPart("roomId", room.id!!.toString())
                                                    //Create Media-Post BodyPart & Call
                                                    val bos = ByteArrayOutputStream()
                                                    it.compress(Bitmap.CompressFormat.PNG, 0, bos)
                                                    val bitmapData = bos.toByteArray()
                                                    formBodyBuilder.addFormDataPart("file[0]",
                                                            "8_" + System.currentTimeMillis(),
                                                            RequestBody.create(MediaType.parse("image/*"), bitmapData))

                                                    val formBody = formBodyBuilder.build()
                                                    val call = Rest.getInstance().getmCallerRx().postChannelMediaPost(token.token!!, formBody, false)
                                                    call.subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe({ response ->
                                                                if (response.isSuccess) {
                                                                    println("Message: " + response.messageInfo?.message)
                                                                } else if (response.isError) {
                                                                    mView.showError("Could not post")
                                                                }
                                                            }, { throwable ->
                                                                mView.showError(throwable.localizedMessage)
                                                            })
                                                }
                                            }
                                            //Start content packaging for message
                                            mMessage?.let {
                                                val call = Rest.getInstance().getmCallerRx().sendMessageString(
                                                        token.token!!,
                                                        SendMessageStringData(user.id!!, it, room.id!!))
                                                call.subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe({ response ->
                                                            if (response.isSuccess) {
                                                                println("Message" + response.message)
                                                            } else if (response.isError) {
                                                                mView.showError("Could not send message")
                                                            }
                                                        }, { throwable ->
                                                            mView.showError(throwable.localizedMessage)
                                                        })
                                            }
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }
            //Share Message
        }
        //TODO: User feedback of sharing success!
        mView.shareCompletion()
    }


    /*Socket i/o*/
    private val onNewMessage = Emitter.Listener { args ->
        println("New Message: $args")
    }

    private val onUpdateRoom = Emitter.Listener { args ->
        println("Room Update: $args")
    }

    private val onError = Emitter.Listener { args ->
        println("On Error: $args")
    }


}