package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.external.ContaClient
import br.com.zup.academy.benzaquem.pix.grpc.*
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class DeleteChavePixService(
    private val contaClient: ContaClient,
    private val chavePixRepository: ChavePixRepository
) : DeletaChavePixGrpcServiceGrpc.DeletaChavePixGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun deletarChavePix(
        request: ChavePixDeletadaRequest,
        responseObserver: StreamObserver<ChavePixDeletadaResponse>
    ) {
        logger.info("iniciando delete -> $request")
        if (request.isInvalida()) {
            logger.warn("Requisição invalida -> $request")
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Requisição inválida")
                    .asRuntimeException()
            )
            return
        }
        val chavePix = chavePixRepository.findByIdClienteAndChave(request.idCliente, request.chave)

        if (chavePix.isPresent) {
            val chaveDeletada = chavePix.get()
            chavePixRepository.delete(chaveDeletada)
            responseObserver.onNext(
                ChavePixDeletadaResponse
                    .newBuilder()
                    .setChave(chaveDeletada.chave)
                    .build()
            )
        } else {
            responseObserver.onError(
                Status.NOT_FOUND.withDescription("Chave '${request.chave}' não foi encontrada para o cliente de 'id=${request.idCliente}'")
                    .asRuntimeException()
            )
            return
        }
        responseObserver.onCompleted()
    }


}