package com.phdlabs.sungwon.a8chat_android.structure.groupchat.groupinvite

import android.app.SearchManager
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.EightQueries
import com.phdlabs.sungwon.a8chat_android.db.TemporaryManager
import com.phdlabs.sungwon.a8chat_android.db.user.UserManager
import com.phdlabs.sungwon.a8chat_android.model.contacts.Contact
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.groupchat.GroupChatContract
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import com.vicpin.krealmextensions.queryAll
import kotlinx.android.synthetic.main.activity_group_invite.*

/**
 * Created by SungWon on 3/5/2018.
 */
class GroupInviteActivity : CoreActivity(), GroupChatContract.Invite.View {

    private var mEightContacts: List<Contact> = listOf()
    private var mAdapter: BaseRecyclerAdapter<Contact, BaseViewHolder>? = null
    private lateinit var recyclerSections: List<String>
    private var currentUserId: Int? = null

    override lateinit var controller: GroupChatContract.Invite.Controller

    override fun layoutId(): Int = R.layout.activity_group_invite

    override fun contentContainerId(): Int = 0

    override fun onStart() {
        super.onStart()
        setToolbarTitle(getString(R.string.invite_friends))
        setUpRecycler()
        setupSearchBar()
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
        loadAndGroupContacts()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun loadAndGroupContacts() {
        //Query realm eight contacts
        mEightContacts = Contact().queryAll().toMutableList()

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

    private fun setUpRecycler() {
        mAdapter = object : BaseRecyclerAdapter<Contact, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Contact?, position: Int, type: Int) {
                val pic = viewHolder?.get<ImageView>(R.id.cvgi_friend_profile_picture)
                val name = viewHolder?.get<TextView>(R.id.cvgi_friend_name)
                val checkbox = viewHolder?.get<CheckBox>(R.id.cvgi_checkbox)
                Picasso.with(context).load(data?.avatar).resize(45, 45).centerCrop().placeholder(R.drawable.addphoto).transform(CircleTransform()).into(pic)
                name?.text = data?.first_name + " " + data?.last_name
                if (TemporaryManager.instance.isMemberChecked(data?.id!!)){
                    checkbox?.isChecked = true
                }
                checkbox?.let {
                    it.setOnCheckedChangeListener ({ _ , b ->
                        if (b) TemporaryManager.instance.mMemberList.add(Triple(data.id, data.first_name + " " + data.last_name, data.avatar!!))
                        else TemporaryManager.instance.mMemberList.remove(Triple(data.id, data.first_name + " " + data.last_name, data.avatar!!))
                    })
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_group_invite, inflater!!, parent){
                }
            }
        }
        mAdapter?.setItems(mEightContacts)
        mAdapter?.setSortComparator(EightQueries.Comparators.alphabetComparator)
        agi_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        agi_recycler.adapter = mAdapter
    }

    private fun setupSearchBar() {
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        activity.agi_searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        activity.agi_searchView?.queryHint = resources.getString(R.string.contacts)
        activity.agi_searchView?.isSubmitButtonEnabled = true
        activity.agi_searchView?.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener{

                    //Text Submit
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        //Search
                        mAdapter?.setFilter { filter ->
                            p0?.let {
                                filter?.first_name?.toLowerCase()?.startsWith(it.toLowerCase(), false)
                            }
                        }
                        activity.agi_searchView?.clearFocus()
                        val inputm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputm.hideSoftInputFromWindow(activity.agi_searchView?.windowToken, 0)
                        return true
                    }

                    //Text Change
                    override fun onQueryTextChange(p0: String?): Boolean {
                        mAdapter?.setFilter { filter ->
                            p0?.let {
                                filter?.first_name?.toLowerCase()?.startsWith(it.toLowerCase(),false)
                            }
                        }
                        return true
                    }


                }
        )
    }

    fun searchClicked(v: View){
        agi_searchView.isIconified = false
    }

    override fun refreshRecycler() {
        mAdapter?.notifyDataSetChanged()
    }

    override val activity: GroupInviteActivity
        get() = this
}