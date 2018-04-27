package com.phdlabs.sungwon.a8chat_android.structure.camera.cameraView

import android.util.Size

/**
 * Created by JPAM on 4/24/18.
 */
/**
 * Compares two sizes based on their areas
 * [CompareSizesByArea]
 * */
class CompareSizesByArea : Comparator<Size> {
    override fun compare(p0: Size?, p1: Size?): Int {
        //Cast to avoid overflow with multipliers
        var compare: Int = 0
        p0?.let {
            p1?.let {
                compare = java.lang.Long.signum(p0.width.toLong() * p0.height -
                        p1.width.toLong() * p1.height)
            }
        }
        return compare
    }

}