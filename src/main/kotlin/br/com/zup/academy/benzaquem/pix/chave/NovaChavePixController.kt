package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.common.ErrorHandler
import br.com.zup.academy.benzaquem.pix.grpc.ChavePixSalvaResponse
import br.com.zup.academy.benzaquem.pix.grpc.NovaChavePixRequest
import br.com.zup.academy.benzaquem.pix.grpc.SalvaNovaChavePixGrpcServiceGrpc
import br.com.zup.academy.benzaquem.pix.grpc.toModel
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@ErrorHandler
@Singleton
class NovaChavePixController(
    private val novaChavePixService: NovaChavePixService
) : SalvaNovaChavePixGrpcServiceGrpc.SalvaNovaChavePixGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun salvarNovaChavePix(
        request: NovaChavePixRequest,
        responseObserver: StreamObserver<ChavePixSalvaResponse>
    ) {
        logger.info("iniciando registro $request")
        var novaChavePix = novaChavePixService.salvarNovaChavePix(request.toModel())

        val responseRegistrarChave = ChavePixSalvaResponse.newBuilder().setPixId(novaChavePix.id).build()

        responseObserver.onNext(responseRegistrarChave)
        responseObserver.onCompleted()
    }

}