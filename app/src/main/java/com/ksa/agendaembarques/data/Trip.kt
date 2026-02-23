package com.ksa.agendaembarques.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Trip(
    val id: String = UUID.randomUUID().toString(),
    val clienteResponsavel: String,
    val passageiroPrincipal: String,
    val cpf: String,
    val idPassageiro: String,
    val dataEmbarqueIso: String,
    val dataRetornoIso: String
)
