package com.phdlabs.sungwon.a8chat_android.structure.setting.mediaFragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.LinearLayout
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.files.File
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
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
        fun newInstance(contactId: Int): MediaSettingFragment {
            mContactId = contactId
            return  MediaSettingFragment()
        }
        var mContactId: Int? = null
    }

    /*Properties*/
    private var mMediaList:List<Media> = emptyList()
    private val mFileList = mutableListOf<File>()
    private val mIVList = mutableListOf<ImageView>()

    private var mContainerHeight = 0
    private var mContainerWidth = 0

    /*LifeCycle*/
    override fun onStart() {
        super.onStart()
        MediaSettingController(this)
        controller.start()

        mMediaList = Media().query { it.equalTo("sharedWithUserId", mContactId) ?: -1}
        val csa = activity as ChatSettingActivity
        csa.updateMenuTitle(String.format(getString(R.string.medianum, mMediaList.size.toString())),
                String.format(getString(R.string.filenum, mFileList.size.toString())))
        fcsm_container1.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fcsm_container1.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mContainerWidth = fcsm_container1.measuredWidth
                mContainerHeight = fcsm_container2.measuredHeight
                setUpIVs()
            }
        })
//        setUpIVs()
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