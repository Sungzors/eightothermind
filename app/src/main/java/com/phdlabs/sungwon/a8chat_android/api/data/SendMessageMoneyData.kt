package com.phdlabs.sungwon.a8chat_android.api.data

/**
 * Created by SungWon on 11/16/2017.
 */
data class SendMessageMoneyData(val roomId: String, val senderId: String, val receiverId: String, val currency: String, val amount: Int)