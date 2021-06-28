package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.common.TipoChave
import br.com.zup.academy.benzaquem.pix.common.TipoConta
import br.com.zup.academy.benzaquem.pix.external.bacen.BacenClient
import br.com.zup.academy.benzaquem.pix.external.bacen.DeletePixKeyRequest
import br.com.zup.academy.benzaquem.pix.grpc.ChavePixDeletadaRequest
import br.com.zup.academy.benzaquem.pix.grpc.DeletaChavePixGrpcServiceGrpc.DeletaChavePixGrpcServiceBlockingStub
import br.com.zup.academy.benzaquem.pix.grpc.DeletaChavePixGrpcServiceGrpc.newBlockingStub
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest(rollback = true, transactionMode = TransactionMode.SEPARATE_TRANSACTIONS)
class DeletaChavePixControllerTest {

    @field:Inject
    lateinit var grpcPixClient: DeletaChavePixGrpcServiceBlockingStub

    @field:Inject
    lateinit var bacenClient: BacenClient

    @field:Inject
    lateinit var chavePixRepository: ChavePixRepository

    @BeforeEach
    fun setup() {
        chavePixRepository.deleteAll()
        val pixId = "03413462-1f4a-46a0-9379-9839275f64e8"
        val clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "02467781054"
        val ispb = "60701190";

        chavePixRepository.save(
            ChavePix(
                id = pixId,
                clienteId = clienteId,
                chave = cpfCliente,
                ispb = ispb,
                tipoChave = TipoChave.CPF,
                tipoConta = TipoConta.CONTA_CORRENTE
            )
        )
    }

    @Test
    fun `deve deletar chave pix com sucesso`() {

        val pixId = "03413462-1f4a-46a0-9379-9839275f64e8"
        val clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "02467781054"
        val ispb = "60701190";

        Mockito.`when`(bacenClient.removerChavePixBacen(DeletePixKeyRequest(cpfCliente, ispb), cpfCliente))
            .thenReturn(HttpResponse.ok())

        val response = grpcPixClient.deletarChavePix(
            ChavePixDeletadaRequest.newBuilder()
                .setClienteId(clienteId)
                .setPixId(pixId)
                .build()
        )

        assertNotNull(response)
        assertNotNull(response.chave)
    }

    @Test
    fun `tentar deletar chave pix com requisicao invalida retorna INVALID_ARGUMENT`() {

        val pixId = ""
        val clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val error = assertThrows<StatusRuntimeException> {
            grpcPixClient.deletarChavePix(
                ChavePixDeletadaRequest.newBuilder()
                    .setClienteId(clienteId)
                    .setPixId(pixId)
                    .build()
            )
        }

        with(error) {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            Assertions.assertEquals("deletarChavePix.pixId: must not be blank", status.description)
        }
    }

    @Test
    fun `tentar deletar chave pix com chave inexistente retorna NOT_FOUND`() {

        val pixId = "fb6db8a1-0182-4b1f-b173-f155b0fd9a59"
        val clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val error = assertThrows<StatusRuntimeException> {
            grpcPixClient.deletarChavePix(
                ChavePixDeletadaRequest.newBuilder()
                    .setClienteId(clienteId)
                    .setPixId(pixId)
                    .build()
            )
        }

        with(error) {
            Assertions.assertEquals(Status.NOT_FOUND.code, status.code)
            Assertions.assertEquals(
                "Chave Pix não encontrada ou não pertence ao cliente",
                status.description
            )
        }
    }

    //    @Factory
//    class Clients {
    @Singleton
    @Replaces
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): DeletaChavePixGrpcServiceBlockingStub? {
        return newBlockingStub(channel)
    }

    //    }
    @MockBean(BacenClient::class)
    fun consultaMockBacen(): BacenClient {
        return Mockito.mock(BacenClient::class.java)
    }

}