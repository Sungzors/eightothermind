package com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.EightQueries
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.chat.ChatActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import kotlinx.android.synthetic.main.fragment_contacts_search.*

/**
 * Created by JPAM on 3/26/18.
 * This fragment manages Favorite & All Contacts access to share media within private conversations
 */
class ShareToContactFragment : CoreFragment() {

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_contacts_search

    /*Properties*/
    private var mContactAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null
    private var mFavContactAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null
    private var mFavContactList = mutableListOf<Contact>()

    /*LifeCycle*/
    override fun onStart() {
        super.onStart()
        //Setup Adapters
        setUpContactsAdapter()
        setUpFavoritesAdapter()
    }

    /*UI Changes*/
    private fun hideFavoritesCard(isHidden: Boolean) {
        if (isHidden) {
            fcnc_fav_contacts_card?.let {
                it.visibility = View.GONE
            }
        } else {
            fcnc_fav_contacts_card?.let {
                it.visibility = View.VISIBLE
            }
        }
    }


    /*Recycler Views & Adapters*/
    //All Contacts
    private fun setUpContactsAdapter() {
        mContactAdapter = object : BaseRecyclerAdapter<Contact, BaseViewHolder>() {
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
                        data?.hasFullName()?.let {
                            if (it.first) {
                                it.second?.let {
                                    contactFullName?.text = it
                                } ?: run {
                                    contactFullName?.text = data?.first_name + " " + data?.last_name
                                }
                            }
                        }
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
                        }
                        super.addClicks(views)
                    }
                }
            }

        }
        mContactAdapter?.setItems(Contact().queryAll().toMutableList())
        mContactAdapter?.setSortComparator(EightQueries.Comparators.alphabetComparator)
        fcnc_allcontacts_recycler.layoutManager = LinearLayoutManager(context)
        fcnc_allcontacts_recycler.adapter = mContactAdapter
    }

    //Favorite Contacts
    private fun setUpFavoritesAdapter() {
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
                            startActivity(intent)
                        }
                        super.addClicks(views)
                    }
                }
            }
        }
        mFavContactAdapter?.setItems(Contact().query { equalTo("isFavorite", true) }.toMutableList())
        mFavContactAdapter?.setSortComparator(EightQueries.Comparators.alphabetComparator)
        fcnc_favorites_recycler.layoutManager = LinearLayoutManager(context)
        fcnc_favorites_recycler.adapter = mFavContactAdapter
    }

    /*Filter*/
    fun filterContactsAdapter(p0: String?) {
        //UI
        p0?.let {
            //Filter Adapter
            mContactAdapter?.setFilter { filter ->
                filter?.first_name?.toLowerCase()?.startsWith(p0.toLowerCase(), false)
            }
            if (p0.isBlank()) {
                hideFavoritesCard(false)
            } else {
                hideFavoritesCard(true)
            }
        }
    }
}