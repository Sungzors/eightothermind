package com.phdlabs.sungwon.a8chat_android.structure.favorite.message

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.message.Message
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.favorite.FavoriteContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.RoundedCornersTransform
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_favorites.*
import java.text.SimpleDateFormat

/**
 * Created by SungWon on 3/20/2018.
 */
class FavoriteMessageActivity : CoreActivity(), FavoriteContract.Message.View{

    override lateinit var controller: FavoriteContract.Message.Controller
    private lateinit var mAdapter: BaseRecyclerAdapter<Message, BaseViewHolder>

    override fun layoutId(): Int = R.layout.activity_favorites

    override fun contentContainerId(): Int = 0

    private var mRoomId: Int = 0


    override fun onStart() {
        super.onStart()
        FavoriteMessageController(this)
        mRoomId = intent?.getIntExtra(Constants.IntentKeys.ROOM_ID, 0)!!

        controller.getFavorites(mRoomId)
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
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_favorites_list, inflater!!, parent){

                }
            }
        }
        mAdapter.setItems(messages)
        val layoutManager = LinearLayoutManager(this)
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
//                val mimeType = mime.getMimeTypeFromExtension(SuffixDetector.instance.getFileSuffix(it[0]?.file_string.toString()))
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
}