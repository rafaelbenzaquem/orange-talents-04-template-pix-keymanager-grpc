package br.com.zup.academy.benzaquem.pix.grpc

import br.com.zup.academy.benzaquem.pix.chave.ChavePix
import br.com.zup.academy.benzaquem.pix.common.TipoChave
import br.com.zup.academy.benzaquem.pix.common.TipoConta
import java.util.*

fun RegistroRequest.toModel(): ChavePix {
    return ChavePix(
        id = UUID.randomUUID().toString(),
        idCliente = idCliente,
        chave =  chave,
        tipoChave = TipoChave.valueOf(tipoChave.name),
        tipoConta = TipoConta.valueOf(tipoConta.name)
    )
}