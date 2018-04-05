package com.phdlabs.sungwon.a8chat_android.structure.groupchat.create

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.api.data.GroupChatPostData
import com.phdlabs.sungwon.a8chat_android.db.EightQueries
import com.phdlabs.sungwon.a8chat_android.db.TemporaryManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.media.Media
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.GroupChatActivity
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.GroupChatContract
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.groupinvite.GroupInviteActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_create.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by SungWon on 3/1/2018.
 */
class GroupCreateActivity : CoreActivity(), GroupChatContract.Create.View{

    override lateinit var controller: GroupChatContract.Create.Controller

    override fun layoutId(): Int  = R.layout.activity_group_create

    override fun contentContainerId(): Int = 0

    private var mAdapter : BaseRecyclerAdapter<Triple<Int, String, String>, BaseViewHolder>? = null
    private var mMedia: Media? = null

    override fun onStart() {
        super.onStart()
        GroupCreateController(this)
        controller.start()
        setUpViews()
        setUpClickers()
        agc_divider.visibility = View.GONE
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

    }

    override fun onBackPressed() {
        TemporaryManager.instance.mMemberList.clear()
        super.onBackPressed()
    }

    private fun setUpViews() {
        setToolbarTitle(getString(R.string.create_group))
        showRightTextToolbar(getString(R.string.create))
        showBackArrow(R.drawable.ic_back)
    }

    private fun setUpClickers() {
        agc_group_picture.setOnClickListener {
            controller.showPicture()
        }
        agc_add_people_container.setOnClickListener {
            val intent = Intent(this, GroupInviteActivity::class.java)
            startActivityForResult(intent, Constants.RequestCodes.INVITE_GROUP)
        }
        toolbar_right_text.setOnClickListener {
            showProgress()
            UserManager.instance.getCurrentUser{ success, user, token ->
                if (success) {
                    token?.token?.let {
                        user?.id?.let {
                            val idlist = mutableListOf<Int>()
                            idlist.add(it)
                            for (triple in TemporaryManager.instance.mMemberList) {
                                idlist.add(triple.first)
                            }
                            controller.createGroupChat(GroupChatPostData(
                                    idlist,
                                    agc_group_name.text.toString().trim(),
                                    it,
                                    mMedia?.id

                            ))
                        }
                    }
                }
            }

        }
    }

    private fun setUpRecycler(){
        mAdapter = object : BaseRecyclerAdapter<Triple<Int, String, String>, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Triple<Int, String, String>, position: Int, type: Int) {
                val pic = viewHolder?.get<ImageView>(R.id.cvgc_friend_profile_picture)
                val name = viewHolder?.get<TextView>(R.id.cvgc_friend_name)
                val duhleeete = viewHolder?.get<ImageButton>(R.id.cvgc_delet_dis)
                data.third.let {
                    Picasso.with(context).load(it).resize(45, 45).centerCrop().placeholder(R.drawable.addphoto).transform(CircleTransform()).into(pic)
                }
                name?.text = data.second
                duhleeete?.setOnClickListener {
                    TemporaryManager.instance.mMemberList.remove(data)
                    notifyItemRemoved(position)
                    mAdapter?.setItems(TemporaryManager.instance.mMemberList)
//                    notifyItemRangeChanged(position, TemporaryManager.instance.mMemberList.size)
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_group_create, inflater!!, parent){

                }
            }
        }
        mAdapter?.setItems(TemporaryManager.instance.mMemberList)
        mAdapter?.setSortComparator(EightQueries.Comparators.alphabetComparatorGroupCreate)
        agc_add_people_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        agc_add_people_recycler.adapter = mAdapter
    }

    override fun setChannelImage(filePath: String) {
        Picasso.with(context)
                .load("file://" + filePath)
                .placeholder(R.drawable.addphoto)
                .transform(CircleTransform())
                .into(agc_group_picture)
    }

    override fun setMedia(media: Media) {
        mMedia = media
    }

    override fun onCreateGroup(name: String, id: Int, pic: String) {
        setResult(Activity.RESULT_OK)
        val intent = Intent(context, GroupChatActivity::class.java)
        intent.putExtra(Constants.IntentKeys.CHAT_NAME, name)
        intent.putExtra(Constants.IntentKeys.ROOM_ID, id)
        intent.putExtra(Constants.IntentKeys.CHAT_PIC, pic)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CameraControl.instance.requestCode()) {
            controller.onPictureResult(requestCode, resultCode, data)
        } else if (requestCode == Constants.RequestCodes.INVITE_GROUP){
            if(mAdapter == null) setUpRecycler() else mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.PermissionsReqCode.CAMERA_REQ_CODE) {
            if (grantResults.size != 1 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
                showError(getString(R.string.request_camera_permission))
            } else {
                controller.showPicture()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override val getActivity: GroupCreateActivity
        get() = this
}