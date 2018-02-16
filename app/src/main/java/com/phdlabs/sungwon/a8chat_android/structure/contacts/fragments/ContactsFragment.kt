package com.phdlabs.sungwon.a8chat_android.structure.contacts.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.contacts.ContactsActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
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
    lateinit var mEightContacts: List<Contact>
    private var mAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null
    private lateinit var recyclerSections: List<String>

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadAndGroupContacts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
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
                            //TODO: Load profile detail
                            context?.let {
                                Toast.makeText(it, "Contact profile in progress", Toast.LENGTH_SHORT).show()
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