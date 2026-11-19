package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.wefunDataStore: DataStore<Preferences> by preferencesDataStore(name = "wefun_prefs")

data class WefunAppSettings(
    val isDarkMode: Boolean = false,
    val isLoggedIn: Boolean = false,
    val hasCompletedOnboarding: Boolean = false,
    val notifyGifts: Boolean = true,
    val notifyRoomInvites: Boolean = true,
    val notifyPkAlerts: Boolean = true,
    val notifyMessages: Boolean = true,
    val parentalControlEnabled: Boolean = false,
    val blockedUserIds: Set<String> = emptySet(),
)

class WefunPreferences(private val context: Context) {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val NOTIFY_GIFTS = booleanPreferencesKey("notify_gifts")
        val NOTIFY_ROOMS = booleanPreferencesKey("notify_rooms")
        val NOTIFY_PK = booleanPreferencesKey("notify_pk")
        val NOTIFY_MESSAGES = booleanPreferencesKey("notify_messages")
        val PARENTAL_CONTROL = booleanPreferencesKey("parental_control")
        val BLOCKED_USERS = stringSetPreferencesKey("blocked_users")
    }

    val settingsFlow: Flow<WefunAppSettings> = context.wefunDataStore.data.map { prefs ->
        WefunAppSettings(
            isDarkMode = prefs[Keys.DARK_MODE] ?: false,
            isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: false,
            hasCompletedOnboarding = prefs[Keys.ONBOARDING_DONE] ?: false,
            notifyGifts = prefs[Keys.NOTIFY_GIFTS] ?: true,
            notifyRoomInvites = prefs[Keys.NOTIFY_ROOMS] ?: true,
            notifyPkAlerts = prefs[Keys.NOTIFY_PK] ?: true,
            notifyMessages = prefs[Keys.NOTIFY_MESSAGES] ?: true,
            parentalControlEnabled = prefs[Keys.PARENTAL_CONTROL] ?: false,
            blockedUserIds = prefs[Keys.BLOCKED_USERS] ?: emptySet(),
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.wefunDataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setParentalControl(enabled: Boolean) {
        context.wefunDataStore.edit { it[Keys.PARENTAL_CONTROL] = enabled }
    }

    suspend fun setLoggedIn(loggedIn: Boolean, onboardingDone: Boolean = true) {
        context.wefunDataStore.edit {
            it[Keys.IS_LOGGED_IN] = loggedIn
            it[Keys.ONBOARDING_DONE] = onboardingDone
        }
    }

    suspend fun updateNotificationPrefs(
        gifts: Boolean,
        rooms: Boolean,
        pk: Boolean,
        messages: Boolean,
    ) {
        context.wefunDataStore.edit {
            it[Keys.NOTIFY_GIFTS] = gifts
            it[Keys.NOTIFY_ROOMS] = rooms
            it[Keys.NOTIFY_PK] = pk
            it[Keys.NOTIFY_MESSAGES] = messages
        }
    }

    suspend fun toggleBlockUser(userId: String) {
        context.wefunDataStore.edit { prefs ->
            val current = prefs[Keys.BLOCKED_USERS] ?: emptySet()
            prefs[Keys.BLOCKED_USERS] = if (userId in current) current - userId else current + userId
        }
    }

    suspend fun clearAll() {
        context.wefunDataStore.edit { it.clear() }
    }
}
