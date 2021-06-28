package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.chave.exceptions.ChavePixExistenteException
import br.com.zup.academy.benzaquem.pix.external.bacen.BacenClient
import br.com.zup.academy.benzaquem.pix.external.bacen.CreatePixKeyRequest
import br.com.zup.academy.benzaquem.pix.external.bacen.criarChavePix
import br.com.zup.academy.benzaquem.pix.external.itau.ContaItauClient
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(
    private val contaItauClient: ContaItauClient,
    private val chavePixRepository: ChavePixRepository,
    private val bacenClient: BacenClient
) {

    @Transactional
    fun salvarNovaChavePix(@Valid novaChavePix: NovaChavePix): ChavePix {

        if (chavePixRepository.existsByChave(novaChavePix.chave)) {
            throw ChavePixExistenteException("'${novaChavePix.tipoChave} = ${novaChavePix.chave}' já foi cadastrada")
        }
        val responseItau = contaItauClient.consultaContaItau(novaChavePix.clienteId, novaChavePix.tipoConta)

        val contaItau = if (responseItau.body() == null)
            throw IllegalStateException(
                "'${novaChavePix.tipoConta} de cliente id=${novaChavePix.clienteId}' não foi encontrado"
            )
        else responseItau.body()

        val responseBacen = bacenClient.cadastrarChavePixBacen(
            CreatePixKeyRequest(contaItau, novaChavePix)
        )
        val chavePixBacen = responseBacen.body()

        val chavePix = chavePixBacen.criarChavePix(novaChavePix.clienteId)
        chavePixRepository.save(chavePix)

        return chavePix
    }

}

