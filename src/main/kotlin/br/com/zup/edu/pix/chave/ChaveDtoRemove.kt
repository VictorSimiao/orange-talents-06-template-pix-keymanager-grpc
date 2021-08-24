package br.com.zup.edu.pix.chave

import br.com.zup.edu.pix.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class ChaveDtoRemove(
    @ValidUUID
    @field:NotBlank
    val clienteId: String,

    @ValidUUID
    @field:NotBlank
    val pixId: String,
) {
}