package br.com.zup.academy.benzaquem.pix.grpc

import br.com.zup.academy.benzaquem.pix.chave.NovaChavePix
import br.com.zup.academy.benzaquem.pix.common.TipoChave
import br.com.zup.academy.benzaquem.pix.common.TipoConta

fun NovaChavePixRequest.toModel(): NovaChavePix {
    return NovaChavePix(
        clienteId = clienteId,
        chave = chave,
        tipoChave = TipoChave.valueOf(tipoChave.name),
        tipoConta = TipoConta.valueOf(tipoConta.name)
    )
}