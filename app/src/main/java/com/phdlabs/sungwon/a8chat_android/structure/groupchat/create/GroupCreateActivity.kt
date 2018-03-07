package com.phdlabs.sungwon.a8chat_android.structure.groupchat.create

import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.TemporaryManager
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.GroupChatContract
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.groupinvite.GroupInviteActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CameraControl
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_create.*

/**
 * Created by SungWon on 3/1/2018.
 */
class GroupCreateActivity : CoreActivity(), GroupChatContract.Create.View{

    override lateinit var controller: GroupChatContract.Create.Controller

    override fun layoutId(): Int  = R.layout.activity_group_create

    override fun contentContainerId(): Int = 0

    private val mMembersList = mutableListOf<Contact>()
    private var mAdapter : BaseRecyclerAdapter<Triple<Int, String, String>, BaseViewHolder>? = null

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
        TemporaryManager.instance.mMemberList.clear()
    }

    private fun setUpViews() {
        setToolbarTitle(getString(R.string.create_event))
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
    }

    private fun setUpRecycler(){
        mAdapter = object : BaseRecyclerAdapter<Triple<Int, String, String>, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Triple<Int, String, String>, position: Int, type: Int) {
                val pic = viewHolder?.get<ImageView>(R.id.cvgc_friend_profile_picture)
                val name = viewHolder?.get<TextView>(R.id.cvgc_friend_name)
                val duhleeete = viewHolder?.get<ImageButton>(R.id.cvgc_delet_dis)
                Picasso.with(context).load(data.third).resize(45, 45).centerCrop().placeholder(R.drawable.addphoto).transform(CircleTransform()).into(pic)
                name?.text = data.second
                duhleeete?.setOnClickListener {
                    TemporaryManager.instance.mMemberList.remove(data)
                    mAdapter?.notifyDataSetChanged()
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_group_invite, inflater!!, parent){

                }
            }
        }
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
}