package br.com.zup.edu.pix.client.bcb.dtos
import br.com.zup.edu.pix.chave.ChavePix
import br.com.zup.edu.pix.chave.ContaAssociada
import br.com.zup.edu.pix.client.bcb.dtos.enums.AccountType
import br.com.zup.edu.pix.client.bcb.dtos.enums.OwnerType
import br.com.zup.edu.pix.client.bcb.dtos.enums.PixKeyType

data class CreatePixKeyRequest(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner
)

