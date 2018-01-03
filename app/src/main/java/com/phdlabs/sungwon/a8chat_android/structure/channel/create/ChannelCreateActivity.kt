package com.phdlabs.sungwon.a8chat_android.structure.channel.create

import android.content.Intent
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.PostChannelData
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannels.MyChannelsListActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.Preferences
import kotlinx.android.synthetic.main.activity_channel_create.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by SungWon on 11/30/2017.
 */
class ChannelCreateActivity: CoreActivity(), ChannelContract.Create.View {

    private var isCheckedAddToProf: Boolean = false

    override fun layoutId(): Int = R.layout.activity_channel_create

    override fun contentContainerId(): Int = 0

    override lateinit var controller: ChannelContract.Create.Controller

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

    private fun setUpViews(){
        setToolbarTitle("Create a Channel")
        showRightTextToolbar("Create")
        showBackArrow(R.drawable.ic_back)
        acc_add_to_profile_button.setOnCheckedChangeListener { compoundButton, b ->
            isCheckedAddToProf = b
        }
    }

    private fun setUpClickers(){
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
            controller.uploadPicture()
        }

    }

    override fun finishActivity() {
        startActivity(Intent(this, MyChannelsListActivity::class.java))
        Toast.makeText(this, "Channel view screen in progress...", Toast.LENGTH_SHORT).show()
    }
}