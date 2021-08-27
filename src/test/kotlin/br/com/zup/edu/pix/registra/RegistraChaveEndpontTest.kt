package br.com.zup.edu.pix.registra

import br.com.zup.edu.RegistraChaveServiceGrpc
import br.com.zup.edu.RegistroChaveRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.chave.ContaAssociada
import br.com.zup.edu.pix.client.itau.ContasNoItauClient
import br.com.zup.edu.pix.client.DadosDaContaResponse
import br.com.zup.edu.pix.client.InstituicaoResponse
import br.com.zup.edu.pix.client.TitularResponse
import br.com.zup.edu.pix.client.bcb.BancoCentralClient
import br.com.zup.edu.pix.client.bcb.dtos.BankAccount
import br.com.zup.edu.pix.client.bcb.dtos.CreatePixKeyRequest
import br.com.zup.edu.pix.client.bcb.dtos.CreatePixKeyResponse
import br.com.zup.edu.pix.client.bcb.dtos.Owner
import br.com.zup.edu.pix.client.bcb.dtos.enums.AccountType
import br.com.zup.edu.pix.client.bcb.dtos.enums.OwnerType
import br.com.zup.edu.pix.client.bcb.dtos.enums.PixKeyType
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime

import java.util.*

@MicronautTest(transactional = false)
class RegistraChaveEndpontTest(
    val repository: ChavePixRepository,
    val grpcClient: RegistraChaveServiceGrpc.RegistraChaveServiceBlockingStub
) {
    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RegistraChaveServiceGrpc.RegistraChaveServiceBlockingStub? {
            return RegistraChaveServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ContasNoItauClient::class)
    fun itauClien(): ContasNoItauClient? {
        return Mockito.mock(ContasNoItauClient::class.java)
    }

    @MockBean(BancoCentralClient::class)
    fun bcbClient(): BancoCentralClient? {
        return Mockito.mock(BancoCentralClient::class.java)
    }

    @Inject
    lateinit var itauClient: ContasNoItauClient

    @Inject
    lateinit var bcbClient: BancoCentralClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }


    //happy-path
    @Test
    fun `deve registrar nova chave pix`() {
        //Cenário
        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        `when`(bcbClient.registraChave(createPixKeyRequest()))
            .thenReturn(HttpResponse.created(createPixKeyResponse()))
        //ação
        val response = grpcClient.registra(request.build())

        //Validação
        with(response) {
            assertEquals(CLIENTE_ID.toString(), clienteId)
            assertNotNull(pixId)
        }
    }

//refatorar
    @Test
    fun ` nao deve registrar nova chave pix quando não for possivel registrar no BCB`() {

        //Cenário
        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        `when`(bcbClient.registraChave(createPixKeyRequest()))
            .thenReturn(HttpResponse.badRequest())
        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registra(request.build())
        }

        //Validação
        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
          assertEquals("Falha ao registrar chave pix no Banco Central do Brasil", status.description.toString())
        }
    }



@Test
fun `nao deve registrar chave pix quando nao encontrar dados da conta cliente`() {
    //cenário
    `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
        .thenReturn(HttpResponse.notFound())
    //ação
    val thrown = assertThrows<StatusRuntimeException> {
        grpcClient.registra(request.build())
    }
    //Validação
    with(thrown) {
        assertEquals(Status.FAILED_PRECONDITION.code, status.code)
        assertEquals("Cliente não encontrado no Itau", status.description)
    }
}


@Test
fun `nao deve registrar chave pix com CPF invalido`() {
    //cenário
    val request = request.setTipoChave(TipoChave.CPF)
        .setValorChave("087665")
        .build()
    //ação
    val thrown = assertThrows<StatusRuntimeException> {
        grpcClient.registra(request)
    }
    //validação
    with(thrown) {
        assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        assertEquals("Chave deve ser obrigatória e usar um formato de CPF válido", status.description)
    }
}

@Test
fun `nao deve registrar chave pix com CELULAR invalido`() {
    //cenário
    val request = request
        .setTipoChave(TipoChave.CELULAR)
        .setValorChave("087665")
        .build()
    //ação
    val thrown = assertThrows<StatusRuntimeException> {
        grpcClient.registra(request)
    }
    //validação
    with(thrown) {
        assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        assertEquals(
            "Chave deve ser obrigatória e usar formato válido, por exemplo: +558598871407",
            status.description
        )
    }
}

@Test
fun `nao deve registrar chave pix com EMAIL invalido`() {
    //cenário
    val request = request
        .setTipoChave(TipoChave.EMAIL)
        .setValorChave("teste")
        .build()
    //ação
    val thrown = assertThrows<StatusRuntimeException> {
        grpcClient.registra(request)
    }
    //validação
    with(thrown) {
        assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        assertEquals("Chave deve ser obrigatória com um formato de e-mail válido", status.description)
    }
}

@Test
fun `nao deve registrar chave pix com chave ALEATORIA informada`() {
    //cenário
    val request = request
        .setTipoChave(TipoChave.ALEATORIA)
        .setValorChave("bdsdbds325gcns")
        .build()
    //ação
    val thrown = assertThrows<StatusRuntimeException> {
        grpcClient.registra(request)
    }

    //validação
    with(thrown) {
        assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        assertEquals(
            "O valor da chave não deve ser preenchido pois o mesmo será gerado pelo sistema",
            status.description
        )
    }
}


@Test
fun `nao deve registrar chave pix quando parametros forem invalidos`() {
    //cenário
    val chaveRequest = RegistroChaveRequest.newBuilder()
        .setTipoChave(TipoChave.EMAIL).build()
    //ação
    val thrown = assertThrows<StatusRuntimeException> {
        grpcClient.registra(chaveRequest)
    }
    //validação
    with(thrown) {
        assertEquals(Status.INVALID_ARGUMENT.code, status.code)

    }
}


private fun dadosDaContaResponse(): DadosDaContaResponse? {
    return DadosDaContaResponse(
        tipo = "CONTA_CORRENTE",
        instituicao = InstituicaoResponse("UNIBANCO ITAU SA", ContaAssociada.ITAU_UNIBANCO_ISPB),
        agencia = "0001",
        numero = "291900",
        titular = TitularResponse("Rafael Ponte", "63657520325")
    )
}

private fun createPixKeyRequest(): CreatePixKeyRequest {
    return CreatePixKeyRequest(
        keyType = PixKeyType.EMAIL,
        key = "rponte@gmail.com",
        bankAccount = bankAccount(),
        owner = owner()
    )
}

private fun bankAccount(): BankAccount {
    return BankAccount(
        participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
        branch = "0001",
        accountNumber = "291900",
        accountType = AccountType.CACC
    )
}

private fun owner(): Owner {
    return Owner(
        type = OwnerType.NATURAL_PERSON,
        name = "Rafael Ponte",
        taxIdNumber = "63657520325"
    )
}

private fun createPixKeyResponse(): CreatePixKeyResponse {
    return CreatePixKeyResponse(
        keyType = PixKeyType.EMAIL,
        key = "rponte@gmail.com",
        bankAccount = bankAccount(),
        owner = owner(),
        createdAt = LocalDateTime.now()
    )
}


val request = RegistroChaveRequest.newBuilder()
    .setClienteId(CLIENTE_ID.toString())
    .setTipoChave(TipoChave.EMAIL)
    .setValorChave("rponte@gmail.com")
    .setTipoConta(TipoConta.CONTA_CORRENTE)

}
