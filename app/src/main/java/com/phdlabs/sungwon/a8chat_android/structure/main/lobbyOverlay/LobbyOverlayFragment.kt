package com.phdlabs.sungwon.a8chat_android.structure.main.lobbyOverlay

import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.room.RoomManager
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.structure.main.LobbyContract
import com.phdlabs.sungwon.a8chat_android.structure.main.MainActivity
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_lobby_overlay.*

/**
 * Created by SungWon on 4/24/2018.
 */
class LobbyOverlayFragment : CoreFragment(), LobbyContract.Overlay.View{
    override lateinit var controller: LobbyContract.Overlay.Controller

    override fun layoutId(): Int = R.layout.fragment_lobby_overlay

    companion object {
        private var mRoomId = 0
        private var mUserName = ""
        private var mUserPic = ""
        private var mUserPhone = ""

        fun newInstance(roomId: Int, userName: String, userPic: String, userPhone: String): LobbyOverlayFragment{
            this.mRoomId = roomId
            this.mUserName = userName
            this.mUserPic = userPic
            this.mUserPhone = userPhone
            return LobbyOverlayFragment()
        }
    }

    override fun onStart() {
        super.onStart()
        LobbyOverlayController(this, mRoomId)
        setupClicker()
        setupView()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun setupClicker(){
        flo_close.setOnClickListener {
            getActivityDirect().removeFragment(this)
        }
        flo_bg.setOnClickListener {
            getActivityDirect().removeFragment(this)
        }
        flo_mute.setOnClickListener {
            RoomManager.instance.toggleNotification(mRoomId, false, {
                it?.let {

                } ?: kotlin.run {
                    Toast.makeText(context, "Notifications from " + mUserName + " muted", Toast.LENGTH_SHORT).show()
                }
            })
        }
        flo_call.setOnClickListener {
            Toast.makeText(context, "call clicked", Toast.LENGTH_SHORT).show()
        }
        flo_money.setOnClickListener {
            Toast.makeText(context, "money clicked", Toast.LENGTH_SHORT).show()

        }
        flo_media.setOnClickListener {
            Toast.makeText(context, "media clicked", Toast.LENGTH_SHORT).show()
        }
        flo_file.setOnClickListener {
            Toast.makeText(context, "file clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupView(){
        Picasso.with(context).load(mUserPic).placeholder(R.drawable.ic_launcher_round).transform(CircleTransform()).into(flo_picture_event)
        flo_title.text = mUserName
        flo_message.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        flo_message.text = mUserPhone
    }

    override fun getActivityDirect(): MainActivity = activity as MainActivity
}