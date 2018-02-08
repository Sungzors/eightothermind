package com.phdlabs.sungwon.a8chat_android.structure.setting.bottomtabfragments

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
import kotlinx.android.synthetic.main.fragment_chat_setting_media.*





/**
 * Created by SungWon on 2/1/2018.
 */
class MediaSettingFragment : CoreFragment(), SettingContract.MediaFragment.View {

    override lateinit var controller: SettingContract.MediaFragment.Controller

    override fun layoutId() = R.layout.fragment_chat_setting_media

    companion object {
        fun newInstance(): MediaSettingFragment = MediaSettingFragment()
    }

    private val mMediaList = mutableListOf<Media>()
    private val mFileList = mutableListOf<File>()
    private val mIVList = mutableListOf<ImageView>()

    private var mContainerHeight = 0
    private var mContainerWidth = 0

    override fun onStart() {
        super.onStart()
        MediaSettingController(this)
        controller.start()
        //TODO: dummy
        mMediaList.add(Media.Builder("https://i.imgur.com/dBZCHa0.jpg").build())
        mMediaList.add(Media.Builder("https://i.imgur.com/BusBwHf.jpg").build())
        mMediaList.add(Media.Builder("https://i.imgur.com/yEwgH7x.jpg").build())
        mMediaList.add(Media.Builder("https://i.imgur.com/nf4NCcE.gif").build())
        mMediaList.add(Media.Builder("https://i.imgur.com/s4YOwgB.jpg").build())
        mMediaList.add(Media.Builder("https://i.imgur.com/kxPCoac.jpg").build())
        mMediaList.add(Media.Builder("https://i.imgur.com/UG8En2q.jpg").build())
        //enddummy
        val csa = activity as ChatSettingActivity
        csa.updateMenuTitle(String.format(getString(R.string.medianum, mMediaList.size.toString())), String.format(getString(R.string.filenum, mFileList.size.toString())))
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

    private fun setUpIVs(){
        var isLeft = true
        for(media in mMediaList){
            val iv = ImageView(context)
            val lp = LinearLayout.LayoutParams(mContainerWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.bottomMargin = 15
            iv.layoutParams = lp

            if(isLeft){
                fcsm_container1.addView(iv)
            } else {
                fcsm_container2.addView(iv)
            }
            mIVList.add(iv)
            Picasso.with(context).load(media.media_file).resize(mContainerWidth,0).transform(RoundedCornersTransform(7, 0)).into(iv)
            isLeft = !isLeft
        }
    }
}