package com.phdlabs.sungwon.a8chat_android.structure.camera.result

import com.otaliastudios.cameraview.Size
import java.io.File

/**
 * Created by JPAM on 4/25/18.
 * Will hold image & video results to be handled in Preview Activity
 */
class ResultHolder {

    companion object {

        /*Private Static Properties*/
        private var image: ByteArray? = null
        private var video: File? = null
        private var nativeCaptureSize: Size? = null
        private var timeToCallback: Long = 0

        fun setResultImage(image: ByteArray?) {
            ResultHolder.image = image
        }

        fun getResultImage(): ByteArray? {
            return image
        }

        fun setResultVideo(video: File?) {
            ResultHolder.video = video
        }

        fun getResultVideo(): File? {
            return video
        }

        fun setResultNativeCaptureSize(nativeCaptureSize: Size?) {
            nativeCaptureSize?.let {
                ResultHolder.nativeCaptureSize = it
            }
        }

        fun getResultNativeCaptureSize(): Size? {
            return nativeCaptureSize
        }

        fun setResultTimeToCallback(timeToCallback: Long) {
            ResultHolder.timeToCallback = timeToCallback
        }

        fun getResultTimeToCallback(): Long {
            return timeToCallback
        }

        fun dispose() {
            setResultImage(null)
            setResultNativeCaptureSize(null)
            setResultTimeToCallback(0)
        }
    }

}