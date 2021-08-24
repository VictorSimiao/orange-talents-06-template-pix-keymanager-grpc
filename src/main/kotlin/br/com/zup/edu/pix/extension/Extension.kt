package br.com.zup.edu.pix.extension

import br.com.zup.edu.RegistroChaveRequest
import br.com.zup.edu.RemoveChaveRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.pix.chave.ChaveDtoRemove
import br.com.zup.edu.pix.chave.NovaChavePix
import br.com.zup.edu.pix.validation.TipoDeChave

fun RegistroChaveRequest.toModel(): NovaChavePix {
    return NovaChavePix(
        clienteId = clienteId,
        tipoChave = when (tipoChave) {
            TipoChave.INDETERMINADA -> null
            else -> TipoDeChave.valueOf(tipoChave.name)
        },
        chave = valorChave,
        tipoConta = when (tipoConta) {
            TipoConta.NAO_ESPECIFICADO -> null
            else -> TipoConta.valueOf(tipoConta.name)
        }
    )
}

fun RemoveChaveRequest.toModel():ChaveDtoRemove{
    return ChaveDtoRemove(
        clienteId = clienteId,
        pixId = pixId
    )
}