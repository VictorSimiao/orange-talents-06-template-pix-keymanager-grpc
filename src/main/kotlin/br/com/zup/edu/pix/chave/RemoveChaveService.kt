package br.com.zup.edu.pix.chave

import br.com.zup.edu.pix.client.bcb.BancoCentralClient
import br.com.zup.edu.pix.client.bcb.dtos.DeletePixKeyRequest
import br.com.zup.edu.pix.exceptions.ChavePixNaoEncontradaException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemoveChaveService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val bcbClient: BancoCentralClient
) {
    @Transactional
    fun remove(@Valid chave: ChaveDtoRemove){

        val uuidPixId = UUID.fromString(chave.pixId)
        val uuidClienteId = UUID.fromString(chave.clienteId)

       val possivelChave = chavePixRepository.findByIdAndClienteId(uuidPixId,uuidClienteId)
        if(possivelChave.isEmpty){
            throw ChavePixNaoEncontradaException("Chave Pix não encontrada ou não pertence ao cliente")
        }

       val deletePixKeyRequest = DeletePixKeyRequest(possivelChave.get().chave)

        val deletePixKeyResponse = bcbClient.deletaChave(key =possivelChave.get().chave, request = deletePixKeyRequest )

        if(deletePixKeyResponse.status != HttpStatus.OK ){
            throw IllegalStateException("Erro ao remover chave Pix no Banco Central do Brasil")
        }

        chavePixRepository.deleteById(possivelChave.get().id)

    }
}