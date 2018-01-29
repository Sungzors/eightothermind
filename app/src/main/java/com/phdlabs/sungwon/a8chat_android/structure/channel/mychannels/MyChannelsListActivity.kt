package com.phdlabs.sungwon.a8chat_android.structure.channel.mychannels

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.channel.Channel
import com.phdlabs.sungwon.a8chat_android.structure.channel.ChannelContract
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.Constants
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import com.phdlabs.sungwon.a8chat_android.utility.camera.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_channel_list.*

/**
 * Created by SungWon on 12/6/2017.
 */
class MyChannelsListActivity: CoreActivity(), ChannelContract.MyChannelsList.View{
    override lateinit var controller: ChannelContract.MyChannelsList.Controller

    override fun layoutId(): Int = R.layout.activity_channel_list

    override fun contentContainerId(): Int = 0

    private lateinit var mAdapter: BaseRecyclerAdapter<Channel, BaseViewHolder>

    private val mChannelList = mutableListOf<Channel>()
    private val mFilteredList = mutableListOf<Channel>()

    override fun onStart() {
        super.onStart()
        MyChannelsListController(this)
        controller.start()
        setUpRecycler()
        controller.retrieveChannels()
        setUpSearcher()
        setToolbarTitle("My Channels")
    }

    override fun onResume() {
        super.onResume()
        controller.resume()
    }

    override fun onPause() {
        super.onPause()
        controller.pause()
    }

    override fun onStop() {
        super.onStop()
        controller.stop()
    }

    private fun setUpSearcher(){
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        acl_searchview.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        acl_searchview.queryHint = resources.getString(R.string.search_channel)
        acl_searchview.isSubmitButtonEnabled = true
        acl_searchview.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    mFilteredList.clear()
                    for(channel in mChannelList){
                        if(channel.name.toLowerCase().contains(p0!!.toLowerCase())){
                            mFilteredList.add(channel)
                        }
                    }
                    updateRecyclerSearch()
                    acl_searchview.clearFocus()
                    val inputm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputm.hideSoftInputFromWindow(acl_searchview.windowToken, 0)
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean = false
            }
        )
    }

    private fun setUpRecycler(){
        mAdapter = object : BaseRecyclerAdapter<Channel, BaseViewHolder>(){
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: Channel?, position: Int, type: Int) {
                bindItemViewHolder(viewHolder!!, data!!)
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder {
                return object : BaseViewHolder(R.layout.card_view_channel_list, inflater!!, parent){
                    override fun addClicks(views: ViewMap?) {
                        views!!.click({ view ->
                            val intent = Intent().putExtra(Constants.IntentKeys.CHANNEL_ID, getItem(adapterPosition).id)
                            setResult(Constants.ResultCode.SUCCESS, intent)
                            finish()
                        })
                    }
                }
            }
        }
        val layoutmanager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mAdapter.setItems(mChannelList)
        acl_my_channels.layoutManager = layoutmanager
        acl_my_channels.adapter = mAdapter
    }

    private fun bindItemViewHolder(viewHolder: BaseViewHolder, data: Channel){
        val pic = viewHolder.get<ImageView>(R.id.cvcl_channel_pic)
        val text = viewHolder.get<TextView>(R.id.cvcl_channel_text)
        Picasso.with(this).load(data.avatar).transform(CircleTransform()).into(pic)
        text.text = data.name
    }

    override fun addChannel(channel: Channel) {
        mChannelList.add(channel)
    }

    override fun updateRecycler() {
        mAdapter.clear()
        mAdapter.setItems(mChannelList)
        mAdapter.notifyDataSetChanged()
    }

    private fun updateRecyclerSearch(){
        mAdapter.clear()
        mAdapter.setItems(mFilteredList)
        mAdapter.notifyDataSetChanged()
    }
}