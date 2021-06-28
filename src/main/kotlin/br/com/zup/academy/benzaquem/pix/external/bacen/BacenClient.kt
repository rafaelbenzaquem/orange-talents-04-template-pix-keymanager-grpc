package br.com.zup.academy.benzaquem.pix.external.bacen

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client
interface BacenClient {
    @Post(
        value = "http://localhost:8082/api/v1/pix/keys",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun cadastrarChavePixBacen(
        @Body request: CreatePixKeyRequest
    ): HttpResponse<CreatePixKeyResponse>

    @Delete(
        value = "http://localhost:8082/api/v1/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun removerChavePixBacen(
        @Body request: DeletePixKeyRequest,
        @PathVariable key: String
    ): HttpResponse<DeletePixKeyResponse>
}