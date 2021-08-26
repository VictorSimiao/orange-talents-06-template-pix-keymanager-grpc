package br.com.zup.edu.pix.endponts.registra

import br.com.zup.edu.RegistraChaveServiceGrpc
import br.com.zup.edu.RegistroChaveRequest
import br.com.zup.edu.RegistroChaveResponse
import br.com.zup.edu.pix.chave.NovaChavePixService
import br.com.zup.edu.pix.exceptions.ArgumentoDeEntradaException
import br.com.zup.edu.pix.extension.toModel
import br.com.zup.edu.pix.exceptions.ChavePixExistenteException
import br.com.zup.edu.pix.exceptions.ClienteNaoEncontradoException


import io.grpc.Status

import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class RegistraChaveEndpont(@Inject private val service: NovaChavePixService) :
    RegistraChaveServiceGrpc.RegistraChaveServiceImplBase() {


    override fun registra(request: RegistroChaveRequest, responseObserver: StreamObserver<RegistroChaveResponse>) {
        try {

            val dtoNovaChavePix = request.toModel()
            dtoNovaChavePix.tipoChave!!.validaChave(dtoNovaChavePix.chave)
            val novaChave = service.registra(dtoNovaChavePix)

            responseObserver.onNext(
                RegistroChaveResponse.newBuilder()
                    .setClienteId(novaChave.clienteId.toString())
                    .setPixId(novaChave.id.toString())
                    .build()
            )
            responseObserver.onCompleted()

        } catch (ex: ChavePixExistenteException) {
            responseObserver.onError(
                Status.ALREADY_EXISTS
                    .withDescription(ex.message)
                    .withCause(ex.cause)
                    .asRuntimeException()
            )
        } catch (ex: ArgumentoDeEntradaException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(ex.message)
                    .withCause(ex.cause)
                    .asRuntimeException()
            )
        } catch (ex: HttpClientResponseException) {
            responseObserver.onError(
                Status.FAILED_PRECONDITION
                    .withDescription("Id do cliente não foi encontrado")
                    .withCause(ex.cause)
                    .asRuntimeException()
            )
        }
    }
}


