package br.com.zup.edu.pix.client.bcb.dtos.enums

import br.com.zup.edu.TipoChave

enum class PixKeyType(val domainType: TipoChave?) {

    CPF(TipoChave.CPF),
    CNPJ(null),
    PHONE(TipoChave.CELULAR),
    EMAIL(TipoChave.EMAIL),
    RANDOM(TipoChave.ALEATORIA);

    companion object {

        private val mapping = PixKeyType.values().associateBy(PixKeyType::domainType)

        fun by(domainType: TipoChave): PixKeyType {
            return mapping[domainType]
                ?: throw IllegalArgumentException("PixKeyType invalid or not found for $domainType")
        }
    }
}