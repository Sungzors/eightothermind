package com.phdlabs.sungwon.a8chat_android.structure.favorite.message

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.structure.channel.postshow.ChannelPostShowActivity
import com.phdlabs.sungwon.a8chat_android.structure.chat.ChatActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.favorite.FavoriteContract
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.GroupChatActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.RoundedCornersTransform
import com.phdlabs.sungwon.a8chat_android.utility.SuffixDetector
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import cz.intik.overflowindicator.OverflowPagerIndicator
import cz.intik.overflowindicator.SimpleSnapHelper
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_favorites.*
import java.text.SimpleDateFormat

/**
 * Created by SungWon on 3/20/2018.
 */
class FavoriteMessageActivity : CoreActivity(), FavoriteContract.Message.View{

    override lateinit var controller: FavoriteContract.Message.Controller
    private lateinit var mAdapter: BaseRecyclerAdapter<Message, BaseViewHolder>
    private lateinit var mPostMediaAdapter: BaseRecyclerAdapter<Media, BaseViewHolder>

    override fun layoutId(): Int = R.layout.activity_favorites

    override fun contentContainerId(): Int = 0

    private var mRoomId: Int = 0

    /*1 for chat 2 for channel 3 for profle*/
    private var mType = 0


    override fun onStart() {
        super.onStart()
        FavoriteMessageController(this)
        mRoomId = intent?.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)!!
        mType = intent?.getIntExtra(Constants.IntentKeys.FAVE_TYPE, 0)!!
        setToolbarTitle("Favorite Messages")
        showBackArrow(R.drawable.ic_back)

        if(mType == 3) controller.getFavorites(mRoomId, true) else controller.getFavorites(mRoomId, false)
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

