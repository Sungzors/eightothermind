package com.phdlabs.sungwon.a8chat_android.services.googlePlay

import android.app.Activity

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.phdlabs.sungwon.a8chat_android.utility.Constants

/**
 * Created by JPAM on 3/21/18.
 * Singleton Helper class to provide
 * information & access to GooglePlay Services
 */
class CheckGPServices {

    private object Holder {
        val INSTANCE = CheckGPServices()
    }

    companion object {
        val instance: CheckGPServices by lazy { Holder.INSTANCE }
    }


    /**
     * [isGooglePlayServicesAvailable]
     * Verify Google Play Services availability
     * & prompt user for a resolution if the device
     * is compatible
     * */
    fun isGooglePlayServicesAvailable(activity: Activity): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(activity)
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(
                        activity, status, Constants.RequestCodes.VALIDATE_GOOGLE_PLAY_SERVICES).show()
            }
            return false
        }
        return true
    }

}