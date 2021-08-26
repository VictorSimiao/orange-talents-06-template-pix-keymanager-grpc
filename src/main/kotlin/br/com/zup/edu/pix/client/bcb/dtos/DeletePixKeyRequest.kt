package br.com.zup.edu.pix.client.bcb.dtos

import br.com.zup.edu.pix.chave.ContaAssociada

data class DeletePixKeyRequest (
    val key: String,
    val participant: String = ContaAssociada.ITAU_UNIBANCO_ISPB,
)