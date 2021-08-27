package br.com.zup.edu.pix.endponts.remove


import br.com.zup.edu.RemoveChaveRequest
import br.com.zup.edu.RemoveChaveResponse
import br.com.zup.edu.RemoveChaveServiceGrpc
import br.com.zup.edu.pix.chave.RemoveChaveService
import br.com.zup.edu.pix.exceptions.ChavePixNaoEncontradaException
import br.com.zup.edu.pix.exceptions.PreCondicaoException
import br.com.zup.edu.pix.extension.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class RemoveChaveEndpont(@Inject private val service: RemoveChaveService) :
    RemoveChaveServiceGrpc.RemoveChaveServiceImplBase() {

    override fun remove(request: RemoveChaveRequest, responseObserver: StreamObserver<RemoveChaveResponse>) {
        try {

            val dtoChaveRemove = request.toModel()
            service.remove(dtoChaveRemove)

            responseObserver.onNext(
                RemoveChaveResponse.newBuilder()
                    .setClienteId(dtoChaveRemove.clienteId.toString())
                    .setPixId(dtoChaveRemove.pixId.toString())
                    .build()
            )
            responseObserver.onCompleted()
        } catch (ex: ChavePixNaoEncontradaException) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(ex.message)
                    .withCause(ex.cause)
                    .asRuntimeException()
            )
        }catch (ex: ConstraintViolationException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(ex.message)
                    .withCause(ex.cause)
                    .asRuntimeException()
            )
        }catch (ex: IllegalArgumentException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(ex.message)
                    .withCause(ex.cause)
                    .asRuntimeException()
            )
        }catch (ex: PreCondicaoException) {
            responseObserver.onError(
                Status.FAILED_PRECONDITION
                    .withDescription(ex.message)
                    .withCause(ex.cause)
                    .asRuntimeException()
            )
        }
    }
}