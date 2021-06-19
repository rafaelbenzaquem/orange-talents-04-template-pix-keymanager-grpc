package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.common.TipoConta
import br.com.zup.academy.benzaquem.pix.external.ConsultaResponse
import br.com.zup.academy.benzaquem.pix.external.ContaClient
import br.com.zup.academy.benzaquem.pix.external.TitularResponse
import br.com.zup.academy.benzaquem.pix.grpc.PixKeymanagerGrpcServiceGrpc.*
import br.com.zup.academy.benzaquem.pix.grpc.RegistroRequest
import br.com.zup.academy.benzaquem.pix.grpc.TipoChave as TipoChaveGrpc
import br.com.zup.academy.benzaquem.pix.grpc.TipoConta as TipoContaGrpc
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import  org.junit.jupiter.api.Assertions.*
import  org.junit.jupiter.api.assertThrows as aThrows
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest(rollback = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ChaveRegistroControllerTest(
    private val grpcPixClient: PixKeymanagerGrpcServiceBlockingStub
) {

    @field:Inject
    lateinit var contaClient: ContaClient

    @Test
    @Order(1)
    fun `deve registrar chave pix com sucesso`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "02467781054"

        Mockito.`when`(contaClient.consultaContaItau(idClienteItau, TipoConta.CONTA_CORRENTE))
            .thenReturn(
                HttpResponse.accepted<ConsultaResponse?>().body(
                    ConsultaResponse(
                        TipoConta.CONTA_CORRENTE,
                        null,
                        null,
                        null,
                        TitularResponse(
                            id = idClienteItau,
                            null,
                            cpf = cpfCliente
                        )
                    )
                )
            )

        val response = grpcPixClient.registrarChave(
            RegistroRequest.newBuilder()
                .setIdCliente(idClienteItau)
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
            grpcPixClient.registrarChave(
                RegistroRequest.newBuilder()
                    .setIdCliente(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(TipoChaveGrpc.CPF)
                    .setTipoConta(TipoContaGrpc.CONTA_CORRENTE)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave pix já cadastrada", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix com conta nao encontrado`() {

        val idClienteItau = "f3cf80e0-9d2c-4c57-b042-aceb29c3e139"
        val cpfCliente = "01145241220"
        val tipoChave = TipoChaveGrpc.CPF
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE

        Mockito.`when`(contaClient.consultaContaItau(idClienteItau, TipoConta.CONTA_CORRENTE))
            .thenReturn(HttpResponse.notFound())

        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.registrarChave(
                RegistroRequest.newBuilder()
                    .setIdCliente(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("'$tipoConta de cliente id=$idClienteItau' não foi encontrado", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix com servico externo indisponivel`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "rafael.ponte@zup.com.br"
        val tipoChave = TipoChaveGrpc.EMAIL
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE

        Mockito.`when`(contaClient.consultaContaItau(idClienteItau, TipoConta.CONTA_CORRENTE))
            .thenThrow(HttpClientException::class.java)

        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.registrarChave(
                RegistroRequest.newBuilder()
                    .setIdCliente(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.UNAVAILABLE.code, status.code)
            assertEquals("Serviço externo indisponível", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix CPF invalida`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "0246as7781054"
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE
        val tipoChave = TipoChaveGrpc.CPF


        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.registrarChave(
                RegistroRequest.newBuilder()
                    .setIdCliente(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("novaChavePixValida.novaChavePix: Não é um formado de CPF válido", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix CELULAR invalida`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "0246as7781054"
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE
        val tipoChave = TipoChaveGrpc.CELULAR


        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.registrarChave(
                RegistroRequest.newBuilder()
                    .setIdCliente(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("novaChavePixValida.novaChavePix: Não é um formado de CELULAR válido", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix EMAIL invalida`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "0246as7781054"
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE
        val tipoChave = TipoChaveGrpc.EMAIL


        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.registrarChave(
                RegistroRequest.newBuilder()
                    .setIdCliente(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("novaChavePixValida.novaChavePix: Não é um formado de EMAIL válido", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix ALEATORIA invalida`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "d67efef4-7901-44fb-84e2-a2cefb157890"
        val tipoConta = TipoContaGrpc.CONTA_CORRENTE
        val tipoChave = TipoChaveGrpc.RANDOM

        val error = aThrows<StatusRuntimeException> {
            grpcPixClient.registrarChave(
                RegistroRequest.newBuilder()
                    .setIdCliente(idClienteItau)
                    .setChave(cpfCliente)
                    .setTipoChave(tipoChave)
                    .setTipoConta(tipoConta)
                    .build()
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("novaChavePixValida.novaChavePix: Não é um formado de RANDOM válido", status.description)
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixKeymanagerGrpcServiceBlockingStub? {
            return newBlockingStub(channel)
        }
    }

    @MockBean(ContaClient::class)
    fun consultaMock(): ContaClient {
        return Mockito.mock(ContaClient::class.java)
    }

}