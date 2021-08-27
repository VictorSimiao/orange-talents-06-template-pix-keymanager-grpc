package br.com.zup.edu.pix.client.bcb.dtos

import br.com.zup.edu.pix.chave.ChavePix
import br.com.zup.edu.pix.chave.ContaAssociada
import br.com.zup.edu.pix.client.bcb.dtos.enums.AccountType
import br.com.zup.edu.pix.client.bcb.dtos.enums.OwnerType
import br.com.zup.edu.pix.client.bcb.dtos.enums.PixKeyType
import java.time.LocalDateTime

data class CreatePixKeyResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreatePixKeyResponse

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    companion object {

        fun of(chave: ChavePix): CreatePixKeyResponse {
            return CreatePixKeyResponse(
                keyType = PixKeyType.by(chave.tipo),
                key = chave.chave,
                bankAccount = BankAccount(
                    participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
                    branch = chave.contaAssociada.agencia,
                    accountNumber = chave.contaAssociada.numeroDaConta,
                    accountType = AccountType.by(chave.tipoConta)
                ),
                owner = Owner(
                    type = OwnerType.NATURAL_PERSON,
                    name = chave.contaAssociada.nomeDoTitular,
                    taxIdNumber = chave.contaAssociada.cpfDoTitular
                ),
                createdAt = chave.criadaEm
            )
        }
    }
}