package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.common.TipoChave
import br.com.zup.academy.benzaquem.pix.common.TipoConta
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixValidador {

    fun novaChavePixValida(@Valid novaChavePix: ChavePix): ChavePix {
        if (novaChavePix.tipoChave == TipoChave.RANDOM)
            return with(novaChavePix) {
                ChavePix(
                    id = id,
                    idCliente = idCliente,
                    chave = UUID.randomUUID().toString(),
                    tipoChave = TipoChave.valueOf(tipoChave.name),
                    tipoConta = TipoConta.valueOf(tipoConta.name)
                )
            }
        return novaChavePix
    }

}

