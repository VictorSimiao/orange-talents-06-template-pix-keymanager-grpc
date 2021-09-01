package br.com.zup.edu.pix.carrega

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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*

@MicronautTest(transactional = false)
class CarregaChaveEndpontTest(
    val repository: ChavePixRepository,
    val grpcClient: CarregaChaveServiceGrpc.CarregaChaveServiceBlockingStub
) {
    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarregaChaveServiceGrpc.CarregaChaveServiceBlockingStub? {
            return CarregaChaveServiceGrpc.newBlockingStub(channel)
        }
    }
    //TODO:Implementar os testes de mock

    @MockBean(BancoCentralClient::class)
    fun bcbClient(): BancoCentralClient? {
        return Mockito.mock(BancoCentralClient::class.java)
    }

    @Inject
    lateinit var bcbClient: BancoCentralClient

    val chavePixTest = ChavePix(
        clienteId = UUID.randomUUID(),
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

    @Test
    fun `deve carregar chave por pixId e clienteId`() {
        // cenário
        repository.save(chavePixTest)
        val chaveExistente = repository.findByChave("victor@gmail.com").get()

        // ação
        val response = grpcClient.carrega(
            CarregaChavePixRequest.newBuilder()
                .setPixId(
                    CarregaChavePixRequest.FiltroPorPixId.newBuilder()
                        .setPixId(chaveExistente.id.toString())
                        .setClienteId(chaveExistente.clienteId.toString())
                        .build()
                ).build()
        )
        // validação
        with(response) {
            assertEquals(chaveExistente.id.toString(), this.pixId)
            assertEquals(chaveExistente.clienteId.toString(), this.clienteId)
            assertEquals(chaveExistente.tipo.name, this.chave.tipo.name)
            assertEquals(chaveExistente.chave, this.chave.chave)
        }
    }

    @Test
    fun `deve carregar a ChavePix pelo valor da chave localmente`() {
        //cenário
        repository.save(chavePixTest)
        val chaveExistente = repository.findByChave("victor@gmail.com").get()

        //ação
        val response = grpcClient.carrega(
            CarregaChavePixRequest.newBuilder()
                .setChave("victor@gmail.com")
                .build()
        )
        // validação
        with(response) {
            assertEquals(chaveExistente.tipo.name, this.chave.tipo.name)
            assertEquals(chaveExistente.chave, this.chave.chave)
        }

    }

    @Test
    fun `nao deve carregar chave por pixId e clienteId quando dados de entrada invalidos`() {
        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest.newBuilder()
                .setPixId(
                    CarregaChavePixRequest.FiltroPorPixId.newBuilder()
                    .build()
                ).build())
        }

        // validação
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)

        }
    }

    @Test
    fun `nao deve carregar chave por pixId e clienteId quando registro nao existir`() {
        // ação
        val pixIdNaoExistente = UUID.randomUUID().toString()
        val clienteIdNaoExistente = UUID.randomUUID().toString()

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest.newBuilder()
                .setPixId(
                    CarregaChavePixRequest.FiltroPorPixId.newBuilder()
                    .setPixId(pixIdNaoExistente)
                    .setClienteId(clienteIdNaoExistente)
                    .build()
                ).build())
        }

        // validação
        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada", status.description)
        }
    }

    @Test
    fun `nao deve carregar chave quando filtro invalido`() {

        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest.newBuilder().build())
        }

        // validação
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave Pix inválida ou não informada", status.description)
        }
    }
}


