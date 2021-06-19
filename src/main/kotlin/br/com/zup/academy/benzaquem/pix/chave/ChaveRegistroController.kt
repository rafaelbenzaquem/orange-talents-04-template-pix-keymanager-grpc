package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.external.ConsultaResponse
import br.com.zup.academy.benzaquem.pix.external.ContaClient
import br.com.zup.academy.benzaquem.pix.grpc.PixKeymanagerGrpcServiceGrpc
import br.com.zup.academy.benzaquem.pix.grpc.RegistroRequest
import br.com.zup.academy.benzaquem.pix.grpc.RegistroResponse
import br.com.zup.academy.benzaquem.pix.grpc.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientException
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class ChaveRegistroController(
    private val novaChaveValidadaService: NovaChaveValidadaService,
    private val contaClient: ContaClient,
    private val chavePixRepository: ChavePixRepository
) : PixKeymanagerGrpcServiceGrpc.PixKeymanagerGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun registrarChave(request: RegistroRequest, responseObserver: StreamObserver<RegistroResponse>) {
        logger.info("iniciando registro $request")
        try {
            val novaChavePix = novaChaveValidadaService.novaChavePixValida(request.toModel())

            if (chavePixRepository.existsByChave(novaChavePix.chave)) {
                responseObserver.onError(
                    Status.ALREADY_EXISTS.withDescription("Chave pix já cadastrada")
                        .asRuntimeException()
                )
                return
            }

            try {
                val responseConsultaClient: HttpResponse<ConsultaResponse> =
                    contaClient.consultaContaItau(novaChavePix.idCliente, novaChavePix.tipoConta)
                logger.info("status client http {}", responseConsultaClient.status)
                if (responseConsultaClient.status == HttpStatus.NOT_FOUND) {
                    responseObserver.onError(
                        Status.NOT_FOUND.withDescription("'${novaChavePix.tipoConta} de cliente id=${novaChavePix.idCliente}' não foi encontrado").asRuntimeException()
                    )
                    return
                }
            } catch (ex: HttpClientException) {
                responseObserver.onError(
                    Status.UNAVAILABLE.withDescription("Serviço externo indisponível").asRuntimeException()
                )
                return
            }
            chavePixRepository.save(novaChavePix)


            val responseRegistrarChave = RegistroResponse.newBuilder().setPixId(novaChavePix.id).build()
            responseObserver.onNext(responseRegistrarChave)
        } catch (ex: ConstraintViolationException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(ex.message)
                    .asRuntimeException()
            )
            return
        }
        responseObserver.onCompleted()
    }

}