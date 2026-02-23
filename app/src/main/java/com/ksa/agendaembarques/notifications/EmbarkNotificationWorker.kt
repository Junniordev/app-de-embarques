package com.ksa.agendaembarques.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ksa.agendaembarques.R
import com.ksa.agendaembarques.data.TripRepository
import com.ksa.agendaembarques.util.NOTIFICATION_CHANNEL_ID
import com.ksa.agendaembarques.util.isoToBr
import com.ksa.agendaembarques.util.parseIso
import com.ksa.agendaembarques.util.today

class EmbarkNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        createChannel()
        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return Result.success()

        val trips = TripRepository(applicationContext).loadTrips()
        val targetDate = today().plusDays(5)
        val due = trips.filter { parseIso(it.dataEmbarqueIso) == targetDate }
        if (due.isEmpty()) return Result.success()

        val names = due.joinToString { it.passageiroPrincipal }
        val text = "Você tem ${due.size} embarque(s) em ${isoToBr(targetDate.toString())}: $names. Toque para ver."

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Embarque em 5 dias")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(5005, notification)
        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Alertas de embarque",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificações para embarques com 5 dias restantes"
            applicationContext.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
