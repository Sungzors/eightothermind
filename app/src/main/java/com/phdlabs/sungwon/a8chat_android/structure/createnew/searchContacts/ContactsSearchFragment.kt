package com.phdlabs.sungwon.a8chat_android.structure.createnew.searchContacts

import android.content.Intent
import android.os.Bundle
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
import com.phdlabs.sungwon.a8chat_android.structure.createnew.CreateNewActivity
import com.phdlabs.sungwon.a8chat_android.structure.createnew.CreateNewContract
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_createnew_contacts_search.*

/**
 * Created by JPAM on 3/12/18.
 * [ContactsSearchFragment]
 * Used to see favorite contacts if available & Search through all available Eight [Contact]
 * @see [Realm] for Queries
 */
class ContactsSearchFragment : CoreFragment(), CreateNewContract.ContactSearch.View {

    /*Controller*/
    override lateinit var controller: CreateNewContract.ContactSearch.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_createnew_contacts_search

    /*Properties*/
    private var mContactAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null
    private var mFavContactAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null
    private var mContactList = mutableListOf<Contact>()
    private var mFavContactList = mutableListOf<Contact>()
    override lateinit var getAct: CreateNewActivity

    init {
        ContactSearchFragController(this)
    }

    /*LifeCycle*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAct = activity as CreateNewActivity
    }

    override fun onStart() {
        super.onStart()
        controller.start()
        //Setup Adapters
        setUpContactsAdapter()
        setUpFavoritesAdapter()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
        controller.getContactData()

    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
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
        mContactAdapter?.setItems(mContactList)
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
        mFavContactAdapter?.setItems(mFavContactList)
        mFavContactAdapter?.setSortComparator(EightQueries.Comparators.alphabetComparator)
        fcnc_favorites_recycler.layoutManager = LinearLayoutManager(context)
        fcnc_favorites_recycler.adapter = mFavContactAdapter
    }

    /*Update*/
    override fun updateAllContactsRecycler(contacts: MutableList<Contact>?) {
        contacts?.let {
            mContactList.clear()
            mContactList.addAll(it)
            mContactAdapter?.setItems(mContactList)
            mContactAdapter?.notifyDataSetChanged()
            filterContactsAdapter("")
        }
    }

    override fun updateFavContactsRecycler(contacts: MutableList<Contact>?) {
        contacts?.let {
            mFavContactList.clear()
            mFavContactList.addAll(it)
            if (it.count() > 0) {
                hideFavoritesCard(false)
            } else {
                hideFavoritesCard(true)
            }
            mFavContactAdapter?.setItems(mFavContactList)
            mFavContactAdapter?.notifyDataSetChanged()
        }
    }

    /*Filter*/
    override fun filterContactsAdapter(p0: String?) {
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