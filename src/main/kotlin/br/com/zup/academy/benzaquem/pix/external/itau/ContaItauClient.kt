package br.com.zup.academy.benzaquem.pix.external.itau

import br.com.zup.academy.benzaquem.pix.common.TipoConta
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client;

@Client
interface ContaItauClient {

    @Get(value = "http://localhost:9091/api/v1/clientes/{idCliente}/contas")
    fun consultaContaItau(@PathVariable idCliente:String?, @QueryValue tipo: TipoConta?):HttpResponse<ContaItauResponse>

}
