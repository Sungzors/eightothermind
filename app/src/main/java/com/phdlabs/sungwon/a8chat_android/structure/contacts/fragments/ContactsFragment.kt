package com.phdlabs.sungwon.a8chat_android.structure.contacts.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.UserManager
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.model.room.Room
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.structure.setting.chat.ChatSettingActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import kotlinx.android.synthetic.main.fragment_contacts.*

/**
 * Created by paix on 2/13/18.
 * [ContactsFragment]
 * - Will load local contacts & send phone numbers to server.
 * - Server will return id for users available in 8.
 * - Those users are the ones that will be shown.
 */
class ContactsFragment : CoreFragment() {

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_contacts

    /*Properties*/
    private var mEightContacts: List<Contact> = listOf()
    private var mAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null
    private lateinit var recyclerSections: List<String>
    private var currentUserId: Int? = null

    /*LifeCycle*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserManager.instance.getCurrentUser { isSuccess, user, _ ->
            if (isSuccess) {
                user?.id.let {
                    currentUserId = it
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //Load grouped contacts
        loadAndGroupContacts()
    }

    private fun loadAndGroupContacts() {
        //Query realm eight contacts
        mEightContacts = Contact().queryAll()

        /*Create Separators by first name -> first character*/
        val firstCharFirstName: MutableList<String> = mutableListOf() //Number of contacts
        mEightContacts
                .asSequence()
                .map {
                    //Get first Name character
                    it.first_name?.take(1)
                }
                .filterNot { it.isNullOrBlank() }
                .mapTo(firstCharFirstName) { it!!.toUpperCase() }
        //Remove duplicates
        recyclerSections = firstCharFirstName.distinct() //Number of sections
        //Sort alphabetically
        recyclerSections = recyclerSections.sorted()

        mAdapter?.setItems(mEightContacts)
        mAdapter?.notifyDataSetChanged()


        print("Sections")

        //todo: order sections alphabetically

        //todo: build sections

        //todo: add contacts depending


    }

    private fun setupRecycler() {
        mAdapter = object : BaseRecyclerAdapter<Contact, BaseViewHolder>() {

            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Contact?, position: Int, type: Int) {
                /*UI*/
                val contactProfilePicture = viewHolder?.get<ImageView>(R.id.fc_friend_profile_picture)
                val contactFullName = viewHolder?.get<TextView>(R.id.fc_friend_name)
                viewHolder?.let {
                    context?.let {
                        /*load profile picture*/
                        Picasso.with(it)
                                .load(data?.avatar)
                                .resize(45, 45)
                                .centerCrop()
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
                            //Transition to contact profile if available
                            val contact: Contact = getItem(adapterPosition)
                            val intent = Intent(context, ChatSettingActivity::class.java)
                            intent.putExtra(Constants.IntentKeys.CHAT_NAME, contact.first_name + " " + contact.last_name)
                            intent.putExtra(Constants.IntentKeys.PARTICIPANT_ID, contact.id)
                            var roomId: Int? = 0
                            val availableRooms = Room().query { it.findAll() }
                            for (roomElement in availableRooms) {
                                //Check if this is a private chat
                                roomElement.chatType?.let {
                                    if (it == Constants.ChatTypes.PRIVATE)
                                    //Check if the user is a participant
                                        roomElement.participantsId?.let {
                                            val participants: ArrayList<Int> = arrayListOf()
                                            for (participantId in it) {
                                                //Retrieve participants ID's
                                                participantId.intValue?.let {
                                                    participants.add(it)
                                                }
                                                //Verify participants
                                                if (participants.contains(contact.id) &&
                                                        participants.contains(currentUserId)) {
                                                    roomId = roomElement.id
                                                }
                                            }
                                        }
                                }
                            }
                            intent.putExtra(Constants.IntentKeys.ROOM_ID, roomId)
                            roomId?.let {
                                if (it != 0) {
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(context, "Could not find profile", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        super.addClicks(views)
                    }
                }
            }
        }

        mAdapter?.setItems(mEightContacts)
        fc_contacts_recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fc_contacts_recyclerView.adapter = mAdapter
    }

}