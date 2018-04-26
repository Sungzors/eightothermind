package com.phdlabs.sungwon.a8chat_android.structure.camera.result

import com.camerakit.CameraKitView
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
        private var nativeCaptureSize: CameraKitView.Size? = null
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

        fun setResultNativeCaptureSize(nativeCaptureSize: CameraKitView.Size?) {
            ResultHolder.nativeCaptureSize = nativeCaptureSize
        }

        fun getResultNativeCaptureSize(): CameraKitView.Size? {
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