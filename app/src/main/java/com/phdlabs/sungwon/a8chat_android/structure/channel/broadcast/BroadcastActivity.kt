package com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.*
import com.demo.heartanimation.HeartLayout
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.channel.Comment
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.model.*
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.ui.GridVideoViewContainer
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.ui.SmallVideoViewAdapter
import com.phdlabs.sungwon.a8chat_android.structure.channel.broadcast.ui.VideoViewEventListener
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.queryFirst
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.android.synthetic.main.activity_broadcast.*
import kotlinx.android.synthetic.main.activity_channel_post_show.*
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Created by JPAM on 4/9/18.
 * Agora.io Live Video Broadcast
 * This activity should be instantiated with Client Role (Audience || Broadcaster)
 * Only if the Eight-Channel owner starts this activity it can be based in a Broadcaster
 */

open class BroadcastActivity : CoreActivity(), ChannelContract.Broadcast.View, AGEventHandler {

    //Dev
    private val log = LoggerFactory.getLogger(BroadcastActivity::class.java)

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_broadcast

    /*Container*/
    override fun contentContainerId(): Int = 0

    /*Controller*/
    override lateinit var controller: ChannelContract.Broadcast.Controller


    /*Message Properties*/
    private var mBroadcastMessage: Message? = null
    private var mMessageId: Int? = null
    private var mRoomId: Int = 0
    private var mOwnerId: Int = 0

    /*Live Commenting*/
    private lateinit var mCommentAdapter: BaseRecyclerAdapter<Comment, BaseViewHolder>
    private lateinit var mAdapterManager: LinearLayoutManager

    /*Video Properties*/
    private var mGridVideoViewContainer: GridVideoViewContainer? = null
    private var mSmallVideoViewAdapter: SmallVideoViewAdapter? = null
    private var mSmallVideoViewDock: RelativeLayout? = null
    private val mUidsList = HashMap<Int, SurfaceView>() // uid = 0 || uid == EngineConfig.mUid

    /*Client Role properties*/
    private fun isBroadcaster(cRole: Int): Boolean =
            cRole == io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER

    private fun isBroadcaster(): Boolean = isBroadcaster(config().mClientRole)

    /*Animation Properties*/
    private var mRandom = Random()
    private var mHeartLayout: HeartLayout? = null

    /*Video View Type*/
    private val VIEW_TYPE_DEFAULT = 0
    private val VIEW_TYPE_SMALL = 1
    private var mViewType = VIEW_TYPE_DEFAULT


