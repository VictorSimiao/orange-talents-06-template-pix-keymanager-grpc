package br.com.zup.edu.pix.chave

import br.com.zup.edu.pix.chave.ChavePix
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.chave.NovaChavePix
import br.com.zup.edu.pix.client.ContasNoItauClient
import br.com.zup.edu.pix.exceptions.ChavePixExistenteException
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.lang.IllegalStateException
import javax.transaction.Transactional

import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val itauClient: ContasNoItauClient
) {

    @Transactional
    fun registra(@Valid novaChavePix: NovaChavePix): ChavePix {

        if (chavePixRepository.existsByChave(novaChavePix.chave)) {
            throw ChavePixExistenteException("Chave Pix'${novaChavePix.chave}' existe")
        }

        val response = itauClient.buscaContaPorTipo(novaChavePix.clienteId, novaChavePix.tipoConta!!.name)
        val contaAssociada = response.body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado")

        val chavePix = novaChavePix.toModel(contaAssociada)
        chavePixRepository.save(chavePix)

        return chavePix
    }

}