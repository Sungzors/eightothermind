package com.phdlabs.sungwon.a8chat_android.utility

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import com.phdlabs.sungwon.a8chat_android.R

class SoundPoolManager private constructor(context: Context) {

    private var playing = false
    private var loaded = false
    private val actualVolume: Float
    private val maxVolume: Float
    private val volume: Float
    private val audioManager: AudioManager
    private var soundPool: SoundPool? = null
    private val ringingSoundId: Int
    private var ringingStreamId: Int = 0
    private val disconnectSoundId: Int

    init {
        // AudioManager audio settings for adjusting the volume
        audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        volume = actualVolume / maxVolume

        // Load the sounds
        val maxStreams = 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .build()
        } else {
            soundPool = SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0)
        }

        soundPool!!.setOnLoadCompleteListener { soundPool, sampleId, status -> loaded = true }
        ringingSoundId = soundPool!!.load(context, R.raw.incoming, 1)
        disconnectSoundId = soundPool!!.load(context, R.raw.disconnect, 1)
    }

    fun playRinging() {
        if (loaded && !playing) {
            ringingStreamId = soundPool!!.play(ringingSoundId, volume, volume, 1, -1, 1f)
            playing = true
        }
    }

    fun stopRinging() {
        if (playing) {
            soundPool!!.stop(ringingStreamId)
            playing = false
        }
    }

    fun playDisconnect() {
        if (loaded && !playing) {
            soundPool!!.play(disconnectSoundId, volume, volume, 1, 0, 1f)
            playing = false
        }
    }

    fun release() {
        if (soundPool != null) {
            soundPool!!.unload(ringingSoundId)
            soundPool!!.unload(disconnectSoundId)
            soundPool!!.release()
            soundPool = null
        }
        instance = null
    }

    companion object {
        private var instance: SoundPoolManager? = null

        fun getInstance(context: Context): SoundPoolManager {
            if (instance == null) {
                instance = SoundPoolManager(context)
            }
            return instance as SoundPoolManager
        }
    }

}
