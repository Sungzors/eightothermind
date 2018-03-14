package com.phdlabs.sungwon.a8chat_android.structure.createnew.searchChannels

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.structure.createnew.CreateNewContract

/**
 * Created by JPAM on 3/12/18.
 */
class ChannelSearchFragment : CoreFragment(), CreateNewContract.ChannelSearch.View {

    /*Controller*/
    override lateinit var controller: CreateNewContract.ChannelSearch.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_createnew_channels_search

    init {
        ChannelSearchFragController(this)
    }

    /*LifeCycle*/

}