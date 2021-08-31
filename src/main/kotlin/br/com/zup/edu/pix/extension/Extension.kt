package br.com.zup.edu.pix.extension

import br.com.zup.edu.*
import br.com.zup.edu.pix.chave.ChaveDtoRemove
import br.com.zup.edu.pix.chave.NovaChavePix
import br.com.zup.edu.pix.endponts.carrega.Filtro
import br.com.zup.edu.pix.validation.TipoDeChave
import javax.validation.ConstraintViolationException
import javax.validation.Validator

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

fun RemoveChaveRequest.toModel(): ChaveDtoRemove {
    return ChaveDtoRemove(
        clienteId = clienteId,
        pixId = pixId
    )
}


fun CarregaChavePixRequest.toModel(validator: Validator): Filtro {
    val filtro = when (filtroCase!!) {
        CarregaChavePixRequest.FiltroCase.PIXID -> pixId.let {
            Filtro.PorPixId(clienteId = it.clienteId, pixId = it.pixId)
        }
        CarregaChavePixRequest.FiltroCase.CHAVE -> Filtro.PorChave(chave)
        CarregaChavePixRequest.FiltroCase.FILTRO_NOT_SET -> Filtro.Invalido()
    }

    val violations = validator.validate(filtro)
    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations);
    }

    return filtro
}