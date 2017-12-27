package com.phdlabs.sungwon.a8chat_android.api.response

import com.phdlabs.sungwon.a8chat_android.model.media.Media

/**
 * Created by SungWon on 10/4/2017.
 * Updated by JPAM on 12/21/2017
 */
class MediaResponse : ErrorResponse() {
    var media: ArrayList<Media>? = ArrayList()
    //TODO: Not mapping array of media
//    internal var media_file_string: String? = null
//    internal var media_file: String? = null
}