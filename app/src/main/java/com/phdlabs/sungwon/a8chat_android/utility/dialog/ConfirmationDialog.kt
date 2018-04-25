package com.phdlabs.sungwon.a8chat_android.utility.dialog

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.utility.Constants.AppPermissions.VIDEO_PERMISSIONS
import com.phdlabs.sungwon.a8chat_android.utility.Constants.PermissionsReqCode.REQUEST_VIDEO_PERMISSIONS

/**
 * Created by JPAM on 4/24/18.
 * Global confirmation dialog
 */

class ConfirmationDialog : DialogFragment() {

    /*Used for Video Control & permissions*/
    override fun onCreateDialog(savedInstanceState: Bundle?) =
            AlertDialog.Builder(activity)
                    .setMessage(R.string.camera_permission_request)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        parentFragment?.requestPermissions(VIDEO_PERMISSIONS,
                                REQUEST_VIDEO_PERMISSIONS)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        parentFragment?.activity?.finish()
                    }
                    .create()
}