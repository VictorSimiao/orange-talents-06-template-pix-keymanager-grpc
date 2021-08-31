package br.com.zup.edu.pix.chave

import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.pix.client.bcb.dtos.BankAccount
import br.com.zup.edu.pix.client.bcb.dtos.CreatePixKeyRequest
import br.com.zup.edu.pix.client.bcb.dtos.Owner
import br.com.zup.edu.pix.client.bcb.dtos.enums.AccountType
import br.com.zup.edu.pix.client.bcb.dtos.enums.OwnerType
import br.com.zup.edu.pix.client.bcb.dtos.enums.PixKeyType
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


    @field:Size(max = 77)
    @Column(unique = true, nullable = false)
    var chave: String,

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
    val criadaEm: LocalDateTime = LocalDateTime.now()

    fun toPixKeyRequest(): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            keyType = PixKeyType.by(this.tipo),
            key = this.chave,
            bankAccount = BankAccount(
                participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
                branch = this.contaAssociada.agencia,
                accountNumber = this.contaAssociada.numeroDaConta,
                accountType = AccountType.by(this.tipoConta)
            ),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = this.contaAssociada.nomeDoTitular,
                taxIdNumber = this.contaAssociada.cpfDoTitular
            )
        )
    }

    fun isAleatoria(): Boolean {
        return tipo == TipoChave.ALEATORIA
    }

    fun atualiza(chave: String): Boolean {
        if (isAleatoria()) {
            this.chave = chave
            return true
        }
        return false
    }

    fun pertenceAo(clienteId: UUID): Boolean {
        return this.clienteId.equals(clienteId)
    }
}
