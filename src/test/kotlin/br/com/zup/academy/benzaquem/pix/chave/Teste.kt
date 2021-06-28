package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.common.TipoChave
import br.com.zup.academy.benzaquem.pix.external.bacen.*
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class Teste(
    val bacenClient: BacenClient
) {

    @Test
    @Order(1)
    fun testCadastrar() {
        try {
            val cadastrarResponse = bacenClient.cadastrarChavePixBacen(
                CreatePixKeyRequest(
                    TipoChave.CPF,
                    "77643070253", BankAccount("60701190", "0001", "123456", TipoContaBacen.CACC),
                    Owner(TipoOwnerBacen.NATURAL_PERSON, "Rafael Benzaquem Neto", "33059192057")
                )
            )
            println(cadastrarResponse.body())
        } catch (ex: HttpClientResponseException) {
            print(ex.response.getBody(Problem::class.java))
        }


    }

    @Test
    @Order(2)
    fun testeRemover() {
        try {
            val removerResponse =
                bacenClient.removerChavePixBacen(DeletePixKeyRequest("77643070253", "60701190"), "77643070253")
            print(removerResponse.body())
        } catch (ex: HttpClientResponseException) {
            print(ex.response.getBody(Problem::class.java))
        }
    }
}