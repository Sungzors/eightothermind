package com.phdlabs.sungwon.a8chat_android.structure.createnew.searchChannels

import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.structure.createnew.CreateNewContract
import com.vicpin.krealmextensions.save

/**
 * Created by JPAM on 3/13/18.
 */
class ChannelSearchFragController(val mView: CreateNewContract.ChannelSearch.View) :
        CreateNewContract.ChannelSearch.Controller {

    init {
        mView.controller = this
    }

    override fun start() {
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    /*CHANNELS*/
    override fun pushChannelFilterChanges(p0: String?) {
        //todo: only call when the string has characters
        p0?.let {
            if (p0.isNotBlank()) {
                ChannelsManager.instance.searchChannels(p0, { response ->
                    response.second?.let {
                        //Error
                        mView.showError(it)
                    } ?: run {
                        //Success
                        response.first?.let {
                            mView.updateChannelRecycler(it.toMutableList())
                        }
                    }
                })
            }
        }
    }

    override fun pullChannelRoom(roomId: Int, callback: (Boolean) -> Unit) {
        RoomManager.instance.getRoomInfo(roomId, { response ->
            response.second?.let {
                //Error
                mView.showError(it)
                callback(false)
            } ?: run {
                response?.first?.let {
                    it.save()
                    callback(true)
                }
            }
        })
    }
}