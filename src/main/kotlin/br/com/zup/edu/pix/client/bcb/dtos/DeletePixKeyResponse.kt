package br.com.zup.edu.pix.client.bcb.dtos

import java.time.LocalDateTime

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)