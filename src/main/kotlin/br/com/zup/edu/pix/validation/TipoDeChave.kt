package br.com.zup.edu.pix.validation

import br.com.zup.edu.pix.exceptions.ArgumentoDeEntradaException
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoDeChave {
    CPF {
        override fun validaChave(chave: String?) {
            if (chave.isNullOrBlank() || !CPFValidator().run {
                initialize(null)
                    isValid(chave,null)
                }) {
                throw ArgumentoDeEntradaException("Chave deve ser obrigatória e usar um formato de CPF válido")
            }

        }
    },
    CELULAR {
        override fun validaChave(chave: String?) {
            if (chave.isNullOrBlank() || !chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())) {
                throw ArgumentoDeEntradaException("Chave deve ser obrigatória e usar formato válido, por exemplo: +558598871407")
            }
        }
    },
    EMAIL {
        override fun validaChave(chave: String?) {
            if (chave.isNullOrBlank() || !EmailValidator().run() {
                initialize(null)
                    isValid(chave, null)
            }) {
                throw ArgumentoDeEntradaException("Chave deve ser obrigatória com um formato de e-mail válido")
            }
        }
    },
    ALEATORIA {
        override fun validaChave(chave: String?) {
            if (!chave.isNullOrBlank()) {
                throw ArgumentoDeEntradaException("O valor da chave não deve ser preenchido pois o mesmo será gerado pelo sistema")
            }
        }
    };

    abstract fun validaChave(chave: String?)
}