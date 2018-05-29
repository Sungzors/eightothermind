package com.phdlabs.sungwon.a8chat_android.structure.event.create

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.EventPostData
import com.phdlabs.sungwon.a8chat_android.model.event.EventsEight
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
class EventCreateActivity : CoreActivity(), EventContract.Create.View, View.OnClickListener {

    /*Properties*/
    private var isLockedAfter24Hours: Boolean = false

    private var mMedia: Media? = null

    private var mLocation: Pair<String?, String?>? = null

    /**
     * [mPrivacy] default value
     * [Constants.EventPrivacy.ONLY_FRIENDS]
     * */
    private var mPrivacy: String = Constants.EventPrivacy.ONLY_FRIENDS

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
        /*Toolbar*/
        setToolbarTitle(getString(R.string.create_event))
        showRightTextToolbar(getString(R.string.create))
        showBackArrow(R.drawable.ic_back)
        /*Locked after 24 hours*/
        aec_lock_switch.setOnCheckedChangeListener { _, b ->
            isLockedAfter24Hours = b
        }
        /*Click listeners*/
        toolbar_right_text.setOnClickListener(this)
        aec_event_picture.setOnClickListener(this)
        aec_friend_of_friends_checkbox.setOnClickListener(this)
        aec_only_friends_checkbox.setOnClickListener(this)
    }

    /*On Click*/
    override fun onClick(p0: View?) {
        when (p0) {
        /*Create Event*/
            toolbar_right_text -> {
                controller.createEvent(EventPostData(
                        mMedia?.id.toString().trim(),
                        mLocation?.first,
                        mLocation?.second,
                        null,
                        aec_event_name.text.toString().trim(),
                        mPrivacy,
                        aec_location_et.text.toString().trim(),
                        isLockedAfter24Hours))
            }
        /*Set Event picture*/
            aec_event_picture -> {
                controller.showPicture()
            }
        /*Privacy*/
            aec_friend_of_friends_checkbox -> {
                if (aec_friend_of_friends_checkbox.isChecked) {
                    aec_only_friends_checkbox.isChecked = false
                    mPrivacy = Constants.EventPrivacy.FRIENDS_OF_FRIENDS
                }

            }
            aec_only_friends_checkbox -> {
                if (aec_only_friends_checkbox.isChecked) {
                    aec_friend_of_friends_checkbox.isChecked = false
                    mPrivacy = Constants.EventPrivacy.ONLY_FRIENDS
                }
            }

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
    override fun setMedia(media: Media) {
        mMedia = media
    }

    /*Get updated user location -> lat, lng*/
    override fun getLocation(location: Pair<String?, String?>?) {
        mLocation = location
    }

    /*Transition*/
    override fun onCreateEvent(event: EventsEight?) {
        //Transition to Event activity
        val intent = Intent(this, EventViewActivity::class.java)
        intent.putExtra(Constants.IntentKeys.EVENT_ID, event?.id)
        intent.putExtra(Constants.IntentKeys.EVENT_NAME, event?.name)
        intent.putExtra(Constants.IntentKeys.EVENT_LOCATION, event?.location_name)
        intent.putExtra(Constants.IntentKeys.ROOM_ID, event?.room_id)
        startActivity(intent)
        //Finish this activity
        setResult(Activity.RESULT_OK)
        finish()
    }

}