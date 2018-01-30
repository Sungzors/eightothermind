package com.phdlabs.sungwon.a8chat_android.structure.event.create

import android.content.Intent
import android.content.pm.PackageManager
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.response.createEvent.EventPostResponse
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.event.EventContract
import com.phdlabs.sungwon.a8chat_android.structure.event.view.EventViewActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_event_create.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by SungWon on 1/2/2018.
 * Updated by JPAM on 1/29/2018
 */
class EventCreateActivity : CoreActivity(), EventContract.Create.View {

    /*Properties*/
    private var isLocked: Boolean = false

    private var mMedia: Media? = null

    /*UI*/
    override fun layoutId(): Int = R.layout.activity_event_create

    override fun contentContainerId(): Int = 0

    /*Controller*/
    override lateinit var controller: EventContract.Create.Controller

    override val getActivity: EventCreateActivity = this

    /*LifeCycle*/
    override fun onStart() {
        super.onStart()
        EventCreateController(this)
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

    /*User Interface*/
    private fun setUpViews() {
        setToolbarTitle(getString(R.string.create_event))
        showRightTextToolbar(getString(R.string.create))
        showBackArrow(R.drawable.ic_back)
        aec_lock_switch.setOnCheckedChangeListener { compoundButton, b ->
            isLocked = b
        }
    }

    /*On Click*/
    private fun setUpClickers() {

        /*Create Event*/
        toolbar_right_text.setOnClickListener {
            //controller.createEvent(aec_event_name.text.toString(), isLocked, aec_location_et.text.toString())
            //TODO: CreateEventPostData
        }

        /*Set Event picture*/
        aec_event_picture.setOnClickListener {
            controller.showPicture()
        }
    }

    /*Show Event Picture*/
    override fun setEventImage(filePath: String) {
        Picasso.with(context)
                .load("file://" + filePath)
                .placeholder(R.drawable.addphoto)
                .transform(CircleTransform())
                .into(aec_event_picture)
    }

    /*Get uploaded media & persist in lifecycle*/
    override fun getMedia(media: Media) {
        mMedia = media
    }

    /*Transition*/
    override fun onCreateEvent(data: EventPostResponse) {
        val a = data
        val intent = Intent(this, EventViewActivity::class.java)
        //TODO: Refactor intents
//        intent.putExtra(Constants.IntentKeys.EVENT_ID, data.event!!.newChannelGroupOrEvent!!.id)
//        intent.putExtra(Constants.IntentKeys.EVENT_NAME, data.event!!.newChannelGroupOrEvent!!.name)
//        intent.putExtra(Constants.IntentKeys.EVENT_LOCATION, data.event!!.newChannelGroupOrEvent!!.location_name)
//        intent.putExtra(Constants.IntentKeys.ROOM_ID, data.event!!.room!!.id)
        startActivity(intent)
    }

}