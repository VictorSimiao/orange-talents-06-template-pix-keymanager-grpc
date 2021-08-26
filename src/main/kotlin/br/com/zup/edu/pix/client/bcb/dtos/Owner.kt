package br.com.zup.edu.pix.client.bcb.dtos

import br.com.zup.edu.pix.client.bcb.dtos.enums.OwnerType

data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
)