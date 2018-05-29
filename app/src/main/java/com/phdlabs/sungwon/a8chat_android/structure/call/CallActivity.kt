package com.phdlabs.sungwon.a8chat_android.structure.call

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.phdlabs.sungwon.a8chat_android.R
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity
import com.phdlabs.sungwon.a8chat_android.utility.SoundPoolManager
import com.twilio.voice.*

class CallActivity : CoreActivity(), CallContract.View {

    override fun layoutId(): Int = R.layout.activity_call

    override fun contentContainerId(): Int = 0

    override lateinit var controller: CallContract.Controller

    private var identity = ""
    private var mRoomID = 0

    private val MIC_PERMISSION_REQUEST_CODE = 1

    private var accessToken = ""
    private lateinit var audioManager: AudioManager
    private var savedAudioMode = AudioManager.MODE_INVALID

    private var isReceiverRegistered = false
    private lateinit var voiceBroadcastReceiver : VoiceBroadcastReceiver

    private val twiMLParams = HashMap<String, String>()

    private lateinit var soundPoolManager: SoundPoolManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var activeCallInvite: CallInvite
    private lateinit var activeCall: Call
    private var activeCallNotificationId: Int = 0

    val INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE"
    val INCOMING_CALL_NOTIFICATION_ID = "INCOMING_CALL_NOTIFICATION_ID"
    val ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL"
    val ACTION_FCM_TOKEN = "ACTION_FCM_TOKEN"

    val registrationListener = registrationListener()
    val callListener = callListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CallController(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setClicker()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        soundPoolManager = SoundPoolManager.getInstance(this)

        voiceBroadcastReceiver = VoiceBroadcastReceiver()
        registerListener()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = true

        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        setUpUI()

        handleIncomingCallIntent(intent)

        if (!checkPermissionForMicrophone()) {
            requestPermissionForMicrophone()
        } else {
            retrieveAccessToken()
        }
    }

    private fun setClicker(){

    }

    private fun setUpUI(){

    }

    private fun registerListener(){
        if (!isReceiverRegistered) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ACTION_INCOMING_CALL)
            intentFilter.addAction(ACTION_FCM_TOKEN)
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    voiceBroadcastReceiver, intentFilter)
            isReceiverRegistered = true
        }
    }

    private fun unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceBroadcastReceiver)
            isReceiverRegistered = false
        }
    }

    private fun handleIncomingCallIntent(intent: Intent){

    }

    private fun checkPermissionForMicrophone(): Boolean {
        val resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        return resultMic == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(context, "Please allow microphone permission to enable audio", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MIC_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MIC_PERMISSION_REQUEST_CODE && permissions.size > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            } else {
                retrieveAccessToken()
            }
        }
    }


//TODO: move this to launch
    inner class VoiceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            if (action == ACTION_INCOMING_CALL){
                handleIncomingCallIntent(p1)
            }
        }
    }

    private fun registrationListener() : RegistrationListener {
        return object: RegistrationListener{
            override fun onRegistered(accessToken: String?, fcmToken: String?) {
                Log.d(TAG, "Successfully registered FCM " + fcmToken)
            }

            override fun onError(error: RegistrationException?, accessToken: String?, fcmToken: String?) {
                val message = String.format("Registration Error: %d, %s", error?.getErrorCode(), error?.message)
                Log.e(TAG, message)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callListener() : Call.Listener {
        return object : Call.Listener{
            override fun onConnectFailure(p0: Call?, p1: CallException?) {
                setAudioFocus(false)
                //TODO: handle connect failure
            }

            override fun onConnected(call: Call?) {
                setAudioFocus(true)
                Log.d(TAG, "Connected")
                activeCall = call!!
            }

            override fun onDisconnected(p0: Call?, error: CallException?) {
                setAudioFocus(false)
                Log.d(TAG, "Disconnected")
                if (error != null) {
                    val message = String.format("Call Error: %d, %s", error.getErrorCode(), error.localizedMessage)
                    Log.e(TAG, message)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                //TODO: handle dc
            }
        }
    }

    private fun setAudioFocus(setFocus: Boolean) {
        if (audioManager != null) {
            if (setFocus) {
                savedAudioMode = audioManager.mode
                // Request audio focus before making any device switch.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val playbackAttributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener { }
                            .build()
                    audioManager.requestAudioFocus(focusRequest)
                } else {
                    audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                }
                /*
                 * Start by setting MODE_IN_COMMUNICATION as default audio mode. It is
                 * required to be in this mode when playout and/or recording starts for
                 * best possible VoIP performance. Some devices have difficulties with speaker mode
                 * if this is not set.
                 */
                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            } else {
                audioManager.mode = savedAudioMode
                audioManager.abandonAudioFocus(null)
            }
        }
    }

    private fun retrieveAccessToken(){
        
    }
}