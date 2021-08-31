package br.com.zup.edu.pix.client.bcb.dtos

import br.com.zup.edu.TipoConta
import br.com.zup.edu.pix.Instituicoes
import br.com.zup.edu.pix.chave.ContaAssociada
import br.com.zup.edu.pix.client.bcb.dtos.enums.AccountType
import br.com.zup.edu.pix.client.bcb.dtos.enums.PixKeyType
import br.com.zup.edu.pix.endponts.carrega.ChavePixInfo
import java.time.LocalDateTime

class PixKeyDetailsResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
    ) {

    fun toModel(): ChavePixInfo {
        return ChavePixInfo(
            tipo = keyType.domainType!!,
            chave = this.key,
            tipoConta = when (this.bankAccount.accountType) {
                AccountType.CACC -> TipoConta.CONTA_CORRENTE
                AccountType.SVGS -> TipoConta.CONTA_POUPANCA
            },
            conta = ContaAssociada(
                instituicao = Instituicoes.nome(bankAccount.participant),
                nomeDoTitular = owner.name,
                cpfDoTitular = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numeroDaConta = bankAccount.accountNumber
            )
        )
    }

}