    override fun setUpRecycler(messages: MutableList<Message>) {
        mAdapter = object : BaseRecyclerAdapter<Message, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Message?, position: Int, type: Int) {
                when(mType){
                    1 -> {
                        val name = viewHolder?.get<TextView>(R.id.cvfl_sender_name)
                        val date = viewHolder?.get<TextView>(R.id.cvfl_send_time)
                        val pic = viewHolder?.get<ImageView>(R.id.cvfl_profile_pic)
                        name?.text = data?.getUserName()
                        val formatter = SimpleDateFormat("EEE - h:mm aaa")
                        date?.text = formatter.format(data?.createdAt)
                        Picasso.with(context!!).load(data?.user?.avatar).transform(CircleTransform()).placeholder(R.drawable.addphoto).into(pic)

                        when(data!!.type){
                            Constants.MessageTypes.TYPE_STRING -> bindMessageViewHolder(viewHolder!!, data)
                            Constants.MessageTypes.TYPE_CONTACT -> bindContactViewHolder(viewHolder!!, data)
                            Constants.MessageTypes.TYPE_LOCATION -> bindLocationViewHolder(viewHolder!!, data)
                            Constants.MessageTypes.TYPE_FILE -> bindFileViewHolder(viewHolder!!, data)
                            Constants.MessageTypes.TYPE_MEDIA -> bindMediaViewHolder(viewHolder!!, data)
                            Constants.MessageTypes.TYPE_MONEY -> bindMoneyViewHolder(viewHolder!!, data)
                            Constants.MessageTypes.TYPE_CHANNEL -> bindChannelViewHolder(viewHolder!!, data)
                        }

                        viewHolder?.get<LinearLayout>(R.id.card_view)?.setOnClickListener {
                            setResult(Constants.ResultCode.SUCCESS)
                            finish()
                        }


                    }
                    2 -> {
                        when(type){
                            0 -> bindMessageChannel(viewHolder!!, data!!)
                            1 -> bindMediaChannel(viewHolder!!, data!!)
                            2 -> bindPostChannel(viewHolder!!, data!!)
                            3 -> bindFileChannel(viewHolder!!, data!!)
                        }

                        viewHolder?.get<CardView>(R.id.card_view)?.setOnClickListener {
                            setResult(Constants.ResultCode.SUCCESS)
                            finish()
                        }
                    }
                    3 -> {
                        if(data?.channel == null){
                            val name = viewHolder?.get<TextView>(R.id.cvfl_sender_name)
                            val date = viewHolder?.get<TextView>(R.id.cvfl_send_time)
                            val pic = viewHolder?.get<ImageView>(R.id.cvfl_profile_pic)
                            name?.text = data?.getUserName()
                            val formatter = SimpleDateFormat("EEE - h:mm aaa")
                            date?.text = formatter.format(data?.createdAt)
                            Picasso.with(context!!).load(data?.user?.avatar).transform(CircleTransform()).placeholder(R.drawable.addphoto).into(pic)

                            when(data!!.type){
                                Constants.MessageTypes.TYPE_STRING -> bindMessageViewHolder(viewHolder!!, data)
                                Constants.MessageTypes.TYPE_CONTACT -> bindContactViewHolder(viewHolder!!, data)
                                Constants.MessageTypes.TYPE_LOCATION -> bindLocationViewHolder(viewHolder!!, data)
                                Constants.MessageTypes.TYPE_FILE -> bindFileViewHolder(viewHolder!!, data)
                                Constants.MessageTypes.TYPE_MEDIA -> bindMediaViewHolder(viewHolder!!, data)
                                Constants.MessageTypes.TYPE_MONEY -> bindMoneyViewHolder(viewHolder!!, data)
                                Constants.MessageTypes.TYPE_CHANNEL -> bindChannelViewHolder(viewHolder!!, data)
                            }
                            viewHolder?.get<LinearLayout>(R.id.card_view)?.setOnClickListener {
                                if(data.roomType == "private"){
                                    val intent = Intent(context, ChatActivity::class.java)
                                    intent.putExtra(Constants.IntentKeys.CHAT_NAME, data.user?.first_name + " " + data.user?.last_name)
                                    intent.putExtra(Constants.IntentKeys.ROOM_ID, data.roomId)
                                    intent.putExtra(Constants.IntentKeys.CHAT_PIC, data.user?.avatar
                                            ?: "")
                                    startActivity(intent)
                                } else if (data.roomType == "group"){
                                    val intent = Intent(context, GroupChatActivity::class.java)
                                    intent.putExtra(Constants.IntentKeys.ROOM_ID, data.roomId)
                                }
                            }
                        } else {
                            when(type){
                                0 -> bindMessageChannel(viewHolder!!, data)
                                1 -> bindMediaChannel(viewHolder!!, data)
                                2 -> bindPostChannel(viewHolder!!, data)
                                3 -> bindFileChannel(viewHolder!!, data)
                            }
                            viewHolder?.get<CardView>(R.id.card_view)?.setOnClickListener {
                                val intent = Intent(context, MyChannelActivity::class.java)
                                intent.putExtra(Constants.IntentKeys.CHANNEL_ID, data.channel?.id)
                                intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, data.channel?.name)
                                intent.putExtra(Constants.IntentKeys.ROOM_ID, data.roomId)
                                intent.putExtra(Constants.IntentKeys.OWNER_ID, data.channel?.user_creator_id)
                                startActivity(intent)
                            }
                        }
                    }
                }



            }

            override fun getItemType(t: Message?): Int {

                if(t?.channel == null){
                    return -1
                }

                //Check for files
                t?.files?.let {
                    if (it.count() > 0) {
                        return 4
                    }
                }

                //Check for media
                t?.mediaArray?.let {
                    if (it.count() > 0) {
                        //Media Post || Media
                        return if (t.post!!) {
                            //Media Post
                            2
                        } else {
                            //Media Only
                            1
                        }
                    } else {
                        t.let {
                            return if (t.post!!) {
                                //Message Post
                                3
                            } else {
                                //Message
                                0
                            }
                        }
                    }
                } ?: run {
                    t?.let {
                        t.post?.let {
                            return if (it) {
                                3
                            } else {
                                0
                            }
                        }
                    }
                }
                return 0 //Message as default
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {

                when (type){
                    -1 -> {
                        return object : BaseViewHolder(R.layout.card_view_favorites_list, inflater!!, parent){

                        }
                    }
                /*Message*/
                    0 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_string, inflater!!, parent) {

                        }
                    }
                /*Media*/
                    1 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_photo, inflater!!, parent) {

                        }
                    }
                /*Post*/
                    2 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_media, inflater!!, parent) {

                        }
                    }
                /*Message Post*/
                    3 -> {
                        return object : BaseViewHolder(R.layout.card_view_post_message_no_media, inflater!!, parent) {

                        }
                    }
                /*Files Post*/
                    4 -> {
                        return object : BaseViewHolder(R.layout.card_view_files, inflater!!, parent) {

                        }
                    }

                }

                return object : BaseViewHolder(R.layout.card_view_post_media, inflater!!, parent) {

                }
            }
        }
        mAdapter.setItems(messages)
        val layoutManager = LinearLayoutManager(context)
        af_recycler.layoutManager = layoutManager
        af_recycler.adapter = mAdapter
    }

    private fun bindMessageViewHolder(viewHolder: BaseViewHolder, message: Message){
        val messagetv = viewHolder.get<TextView>(R.id.cvfl_message)
        val messagePic = viewHolder.get<ImageView>(R.id.cvfl_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvfl_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvfl_message_button_money_decline)
        messagePic.visibility = ImageView.GONE
        messagetv.visibility = TextView.VISIBLE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.black))
        messagetv.text = message.message
    }

    private fun bindContactViewHolder(viewHolder: BaseViewHolder, message: Message){
        val messagetv = viewHolder.get<TextView>(R.id.cvfl_message)
        val messagePic = viewHolder.get<ImageView>(R.id.cvfl_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvfl_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvfl_message_button_money_decline)
        messagePic.visibility = ImageView.VISIBLE
        messagetv.visibility = TextView.VISIBLE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        messagetv.text = message.contactInfo!!.first_name + message.contactInfo?.last_name
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.confirmText))
        Picasso.with(this).load(message.contactInfo?.avatar).placeholder(R.drawable.addphoto).transform(CircleTransform()).into(messagePic)
    }

    private fun bindLocationViewHolder(viewHolder: BaseViewHolder, message: Message){
        val messagetv = viewHolder.get<TextView>(R.id.cvfl_message)
        val messagePic = viewHolder.get<ImageView>(R.id.cvfl_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvfl_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvfl_message_button_money_decline)
        messagePic.visibility = ImageView.GONE
        messagetv.visibility = TextView.VISIBLE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        messagetv.text = message.locationInfo?.streetAddress
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.confirmText))
    }

    private fun bindFileViewHolder(viewHolder: BaseViewHolder, message: Message){
        val messagetv = viewHolder.get<TextView>(R.id.cvfl_message)
        val messagePic = viewHolder.get<ImageView>(R.id.cvfl_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvfl_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvfl_message_button_money_decline)
        messagePic.visibility = ImageView.VISIBLE
        messagetv.visibility = TextView.VISIBLE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        message.files?.let {
            messagetv.text = it[0]?.file_string
        }
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.confirmText))
        Picasso.with(this).load(R.drawable.ic_attachment_file).into(messagePic)
        messagetv.setOnClickListener {
//            message.files?.let {
//                val mime = MimeTypeMap.getSingleton()
//                val intent = Intent(Intent.ACTION_VIEW)
//                val mimeType = mime.getMimeTypeFromExtension(SuffixDetector.INSTANCE.getFileSuffix(it[0]?.file_string.toString()))
//                intent.setDataAndType(Uri.parse(it[0]?.s3_url), mimeType)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                try {
//                    context?.startActivity(intent)
//                } catch (e: ActivityNotFoundException) {
//                    Toast.makeText(this, "Can't open this type of File", Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    }

    private fun bindMediaViewHolder(viewHolder: BaseViewHolder, message: Message){
        val messagetv = viewHolder.get<TextView>(R.id.cvfl_message)
        val messagePic = viewHolder.get<ImageView>(R.id.cvfl_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvfl_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvfl_message_button_money_decline)
        messagetv.visibility = TextView.GONE
        messagePic.visibility = ImageView.GONE
        picContainer1.visibility = LinearLayout.VISIBLE
        picContainer2.visibility = LinearLayout.VISIBLE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        if (picContainer1.childCount > 0) {
            picContainer1.removeAllViews()
        }
        if (picContainer2.childCount > 0) {
            picContainer2.removeAllViews()
        }
        var i = 0
        for (media in message.mediaArray!!) {
            val iv = ImageView(context)
            if (i < 3) {
                val lp = LinearLayout.LayoutParams(240, 240)
                lp.marginEnd = 15
                lp.bottomMargin = 15
                iv.layoutParams = lp
                picContainer1.addView(iv)
                Picasso.with(context).load(media.media_file).resize(240, 240).centerCrop().transform(RoundedCornersTransform(7, 0)).into(iv)
            } else if (i > 2 || i < 7) {
                val lp = LinearLayout.LayoutParams(180, 180)
                lp.marginEnd = 10
                lp.bottomMargin = 15
                iv.layoutParams = lp
                picContainer2.addView(iv)
                Picasso.with(context).load(media.media_file).resize(240, 240).centerCrop().transform(RoundedCornersTransform(7, 0)).into(iv)
            }
            i++
        }
    }

    private fun bindMoneyViewHolder(viewHolder: BaseViewHolder, message: Message){
        val messagetv = viewHolder.get<TextView>(R.id.cvfl_message)
        val messagePic = viewHolder.get<ImageView>(R.id.cvfl_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvfl_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvfl_message_button_money_decline)
        messagetv.visibility = TextView.VISIBLE
        messagePic.visibility = ImageView.VISIBLE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.VISIBLE
        moneyButtonDecline.visibility = Button.VISIBLE
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.black))
        Picasso.with(this).load(R.drawable.ic_money).into(messagePic)
    }

    private fun bindChannelViewHolder(viewHolder: BaseViewHolder, message: Message){
        val messagetv = viewHolder.get<TextView>(R.id.cvfl_message)
        val messagePic = viewHolder.get<ImageView>(R.id.cvfl_message_pic_small)
        val picContainer1 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_1)
        val picContainer2 = viewHolder.get<LinearLayout>(R.id.cvfl_message_pic_container_2)
        val moneyButtonAccept = viewHolder.get<Button>(R.id.cvfl_message_button_money_accept)
        val moneyButtonDecline = viewHolder.get<Button>(R.id.cvfl_message_button_money_decline)
        messagePic.visibility = ImageView.VISIBLE
        messagetv.visibility = TextView.VISIBLE
        picContainer1.visibility = LinearLayout.GONE
        picContainer2.visibility = LinearLayout.GONE
        moneyButtonAccept.visibility = Button.GONE
        moneyButtonDecline.visibility = Button.GONE
        messagetv.text = message.channelInfo!!.name
        messagetv.setTextColor(ContextCompat.getColor(this, R.color.confirmText))
        Picasso.with(this).load(message.channelInfo!!.avatar).transform(CircleTransform()).placeholder(R.drawable.addphoto).into(messagePic)
    }

    private fun bindMessageChannel(viewHolder: BaseViewHolder, data: Message){
        val pic = viewHolder.get<ImageView>(R.id.cvps_poster_pic)
        val text = viewHolder.get<TextView>(R.id.cvps_post_text)
        val posterName = viewHolder.get<TextView>(R.id.cvps_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvps_post_date)

        Picasso.with(this).load(data.channel!!.avatar).transform(CircleTransform()).into(pic)
        text.text = data.message
        posterName.text = data.channel?.name
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
    }

    private fun bindMediaChannel(viewHolder: BaseViewHolder, data: Message){
        val pic = viewHolder.get<ImageView>(R.id.cvpp_poster_pic)
        val postPic = viewHolder.get<ImageView>(R.id.cvpp_post_pic)
        val posterName = viewHolder.get<TextView>(R.id.cvpp_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvpp_post_date)

        Picasso.with(this).load(data.channel!!.avatar).transform(CircleTransform()).into(pic)
        data.mediaArray?.let {
            Picasso.with(this).load(it[0]?.media_file).into(postPic)
        }
        posterName.text = data.channel?.name
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
    }

    private fun bindPostChannel(viewHolder: BaseViewHolder, data: Message){
//Post Owner
        val picasso = Picasso.with(this)
        val posterPic = viewHolder.get<ImageView>(R.id.cvpm_poster_pic)
        val posterName = viewHolder.get<TextView>(R.id.cvpm_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvpm_post_date)
        //Media Recycler
        val postPicRecycler = viewHolder.get<RecyclerView>(R.id.cvpm_post_pic_recycler)
        //Page Indicator
        val pageIndicator = viewHolder.get<OverflowPagerIndicator>(R.id.cvpm_view_pager_indicator)
        setupMediaRecycler(data.mediaArray, postPicRecycler, data, pageIndicator)
        //Actions
        val likeButton = viewHolder.get<ImageView>(R.id.cvpm_like_button)
        val commentButton = viewHolder.get<ImageView>(R.id.cvpm_comment_button)
        val postText = viewHolder.get<TextView>(R.id.cvpm_post_text)
        val likeCount = viewHolder.get<TextView>(R.id.cvpm_like_count)
        val commentCount = viewHolder.get<TextView>(R.id.cvpmnm_comment_count)
        //Load info
        picasso.load(data.channel!!.avatar).transform(CircleTransform()).into(posterPic)
        posterName.text = data.channel?.name
        //Date
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
        var liked = false
        data.userLiked?.let {
            if (it) {
                //Liked
                likeButton.background = getDrawable(R.drawable.ic_like)
                liked = true
            } else {
                //Not Liked
                likeButton.background = getDrawable(R.drawable.like_empty)
                liked = false
            }
        }
        //Like Post
        likeButton.setOnClickListener {
            if (liked) {
                controller.likePost(data.id!!, true)
                likeButton.background = getDrawable(R.drawable.like_empty)
                data.likes?.let {
                    if (it > 0) {
                        likeCount.text = (likeCount.text.toString().toInt() - 1).toString()
                        data.likes = likeCount.text.toString().toInt()
                    }
                }
                liked = false
                data.userLiked = liked
                data.save()
            } else {
                controller.likePost(data.id!!, false)
                likeCount.text = (likeCount.text.toString().toInt() + 1).toString()
                data.likes = likeCount.text.toString().toInt()
                likeButton.background = getDrawable(R.drawable.ic_like)
                liked = true
                data.userLiked = liked
                data.save()
            }
        }
        //Transition to Full Post View to see the comments
        commentButton.setOnClickListener {
            data.save()
            //Transition to Post Show Activity
            val intent = Intent(context, ChannelPostShowActivity::class.java)
            intent.putExtra(Constants.IntentKeys.MESSAGE_ID, data.id)
            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, data.channelInfo?.name)
            intent.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, true)
            startActivityForResult(intent, Constants.RequestCodes.VIEW_POST_REQ_CODE)
        }
        postText.text = data.message
        likeCount.text = data.likes.toString()
        commentCount.text = data.comments.toString()
    }

    private fun bindMessagePostViewHolder(viewHolder: BaseViewHolder, data: Message) {
        //Post Owner
        val picasso = Picasso.with(this)
        val posterPic = viewHolder.get<ImageView>(R.id.cvpmnm_poster_pic)
        val posterName = viewHolder.get<TextView>(R.id.cvpmnm_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvpmnm_post_date)
        //Content
        val postText = viewHolder.get<TextView>(R.id.cvpmnm_post_text)
        //Actions
        val likeButton = viewHolder.get<ImageView>(R.id.cvpmnm_like_button)
        val commentButton = viewHolder.get<ImageView>(R.id.cvpmnm_comment_button)
        val likeCount = viewHolder.get<TextView>(R.id.cvpmnm_like_count)
        val commentCount = viewHolder.get<TextView>(R.id.cvpmnm_comment_count)

        //Load Info
        data.user?.avatar?.let {
            picasso.load(it).transform(CircleTransform()).into(posterPic)
        }
        posterName.text = data.getUserName()

        //Date
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)

        var liked = false
        data.userLiked?.let {
            if (it) {
                //Liked
                likeButton.background = getDrawable(R.drawable.ic_like)
                liked = true
            } else {
                //Not Liked
                likeButton.background = getDrawable(R.drawable.like_empty)
                liked = false
            }
        }
        //Like Post
        likeButton.setOnClickListener {
            if (liked) {
                controller.likePost(data.id!!, true)
                likeButton.background = getDrawable(R.drawable.like_empty)
                data.likes?.let {
                    if (it > 0) {
                        likeCount.text = (likeCount.text.toString().toInt() - 1).toString()
                        data.likes = likeCount.text.toString().toInt()
                    }
                }
                liked = false
                data.userLiked = liked
                data.save()
            } else {
                controller.likePost(data.id!!, false)
                likeCount.text = (likeCount.text.toString().toInt() + 1).toString()
                likeButton.background = getDrawable(R.drawable.ic_like)
                data.likes = likeCount.text.toString().toInt()
                liked = true
                data.userLiked = liked
                data.save()
            }
        }

        //Transition to Full Post View to see the comments
        commentButton.setOnClickListener {
            data.save()
            //Transition to Post Show Activity
            val intent = Intent(context, ChannelPostShowActivity::class.java)
            intent.putExtra(Constants.IntentKeys.MESSAGE_ID, data.id)
            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, data.channelInfo?.name)
            intent.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, false)
            startActivityForResult(intent, Constants.RequestCodes.VIEW_POST_REQ_CODE)
        }
        //Post
        postText.text = data.message
        data.likes?.let {
            likeCount.text = it.toString()
        } ?: run {
            likeCount.text = "0"
        }
        data.comments?.let {
            commentCount.text = it.toString()
        } ?: run {
            commentCount.text = "0"
        }
    }

    private fun bindFileChannel(viewHolder: BaseViewHolder, data: Message){
//Post Owner
        val picasso = Picasso.with(this)
        val posterPic = viewHolder.get<ImageView>(R.id.cvf_poster_pic)
        val posterName = viewHolder.get<TextView>(R.id.cvf_poster_name)
        val postDate = viewHolder.get<TextView>(R.id.cvf_post_date)
        val fileName = viewHolder.get<TextView>(R.id.cvf_file_name)
        val container = viewHolder.get<LinearLayout>(R.id.cvf_file_container)
        //Load Info
        data.user?.avatar?.let {
            picasso.load(it).transform(CircleTransform()).into(posterPic)
        }
        posterName.text = data.getUserName()

        //Date
        val formatter = SimpleDateFormat("EEE - h:mm aaa")
        postDate.text = formatter.format(data.createdAt)
        //File
        data.files?.let {
            if (it.count() > 0) {
                fileName.text = it[0]?.file_string ?: "n/a"
            }
        }
        //Open File
        container.setOnClickListener {
            println("TAPPED FILE: " + fileName.text)
            val mime = MimeTypeMap.getSingleton()
            val intent = Intent(Intent.ACTION_VIEW)
            val mimeType = mime.getMimeTypeFromExtension(SuffixDetector.instance.getFileSuffix(data.files?.get(0)?.file_string.toString()))
            intent.setDataAndType(Uri.parse(data.files?.get(0)?.s3_url), mimeType)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context?.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Can't open this type of File", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMediaRecycler(mediaArray: RealmList<Media>?, recyclerView: RecyclerView, message: Message, pageIndicator: OverflowPagerIndicator) {
        mPostMediaAdapter = object : BaseRecyclerAdapter<Media, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Media?, position: Int, type: Int) {
                val imageView = viewHolder?.get<ImageView>(R.id.vmpi_imageView)
                data?.media_file?.let {
                    Picasso.with(context)
                            .load(it)
                            .resize(400, 400)
                            .centerInside()
                            .into(imageView)
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_multiple_media_post_item, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        views?.click {
                            //Save || Update @see Realm
                            val messageQuery = Message().queryFirst { equalTo("id", message.id) }
                            if (messageQuery == null) {
                                message.save()
                            }
                            //Transition to Post Show Activity
                            val intent = Intent(context, ChannelPostShowActivity::class.java)
                            intent.putExtra(Constants.IntentKeys.MESSAGE_ID, message.id)
                            intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, message.channelInfo?.name)
                            intent.putExtra(Constants.IntentKeys.INCLUDES_MEDIA, true)
                            startActivityForResult(intent, Constants.RequestCodes.VIEW_POST_REQ_CODE)
                        }
                        super.addClicks(views)
                    }
                }
            }
        }
        mPostMediaAdapter.setItems(mediaArray)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = mPostMediaAdapter
        //Pager indicator
        recyclerView.onFlingListener = null
        pageIndicator.attachToRecyclerView(recyclerView)
        val simpleSnapHelper = SimpleSnapHelper(pageIndicator)
        simpleSnapHelper.attachToRecyclerView(recyclerView)
    }
}