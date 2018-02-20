package com.phdlabs.sungwon.a8chat_android.structure.contacts.invite

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.LocalContact
import com.phdlabs.sungwon.a8chat_android.structure.contacts.ContactsContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import kotlinx.android.synthetic.main.activity_invite_contact.*
import kotlinx.android.synthetic.main.toolbar.*

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

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InviteContactsController(this)
        setupToolbar()
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
                        contactFullName?.text = data?.displayName
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_local_contact, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        views?.click {
                            context?.let {
                                Toast.makeText(it, "Contact checkmark in progress", Toast.LENGTH_SHORT).show()
                            }
                        }
                        super.addClicks(views)
                    }
                }
            }

        }
        mAdapter?.setItems(mLocalContacts)
        aic_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        aic_recycler_view.adapter = mAdapter
    }

    override fun deliverLocalContacts(localContacts: ArrayList<LocalContact>) {
        mLocalContacts = localContacts
        mAdapter?.setItems(mLocalContacts)
        mAdapter?.notifyDataSetChanged()
    }

    /*Toolbar*/
    private fun setupToolbar() {
        //UI
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.whiteTransparent)
        toolbar_title.text = getString(R.string.invite_contacts)
        toolbar_leftoolbart_action.setImageDrawable(getDrawable(R.drawable.ic_back))
        toolbar_leftoolbart_action.scaleType = ImageView.ScaleType.CENTER
        toolbar_left_action_container.setOnClickListener(this)
        //Swipe refresh
        aic_swipe_refresh.setOnRefreshListener(this)
        aic_swipe_refresh.setColorSchemeResources(R.color.blue_color_picker, R.color.sky_blue_color_picker)
    }

    /*On Click*/
    override fun onClick(p0: View?) {

        when (p0) {
        /**
         * Back to [MainActivity] on [MyProfileFragment]
         * */
            toolbar_left_action_container -> onBackPressed()
        }

    }

    override fun onRefresh() {
        controller.loadLocalContacts()
    }

    override fun stopRefreshing() {
        aic_swipe_refresh.isRefreshing = false
    }

}