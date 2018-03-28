package com.phdlabs.sungwon.a8chat_android.structure.camera.share

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.camera.CameraContract
import com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments.ShareToChannelFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments.PostFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments.ShareToContactFragment
import com.phdlabs.sungwon.a8chat_android.structure.camera.share.fragments.ShareToEventFragment
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.activity_create_new.*
import kotlinx.android.synthetic.main.activity_share_camera.*
import kotlinx.android.synthetic.main.toolbar_create_new.*

/**
 * Created by JPAM on 3/26/18.
 * Used for sharing camera media to Channels, Events & Private Conversations.
 */
class ShareCameraMediaActivity : CoreActivity(), CameraContract.Share.View, View.OnClickListener {

    //TODO: on Sent completion setResult OK to finish Editing Activity & go back to Camera App

    /*Controller*/
    override lateinit var controller: CameraContract.Share.Controller

    /*Properties*/
    private var currentContainer: Int = 0
    private var mFilePath: String? = null
    private var shareToChannelFragment: ShareToChannelFragment? = null
    private var shareToContactFragment: ShareToContactFragment? = null
    private var shareToEventFragment: ShareToEventFragment? = null
    private var mPostFragment: PostFragment? = null
    //Context
    override val getActivity: ShareCameraMediaActivity = this
    override val get8Application: Application
        get() = application as Application

    //Share Type
    enum class ShareType {
        Media, Post
    }

    /*Layout*/
    override fun layoutId(): Int = R.layout.activity_share_camera

    override fun contentContainerId(): Int = currentContainer

    override fun swapContainer(contentContainer: Int): Int {
        currentContainer = contentContainer
        return currentContainer
    }

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ShareCameraMediaController(this)
        controller.onCreate()
        processIntent()
        setupClickers()
        setupSearchBar()
    }

    override fun onStart() {
        super.onStart()
        controller.start()
        controller.loadMyChannels()
        controller.loadMyEvents()
        controller.loadMyContacts()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }

    override fun shareCompletion() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    /*Process Intent Information*/
    private fun processIntent() {
        //Thumbnail
        intent.getStringExtra(Constants.CameraIntents.IMAGE_FILE_PATH)?.let {
            mFilePath = it
            currentContainer = asc_thumbnail_container.id
            //Post Fragment
            mPostFragment = PostFragment.create(it)
            mPostFragment?.let {
                addFragment(contentContainerId(), it, false)
            }
            currentContainer = 0
        }
    }

    /*OnClick*/
    private fun setupClickers() {
        asc_share_button.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
        /*Share*/
            asc_share_button -> {
                //Validate Selection
                if (controller.validatedSelection(shareToChannelFragment?.mSelectedChannelList,
                                shareToEventFragment?.mEventList, shareToContactFragment?.getSharingContactsList())) {
                    //Validate Content
                    controller.infoValidation(mFilePath, mPostFragment?.getMessage())?.let {
                        if (it == ShareType.Media) {
                            //Only media - No message
                            shareToChannelFragment?.let {
                                controller.pushToChannel(it.mSelectedChannelList, ShareType.Media)
                            }
                            shareToEventFragment?.let {
                                controller.pushToEvent(it.mEventList, ShareType.Media)
                            }
                            shareToContactFragment?.let {
                                controller.pushToContact(it.getSharingContactsList(), ShareType.Media)
                            }
                        } else {
                            //Media + Message
                            shareToChannelFragment?.let {
                                controller.pushToChannel(it.mSelectedChannelList, ShareType.Post)
                            }
                            shareToEventFragment?.let {
                                controller.pushToEvent(it.mEventList, ShareType.Post)
                            }
                            shareToContactFragment?.let {
                                controller.pushToContact(it.getSharingContactsList(), ShareType.Post)
                            }
                        }
                    }
                } else {
                    showError("Select Channel, Event or Contact to share with")
                }
            }
        }
    }

    /*Show My Channels*/
    override fun showMyChannels() {
        currentContainer = asc_channels_container.id
        shareToChannelFragment = ShareToChannelFragment()
        shareToChannelFragment?.let {
            addFragment(contentContainerId(), it, false)
        }
        currentContainer = 0
    }

    /*Show My Events*/
    override fun showMyEvents() {
        currentContainer = asc_events_container.id
        shareToEventFragment = ShareToEventFragment()
        shareToEventFragment?.let {
            addFragment(contentContainerId(), it, false)
        }
        currentContainer = 0
    }

    /*Show My Contacts*/
    override fun showMyContacts() {
        currentContainer = asc_contacts_container.id
        shareToContactFragment = ShareToContactFragment()
        shareToContactFragment?.let {
            addFragment(contentContainerId(), it, false)
        }
        currentContainer = 0
    }

    //TODO: Setup SearchView & act upon available fragments to filter all simultaneously


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
                            //Filter Channels
                            shareToChannelFragment?.let {
                                it.filterChannelsAdapter(p0)
                            }
                            //Filter events
                            shareToEventFragment?.let {
                                it.filterEventsAdapter(p0)
                            }
                            //Filter contacts
                            shareToContactFragment?.let {
                                it.filterContactsAdapter(p0)
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
                            //Filter Channels
                            shareToChannelFragment?.let {
                                it.filterChannelsAdapter(p0)
                            }
                            //Filter events
                            shareToEventFragment?.let {
                                it.filterEventsAdapter(p0)
                            }
                            //Filter contacts
                            shareToContactFragment?.let {
                                it.filterContactsAdapter(p0)
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