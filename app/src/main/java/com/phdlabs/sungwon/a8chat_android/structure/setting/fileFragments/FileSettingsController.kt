package com.phdlabs.sungwon.a8chat_android.structure.setting.fileFragments

import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.model.files.File
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by JPAM on 3/14/18.
 */
class FileSettingsController(val mView: SettingContract.FileFragment.View) :
        SettingContract.FileFragment.Controller {

    /*Properties*/

    init {
        mView.controller = this
    }

    /*LifeCycle*/
    override fun start() {

    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun stop() {
    }

    /*FILES*/
    override fun queryFiilesForChatRoom(chatRoomId: Int) {
        RoomManager.instance.getFilesFromPrivateChat(chatRoomId)?.let {
            if (it.count() > 0) {
                mView.updateFileAdapter(it)
            }
        }
    }

    override fun queryFilesForChannelRoom(roomId: Int) {
        val fileList = mutableListOf<File>()
        ChannelsManager.instance.queryChannelMessagesByType(roomId, Constants.MessageTypes.TYPE_FILE)?.let {
            //Iterate over messages for a file count
            for (message in it) {
                message.files?.let {
                    if (it.count() > 0) {
                        fileList.addAll(0, it)
                    }
                }
            }
            if (fileList.count() > 0) {
                mView.updateFileAdapter(fileList)
            }
        }
    }
}