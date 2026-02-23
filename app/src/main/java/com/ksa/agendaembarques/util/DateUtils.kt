package com.ksa.agendaembarques.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val brFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR"))
private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun millisToIso(millis: Long): String {
    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(isoFormatter)
}

fun isoToBr(iso: String): String = runCatching {
    LocalDate.parse(iso, isoFormatter).format(brFormatter)
}.getOrDefault(iso)

fun parseIso(iso: String): LocalDate = LocalDate.parse(iso, isoFormatter)
fun today(): LocalDate = LocalDate.now()
