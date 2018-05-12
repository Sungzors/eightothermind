package com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.EightQueries
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import kotlinx.android.synthetic.main.fragment_share.*

/**
 * Created by JPAM on 3/26/18.
 * This fragment manages Favorite & All Contacts access to share media within private conversations
 */
class ShareToContactFragment : CoreFragment() {

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_share

    /*Properties*/
    private var mContactAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null
    private var mFavContactAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null
    private var mFavContactList = mutableListOf<Contact>()
    private var mContactsList = mutableListOf<Contact>()

    /*LifeCycle*/
    override fun onStart() {
        super.onStart()
        //Setup Adapters
        setUpContactsAdapter()
        setUpFavoritesAdapter()
        //Lists
        mContactsList.clear()
        mFavContactList.clear()
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
                val selectionView = viewHolder?.get<ImageView>(R.id.fc_selected_iv)
                val selectionContainer = viewHolder?.get<RelativeLayout>(R.id.fc_all_container)
                viewHolder?.let {
                    context?.let {
                        /*load profile picture*/
                        Picasso.with(it)
                                .load(data?.avatar)
                                .placeholder(R.mipmap.ic_launcher_round)
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
                        /*Selection*/
                        selectionContainer?.setOnClickListener {
                            if (selectionView?.visibility == View.GONE) {
                                selectionView.visibility = View.VISIBLE
                                if (!mContactsList.contains(data)) {
                                    data?.let {
                                        mContactsList.add(data)
                                    }
                                }
                            } else {
                                selectionView?.visibility = View.GONE
                                if (mContactsList.contains(data)) {
                                    mContactsList.remove(data)
                                }
                            }
                        }
                    }
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_eight_contact, inflater!!, parent) {
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
                val selectionView = viewHolder?.get<ImageView>(R.id.fc_selected_iv)
                val selectionContainer = viewHolder?.get<RelativeLayout>(R.id.fc_all_container)
                viewHolder?.let {
                    context?.let {
                        /*load profile picture*/
                        Picasso.with(it)
                                .load(data?.avatar)
                                .placeholder(R.mipmap.ic_launcher_round)
                                .transform(CircleTransform())
                                .into(contactProfilePicture)
                        /*load name*/
                        contactFullName?.text = data?.first_name + " " + data?.last_name
                    }
                }
                /*Selection*/
                selectionContainer?.setOnClickListener {
                    if (selectionView?.visibility == View.GONE) {
                        selectionView.visibility = View.VISIBLE
                        if (!mContactsList.contains(data)) {
                            data?.let {
                                mContactsList.add(data)
                            }
                        }
                    } else {
                        selectionView?.visibility = View.GONE
                        if (mContactsList.contains(data)) {
                            mContactsList.remove(data)
                        }
                    }
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_eight_contact, inflater!!, parent) {
                }
            }
        }
        //UI
        Contact().query { equalTo("isFavorite", true) }.toMutableList()?.let {
            if (it.count() > 0) {
                hideFavoritesCard(false)
            } else {
                hideFavoritesCard(true)
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

    /*All sharing contacts*/
    fun getSharingContactsList(): List<Contact> {
        val mAllContactsList = mutableListOf<Contact>()
        if (mContactsList.count() > 0) {
            mAllContactsList.addAll(mContactsList)
        }
        if (mFavContactList.count() > 0) {
            mAllContactsList.addAll(mFavContactList)
        }
        return mAllContactsList
    }
}