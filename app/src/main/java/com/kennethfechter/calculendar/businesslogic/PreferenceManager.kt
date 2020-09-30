package com.kennethfechter.calculendar.businesslogic

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import com.kennethfechter.calculendar.enumerations.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferenceManager(context: Context) {
    private val dataStore = context.createDataStore(name = "calculendar_settings")

    companion object {
        val THEME_SETTING = preferencesKey<Int>("theme_setting")
    }

    suspend fun setTheme(theme: Theme) {
        dataStore.edit {preferences ->
            preferences[THEME_SETTING] = when (theme) {
                Theme.Day -> 1
                Theme.Night -> 2
                Theme.PowerSave -> 3
                Theme.System -> 4
            }
        }
    }

    val themeFlow: Flow<Theme> = dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            val themeSetting = preference[THEME_SETTING] ?: 1

            when (themeSetting) {
                1 -> Theme.Day
                2 -> Theme.Night
                3 -> Theme.PowerSave
                4 -> Theme.System
                else -> Theme.Day
            }
        }
}