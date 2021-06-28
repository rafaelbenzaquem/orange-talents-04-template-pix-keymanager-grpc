package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.common.TipoChave
import br.com.zup.academy.benzaquem.pix.common.TipoConta
import br.com.zup.academy.benzaquem.pix.external.bacen.BacenClient
import br.com.zup.academy.benzaquem.pix.external.bacen.CreatePixKeyRequest
import br.com.zup.academy.benzaquem.pix.external.bacen.CreatePixKeyResponse
import br.com.zup.academy.benzaquem.pix.external.itau.ContaItauResponse
import br.com.zup.academy.benzaquem.pix.external.itau.ContaItauClient
import br.com.zup.academy.benzaquem.pix.external.itau.InstituicaoResponse
import br.com.zup.academy.benzaquem.pix.external.itau.TitularResponse
import br.com.zup.academy.benzaquem.pix.grpc.NovaChavePixRequest
import br.com.zup.academy.benzaquem.pix.grpc.SalvaNovaChavePixGrpcServiceGrpc.SalvaNovaChavePixGrpcServiceBlockingStub
import br.com.zup.academy.benzaquem.pix.grpc.SalvaNovaChavePixGrpcServiceGrpc.newBlockingStub
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton
import br.com.zup.academy.benzaquem.pix.grpc.TipoChave as TipoChaveGrpc
import br.com.zup.academy.benzaquem.pix.grpc.TipoConta as TipoContaGrpc
import org.junit.jupiter.api.assertThrows as aThrows


@MicronautTest(rollback = true, transactionMode = TransactionMode.SEPARATE_TRANSACTIONS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class NovaChavePixControllerTest {

    @field:Inject
    lateinit var itauClient: ContaItauClient

    @field:Inject
    lateinit var bacenClient: BacenClient

    @field:Inject
    lateinit var grpcPixClient: SalvaNovaChavePixGrpcServiceBlockingStub

    @Test
    @Order(1)
    fun `deve registrar chave pix com sucesso`() {

        val clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "02467781054"

        val contaItau = ContaItauResponse(
            TipoConta.CONTA_CORRENTE,
            InstituicaoResponse("Itau", "60701190"),
            "0001",
            "123456",
            TitularResponse(
                id = clienteId,
                nome = "Rafael Ponte",
                cpf = cpfCliente
            )
        )

        Mockito.`when`(itauClient.consultaContaItau(clienteId, TipoConta.CONTA_CORRENTE))
            .thenReturn(HttpResponse.accepted<ContaItauResponse?>().body(contaItau))
        Mockito.`when`(bacenClient.cadastrarChavePixBacen(CreatePixKeyRequest(contaItau, TipoChave.CPF, cpfCliente)))
            .thenReturn(HttpResponse.accepted<CreatePixKeyResponse?>()
                    .body(CreatePixKeyResponse(contaItau, TipoChave.CPF, cpfCliente)))

                val response = grpcPixClient . salvarNovaChavePix (
                NovaChavePixRequest.newBuilder()
                    .setClienteId(clienteId)
                    .setChave(cpfCliente)
                    .setTipoChave(TipoChaveGrpc.CPF)
                    .setTipoConta(TipoContaGrpc.CONTA_CORRENTE)
                    .build()
                )

        assertNotNull(response)
        assertNotNull(response.pixId)
    }

    @Test
    @Order(2)
    fun `nao deve registrar chave pix duplicada`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "02467781054"


        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.salvarNovaChavePix(
                NovaChavePixRequest.newBuilder()
                    .setClienteId(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(TipoChaveGrpc.CPF)
                    .setTipoConta(TipoContaGrpc.CONTA_CORRENTE)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("'CPF = $cpfCliente' já foi cadastrada", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix com conta nao encontrado`() {

        val idClienteItau = "f3cf80e0-9d2c-4c57-b042-aceb29c3e139"
        val cpfCliente = "01145241220"
        val tipoChave = TipoChaveGrpc.CPF
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE

        Mockito.`when`(itauClient.consultaContaItau(idClienteItau, TipoConta.CONTA_CORRENTE))
            .thenReturn(HttpResponse.notFound())

        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.salvarNovaChavePix(
                NovaChavePixRequest.newBuilder()
                    .setClienteId(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("'$tipoConta de cliente id=$idClienteItau' não foi encontrado", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix com servico externo indisponivel`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "rafael.ponte@zup.com.br"
        val tipoChave = TipoChaveGrpc.EMAIL
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE

        Mockito.`when`(itauClient.consultaContaItau(idClienteItau, TipoConta.CONTA_CORRENTE))
            .thenThrow(HttpClientException("unavailable"))

        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.salvarNovaChavePix(
                NovaChavePixRequest.newBuilder()
                    .setClienteId(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.UNAVAILABLE.code, status.code)
            assertEquals("External Service: unavailable", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix CPF invalida`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "0246as7781054"
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE
        val tipoChave = TipoChaveGrpc.CPF


        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.salvarNovaChavePix(
                NovaChavePixRequest.newBuilder()
                    .setClienteId(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("salvarNovaChavePix.novaChavePix: Não é um formado de CPF válido", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix CELULAR invalida`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "0246as7781054"
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE
        val tipoChave = TipoChaveGrpc.PHONE


        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.salvarNovaChavePix(
                NovaChavePixRequest.newBuilder()
                    .setClienteId(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("salvarNovaChavePix.novaChavePix: Não é um formado de PHONE válido", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix EMAIL invalida`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "0246as7781054"
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE
        val tipoChave = TipoChaveGrpc.EMAIL


        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.salvarNovaChavePix(
                NovaChavePixRequest.newBuilder()
                    .setClienteId(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("salvarNovaChavePix.novaChavePix: Não é um formado de EMAIL válido", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix ALEATORIA invalida`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "d67efef4-7901-44fb-84e2-a2cefb157890"
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE
        val tipoChave = TipoChaveGrpc.RANDOM

        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.salvarNovaChavePix(
                NovaChavePixRequest.newBuilder()
                    .setClienteId(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("salvarNovaChavePix.novaChavePix: Não é um formado de RANDOM válido", status.description)
        }
    }

    //    @Factory
//    class ClientsA {
    @Singleton
    @Replaces
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): SalvaNovaChavePixGrpcServiceBlockingStub? {
        return newBlockingStub(channel)
    }
//    }

    @MockBean(ContaItauClient::class)
    fun consultaMockItau(): ContaItauClient {
        return Mockito.mock(ContaItauClient::class.java)
    }

    @MockBean(BacenClient::class)
    fun consultaMockBacen(): BacenClient {
        return Mockito.mock(BacenClient::class.java)
    }

}