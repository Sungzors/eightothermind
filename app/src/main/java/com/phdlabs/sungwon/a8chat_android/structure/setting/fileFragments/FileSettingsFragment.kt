package com.phdlabs.sungwon.a8chat_android.structure.setting.fileFragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.model.files.File
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreFragment
import com.phdlabs.sungwon.a8chat_android.structure.setting.SettingContract
import com.phdlabs.sungwon.a8chat_android.structure.setting.channel.ChannelSettingsActivity
import com.phdlabs.sungwon.a8chat_android.structure.setting.chat.ChatSettingActivity
import com.phdlabs.sungwon.a8chat_android.utility.SuffixDetector
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseRecyclerAdapter
import com.phdlabs.sungwon.a8chat_android.utility.adapter.BaseViewHolder
import com.phdlabs.sungwon.a8chat_android.utility.adapter.ViewMap
import kotlinx.android.synthetic.main.fragment_settings_files.*

/**
 * Created by JPAM on 3/14/18.
 */
class FileSettingsFragment : CoreFragment(), SettingContract.FileFragment.View {

    /*Controller*/
    override lateinit var controller: SettingContract.FileFragment.Controller

    /*Layout*/
    override fun layoutId(): Int = R.layout.fragment_settings_files

    /*Properties*/
    private var mFileList: MutableList<File> = mutableListOf()
    private lateinit var mFileAdapter: BaseRecyclerAdapter<File?, BaseViewHolder?>
    override lateinit var chatActivity: ChatSettingActivity
    override lateinit var channelActivity: ChannelSettingsActivity

    init {
        FileSettingsController(this)
    }

    companion object {

        /**
         * [newInstanceChatRoom]
         * @param chatRoomId
         * - Prepares the fragment to display files shared in a Private Chat Room
         * */
        fun newInstanceChatRoom(chatRoomId: Int): FileSettingsFragment {
            this.mChatRoomId = chatRoomId
            return FileSettingsFragment()
        }

        /**
         * [newInstanceChannelRoom]
         * @param roomId
         * - Prepares the fragment to display files shared in a Channel Room
         * */
        fun newInstanceChannelRoom(roomId: Int): FileSettingsFragment {
            mRoomId = roomId
            return FileSettingsFragment()
        }

        var mChatRoomId: Int? = null
        var mRoomId: Int? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mChatRoomId?.let {
            chatActivity = activity as ChatSettingActivity
        }
        mRoomId?.let {
            channelActivity = activity as ChannelSettingsActivity
        }
    }

    override fun onStart() {
        super.onStart()
        controller.start()
        //Adapter
        setupFileAdapter()
        //Only a single value should be initialized
        mChatRoomId?.let {
            controller.queryFiilesForChatRoom(it)
        }
        mRoomId?.let {
            controller.queryFilesForChannelRoom(it)
        }
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
        mRoomId = null
        mChatRoomId = null
    }

    /*ADAPTER*/
    fun setupFileAdapter() {
        mFileAdapter = object : BaseRecyclerAdapter<File?, BaseViewHolder?>() {
            override fun onBindItemViewHolder(viewHolder: BaseViewHolder?, data: File?, position: Int, type: Int) {
                val fileNameTextView = viewHolder?.get<TextView>(R.id.vfi_file_name)
                data?.file_string?.let {
                    fileNameTextView?.text = it
                }
            }

            override fun viewHolder(inflater: LayoutInflater?, parent: ViewGroup?, type: Int): BaseViewHolder? {
                return object : BaseViewHolder(R.layout.view_file_item, inflater!!, parent) {
                    override fun addClicks(views: ViewMap?) {
                        views?.click {
                            val file = getItem(adapterPosition)
                            file?.let {
                                val mime = MimeTypeMap.getSingleton()
                                val intent = Intent(Intent.ACTION_VIEW)
                                val mimeType = mime.getMimeTypeFromExtension(SuffixDetector.instance.getFileSuffix(it.file_string.toString()))
                                intent.setDataAndType(Uri.parse(it.s3_url), mimeType)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                try {
                                    context?.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(context, "Can't open this type of File", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
        mFileAdapter.setItems(mFileList)
        fsf_file_recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fsf_file_recyclerView.adapter = mFileAdapter
        fsf_file_recyclerView.isNestedScrollingEnabled = false
    }

    override fun updateFileAdapter(fileList: List<File>?) {
        fileList?.let {
            if (it.count() > 0) {
                for (file in it) {
                    mFileList.add(file)
                }
                mFileAdapter.clear()
                mFileAdapter.setItems(mFileList)
                mFileAdapter.notifyDataSetChanged()
            }

            mRoomId?.let {
                channelActivity.updateSelectorTitle(null, String.format(getString(R.string.filenum, mFileList.size.toString())))
            }
            mChatRoomId?.let {
                chatActivity.updateSelectorTitle(null, String.format(getString(R.string.filenum, mFileList.size.toString())))
            }

        }
    }

}