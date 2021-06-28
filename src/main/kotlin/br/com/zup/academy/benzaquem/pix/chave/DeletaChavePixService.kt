package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.chave.exceptions.ChavePixInexistenteException
import br.com.zup.academy.benzaquem.pix.common.ValidUUID
import br.com.zup.academy.benzaquem.pix.external.bacen.BacenClient
import br.com.zup.academy.benzaquem.pix.external.bacen.DeletePixKeyRequest
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class DeletaChavePixService(
    private val chavePixRepository: ChavePixRepository,
    private val bacenClient: BacenClient
) {

    @Transactional
    fun deletarChavePix(
        @NotBlank @ValidUUID pixId: String,
        @NotBlank @ValidUUID clienteId: String
    ): ChavePix {

        val chavePix = chavePixRepository.findByIdAndClienteId(pixId, clienteId)
            .orElseThrow { ChavePixInexistenteException("Chave Pix não encontrada ou não pertence ao cliente") }
        val bacenDeletePixKeyRequest = DeletePixKeyRequest(chavePix.chave, chavePix.ispb)
        val bacenDeletePixKeyResponse = bacenClient.removerChavePixBacen(bacenDeletePixKeyRequest, chavePix.chave)

        if (bacenDeletePixKeyResponse.status != HttpStatus.OK) { // 1
            throw IllegalStateException("Erro ao remover chave Pix no Banco Central do Brasil")
        }
        chavePixRepository.delete(chavePix)
        return chavePix
    }

}

