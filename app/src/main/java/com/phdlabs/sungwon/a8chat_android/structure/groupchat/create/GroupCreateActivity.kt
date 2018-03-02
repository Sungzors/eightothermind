package com.phdlabs.sungwon.a8chat_android.structure.groupchat.create

import android.content.Intent
import android.content.pm.PackageManager
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.GroupChatContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import kotlinx.android.synthetic.main.activity_group_create.*

/**
 * Created by SungWon on 3/1/2018.
 */
class GroupCreateActivity : CoreActivity(), GroupChatContract.Create.View{

    override lateinit var controller: GroupChatContract.Create.Controller

    override fun layoutId(): Int  = R.layout.activity_group_create

    override fun contentContainerId(): Int = 0

    private val mMembersList = mutableListOf<Contact>()

    override fun onStart() {
        super.onStart()
        GroupCreateController(this)
        controller.start()
        setUpViews()
        setUpClickers()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CameraControl.instance.requestCode()) {
            controller.onPictureResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.PermissionsReqCode.CAMERA_REQ_CODE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                showError(getString(R.string.request_camera_permission))
            } else {
                controller.showPicture()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun setUpViews() {
        setToolbarTitle(getString(R.string.create_event))
        showRightTextToolbar(getString(R.string.create))
        showBackArrow(R.drawable.ic_back)
    }

    private fun setUpClickers() {
        agc_group_picture.setOnClickListener {
            controller.showPicture()
        }
        agc_add_people_container.setOnClickListener {

        }
    }
}