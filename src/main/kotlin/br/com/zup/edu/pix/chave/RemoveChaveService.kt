package br.com.zup.edu.pix.chave

import br.com.zup.edu.pix.exceptions.ChavePixNaoEncontradaException
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemoveChaveService(
    @Inject val chavePixRepository: ChavePixRepository
) {
    @Transactional
    fun remove(@Valid chave: ChaveDtoRemove){

        val uuidPixId = UUID.fromString(chave.pixId)
        val uuidClienteId = UUID.fromString(chave.clienteId)

       val possivelChave = chavePixRepository.findByIdAndClienteId(uuidPixId,uuidClienteId)
        if(possivelChave.isEmpty){
            throw ChavePixNaoEncontradaException("Chave Pix não encontrada ou não pertence ao cliente")
        }
        chavePixRepository.deleteById(uuidPixId)
    }
}