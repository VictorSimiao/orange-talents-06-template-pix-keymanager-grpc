package br.com.zup.edu.pix.endponts.lista

import br.com.zup.edu.ListaChavePixRequest
import br.com.zup.edu.ListaChavePixResponse
import br.com.zup.edu.ListaChaveServiceGrpc
import br.com.zup.edu.pix.chave.ChavePixRepository
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.lang.IllegalArgumentException
import java.time.ZoneId
import java.util.*

@Singleton
class ListaChaveEndpoint(@Inject private val repository: ChavePixRepository)
    :ListaChaveServiceGrpc.ListaChaveServiceImplBase(){

    override fun lista(request: ListaChavePixRequest, responseObserver: StreamObserver<ListaChavePixResponse>) {
        try {
            if (request.clienteId.isNullOrBlank())
                throw IllegalArgumentException("Cliente Id não pode ser nulo ou vazio")

            val clienteId = UUID.fromString(request.clienteId)
            val chaves = repository.findAllByClienteId(clienteId).map {
                ListaChavePixResponse.ChavePix.newBuilder()
                    .setPixId(it.id.toString())
                    .setTipo(br.com.zup.edu.TipoChave.valueOf(it.tipo.name))
                    .setChave(it.chave)
                    .setTipoConta(br.com.zup.edu.TipoConta.valueOf(it.tipoConta.name))
                    .setCriadaEm(it.criadaEm.let {
                        val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                        Timestamp.newBuilder()
                            .setSeconds(createdAt.epochSecond)
                            .setNanos(createdAt.nano)
                            .build()
                    })
                    .build()
            }
            responseObserver.onNext(
                ListaChavePixResponse.newBuilder()
                    .setClienteId(clienteId.toString())
                    .addAllChaves(chaves)
                    .build()
            )
            responseObserver.onCompleted()
        }catch (ex: IllegalArgumentException){
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(ex.message)
                    .withCause(ex.cause)
                    .asRuntimeException())
        }
    }

}