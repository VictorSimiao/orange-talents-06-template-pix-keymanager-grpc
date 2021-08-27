package br.com.zup.edu.pix.chave

import br.com.zup.edu.pix.client.bcb.BancoCentralClient
import br.com.zup.edu.pix.client.bcb.dtos.CreatePixKeyRequest
import br.com.zup.edu.pix.client.itau.ContasNoItauClient
import br.com.zup.edu.pix.exceptions.ChavePixExistenteException
import br.com.zup.edu.pix.exceptions.ClienteNaoEncontradoException
import br.com.zup.edu.pix.exceptions.PreCondicaoException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.transaction.Transactional

import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val itauClient: ContasNoItauClient,
    @Inject val bcbClient: BancoCentralClient
) {

    @Transactional
    fun registra(@Valid novaChavePix: NovaChavePix): ChavePix {

        if (chavePixRepository.existsByChave(novaChavePix.chave)) {
            throw ChavePixExistenteException("Chave Pix'${novaChavePix.chave}' existe")
        }

        val response = itauClient.buscaContaPorTipo(novaChavePix.clienteId, novaChavePix.tipoConta!!.name)
        val contaAssociada =
            response.body()?.toModel() ?: throw ClienteNaoEncontradoException("Cliente não encontrado no Itau")

        var chavePix = novaChavePix.toModel(contaAssociada)
        chavePixRepository.save(chavePix)

        val bcbPixKeyResponse = bcbClient.registraChave(chavePix.toPixKeyRequest())
        if (bcbPixKeyResponse.status != HttpStatus.CREATED) {
            throw PreCondicaoException("Falha ao registrar chave pix no Banco Central do Brasil")
        }
        //Caso a chave seja aleatoria ela será atualizada pela chave gerada no BCB
        chavePix.atualiza(bcbPixKeyResponse.body()!!.key)

        return chavePix
    }

}