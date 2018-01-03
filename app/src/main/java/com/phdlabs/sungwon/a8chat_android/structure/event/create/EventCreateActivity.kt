package com.phdlabs.sungwon.a8chat_android.structure.event.create

import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.application.Application
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.structure.event.EventContract
import kotlinx.android.synthetic.main.activity_event_create.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by SungWon on 1/2/2018.
 */
class EventCreateActivity: CoreActivity(), EventContract.Create.View {

    private var isLocked: Boolean = false

    override fun layoutId(): Int = R.layout.activity_event_create

    override fun contentContainerId(): Int = 0

    override lateinit var controller: EventContract.Create.Controller

    override fun onStart() {
        super.onStart()
        EventCreateController(this)
        controller.start()
        setUpViews()
        setUpClickers()
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

    private fun setUpViews(){
        setToolbarTitle("Create an Event")
        showRightTextToolbar("Create")
        showBackArrow(R.drawable.ic_back)
        aec_lock_switch.setOnCheckedChangeListener{ compoundButton, b ->
            isLocked = b
        }
    }

    private fun setUpClickers(){
        toolbar_right_text.setOnClickListener {

        }
        aec_event_picture.setOnClickListener {

        }
    }

    override val get8Application: Application
        get() = application as Application
}