package com.phdlabs.sungwon.a8chat_android.api.response.media

import com.phdlabs.sungwon.a8chat_android.api.response.ErrorResponse
import com.phdlabs.sungwon.a8chat_android.model.media.Media

/**
 * Created by SungWon on 10/4/2017.
 * Updated by JPAM on 12/21/2017
 */
class MediaResponse : ErrorResponse() {

    //Media Array
    var mediaArray: Array<Media>? = emptyArray()

    //Shared Media Array (Between two users)
    var sharedMedia: Array<Media>? = emptyArray()
}