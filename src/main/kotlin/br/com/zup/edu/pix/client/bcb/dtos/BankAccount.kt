package br.com.zup.edu.pix.client.bcb.dtos

import br.com.zup.edu.pix.client.bcb.dtos.enums.AccountType

data class BankAccount(

    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
)