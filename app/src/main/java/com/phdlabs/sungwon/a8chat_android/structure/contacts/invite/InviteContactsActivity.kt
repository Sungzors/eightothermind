package com.phdlabs.sungwon.a8chat_android.structure.contacts.invite

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.*
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact
import com.phdlabs.sungwon.a8chat_android.structure.contacts.ContactsContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import kotlinx.android.synthetic.main.activity_invite_contact.*
import kotlinx.android.synthetic.main.toolbar.*
import com.transitionseverywhere.TransitionManager
import com.transitionseverywhere.extra.Scale

/**
 * Created by paix on 2/19/18.
 * [InviteContactsActivity] used to invite firends to join Eight App
 * This activity will trigger an sms through the API to invite friends
 */
class InviteContactsActivity : CoreActivity(), ContactsContract.InviteContacts.View
        , View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    /*Controller*/
    override lateinit var controller: ContactsContract.InviteContacts.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_invite_contact

    override fun contentContainerId(): Int = 0

    /*Properties*/
    private var mAdapter: BaseRecyclerAdapter<LocalContact, BaseViewHolder>? = null
    override var activity: InviteContactsActivity = this
    private var mLocalContacts: List<LocalContact> = emptyList()
    private var mSelectedContactsForInvite: ArrayList<LocalContact> = ArrayList()
    private var mSelectedRecyclerPositions: ArrayList<Int> = ArrayList()

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Controller
        InviteContactsController(this)
        setupToolbar()
        setClickers()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
        setupAdapter()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!controller.permissionResults(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /*Contacts*/
    private fun setupAdapter() {

        //Setup adapter
        mAdapter = object : BaseRecyclerAdapter<LocalContact, BaseViewHolder>() {

            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: LocalContact?, position: Int, type: Int) {
                val contactFullName = viewHolder?.get<TextView>(R.id.vlc_name)
                viewHolder?.let {
                    //Display Name
                    contactFullName?.text = data?.displayName
                    //Keep selection & avoid recycling on selected items
                    it.itemView?.tag = position
                    it.itemView?.findViewById<CheckBox>(R.id.vlc_checkbox)?.isChecked =
                            mSelectedRecyclerPositions.contains(position)
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder =
                    object : BaseViewHolder(R.layout.view_local_contact, inflater!!, parent) {}

        }

        //Multiple Item selection
        mAdapter?.choiceMode = BaseRecyclerAdapter.CHOICE_MODE_MULTIPLE
        mAdapter?.setItemSelectedListener { isSelected, data, position ->

            //Check box reacting with row selection
            val tag = aic_recycler_view.findViewHolderForAdapterPosition(position).itemView.tag as Int
            if (mSelectedRecyclerPositions.contains(tag)) {
                mSelectedRecyclerPositions.remove(tag)
                mSelectedContactsForInvite.remove(data)
            } else {
                mSelectedRecyclerPositions.add(tag)
                mSelectedContactsForInvite.add(data)
            }

            //Animation
            if (mSelectedRecyclerPositions.count() > 0) {
                inviteAnimation(true, mSelectedRecyclerPositions.count())
            } else {
                inviteAnimation(false, null)
            }
            //Notify adapter
            mAdapter?.notifyDataSetChanged()
        }

        mAdapter?.setItems(mLocalContacts)
        aic_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        aic_recycler_view.adapter = mAdapter

    }

    override fun doneInvitingContacts() {
        finish()
    }

    /*UI Animation*/
    private fun inviteAnimation(isVisible: Boolean, count: Int?) {
        //Animation
        TransitionManager.beginDelayedTransition(aic_animation_container, Scale())
        aic_invite_button.visibility = (if (isVisible) View.VISIBLE else View.GONE)
        //Invite count
        count?.let {
            aic_invite_button.text = "INVITE ($count)"
        }
    }

    override fun deliverLocalContacts(localContacts: ArrayList<LocalContact>) {
        mLocalContacts = localContacts
        mAdapter?.setItems(mLocalContacts)
        mAdapter?.notifyDataSetChanged()
    }

    /*Toolbar*/
    private fun setupToolbar() {
        toolbar_title.text = getString(R.string.invite_contacts)
        toolbar_leftoolbart_action.setImageDrawable(getDrawable(R.drawable.ic_back))
        toolbar_leftoolbart_action.scaleType = ImageView.ScaleType.CENTER
        //Swipe refresh
        aic_swipe_refresh.setColorSchemeResources(R.color.blue_color_picker, R.color.sky_blue_color_picker)
    }

    private fun setClickers() {
        //Toolbar
        toolbar_left_action_container.setOnClickListener(this)
        //Swipe Refresh
        aic_swipe_refresh.setOnRefreshListener(this)
        //Invite
        aic_invite_button.setOnClickListener(this)
    }

    /*On Click*/
    override fun onClick(p0: View?) {

        when (p0) {
        /**
         * Back to [MainActivity] on [MyProfileFragment]
         * */
            toolbar_left_action_container -> onBackPressed()

        /**
         * Invite button
         * */
            aic_invite_button -> {
                controller.notifyContacts(mSelectedContactsForInvite)
            }
        }

    }

    override fun onRefresh() {
        controller.loadLocalContacts()
    }

    override fun stopRefreshing() {
        aic_swipe_refresh.isRefreshing = false
    }

    override fun feedback(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}