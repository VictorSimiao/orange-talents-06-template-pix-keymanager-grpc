package br.com.zup.edu.pix.endponts.carrega

import br.com.zup.edu.CarregaChavePixRequest
import br.com.zup.edu.CarregaChavePixResponse
import br.com.zup.edu.CarregaChaveServiceGrpc
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.client.bcb.BancoCentralClient
import br.com.zup.edu.pix.exceptions.ChavePixNaoEncontradaException
import br.com.zup.edu.pix.exceptions.ClienteNaoEncontradoException
import br.com.zup.edu.pix.extension.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@Singleton
class CarregaChaveEndpont(
    @Inject private val chaveRepository: ChavePixRepository,
    @Inject private val bcbClient: BancoCentralClient,
    @Inject private val validator: Validator
) : CarregaChaveServiceGrpc.CarregaChaveServiceImplBase() {

    override fun carrega(request: CarregaChavePixRequest, responseObserver: StreamObserver<CarregaChavePixResponse>) {
        try {
            val filtro = request.toModel(validator)
            val chaveInfo = filtro.filtra(repository = chaveRepository, bcbClient = bcbClient)

            responseObserver.onNext(CarregaChavePixResponseConverter().convert(chaveInfo))
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
                Status.FAILED_PRECONDITION
                    .withDescription(ex.message)
                    .withCause(ex.cause)
                    .asRuntimeException()
            )
        }

    }
}