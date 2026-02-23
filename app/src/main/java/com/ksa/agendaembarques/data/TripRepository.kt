package com.ksa.agendaembarques.data

import android.content.Context
import com.ksa.agendaembarques.util.KEY_TRIPS
import com.ksa.agendaembarques.util.PREFS_NAME
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TripRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun loadTrips(): List<Trip> {
        val raw = prefs.getString(KEY_TRIPS, null) ?: return emptyList()
        return runCatching { json.decodeFromString<List<Trip>>(raw) }.getOrDefault(emptyList())
    }

    fun saveTrips(trips: List<Trip>) {
        prefs.edit().putString(KEY_TRIPS, json.encodeToString(trips)).apply()
    }
}
