package com.phdlabs.sungwon.a8chat_android.structure.createnew

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.channel.create.ChannelCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.chat.ChatActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.event.create.EventCreateActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.queryAll
import kotlinx.android.synthetic.main.activity_create_new.*
import kotlinx.android.synthetic.main.toolbar_create_new.*

/**
 * Created by SungWon on 2/14/2018.
 */
class CreateNewActivity: CoreActivity(){

    override fun layoutId(): Int = R.layout.activity_create_new

    override fun contentContainerId(): Int = 0

    private lateinit var mContactList : List<Contact>
    private val mFilteredContactList = mutableListOf<Contact>()
    private val mFavoriteList = mutableListOf<Contact>()
    private val mFilteredFavoriteList = mutableListOf<Contact>()
    private var mContactAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null

    override fun onStart() {
        super.onStart()
        tcn_searchview.maxWidth = Int.MAX_VALUE
        callContacts()
        setUpClickers()
        setUpSearchers()
        setUpContactsAdapter(mContactList)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun callContacts(){
        mContactList = Contact().queryAll()
    }

    private fun setUpClickers(){
        tcn_cancel.setOnClickListener {
            onBackPressed()
        }
        acn_group_container.setOnClickListener {
            Toast.makeText(this, "Group Chat in progress", Toast.LENGTH_SHORT).show()//TODO: groupchat activty lead
        }
        acn_channel_container.setOnClickListener {
            startActivity(Intent(this, ChannelCreateActivity::class.java))
        }
        acn_event_container.setOnClickListener {
            startActivity(Intent(this, EventCreateActivity::class.java))
        }
    }

    private fun setUpSearchers(){
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        tcn_searchview.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        tcn_searchview.queryHint = resources.getString(R.string.searchcontacthint)
        tcn_searchview.isSubmitButtonEnabled = true
        tcn_searchview.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        mFilteredContactList.clear()
                        mFilteredFavoriteList.clear()
                        for (contact in mContactList){
                            if(contact.first_name?.toLowerCase()!!.contains(p0!!.toLowerCase())||contact.last_name?.toLowerCase()!!.contains(p0.toLowerCase())){
                                mFilteredContactList.add(contact)
                            }
                        }
                        for (contact in mFavoriteList){
                            if(contact.first_name?.toLowerCase()!!.contains(p0!!.toLowerCase())||contact.last_name?.toLowerCase()!!.contains(p0.toLowerCase())){
                                mFilteredFavoriteList.add(contact)
                            }
                        }
                        tcn_searchview.clearFocus()
                        val inputm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputm.hideSoftInputFromWindow(tcn_searchview.windowToken, 0)
                        return false
                    }

                    override fun onQueryTextChange(p0: String?): Boolean = false
                }
        )
    }

    private fun setUpContactsAdapter(list: List<Contact>){
        mContactAdapter = object : BaseRecyclerAdapter<Contact, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Contact?, position: Int, type: Int) {
                val contactProfilePicture = viewHolder?.get<ImageView>(R.id.fc_friend_profile_picture)
                val contactFullName = viewHolder?.get<TextView>(R.id.fc_friend_name)
                viewHolder?.let {
                    context?.let {
                        /*load profile picture*/
                        Picasso.with(it)
                                .load(data?.avatar)
                                .placeholder(R.drawable.addphoto)
                                .transform(CircleTransform())
                                .into(contactProfilePicture)
                        /*load name*/
                        contactFullName?.text = data?.first_name + " " + data?.last_name
                    }
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_eight_contact, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        views?.click {
                            val contact = getItem(adapterPosition)
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra(Constants.IntentKeys.CHAT_NAME, contact.first_name + " " + contact.last_name)
                            intent.putExtra(Constants.IntentKeys.PARTICIPANT_ID, contact.id)
                            intent.putExtra(Constants.IntentKeys.CHAT_PIC, contact.avatar)
                        }
                        super.addClicks(views)
                    }
                }
            }
        }
        mContactAdapter?.setItems(list)
        acn_contacts_recycler.layoutManager = LinearLayoutManager(this)
        acn_contacts_recycler.adapter = mContactAdapter
    }

}