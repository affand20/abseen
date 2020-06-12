package id.trydev.abseen.prefs

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val PREFS_NAME = "id.trydev.abseen.prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val USER_NAME = "USER_NAME"
    private val USER_PHONE = "USER_PHONE"
    private val USER_ROLE = "USER_ROLE"

    var name: String?
        get() = prefs.getString(USER_NAME, null)
        set(value) = prefs.edit().putString(USER_NAME, value).apply()

    var phone: String?
        get() = prefs.getString(USER_PHONE, null)
        set(value) = prefs.edit().putString(USER_PHONE, value).apply()

    var role: Int
        get() = prefs.getInt(USER_ROLE, -1)
        set(value) = prefs.edit().putInt(USER_ROLE, value).apply()

    fun resetPrefs() {
        prefs.edit().clear().apply()
    }

}