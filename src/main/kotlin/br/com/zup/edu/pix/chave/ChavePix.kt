package br.com.zup.edu.pix.chave

import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class ChavePix(

    @field:NotNull
    @Column(nullable = false)
    val clienteId: UUID,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipo: TipoChave,

    @field:NotBlank
    @field:Size(max = 77)
    @Column(unique = true, nullable = false)
    val chave: String,

    @field:Valid
    @Embedded
    val contaAssociada: ContaAssociada,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoConta: TipoConta,


) {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    var id: UUID? = null

    @Column(nullable = false)
    @field:NotNull
    val criadaEm:LocalDateTime = LocalDateTime.now()
}
