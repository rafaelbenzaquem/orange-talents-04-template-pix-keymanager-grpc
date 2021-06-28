package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.common.ErrorHandler
import br.com.zup.academy.benzaquem.pix.grpc.ChavePixDeletadaRequest
import br.com.zup.academy.benzaquem.pix.grpc.ChavePixDeletadaResponse
import br.com.zup.academy.benzaquem.pix.grpc.DeletaChavePixGrpcServiceGrpc
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@ErrorHandler
@Singleton
class DeletaChavePixController(
    private val deletaChavePixService:DeletaChavePixService
) : DeletaChavePixGrpcServiceGrpc.DeletaChavePixGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun deletarChavePix(
        request: ChavePixDeletadaRequest,
        responseObserver: StreamObserver<ChavePixDeletadaResponse>
    ) {
        logger.info("iniciando delete -> $request")

            val chaveDeletada = deletaChavePixService.deletarChavePix(request.pixId,request.clienteId)
            responseObserver.onNext(
                ChavePixDeletadaResponse
                    .newBuilder()
                    .setChave(chaveDeletada.chave)
                    .build()
            )

        responseObserver.onCompleted()
    }


}