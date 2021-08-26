package br.com.zup.edu.pix.remove

import br.com.zup.edu.RemoveChaveRequest
import br.com.zup.edu.RemoveChaveServiceGrpc
import br.com.zup.edu.TipoChave
import br.com.zup.edu.pix.chave.ChavePix
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.chave.ContaAssociada
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
class RemoveChaveEndpontTest(
    val repository: ChavePixRepository,
    val grpcClient: RemoveChaveServiceGrpc.RemoveChaveServiceBlockingStub
) {
    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RemoveChaveServiceGrpc.RemoveChaveServiceBlockingStub? {
            return RemoveChaveServiceGrpc.newBlockingStub(channel)
        }
    }

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
        tipoConta = br.com.zup.edu.TipoConta.CONTA_CORRENTE,
    )

    @Test
    fun `deve remover a chave pix registrada`() {

        //cenário
        val chaveSalva = repository.save(chavePixTest)

        //ação
        val respose = grpcClient.remove(
            RemoveChaveRequest.newBuilder()
                .setClienteId(chaveSalva.clienteId.toString())
                .setPixId(chaveSalva.id.toString())
                .build()
        )
        //validação
        assertEquals(chaveSalva.clienteId.toString(), respose.clienteId)
        assertEquals(chaveSalva.id.toString(), respose.pixId)
        assertTrue(repository.findById(chavePixTest.id).isEmpty)
    }

    @Test
    fun `nao deve remover se o registro da chave Pix nao for encontrado`() {

        //cenário
        val chaveSalva = repository.save(chavePixTest)

        //ação
        val thrown = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveChaveRequest.newBuilder()
                    .setClienteId(chaveSalva.clienteId.toString())
                    .setPixId(UUID.randomUUID().toString())
                    .build())
        }
        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada ou não pertence ao cliente", status.description)
            assertTrue(repository.findAll().size==1)
        }
    }

    @Test
    fun `nao deve remover chave Pix se o cliente nao for encontrado`() {

        //cenário
        val chaveSalva = repository.save(chavePixTest)

        //ação
        val thrown = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveChaveRequest.newBuilder()
                    .setClienteId(UUID.randomUUID().toString())
                    .setPixId(chaveSalva.id.toString())
                    .build())
        }

        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada ou não pertence ao cliente", status.description)
            assertTrue(repository.findAll().size==1)
        }

    }
    @Test
    fun `deve retornar um erro caso request nao tenha inforamacoes `() {

        //ação
        val thrown = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveChaveRequest.newBuilder().build())
        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }

    }

    @Test
    fun `deve retornar um erro caso request seja invalido`() {

        //ação
        val thrown = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                RemoveChaveRequest.newBuilder()
                    .setClienteId("1234")
                    .setPixId("werrvvd")
                    .build())
        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }
}