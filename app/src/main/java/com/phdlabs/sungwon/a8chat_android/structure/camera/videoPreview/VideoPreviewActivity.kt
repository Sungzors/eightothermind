package com.phdlabs.sungwon.a8chat_android.structure.camera.videoPreview

import android.app.Activity
import android.app.LoaderManager
import android.content.Loader
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.camera.result.ResultHolder
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.phdlabs.sungwon.a8chat_android.utility.camera.ImageUtils
import com.phdlabs.sungwon.a8chat_android.utility.camera.LoadVideoAsyncLoader
import kotlinx.android.synthetic.main.activity_video_preview.*
import kotlinx.android.synthetic.main.view_camera_control_close.*
import kotlinx.android.synthetic.main.view_camera_control_save.*
import kotlinx.android.synthetic.main.view_camera_control_send.*
import net.protyposis.android.mediaplayer.MediaPlayer
import net.protyposis.android.mediaplayer.MediaSource

/**
 * Created by JPAM on 4/30/18.
 * Video Preview based on protyposis/MediaPlayer-Extended
 */
class VideoPreviewActivity : CoreActivity(), LoaderManager.LoaderCallbacks<MediaSource>, View.OnClickListener {

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_video_preview

    override fun contentContainerId(): Int = 0

    /*Properties*/
    private lateinit var mMediaPlayerControl: MediaController.MediaPlayerControl
    private lateinit var mMediaController: MediaController
    private lateinit var mVideoUri: Uri
    private var mVideoPosition: Int = 0
    private var mVideoPlaybackSpeed: Float = 1f
    private var mVideoPlaying: Boolean = false
    private var mVideoSource: MediaSource? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Video Utilities
        mMediaPlayerControl = avp_video_view //Dummy player control
        //Controller
        mMediaController = MediaController(this)
        mMediaController.setAnchorView(avp_top_container)
        mMediaController.setMediaPlayer(mMediaPlayerControl)
        mMediaController.isEnabled = false
        //UI
        setupUI()
        //Get Result Info for video playback
        ResultHolder.getResultVideo()?.let {
            mVideoUri = Uri.fromFile(it)
        } ?: kotlin.run {
            Toast.makeText(this, "No video found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * Restore Video Playback State if app went into background
     * */
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.let {
            mVideoUri = it.getParcelable("uri")
            mVideoPosition = it.getInt("position")
            mVideoPlaybackSpeed = it.getFloat("playbackSpeed")
            mVideoPlaying = it.getBoolean("playing")

        }
    }

    /**
     * Save Video state if activity goes into background
     * */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        avp_video_view?.let {
            mVideoPosition = it.currentPosition
            mVideoPlaybackSpeed = it.playbackSpeed
            mVideoPlaying = it.isPlaying
            //Video Uri stored
            outState?.putParcelable("uri", mVideoUri)
            outState?.putInt("position", mVideoPosition)
            outState?.putFloat("playbackSpeed", mVideoPlaybackSpeed)
            outState?.putBoolean("playing", mVideoPlaying)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!avp_video_view.isPlaying) {
            initPlayer()
        }
    }

    override fun onStop() {
        mMediaController.hide()
        super.onStop()
    }

    /**
     * [setupUI]
     * Handles reused views for [EditingActivity]
     * and click controls
     * */
    private fun setupUI() {
        //Hide Unwanted controls
        clear_all_tv.visibility = View.GONE
        clear_all_text_tv.visibility = View.GONE
        iv_undo.visibility = View.GONE
        undo_text_tv.visibility = View.GONE
        //Click listeners
        cc_close_back.setOnClickListener(this)
        iv_camera_save.setOnClickListener(this)
        iv_camera_send.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0) {
        /*Close Video Preview*/
            cc_close_back -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        /*Save Video Locally*/
            iv_camera_save -> {
                //Save video to external storage & send broadcast to gallery
                ResultHolder.getResultVideo()?.let {
                    CameraControl.instance.addToGallery(context,
                            ImageUtils.instance.saveVideoWithSuffix(context, "8", it.absolutePath)
                    )
                }

                Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
            }
        /*Share Video*/
            iv_camera_send -> {
                //TODO: Send Video to Share Screen -> Should generate a thumbnail
                Toast.makeText(this, "Should transition to share screen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    /**
     * [initPlayer]
     * Start Video Playback on Video View
     * */
    private fun initPlayer() {
        //Video Listener
        avp_video_view.setOnPreparedListener { mp ->
            //UI
            mMediaController.isEnabled = true
            mMediaController.show()
            //Cue Controls
            mp?.addCue(1000, "test cue @ 1000")
            mp?.addCue(2000, "test cue @ 2000")
            mp?.addCue(3000, "test cue @ 3000")
            mp?.addCue(10000, "test cue @ 10000")
            mp?.setOnCueListener { mp, cue ->
                //Dev
                Log.d(TAG, cue.toString() + "current position: ${mp?.currentPosition}")
            }
        }
        //Error Listener
        avp_video_view.setOnErrorListener { _, _, _ ->
            Toast.makeText(this@VideoPreviewActivity, "Can't play video", Toast.LENGTH_SHORT).show()
            mMediaController.isEnabled = false
            true
        }
        //Information Listener
        avp_video_view.setOnInfoListener { _, what, _ ->
            var whatInfo = ""
            when (what) {
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    whatInfo = "MEDIA_INFO_BUFFERING_END"
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    whatInfo = "MEDIA_INFO_BUFFERING_START"
                }
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                    whatInfo = "MEDIA_INFO_VIDEO_RENDERING_START"
                }
                MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING -> {
                    whatInfo = "MEDIA_INFO_VIDEO_TRACK_LAGGING"
                }
            }
            //Dev
            Log.d(TAG, "onInfo: $whatInfo")
            false
        }
        //Seek Listeners
        avp_video_view.setOnSeekListener {
            //Dev
            Log.d(TAG, "onSeek")
        }
        avp_video_view.setOnSeekCompleteListener {
            //Dev
            Log.d(TAG, "onSeekComplete")
        }
        //Buffering
        avp_video_view.setOnBufferingUpdateListener { _, percent ->
            //Dev
            Log.d(TAG, "onBufferingUpdate: $percent")
        }

        mVideoSource?.let {
            startVideoPlayback(it)
        } ?: run {
            val bundle = Bundle()
            bundle.putParcelable("uri", mVideoUri)
            this.loaderManager.initLoader(0, bundle, this).forceLoad()
        }

    }


    /**
     * Video Async Loader callbacks
     * */
    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<MediaSource> = LoadVideoAsyncLoader(this, p1)

    override fun onLoadFinished(p0: Loader<MediaSource>?, p1: MediaSource?) {
        p1?.let {
            startVideoPlayback(it)
        }
    }

    override fun onLoaderReset(p0: Loader<MediaSource>?) {
        mVideoSource = null
    }

    /**
     * [startVideoPlayback]
     * Start video playback when [mVideoSource] is available
     * */
    private fun startVideoPlayback(videoSource: MediaSource?) {
        videoSource?.let {
            mVideoSource = it
            avp_video_view.setVideoSource(mVideoSource)
            avp_video_view.seekTo(mVideoPosition)
            avp_video_view.playbackSpeed = mVideoPlaybackSpeed
            if (mVideoPlaying) {
                avp_video_view.start()
            }
        }
    }

    /**
     * [onTouchEvent]
     * Handles showing and hiding the video preview controls
     * */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (mMediaController.isShowing) {
                mMediaController.hide()
            } else {
                mMediaController.show()
            }
        }
        return super.onTouchEvent(event)
    }
}