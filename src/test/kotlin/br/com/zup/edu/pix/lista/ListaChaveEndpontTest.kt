package br.com.zup.edu.pix.lista

import br.com.zup.edu.*
import br.com.zup.edu.pix.chave.ChavePix
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.chave.ContaAssociada
import br.com.zup.edu.pix.client.bcb.BancoCentralClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*

@MicronautTest(transactional = false)
class ListaChaveEndpontTest(
    val repository: ChavePixRepository,
    val grpcClient: ListaChaveServiceGrpc.ListaChaveServiceBlockingStub
) {


    @BeforeEach
    fun setup() {
        repository.save(chavePixTest)
        repository.save(chavePixTest2)
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ListaChaveServiceGrpc.ListaChaveServiceBlockingStub? {
            return ListaChaveServiceGrpc.newBlockingStub(channel)
        }
    }


    @Test
    fun `deve listar todas chaves registra do cliente`() {

        val response = grpcClient.lista(
            ListaChavePixRequest.newBuilder()
                .setClienteId(chavePixTest.clienteId.toString())
                .build()
        )

        with(response) {
            assertEquals(response.chavesList.size, 2)
        }

    }

    @Test
    fun `deve retornar uma lista vazia quando clienteId nao for localizado`() {

        val response = grpcClient.lista(
            ListaChavePixRequest.newBuilder()
                .setClienteId("108ff357-be79-45d1-b8e8-0f44d8fcf32d")
                .build()
        )

        with(response) {
            assertEquals(response.chavesList.size, 0)
        }
    }

    @Test
    fun `nao deve informar o erro de clienteId nao informado`() {
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.lista(ListaChavePixRequest.newBuilder().build())
        }
        with(response){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Cliente Id não pode ser nulo ou vazio",status.description)
        }
    }


    val chavePixTest = ChavePix(
        clienteId = UUID.fromString("ddc96b41-72aa-42e7-af2b-1923a11bfb3e"),
        tipo = TipoChave.EMAIL,
        chave = "victor@gmail.com",
        contaAssociada = ContaAssociada(
            instituicao = "UNIBANCO ITAU SA",
            nomeDoTitular = "Victor Simiao",
            cpfDoTitular = "83364378860",
            agencia = "0001",
            numeroDaConta = "098765"
        ),
        tipoConta = TipoConta.CONTA_CORRENTE,
    )
    val chavePixTest2 = ChavePix(
        clienteId = UUID.fromString("ddc96b41-72aa-42e7-af2b-1923a11bfb3e"),
        tipo = TipoChave.CELULAR,
        chave = "+5527998887667",
        contaAssociada = ContaAssociada(
            instituicao = "UNIBANCO ITAU SA",
            nomeDoTitular = "Victor Simiao",
            cpfDoTitular = "83364378860",
            agencia = "0001",
            numeroDaConta = "098765"
        ),
        tipoConta = TipoConta.CONTA_CORRENTE,
    )

}


