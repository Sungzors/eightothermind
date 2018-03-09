package com.phdlabs.sungwon.a8chat_android.structure.createnew

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.EightQueries
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.channel.create.ChannelCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.chat.ChatActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.event.create.EventCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.create.GroupCreateActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import kotlinx.android.synthetic.main.activity_create_new.*
import kotlinx.android.synthetic.main.toolbar_create_new.*

/**
 * Created by SungWon on 2/14/2018.
 * Updated by JPAM on 2/25/2018
 */
class CreateNewActivity: CoreActivity(){
    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_create_new

    override fun contentContainerId(): Int = 0

    /*Properties*/
    private lateinit var mContactList : List<Contact>
    private var mFavoriteList = mutableListOf<Contact>()
    private var mContactAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null
    private var mFavContactAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null

    /*LifeCycle*/
    override fun onStart() {
        super.onStart()
        tcn_searchview.maxWidth = Int.MAX_VALUE
        queryContacts()
        setUpClickers()
        setUpSearchers()
        setUpContactsAdapter(mContactList)
        setUpFavoritesAdapter(mFavoriteList)
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

    override fun onDestroy() {
        super.onDestroy()
        setResult(Activity.RESULT_CANCELED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RequestCodes.CREATE_NEW_BACK_REQ_CODE) { //Created New Content
                setResult(Activity.RESULT_OK)
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /*UI Changes*/
    private fun hideFavoritesCard(isHidden: Boolean) {
        if (isHidden){
            acn_fav_contacts_card.visibility = View.GONE
        }else {
            acn_fav_contacts_card.visibility = View.VISIBLE
        }
    }

    /*Data*/
    private fun queryContacts   (){
        //Cached Contacts
        mContactList = Contact().queryAll()
        mFavoriteList = Contact().query { equalTo("isFavorite", true) }.toMutableList()
    }


    private fun setUpClickers(){
        tcn_cancel.setOnClickListener {
            onBackPressed()
        }
        acn_group_container.setOnClickListener {
            startActivity(Intent(this, GroupCreateActivity::class.java))
        }
        /*Create new channel*/
        acn_channel_container.setOnClickListener {
            val createChannelIntent = Intent(this, ChannelCreateActivity::class.java)
            startActivityForResult(createChannelIntent, Constants.RequestCodes.CREATE_NEW_BACK_REQ_CODE)
        }
        acn_event_container.setOnClickListener {
            startActivity(Intent(this, EventCreateActivity::class.java))
        }
    }

    /*Recycler Views & Adapters*/
    //Contacts & Filtered contacts for search bar
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
                            //Go to private chat
                            val contact = getItem(adapterPosition)
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra(Constants.IntentKeys.CHAT_NAME, contact.first_name + " " + contact.last_name)
                            intent.putExtra(Constants.IntentKeys.PARTICIPANT_ID, contact.id)
                            intent.putExtra(Constants.IntentKeys.CHAT_PIC, contact.avatar)
                            startActivity(intent)
                            //Clear search info
                            tcn_searchview.setQuery("",false)
                            tcn_searchview.clearFocus()
                        }
                        super.addClicks(views)
                    }
                }
            }

        }
        mContactAdapter?.setItems(list)
        mContactAdapter?.setSortComparator(EightQueries.Comparators.alphabetComparator)
        acn_contacts_recycler.layoutManager = LinearLayoutManager(this)
        acn_contacts_recycler.adapter = mContactAdapter
    }

    //Favorite Contacts
    private fun setUpFavoritesAdapter(list: List<Contact>) {
        mFavContactAdapter = object : BaseRecyclerAdapter<Contact, BaseViewHolder>() {

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
                }            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_eight_contact, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        views?.click {
                            val contact = getItem(adapterPosition)
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra(Constants.IntentKeys.CHAT_NAME, contact.first_name + " " + contact.last_name)
                            intent.putExtra(Constants.IntentKeys.PARTICIPANT_ID, contact.id)
                            intent.putExtra(Constants.IntentKeys.CHAT_PIC, contact.avatar)
                            startActivity(intent)
                        }
                        super.addClicks(views)
                    }
                }
            }
        }
        //Fav contacts visibility
        if (mFavoriteList.count() > 0) {
            hideFavoritesCard(false)
        }else {
            hideFavoritesCard(true)
        }
        mFavContactAdapter?.setItems(mFavoriteList)
        mFavContactAdapter?.setSortComparator(EightQueries.Comparators.alphabetComparator)
        acn_favorites_recycler.layoutManager = LinearLayoutManager(this)
        acn_favorites_recycler.adapter = mFavContactAdapter
    }


    /*SEARCH*/
    private fun setUpSearchers(){

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        tcn_searchview.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        tcn_searchview.queryHint = resources.getString(R.string.searchcontacthint)
        tcn_searchview.isSubmitButtonEnabled = true
        tcn_searchview.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener{

                    //Text Submit
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        //UI
                        if (!p0.isNullOrBlank()) {
                            hideFavoritesCard(true)
                        }else {
                            hideFavoritesCard(false)
                        }
                        //Search
                        mContactAdapter?.setFilter { filter ->
                            p0?.let {
                                filter?.first_name?.toLowerCase()?.startsWith(it, false)
                            }
                        }
                        tcn_searchview.clearFocus()
                        val inputm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputm.hideSoftInputFromWindow(tcn_searchview.windowToken, 0)
                        return true
                    }

                    //Text Change
                    override fun onQueryTextChange(p0: String?): Boolean {
                        if (!p0.isNullOrBlank()) {
                            hideFavoritesCard(true)
                        }else {
                            hideFavoritesCard(false)
                        }
                        mContactAdapter?.setFilter { filter ->
                            p0?.let {
                                filter?.first_name?.toLowerCase()?.startsWith(it,false)
                            }
                        }
                        return true
                    }


                }
        )
    }

    /**
     * Listener to make whole search bar touchable
     * */
    fun searchClicked(v: View) {
        tcn_searchview.isIconified = false
    }

}