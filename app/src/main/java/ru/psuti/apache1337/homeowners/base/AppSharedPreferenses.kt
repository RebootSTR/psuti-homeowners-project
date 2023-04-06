package ru.psuti.apache1337.homeowners.base

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


private const val USER_ID = "UserId"
private const val NUMBER = "number"
private const val NUMBER_INPUT = "NUMBER_INPUT"
const val BACKEND_VERSION = "BackendVersion"
const val BACKEND_URL = "BACKEND_URL"

@Singleton
class AppSharedPreferences @Inject constructor(
    @ApplicationContext context : Context
){
    val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    val userId: IntSharedPref = IntSharedPref(prefs, USER_ID, 0)
    var number: StringSharedPref = StringSharedPref(prefs, NUMBER, "")
    var numberInput: StringSharedPref = StringSharedPref(prefs, NUMBER_INPUT, "")
    var backendVersion: StringSharedPref = StringSharedPref(prefs, BACKEND_VERSION, "")
    var backendUrl: StringSharedPref = StringSharedPref(prefs, BACKEND_URL, "http://192.168.0.1:8080")
}

open class SharedPreferenceParam<T>(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: T,
    private val getMethod: SharedPreferences.(String, T) -> T?,
    private val setMethod: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
) {
    fun get(): T {
        return sharedPreferences.getMethod(key, defaultValue)!!
    }

    fun set(value: T) {
        sharedPreferences.edit().setMethod(key, value).apply()
    }

    fun remove() {
        sharedPreferences.edit().remove(key).apply()
    }
}

class StringSharedPref(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: String
): SharedPreferenceParam<String>(
    sharedPreferences,
    key,
    defaultValue,
    SharedPreferences::getString,
    SharedPreferences.Editor::putString
)

class IntSharedPref(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Int
): SharedPreferenceParam<Int>(
    sharedPreferences,
    key,
    defaultValue,
    SharedPreferences::getInt,
    SharedPreferences.Editor::putInt
)