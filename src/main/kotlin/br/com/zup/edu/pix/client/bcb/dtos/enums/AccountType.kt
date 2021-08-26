package br.com.zup.edu.pix.client.bcb.dtos.enums

import br.com.zup.edu.TipoConta

enum class AccountType() {

    CACC,
    SVGS;

    companion object {
        fun by(domainType: TipoConta): AccountType {
            return when (domainType) {
                TipoConta.CONTA_CORRENTE -> CACC
                TipoConta.CONTA_POUPANCA -> SVGS
                TipoConta.NAO_ESPECIFICADO -> TODO()
                TipoConta.UNRECOGNIZED -> TODO()
            }
        }
    }
}