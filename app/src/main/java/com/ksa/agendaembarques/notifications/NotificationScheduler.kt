package com.ksa.agendaembarques.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val UNIQUE_NAME = "embarque_daily_check"

    fun scheduleDaily(context: Context) {
        val now = LocalDateTime.now()
        val nextRun = now.with(LocalTime.of(9, 0)).let { if (it.isAfter(now)) it else it.plusDays(1) }
        val delay = Duration.between(now, nextRun).toMinutes().coerceAtLeast(1)

        val request = PeriodicWorkRequestBuilder<EmbarkNotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
