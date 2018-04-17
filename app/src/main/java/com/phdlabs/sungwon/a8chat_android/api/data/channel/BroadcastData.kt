package com.phdlabs.sungwon.a8chat_android.api.data.channel

import com.google.firebase.iid.FirebaseInstanceId

/**
 * Created by JPAM on 4/10/18.
 * Body data for starting a Live Video Broadcast inside an Eight [Channel]
 */
data class BroadcastData(var userId: String, var roomId: String)