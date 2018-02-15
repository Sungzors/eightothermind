package com.phdlabs.sungwon.a8chat_android.structure.createnew

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.channel.create.ChannelCreateActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.event.create.EventCreateActivity
import kotlinx.android.synthetic.main.activity_create_new.*
import kotlinx.android.synthetic.main.toolbar_create_new.*

/**
 * Created by SungWon on 2/14/2018.
 */
class CreateNewActivity: CoreActivity(){

    override fun layoutId(): Int = R.layout.activity_create_new

    override fun contentContainerId(): Int = 0

    private val mContactList = mutableListOf<Contact>()
    private val mFilteredContactList = mutableListOf<Contact>()
    private val mFavoriteList = mutableListOf<Contact>()
    private val mFilteredFavoriteList = mutableListOf<Contact>()

    override fun onStart() {
        super.onStart()
        tcn_searchview.maxWidth = Int.MAX_VALUE
        callContacts()
        setUpClickers()
        setUpSearchers()
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

}