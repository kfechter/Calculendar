package com.kennethfechter.calculendar.businesslogic

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.*
import com.kennethfechter.calculendar.enumerations.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "calculendar_settings")
val THEME_SETTING = intPreferencesKey("theme_setting")
val ANALYTICS_SETTING = intPreferencesKey("analytics_setting")
val TUTORIAL_SHOWN = booleanPreferencesKey("TUTORIAL_SHOWN")

suspend fun Context.setAppTheme(theme: Theme) {
    dataStore.edit { settings ->
            settings[THEME_SETTING] = when (theme) {
                Theme.Day -> 1
                Theme.Night -> 2
                Theme.PowerSave -> 3
                Theme.System -> 4
            }
        }
    }

    suspend fun Context.setAnalytics(analyticsEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ANALYTICS_SETTING] = when (analyticsEnabled) {
                false -> 0
                true -> 1
            }
        }
    }

    suspend fun Context.setTutorial(tutorialShown: Boolean) {
        dataStore.edit {preferences ->
            preferences[TUTORIAL_SHOWN] = tutorialShown
        }
    }

fun Context.readTheme(): Flow<Theme> {
    return dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            when (preference[THEME_SETTING] ?: 1) {
                1 -> Theme.Day
                2 -> Theme.Night
                3 -> Theme.PowerSave
                4 -> Theme.System
                else -> Theme.Day
            }
        }
}
fun Context.readTutorial() : Flow<Boolean> {
    return dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
                preference -> preference[TUTORIAL_SHOWN] ?: false
        }
}

fun Context.readAnalytics(): Flow<Int> {
    return dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            preference[ANALYTICS_SETTING] ?: -1
        }
}