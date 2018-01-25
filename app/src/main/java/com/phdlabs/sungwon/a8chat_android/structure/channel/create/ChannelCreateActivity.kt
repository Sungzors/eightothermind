package com.phdlabs.sungwon.a8chat_android.structure.channel.create

import android.content.Intent
import android.widget.ImageView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.PostChannelData
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_channel_create.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by SungWon on 11/30/2017.
 */
class ChannelCreateActivity : CoreActivity(), ChannelContract.Create.View {

    private var isCheckedAddToProf: Boolean = false

    override fun layoutId(): Int = R.layout.activity_channel_create

    override fun contentContainerId(): Int = 0

    override lateinit var controller: ChannelContract.Create.Controller

    override val getActivity: ChannelCreateActivity = this

    private var channelImage: ImageView? = null

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

    private fun setUpViews() {
        setToolbarTitle("Create a Channel")
        showRightTextToolbar("Create")
        showBackArrow(R.drawable.ic_back)
        acc_add_to_profile_button.setOnCheckedChangeListener { compoundButton, b ->
            isCheckedAddToProf = b
        }
        channelImage = acc_channel_picture
    }

    /*Channel Image*/
    override val getChannelImage: ImageView? = channelImage

    override fun setChannelImage(filePath: String) {
        Picasso.with(context).load("file://" + filePath).transform(CircleTransform()).into(channelImage)
    }

    private fun setUpClickers() {
        toolbar_right_text.setOnClickListener {
            controller.createChannel(PostChannelData(
                    "1",
                    acc_channel_name.text.toString(),
                    acc_unique_id.text.toString(),
                    acc_short_description.text.toString(),
//                    null,
//                    null,
                    isCheckedAddToProf,
                    Preferences(this).getPreferenceInt(Constants.PrefKeys.USER_ID).toString()))
        }
        acc_channel_picture.setOnClickListener {
            controller.showPicture()
        }

    }

    override fun finishActivity(chanId: String, chanName: String, roomId: Int) {
        val intent = Intent(this, MyChannelActivity::class.java)
        intent.putExtra(Constants.IntentKeys.CHANNEL_ID, chanId)
        intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, chanName)
        intent.putExtra(Constants.IntentKeys.ROOM_ID, roomId)
        startActivity(intent)
    }
}