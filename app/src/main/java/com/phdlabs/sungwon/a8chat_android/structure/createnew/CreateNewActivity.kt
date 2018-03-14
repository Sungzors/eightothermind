package com.phdlabs.sungwon.a8chat_android.structure.createnew

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.channel.create.ChannelCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.createnew.searchChannels.ChannelSearchFragment
import com.phdlabs.sungwon.a8chat_android.structure.createnew.searchContacts.ContactsSearchFragment
import com.phdlabs.sungwon.a8chat_android.structure.event.create.EventCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.create.GroupCreateActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.activity_create_new.*
import kotlinx.android.synthetic.main.toolbar_create_new.*

/**
 * Created by SungWon on 2/14/2018.
 * Updated by JPAM on 2/25/2018
 */
class CreateNewActivity : CoreActivity(), CreateNewContract.CreateNew.View, View.OnClickListener {

    /*Controller*/
    override lateinit var controller: CreateNewContract.CreateNew.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_create_new

    override fun contentContainerId(): Int = R.id.acn_fragment_container

    /*Properties*/
    private lateinit var mContactList: MutableList<Contact>
    private lateinit var mFavoriteList: MutableList<Contact>
    private var contactSearchFragment: ContactsSearchFragment
    private var channelSearchFragment: ChannelSearchFragment

    init {
        CreateNewAController(this)
        contactSearchFragment = ContactsSearchFragment()
        channelSearchFragment = ChannelSearchFragment()
    }

    /*LifeCycle*/
    override fun onStart() {
        super.onStart()
        //Default UI
        setupDefaultUI()
        //Actions
        setUpClickers()
        //SearchBar
        setupSearchBar()
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

    /*Default UI -> Contacts Search Fragment*/
    override fun setupDefaultUI() {
        //Default Contacts Fragment
        acn_fragment_container.visibility = View.VISIBLE
        replaceFragment(contentContainerId(), contactSearchFragment, false)

        //Segmented control default state
        acn_contacts_selector.text = getString(R.string.contacts_selector_default_text)
        acn_channels_selector.text = getString(R.string.channels_selector_default_text)
        acn_contacts_selector.setOnClickListener {
            acn_contacts_selector.isChecked = true
            acn_channels_selector.isChecked = false
            replaceFragment(contentContainerId(), contactSearchFragment, false)
        }
        acn_channels_selector.setOnClickListener {
            acn_contacts_selector.isChecked = false
            acn_channels_selector.isChecked = true
            //replaceFragment(contentContainerId(), channelSearchFragment, false)
        }
        //Default -> Contacts
        acn_contacts_selector.isChecked = true
        acn_channels_selector.isChecked = false
    }

    /*On Click*/
    private fun setUpClickers() {
        tcn_cancel.setOnClickListener(this)
        acn_group_container.setOnClickListener(this)
        acn_channel_container.setOnClickListener(this)
        acn_event_container.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tcn_cancel -> {
                onBackPressed()
            }
        /*Create Group*/
            acn_group_container -> {
                startActivity(Intent(this, GroupCreateActivity::class.java))
            }
        /*Create Channel*/
            acn_channel_container -> {
                val createChannelIntent = Intent(this, ChannelCreateActivity::class.java)
                startActivityForResult(createChannelIntent, Constants.RequestCodes.CREATE_NEW_BACK_REQ_CODE)
            }
        /*Create Event*/
            acn_event_container -> {
                startActivity(Intent(this, EventCreateActivity::class.java))
            }
        }
    }

    override fun getContactData() {
        //Load Contact Data
        controller.queryContacts { contacts ->
            contacts.first?.let {
                mContactList = it
                contactSearchFragment.controller.pushContactInfoChanges(Pair(it, null))

            }
            contacts.second?.let {
                mFavoriteList = it
                contactSearchFragment.controller.pushContactInfoChanges(Pair(null, it))
            }
        }
    }

    /*Search*/
    private fun setupSearchBar() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        tcn_searchview?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        tcn_searchview?.queryHint = resources.getString(R.string.searchcontacthint)
        tcn_searchview?.isSubmitButtonEnabled = true
        tcn_searchview?.setOnQueryTextListener(
                object : android.support.v7.widget.SearchView.OnQueryTextListener {

                    //Text Submit
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        //Hide search options
                        p0?.let {
                            acn_search_selector_container.visibility = View.VISIBLE
                            if (acn_contacts_selector.isChecked) {//Contacts
                                contactSearchFragment.controller.pushContactFilterChanges(p0)
                            } else if (acn_channels_selector.isChecked) {//Channels

                            }
                            if (it.isBlank()) {
                                ca_searchView?.clearFocus()
                                //Selector Menu
                                acn_search_selector_container.visibility = View.GONE
                                contactSearchFragment.controller.getContactData()
                            }
                        }
                        tcn_searchview.clearFocus()
                        val input = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        input.hideSoftInputFromWindow(tcn_searchview.windowToken, 0)
                        return true
                    }

                    //Text Change
                    override fun onQueryTextChange(p0: String?): Boolean {
                        //Hide search options
                        p0?.let {
                            acn_search_selector_container.visibility = View.VISIBLE
                            if (acn_contacts_selector.isChecked) {//Contacts
                                if (p0 != "") {
                                    contactSearchFragment.controller.pushContactFilterChanges(p0)
                                }
                            } else if (acn_channels_selector.isChecked) {//Channels

                            }
                            if (it.isBlank()) {
                                //Selector Menu
                                acn_search_selector_container.visibility = View.GONE
                                contactSearchFragment.controller.getContactData()
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