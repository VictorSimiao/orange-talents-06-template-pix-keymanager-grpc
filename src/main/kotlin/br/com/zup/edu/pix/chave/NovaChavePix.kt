package br.com.zup.edu.pix.chave

import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.pix.validation.TipoDeChave

import br.com.zup.edu.pix.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Introspected
data class NovaChavePix(

    @ValidUUID
    @field:NotBlank
    val clienteId: String,

    @field:NotNull
    val tipoChave: TipoDeChave?,

    @field:Size(max = 77)
    val chave: String,

    @field:NotNull
    val tipoConta: TipoConta?

) {
    fun toModel(contaAssociada: ContaAssociada): ChavePix {
        return ChavePix(
            clienteId = UUID.fromString(this.clienteId),
            tipo = TipoChave.valueOf(this.tipoChave!!.name),
            chave = this.chave,
            tipoConta = TipoConta.valueOf(this.tipoConta!!.name),
            contaAssociada = contaAssociada
        )
    }
}