package com.phdlabs.sungwon.a8chat_android.api.data

/**
 * Created by paix on 2/28/18.
 * Only use one variable when enabling or disabling Read Receipts
 * @param enable -> true || false
 * @param disable -> true || false
 */
data class ReceiptPatchData(val enable: Int?, val disable: Boolean)