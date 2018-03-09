package com.phdlabs.sungwon.a8chat_android.structure.channel.searchFragments

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.db.channels.ChannelsManager
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.structure.channel.mychannel.MyChannelActivity
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
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
    private var mChannelsAll = mutableListOf<Channel>()
    private var mChannelsFollowedAdapter: BaseRecyclerAdapter<Channel, BaseViewHolder>? = null
    private var mChannelsPopularAdapter: BaseRecyclerAdapter<Channel, BaseViewHolder>? = null
    private var mChannelsAllAdapter: BaseRecyclerAdapter<Channel, BaseViewHolder>? = null

    /*LifeCycle*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChannelsIFollowRecycler()
        setupChannelsPopularRecycler()
        setupAllChannelsRecycler()
        setupSearchBar()
    }

    override fun onResume() {
        super.onResume()
        //Load Info
        loadChannels()
    }

    /*Channels I Follow Recycler*/
    private fun setupChannelsIFollowRecycler() {
        mChannelsFollowedAdapter = object : BaseRecyclerAdapter<Channel, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                /*UI*/
                bindChannel(viewHolder, data)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_eight_channel_card, inflater!!, parent) {
                    /*Actions*/
                    override fun addClicks(views: ViewMap?) {
                        super.addClicks(views)
                        //Open Channel
                        views?.click {
                            openChannel(getItem(adapterPosition))
                        }
                        super.addClicks(views)
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
    private fun setupChannelsPopularRecycler() {
        mChannelsPopularAdapter = object : BaseRecyclerAdapter<Channel, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                /*UI*/
                bindChannel(viewHolder, data)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_eight_channel_card, inflater!!, parent) {
                    /*Actions*/
                    override fun addClicks(views: ViewMap?) {
                        //Open Channel
                        views?.click {
                            openChannel(getItem(adapterPosition))
                        }
                        super.addClicks(views)
                    }
                }
            }

        }
        mChannelsPopularAdapter?.setItems(mChannelsPopular)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        cf_channels_popular_recycler.layoutManager = layoutManager
        cf_channels_popular_recycler.adapter = mChannelsPopularAdapter
    }

    /*All Channels Recycler*/
    private fun setupAllChannelsRecycler() {
        mChannelsAllAdapter = object : BaseRecyclerAdapter<Channel, BaseViewHolder>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                /*UI*/
                bindChannel(viewHolder, data)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.view_eight_channel_card, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        //Open Channel
                        //Open Channel
                        views?.click {
                            openChannel(getItem(adapterPosition))
                        }
                        super.addClicks(views)
                    }
                }
            }
        }
        mChannelsAllAdapter?.setItems(mChannelsAll)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        cf_channels_all_recycler.layoutManager = layoutManager
        cf_channels_all_recycler.adapter = mChannelsAllAdapter
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
                        .transform(CircleTransform())
                        .into(channelImage)
                channelName?.text = data?.name
            }
        }
    }

    /*Selected channel intent*/
    private fun openChannel(channel: Channel) {
        //On channel clicked
        val intent = Intent(context, MyChannelActivity::class.java)
        intent.putExtra(Constants.IntentKeys.CHANNEL_ID, channel.id)
        intent.putExtra(Constants.IntentKeys.CHANNEL_NAME, channel.name)
        intent.putExtra(Constants.IntentKeys.ROOM_ID, channel.room_id)
        intent.putExtra(Constants.IntentKeys.OWNER_ID, channel.user_creator_id)
        activity?.startActivity(intent)
    }

    /*UI Changes*/
    private fun hideFollowedAndPopularChannels(isHidden: Boolean) {
        if (isHidden) {
            fc_followed_container.visibility = View.GONE
            fc_popular_container.visibility = View.GONE
            fc_all_container.visibility = View.VISIBLE
        } else {
            fc_followed_container.visibility = View.VISIBLE
            fc_popular_container.visibility = View.VISIBLE
            fc_all_container.visibility = View.GONE
        }
    }


    /*Search Channels*/
    private fun setupSearchBar() {
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        activity?.ca_searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        activity?.ca_searchView?.queryHint = resources.getString(R.string.channels)
        activity?.ca_searchView?.isSubmitButtonEnabled = true
        activity?.ca_searchView?.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {

                    //Text Submit
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        //UI
                        if (!p0.isNullOrBlank()) {
                            hideFollowedAndPopularChannels(true)
                        } else {
                            hideFollowedAndPopularChannels(false)
                        }
                        //Filter Popular channels
                        mChannelsAllAdapter?.setFilter { filter ->
                            p0?.let {
                                filter?.name?.toLowerCase()?.startsWith(it.toLowerCase(), false)
                            }
                        }
                        activity?.ca_searchView?.clearFocus()
                        val inputm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputm.hideSoftInputFromWindow(activity?.ca_searchView?.windowToken, 0)
                        return true
                    }

                    //Text Change
                    override fun onQueryTextChange(p0: String?): Boolean {
                        //UI
                        if (!p0.isNullOrBlank()) {
                            hideFollowedAndPopularChannels(true)
                        } else {
                            hideFollowedAndPopularChannels(false)
                        }
                        //Filter popular channels
                        mChannelsAllAdapter?.setFilter { filter ->
                            p0?.let {
                                filter?.name?.toLowerCase()?.startsWith(it.toLowerCase(), false)
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
        ChannelsManager.instance.getAllFollowedChannels()?.let {
            mChannelsIFollow = it.toMutableList()
            if (mChannelsIFollow.count() > 0) {
                mChannelsFollowedAdapter?.clear()
                mChannelsFollowedAdapter?.setItems(mChannelsIFollow)
                mChannelsFollowedAdapter?.notifyDataSetChanged()
            }
        }
        //All Channels
        ChannelsManager.instance.getAllChannels()?.let {
            mChannelsAll = it.toMutableList()
            if (mChannelsAll.count() > 0) {
                mChannelsAllAdapter?.clear()
                mChannelsAllAdapter?.setItems(mChannelsAll)
                mChannelsAllAdapter?.notifyDataSetChanged()
            }
        }

    }


}