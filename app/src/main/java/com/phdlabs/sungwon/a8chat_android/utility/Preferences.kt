package com.phdlabs.sungwon.a8chat_android.utility

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by SungWon on 9/18/2017.
 */
class Preferences(val context: Context) {

    fun preferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun putPreference(key: String, value: String? = null) {
        preferences().edit().putString(key, value).apply()
    }

    fun putPreference(key: String, value: Int = 0) {
        preferences().edit().putInt(key, value).apply()
    }

    fun putPreference(key: String, value: Boolean = false) {
        preferences().edit().putBoolean(key, value).apply()
    }

    fun getPreferenceString(key: String, defValue: String? = null): String? = preferences().getString(key, defValue)

    fun getPreferenceInt(key: String, defValue: Int = 0): Int = preferences().getInt(key, defValue)

    fun getPreferenceBool(key: String, defValue: Boolean = false): Boolean = preferences().getBoolean(key, defValue)
}