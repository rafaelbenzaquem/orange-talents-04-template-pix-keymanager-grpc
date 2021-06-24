package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.common.TipoChave
import br.com.zup.academy.benzaquem.pix.common.TipoConta
import br.com.zup.academy.benzaquem.pix.grpc.ChavePixDeletadaRequest
import br.com.zup.academy.benzaquem.pix.grpc.DeletaChavePixGrpcServiceGrpc.DeletaChavePixGrpcServiceBlockingStub
import br.com.zup.academy.benzaquem.pix.grpc.DeletaChavePixGrpcServiceGrpc.newBlockingStub
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest(rollback = true, transactionMode = TransactionMode.SEPARATE_TRANSACTIONS)
class DeleteChavePixServiceTest {

    @field:Inject
    lateinit var grpcPixClient: DeletaChavePixGrpcServiceBlockingStub

    @field:Inject
    lateinit var chavePixRepository: ChavePixRepository

    @BeforeEach
    fun setup() {
        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "02467781054"

        chavePixRepository.save(
            ChavePix(
                id = UUID.randomUUID().toString(),
                idCliente = idClienteItau,
                chave = cpfCliente,
                tipoChave = TipoChave.CPF,
                tipoConta = TipoConta.CONTA_CORRENTE
            )
        )
    }

    @Test
    fun `deve deletar chave pix com sucesso`() {

        val idClienteItau = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "02467781054"

        val response = grpcPixClient.deletarChavePix(
            ChavePixDeletadaRequest.newBuilder()
                .setIdCliente(idClienteItau)
                .setChave(cpfCliente)
                .build()
        )

        assertNotNull(response)
        assertNotNull(response.chave)
    }

    @Test
    fun `tentar deletar chave pix com requisicao invalida retorna INVALID_ARGUMENT`() {

        val idClienteItau = ""
        val cpfCliente = "02467781054"

        val error = assertThrows<StatusRuntimeException> {
            grpcPixClient.deletarChavePix(
                ChavePixDeletadaRequest.newBuilder()
                    .setIdCliente(idClienteItau)
                    .setChave(cpfCliente)
                    .build()
            )
        }

        with(error) {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            Assertions.assertEquals("Requisição inválida", status.description)
        }
    }

    @Test
    fun `tentar deletar chave pix com chave inexistente retorna NOT_FOUND`() {

        val idClienteItau = "d56dfef4-7901-44fb-84e2-a2cefb157890"
        val cpfCliente = "12467781054"

        val error = assertThrows<StatusRuntimeException> {
            grpcPixClient.deletarChavePix(
                ChavePixDeletadaRequest.newBuilder()
                    .setIdCliente(idClienteItau)
                    .setChave(cpfCliente)
                    .build()
            )
        }

        with(error) {
            Assertions.assertEquals(Status.NOT_FOUND.code, status.code)
            Assertions.assertEquals(
                "Chave '$cpfCliente' não foi encontrada para o cliente de 'id=$idClienteItau'",
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

}