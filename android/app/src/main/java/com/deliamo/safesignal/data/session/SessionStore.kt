package com.deliamo.safesignal.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.deliamo.safesignal.domain.session.StoredSession
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "session_store")

object SessionStoreKeys {
    val SESSION_JSON = stringPreferencesKey("session_json")
}

class SessionStore(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    suspend fun load(): StoredSession {
        val prefs = context.dataStore.data.first()
        val raw = prefs[SessionStoreKeys.SESSION_JSON] ?: return StoredSession()
        return runCatching { json.decodeFromString<StoredSession>(raw) }
            .getOrElse { StoredSession() }
    }

    suspend fun save(session: StoredSession) {
        val raw = json.encodeToString(session)
        context.dataStore.edit { it[SessionStoreKeys.SESSION_JSON] = raw }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(SessionStoreKeys.SESSION_JSON) }
    }
}