package br.com.zup.academy.benzaquem.pix.common

import br.com.zup.academy.benzaquem.pix.chave.exceptions.ChavePixExistenteException
import br.com.zup.academy.benzaquem.pix.chave.exceptions.ChavePixInexistenteException
import br.com.zup.academy.benzaquem.pix.external.bacen.Problem
import br.com.zup.academy.benzaquem.pix.grpc.SalvaNovaChavePixGrpcServiceGrpc
import io.grpc.BindableService
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class ExceptionHandlerInterceptor :
    MethodInterceptor<BindableService, Any?> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {
        try {
            return context.proceed()
        } catch (ex: Exception) {
            logger.error(
                "Manipulando a excessão '${ex.javaClass.name}'" +
                        " diparada no método '${context.targetMethod.name}'", ex
            )

            val status = when (ex) {
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(ex.message)
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(ex.message)
                is ChavePixExistenteException -> Status.ALREADY_EXISTS.withDescription(ex.message)
                is ChavePixInexistenteException -> Status.NOT_FOUND.withDescription(ex.message)
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withDescription(ex.message)
                is HttpClientResponseException -> Status.ALREADY_EXISTS.withDescription("External Service: " +
                            "Chave pix já foi cadastrada no Banco Central do Brasil")
                is HttpClientException -> Status.UNAVAILABLE.withDescription("External Service: " + ex.message)
                else -> Status.UNKNOWN
            }

            GrpcEndpointArguments(context).response()
                .onError(status.asRuntimeException())
            return null
        }
    }

    private class GrpcEndpointArguments(val context: MethodInvocationContext<BindableService, Any?>) {
        fun response(): StreamObserver<*> {
            return context.parameterValues[1] as StreamObserver<*>
        }
    }
}