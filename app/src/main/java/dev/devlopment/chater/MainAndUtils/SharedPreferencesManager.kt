package dev.devlopment.chater.MainAndUtils

import android.content.Context
import android.content.SharedPreferences
import dev.devlopment.chater.MyApp

object SharedPreferencesManager {

    private const val PREF_NAME = "app_prefs"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize() {
        val context = MyApp.instance?.applicationContext
        sharedPreferences = context?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)!!
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}