    /*LifeCycle*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BroadcastController(this)
        getBroadcastMessageIntent()
        setupCommentRecycler()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
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

    override fun onDestroy() {
        super.onDestroy()
        doLeaveChannel()
        event().removeEventHandler(this)
        mUidsList.clear()
    }

    /**
     * Get messageId [Intent] from [MyChannelActivity]
     * and Query [Message]
     * @see Realm
     * */
    private fun getBroadcastMessageIntent() {
        //Event Handler
        event().addEventHandler(this)
        mRoomId = intent.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)
        intent.getIntExtra(Constants.IntentKeys.USER_ID, 0).let { userId ->
            if (userId != 0) {
                config().setUid(userId)
            }
        }
        //Eight Channel owner ID
        mOwnerId = intent.getIntExtra(Constants.IntentKeys.OWNER_ID, 0)
        //Broadcast message
        mMessageId = intent.getIntExtra(Constants.IntentKeys.BROADCAST_MESSAGE_ID, 0)
        //Broadcast Message info used for Live comments
        if (mMessageId != 0) {
            Message().queryFirst { equalTo("id", mMessageId) }?.let {
                mBroadcastMessage = it
            }
        } else {
            //No Broadcast message available
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        //Broadcast Client Role
        val cRole = intent.getIntExtra(Constants.Broadcast.ACTION_KEY_CROLE, 0)
        if (cRole == 0) {
            //No Client Broadcast role
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        doConfigEngine(cRole)
        /*Video Container*/
        mGridVideoViewContainer = findById(R.id.grid_video_view_container)
        mGridVideoViewContainer?.setItemEventHandler { v, item ->
            //Dev
            log.debug("onItemDoubleClick: $v , $item")
            if (mUidsList.size < 2) {
                return@setItemEventHandler
            }
            //TODO: Only allow a single Default View
            if (mViewType == VIEW_TYPE_DEFAULT) {
                val currentItem = item as VideoStatusData
                switchToSmallVideoView(currentItem.mUid)
            } else {
                switchToDefaultVideoView()
            }
        }
        //UI -> Broadcaster of Audience || Broadcaster
        if (isBroadcaster(cRole)) {
            val surfaceV = RtcEngine.CreateRendererView(applicationContext)
            rtcEngine().setupLocalVideo(VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, 0))
            surfaceV.setZOrderOnTop(true)
            surfaceV.setZOrderMediaOverlay(true)
            mUidsList[0] = surfaceV//Get first surface View
            mGridVideoViewContainer?.initViewContainer(applicationContext, 0, mUidsList)//First is now full view
            worker().preview(true, surfaceV, 0)
            broadcasterUI(ab_flip_camera, ab_mic_control)
        } else {
            audienceUI(ab_flip_camera, ab_mic_control)
        }
        //Heart Animation layout
        mHeartLayout = findById(R.id.heart_layout)
        //Join Agora.io Room with roomId as Room Name
        if (mRoomId != 0) {
            worker().joinChannel(mRoomId.toString(), config().mUid)
        } else {
            //Not a valid room
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        //Close Broadcast
        click_close.setOnClickListener {
            if (isBroadcaster(cRole)) {
                backToChannel()
            } else {
                onBackPressed()
            }
        }

    }

    /*Live Comments*/
    private fun setupCommentRecycler() {
        //Bind comment content
        mCommentAdapter = object : BaseRecyclerAdapter<Comment, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Comment?, position: Int, type: Int) {
                val profileImage = viewHolder?.get<ImageView>(R.id.vlc_friend_profile_picture)
                val profileName = viewHolder?.get<TextView>(R.id.vlc_friend_name)
                val comment = viewHolder?.get<TextView>(R.id.vlc_comment)
                //Load Commenter Information
                data?.user?.let {
                    Picasso.with(context)
                            .load(it.avatar)
                            .placeholder(R.drawable.ic_launcher_round)
                            .resize(45, 45)
                            .centerCrop()
                            .transform(CircleTransform())
                            .into(profileImage)
                    it.hasFullName().let {
                        if (it.first) {
                            it.second?.let {
                                profileName?.text = it
                            }
                        } else {
                            profileName?.text = data.user!!.first_name ?: ""
                        }
                    }
                }
                //Load Comment
                data?.comment?.let {
                    comment?.text = it
                }
            }

            //Comment view holder
            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder =
                    object : BaseViewHolder(R.layout.view_live_comment, inflater!!, parent) {}

        }
        mAdapterManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        ab_recycler_view_live_comments.layoutManager = mAdapterManager
        ab_recycler_view_live_comments.adapter = mCommentAdapter
        //Recycler automatic scrolling
        mCommentAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                mAdapterManager.smoothScrollToPosition(ab_recycler_view_live_comments, null, itemCount)
            }
        })
    }

    override fun updateCommentsRecycler(comments: ArrayList<Comment>) {
        //Show continuous dark background for comments
        ab_bottom_dark_view.visibility = View.VISIBLE
        //Only first comment shows on bottom to avoid layout expansion
        if (comments.count() >= 2) {
            mAdapterManager.reverseLayout = false
        }
        //Show comments
        mCommentAdapter.clear()
        mCommentAdapter.setItems(comments)
        mCommentAdapter.notifyDataSetChanged()
    }

    private fun broadcasterUI(cameraControl: ImageView, audioControl: ImageView) {
        //Camera Flip control
        Picasso.with(this).load(R.drawable.btn_switch_camera).transform(CircleTransform()).into(cameraControl)
        var cameraFlipped = false
        cameraControl.setOnClickListener {
            //Action
            worker().getRtcEngine()?.switchCamera()
            //Selection State
            cameraFlipped = if (!cameraFlipped) {
                cameraControl.setColorFilter(resources.getColor(R.color.blue_color_picker), PorterDuff.Mode.MULTIPLY)
                true
            } else {
                cameraControl.setColorFilter(resources.getColor(R.color.transparent))
                false
            }
        }

        //Mute Audio control
        Picasso.with(this).load(R.drawable.btn_mute).transform(CircleTransform()).into(audioControl)
        var audioMuted = false
        audioControl.setOnClickListener {
            //Action
            val tag = it.tag
            var flag = true
            if (tag != null && tag as Boolean) {
                flag = false
            }
            worker().getRtcEngine()?.muteLocalAudioStream(flag)
            //Selection State
            audioMuted = if (!audioMuted) {
                audioControl.setColorFilter(resources.getColor(R.color.blue_color_picker), PorterDuff.Mode.MULTIPLY)
                true
            } else {
                audioControl.setColorFilter(resources.getColor(R.color.transparent))
                false
            }
        }
    }

    //TODO: Test audience UI with Justin
    private fun audienceUI(likeControl: ImageView, comment: ImageView) {
        //Leave Broadcast UI
        click_close.text = getString(R.string.leave_broadcast)
        //Like Control
        ab_flip_camera.background = getDrawable(R.color.transparent)
        Picasso.with(this).load(R.drawable.ic_like).into(likeControl)
        likeControl.setOnClickListener {
            mMessageId?.let {
                controller.likePost(it)
            }
        }
        //Comment
        ab_mic_control.background = getDrawable(R.color.transparent)
        Picasso.with(this).load(R.drawable.ic_comment).into(comment)
        comment.setOnClickListener {
            isValidPost { comment ->
                if (comment.isBlank()) {
                    Toast.makeText(context, "Empty comment", Toast.LENGTH_SHORT).show()
                } else {
                    //Create comment
                    controller.commentPost(mMessageId.toString(), comment)

                }
            }
        }
    }

    /*Engine Config*/
    private fun doConfigEngine(cRole: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var prefIndex = sharedPreferences
                .getInt(Constants.Broadcast.PrefManager.PREF_PROPERTY_PROFILE_IDX, Constants.Broadcast.DEFAULT_PROFILE_IDX)
        if (prefIndex > Constants.Broadcast.VIDEO_PROFILES.count() - 1) {
            prefIndex = Constants.Broadcast.DEFAULT_PROFILE_IDX
        }
        val vProfile = Constants.Broadcast.VIDEO_PROFILES[prefIndex]
        worker().configEngine(cRole, vProfile)
    }

    private fun doLeaveChannel() {
        config().mChannel?.let {
            worker().leaveChannel(it)
            if (isBroadcaster()) {
                worker().preview(false, null, 0)
            }
        }
    }

    private fun backToChannel() {
        val intent = Intent()
        intent.putExtra(Constants.IntentKeys.BROADCAST_MESSAGE_ID, mMessageId!!)
        intent.putExtra(Constants.IntentKeys.ROOM_ID, mRoomId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun doRenderRemoteUI(uid: Int) {
        runOnUiThread(Runnable {
            if (isFinishing) {
                return@Runnable
            }

            val surfaceV = RtcEngine.CreateRendererView(applicationContext)
            surfaceV.setZOrderOnTop(true)
            surfaceV.setZOrderMediaOverlay(true)
            mUidsList[uid] = surfaceV
            if (config().mUid == uid) {
                rtcEngine().setupLocalVideo(VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid))
            } else {
                rtcEngine().setupRemoteVideo(VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid))
            }

            if (mViewType == VIEW_TYPE_DEFAULT) {
                log.debug("doRenderRemoteUi VIEW_TYPE_DEFAULT" + " " + (uid and 0xFFFFFFFFL.toInt()))
                switchToDefaultVideoView()
            } else {
                val bigBgUid = mSmallVideoViewAdapter?.exceptedUid
                bigBgUid?.let {
                    log.debug("doRenderRemoteUi VIEW_TYPE_SMALL" + " " +
                            (uid and 0xFFFFFFFFL.toInt()) + " " + (it and 0xFFFFFFFFL.toInt()))
                    switchToSmallVideoView(it)
                }
            }
        })
    }

    private fun doRemoveRemoteUI(uid: Int) {
        runOnUiThread(Runnable {
            if (isFinishing) {
                return@Runnable
            }
            mUidsList.remove(uid)
            var bigBgUid = -1
            mSmallVideoViewAdapter?.exceptedUid?.let {
                bigBgUid = it
            }
            log.debug("doRemoveRemoteUi " + (uid and 0xFFFFFFFFL.toInt()) + " " + (bigBgUid and 0xFFFFFFFFL.toInt()))
            if (mViewType == VIEW_TYPE_DEFAULT || uid == bigBgUid) {
                switchToDefaultVideoView()
            } else {
                switchToSmallVideoView(bigBgUid)
            }
        })
    }

    private fun requestRemoteStreamType(currentHostCount: Int) {
        //Dev
        log.debug("requestRemoteStreamType $currentHostCount")
        Handler().postDelayed({
            var highest: MutableMap.MutableEntry<Int, SurfaceView>? = null
            for (pair in mUidsList.entries) {
                //Dev
                log.debug("requestRemoteStreamType " + currentHostCount +
                        " local " + (config().mUid and 0xFFFFFFFFL.toInt()) +
                        " " + (pair.key and 0xFFFFFFFFL.toInt()) + " " + pair.value.height + " " + pair.value.width)

                if (pair.key != config().mUid && (highest == null || highest.value.height < pair.value.height)) {
                    if (highest != null) {
                        rtcEngine().setRemoteVideoStreamType(highest.key, io.agora.rtc.Constants.VIDEO_STREAM_LOW)
                        //Dev
                        log.debug("setRemoteVideoStreamType switch highest VIDEO_STREAM_LOW " +
                                currentHostCount + " " + (highest.key and 0xFFFFFFFFL.toInt()) +
                                " " + highest.value.width + " " + highest.value.height)
                    }
                    highest = pair
                } else if (pair.key != config().mUid && highest != null && highest.value.height >= pair.value.height) {
                    rtcEngine().setRemoteVideoStreamType(pair.key, io.agora.rtc.Constants.VIDEO_STREAM_LOW)
                    //Dev
                    log.debug("setRemoteVideoStreamType VIDEO_STREAM_LOW " + currentHostCount + " " +
                            (pair.key and 0xFFFFFFFFL.toInt()) + " " + pair.value.width + " " + pair.value.height)
                }
            }
            if (highest != null && highest.key != 0) {
                rtcEngine().setRemoteVideoStreamType(highest.key, io.agora.rtc.Constants.VIDEO_STREAM_HIGH)
                //Dev
                log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + currentHostCount + " " +
                        (highest.key and 0xFFFFFFFFL.toInt()) + " " + highest.value.width + " " + highest.value.height)
            }
        }, 500)
    }


    /*RTC Control*/
    protected fun rtcEngine(): RtcEngine =
            (application as Application).getWorkerThread()?.getRtcEngine()!!

    protected fun worker(): WorkerThread = (application as Application).getWorkerThread()!!

    protected fun config(): EngineConfig =
            (application as Application).getWorkerThread()?.getEngineConfig()!!

    protected fun event(): MyEngineEventHandler =
            (application as Application).getWorkerThread()?.eventHandler()!!

    /*VideoType Config*/
    private fun switchToSmallVideoView(uid: Int) {
        val slice = HashMap<Int, SurfaceView>(1)
        mUidsList[uid]?.let {
            slice.put(uid, it)
        }
        mGridVideoViewContainer?.initViewContainer(applicationContext, uid, slice)

        bindToSmallVideoView(uid)

        mViewType = VIEW_TYPE_SMALL

        requestRemoteStreamType(mUidsList.size)
    }

    private fun switchToDefaultVideoView() {
        mSmallVideoViewDock?.visibility = View.GONE
        mGridVideoViewContainer?.initViewContainer(applicationContext, config().mUid, mUidsList)

        mViewType = VIEW_TYPE_DEFAULT

        var sizeLimit = mUidsList.size
        if (sizeLimit > Constants.Broadcast.MAX_PEER_COUNT + 1) {
            sizeLimit = Constants.Broadcast.MAX_PEER_COUNT + 1
        }
        for (i in 0 until sizeLimit) {
            mGridVideoViewContainer?.getItem(i)?.mUid?.let {
                if (config().mUid != it) {
                    rtcEngine().setRemoteVideoStreamType(it, io.agora.rtc.Constants.VIDEO_STREAM_HIGH)
                    //Dev
                    log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + mUidsList.size + " " +
                            (it and 0xFFFFFFFFL.toInt()))
                }
            }
        }
    }

    private fun bindToSmallVideoView(exceptUid: Int) {
        if (mSmallVideoViewDock == null) {
            val stub = findById<ViewStub>(R.id.small_video_view_dock)
            mSmallVideoViewDock = stub.inflate() as RelativeLayout
        }
        val recycler = findById<RecyclerView>(R.id.small_video_view_container)

        var create = false

        if (mSmallVideoViewAdapter == null) {
            create = true
            mSmallVideoViewAdapter = SmallVideoViewAdapter(this, exceptUid, mUidsList, object : VideoViewEventListener {
                override fun onItemDoubleClick(v: View?, item: Any?) {
                    switchToDefaultVideoView()
                }
            })
            mSmallVideoViewAdapter?.setHasStableIds(true)
        }
        recycler.setHasFixedSize(true)

        recycler.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        recycler.adapter = mSmallVideoViewAdapter

        recycler.isDrawingCacheEnabled = true
        recycler.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_AUTO

        if (!create) {
            mSmallVideoViewAdapter?.notifyUiChanged(mUidsList, exceptUid, null, null)
        }
        recycler.visibility = View.VISIBLE
        mSmallVideoViewDock?.visibility = View.VISIBLE
    }

    /*AGEventHandler*/
    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
        //Nothing
    }

    override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        runOnUiThread(Runnable {
            if (isFinishing) {
                return@Runnable
            }

            if (mUidsList.containsKey(uid)) {
                log.debug("already added to UI, ignore it " + (uid and 0xFFFFFFFFL.toInt()) + " " + mUidsList[uid])
                return@Runnable
            }

            val isBroadcaster = isBroadcaster()
            log.debug("onJoinChannelSuccess $channel $uid $elapsed $isBroadcaster")

            worker().getEngineConfig()?.mUid = uid

            val surfaceV = mUidsList.remove(0)
            surfaceV?.let {
                mUidsList[uid] = it
            }
        })
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        log.debug("onUserOffline " + (uid and 0xFFFFFFFFL.toInt()) + " " + reason)
        doRemoveRemoteUI(uid)
        //If broadcaster ends broadcast terminate activity and go back to Channel
        if (uid == mOwnerId) { //TODO: Test broadcast ending
            backToChannel()
        }
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        doRenderRemoteUI(uid)
    }

    /*Animations*/
    override fun receivedLikeAnimation() {
        object : CountDownTimer(5000, 150) {
            override fun onTick(millisUntilFinished: Long) {
                mHeartLayout?.addHeart(randomColor())

            }

            override fun onFinish() {
                Log.d(TAG, "onFinish() called with: " + "")
            }
        }.start()
    }

    /**
     * [isValidPost]
     * Validates & Returns the comment message to be posted
     * @callback Comment on Post
     * */
    private fun isValidPost(callback: (String) -> Unit) {
        val alertDialog = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.view_dialog_comment, null)
        alertDialog.setView(dialogLayout)
        val commentEditText = dialogLayout.findViewById<EditText>(R.id.vdc_comment)

        commentEditText.minLines = 2
        commentEditText.setPadding(10, 0, 10, 0)
        alertDialog.setTitle("Comment")
        alertDialog.setPositiveButton("publish") { p0, p1 ->
            val comment = commentEditText.text.toString()
            callback(comment)
        }
        alertDialog.setNegativeButton("cancel") { p0, p1 ->
            callback("")
        }
        alertDialog.show()
    }

    private fun randomColor(): Int =
            Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255))

    /*Activity Getters*/
    override val get8Application: Application
        get() = application as Application

    override val getRoomId: Int
        get() = mRoomId

    override val getActivity: BroadcastActivity
        get() = this

}