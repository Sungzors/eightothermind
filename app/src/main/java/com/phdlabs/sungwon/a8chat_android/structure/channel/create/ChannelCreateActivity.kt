package com.phdlabs.sungwon.a8chat_android.structure.channel.create

import android.content.Intent
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.PostChannelData
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_channel_create.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by SungWon on 11/30/2017.
 * Updated by JPAM on 01/25/2018
 */
class ChannelCreateActivity : CoreActivity(), ChannelContract.Create.View {

    /*Properties*/
    override fun layoutId(): Int = R.layout.activity_channel_create

    override fun contentContainerId(): Int = 0
    override lateinit var controller: ChannelContract.Create.Controller
    override val getActivity: ChannelCreateActivity = this

    //UI form
    private var isCheckedAddToProf: Boolean = false
    private var mMedia: Media? = null


    /*LifeCycle*/
    override fun onStart() {
        super.onStart()
        ChannelCreateController(this)
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
        controller.onPictureResult(requestCode, resultCode, data)

    }

    override fun finishActivity(chanId: String, chanName: String, roomId: Int) {
        val intent = Intent(this, MyChannelActivity::class.java)
        intent.putExtra(Constants.IntentKeys.CHANNEL_ID, chanId)
        intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, chanName)
        intent.putExtra(Constants.IntentKeys.ROOM_ID, roomId)
        startActivity(intent)
    }

    /*UI*/
    private fun setUpViews() {
        /*Toolbar & Controls*/
        setToolbarTitle("Create a Channel")
        showRightTextToolbar("Create")
        showBackArrow(R.drawable.ic_back)
        /*Form*/
        acc_add_to_profile_button.setOnCheckedChangeListener { compoundButton, b ->
            isCheckedAddToProf = b
        }
    }

    /*Set Channel Image*/
    override fun setChannelImage(filePath: String) {
        Picasso.with(context).load("file://" + filePath).transform(CircleTransform()).into(acc_channel_picture)
    }

    /*Get uploaded media*/
    override fun getMedia(media: Media) {
        mMedia = media
    }

    /*On Click*/
    private fun setUpClickers() {
        toolbar_right_text.setOnClickListener {
            /*Create Channel*/
            controller.createChannel(
                    PostChannelData(
                            mMedia?.id.toString().trim(),
                            acc_channel_name.text.toString().trim(),
                            acc_unique_id.text.toString().trim(),
                            acc_short_description.text.toString().trim(),
                            isCheckedAddToProf,
                            null
                    )
            )
        }
        /*Profile picture*/
        acc_channel_picture.setOnClickListener {
            controller.showPicture()
        }
    }
}