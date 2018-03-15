package com.phdlabs.sungwon.a8chat_android.structure.setting.mediaFragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.LinearLayout
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.files.File
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.structure.setting.channel.ChannelSettingsActivity
import com.phdlabs.sungwon.a8chat_android.structure.setting.chat.ChatSettingActivity
import com.phdlabs.sungwon.a8chat_android.utility.RoundedCornersTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.query
import kotlinx.android.synthetic.main.fragment_chat_setting_media.*


/**
 * Created by SungWon on 2/1/2018.
 * Updated by JPAM on 02/23/2018
 */
class MediaSettingFragment : CoreFragment(), SettingContract.MediaFragment.View {

    /*Controller*/
    override lateinit var controller: SettingContract.MediaFragment.Controller

    /*Layout*/
    override fun layoutId() = R.layout.fragment_chat_setting_media

    //singleton
    companion object {
        /**
         * [newInstanceChatRoom]
         * @param contactId
         * Prepares the fragment to show the media shared between two people in a private chat
         * */
        fun newInstanceChatRoom(contactId: Int): MediaSettingFragment {
            mContactId = contactId
            return MediaSettingFragment()
        }

        /**
         * [newInstanceChannelRoom]
         * @param roomId
         * Prepares the fragment to show the media shared in a Room (Channel)
         * */
        fun newInstanceChannelRoom(roomId: Int): MediaSettingFragment {
            mRoomId = roomId
            return MediaSettingFragment()
        }

        var mRoomId: Int? = null
        var mContactId: Int? = null
    }

    /*Properties*/
    private var mMediaList: List<Media> = emptyList()
    private val mIVList = mutableListOf<ImageView>()
    override lateinit var chatActivity: ChatSettingActivity
    override lateinit var channelActivity: ChannelSettingsActivity

    private var mContainerHeight = 0
    private var mContainerWidth = 0

    init {
        MediaSettingController(this)
    }

    /*LifeCycle*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Only a singlel value should be initialized
        mContactId?.let {
            chatActivity = activity as ChatSettingActivity
        }
        mRoomId?.let {
            channelActivity = activity as ChannelSettingsActivity
        }
    }

    override fun onStart() {
        super.onStart()
        controller.start()
        //Chat Room
        mContactId?.let {
            controller.queryMediaForChatRoom(it)?.let {
                mMediaList = it
                fcsm_container1.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        fcsm_container1.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        mContainerWidth = fcsm_container1.measuredWidth
                        mContainerHeight = fcsm_container2.measuredHeight
                        setUpIVs()
                    }
                })
                //Update Media count in UI
                chatActivity.updateSelectorTitle(String.format(getString(R.string.medianum, mMediaList.size.toString())), null)
            }
        }
        //Channel Room
        mRoomId?.let {
            controller.queryMediaForChannelRoom(it, {
                it?.let {
                    mMediaList = it
                    fcsm_container1.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            fcsm_container1.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            mContainerWidth = fcsm_container1.measuredWidth
                            mContainerHeight = fcsm_container2.measuredHeight
                            setUpIVs()
                        }
                    })
                    //Update Media count in UI
                    channelActivity.updateSelectorTitle(String.format(getString(R.string.medianum, mMediaList.size.toString())), null)
                }
            })
        }

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
        mRoomId = null
        mContactId = null
    }

    private fun setUpIVs() {
        var isLeft = true
        for (media in mMediaList) {
            val iv = ImageView(context)
            iv.background = activity?.getDrawable(R.drawable.media_background)
            iv.setColorFilter(Color.WHITE, PorterDuff.Mode.DARKEN)
            iv.elevation = 2f
            val lp = LinearLayout.LayoutParams(mContainerWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.bottomMargin = 30
            iv.layoutParams = LinearLayout.LayoutParams(lp.width, LinearLayout.LayoutParams.WRAP_CONTENT)

            if (isLeft) {
                fcsm_container1.addView(iv)
            } else {
                fcsm_container2.addView(iv)
            }
            mIVList.add(iv)
            Picasso.with(context).load(media.media_file).resize(mContainerWidth, 0).transform(RoundedCornersTransform(20, 8)).into(iv)
            isLeft = !isLeft
        }
    }
}