package com.phdlabs.sungwon.a8chat_android.structure.channel.searchFragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.fragment_channels.*

/**
 * Created by paix on 2/13/18.
 * [ChannelsFragment]
 * - Shows the channels I'm following
 * - Selecting a channel will take me to the channel detail view
 * - Control for this fragment is done through [ContactsActivity]
 */
class ChannelsFragment : CoreFragment() {

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_channels

    /*Properties*/
    private var mChannelsIFollow = mutableListOf<Channel>()
    private var mChannelsPopular = mutableListOf<Channel>()
    private var mChannelsFollowedAdapter: BaseRecyclerAdapter<Channel, BaseViewHolder>? = null
    private var mChannelsPopularAdapter: BaseRecyclerAdapter<Channel, BaseViewHolder>? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChannelsIFollowRecycler()
        setupChannelsPopularRecycler()
        setupSearchBar()
    }

    override fun onResume() {
        super.onResume()
        //Load Info
        loadChannels()
    }

    /*Channels I Follow Recycler*/
    fun setupChannelsIFollowRecycler() {
        mChannelsFollowedAdapter = object : BaseRecyclerAdapter<Channel, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                /*UI*/
                bindChannel(viewHolder, data)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_eight_channel_card, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        super.addClicks(views)
                        //TODO: Open Channel with intent -> Could be the same function as the other adapter
                    }
                }
            }
        }
        mChannelsFollowedAdapter?.setItems(mChannelsIFollow)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        cf_channels_i_follow_recycler.layoutManager = layoutManager
        cf_channels_i_follow_recycler.adapter = mChannelsFollowedAdapter
    }

    /*Channels Popular Recycler*/
    fun setupChannelsPopularRecycler() {
        mChannelsPopularAdapter = object : BaseRecyclerAdapter<Channel, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                /*UI*/
                bindChannel(viewHolder, data)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_eight_channel_card, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        super.addClicks(views)
                        //TODO: Open Channel with intent -> Could be the same function as the other adapter
                    }
                }
            }

        }
        mChannelsPopularAdapter?.setItems(mChannelsPopular)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        cf_channels_popular_recycler.layoutManager = layoutManager
        cf_channels_popular_recycler.adapter = mChannelsPopularAdapter
    }

    //Bind Recycler's Channels
    private fun bindChannel(viewHolder: BaseViewHolder?, data: Channel?) {
        val channelImage = viewHolder?.get<ImageView>(R.id.vech_channel_image)
        val channelName = viewHolder?.get<TextView>(R.id.vech_channel_name)
        viewHolder?.let {
            context?.let {
                /*Load profile picture*/
                Picasso.with(it)
                        .load(data?.avatar)
                        .resize(70, 70)
                        .centerCrop()
                        .placeholder(R.drawable.addphoto)
                        .into(channelImage)
                channelName?.text = data?.name
            }
        }
    }

    /*UI Changes*/
    private fun hideMyFollowedChannels(isHidden: Boolean) {
        if (isHidden) {
            fc_followed_container.visibility = View.GONE
        } else {
            fc_followed_container.visibility = View.VISIBLE
        }
    }


    private fun setupSearchBar() {
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        activity?.ca_searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        activity?.ca_searchView?.queryHint = resources.getString(R.string.channels)
        activity?.ca_searchView?.isSubmitButtonEnabled = true
        activity?.ca_searchView?.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {

                    //Text Submit
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        if (!p0.isNullOrBlank()) {
                            hideMyFollowedChannels(true)
                        } else {
                            hideMyFollowedChannels(false)
                        }
                        //Search
                        mChannelsPopularAdapter?.setFilter { filter ->
                            p0?.let {
                                filter?.name?.toLowerCase()?.startsWith(it, false)
                            }
                        }
                        activity?.ca_searchView?.clearFocus()
                        val inputm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputm.hideSoftInputFromWindow(activity?.ca_searchView?.windowToken, 0)
                        return true
                    }

                    //Text Change
                    override fun onQueryTextChange(p0: String?): Boolean {
                        if (!p0.isNullOrBlank()) {
                            hideMyFollowedChannels(true)
                        } else {
                            hideMyFollowedChannels(false)
                        }
                        mChannelsPopularAdapter?.setFilter { filter ->
                            p0?.let {
                                filter?.name?.toLowerCase()?.startsWith(it, false)
                            }
                        }
                        return true
                    }


                }
        )
    }

    /*Data*/
    private fun loadChannels() {
        //Query Popular Channels
        ChannelsManager.instance.getPopularChannels()?.let {
            mChannelsPopular = it.toMutableList()
            if (mChannelsPopular.count() > 0) {
                mChannelsPopularAdapter?.clear()
                mChannelsPopularAdapter?.setItems(mChannelsPopular)
                mChannelsPopularAdapter?.notifyDataSetChanged()
            }
        }
        //Query Followed Channels
        ChannelsManager.instance.getAllFollwedChannels()?.let {
            mChannelsIFollow = it.toMutableList()
            if (mChannelsIFollow.count() > 0) {
                mChannelsFollowedAdapter?.clear()
                mChannelsFollowedAdapter?.setItems(mChannelsIFollow)
                mChannelsFollowedAdapter?.notifyDataSetChanged()
            }
        }

    }


}