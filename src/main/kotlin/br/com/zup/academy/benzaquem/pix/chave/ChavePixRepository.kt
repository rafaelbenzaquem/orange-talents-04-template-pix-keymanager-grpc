package br.com.zup.academy.benzaquem.pix.chave

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, String> {

    fun existsByChave(chave: String?): Boolean

    @Query(value = "select c from ChavePix as c where c.idCliente =:idCliente and c.chave =:chave")
    fun findByIdClienteAndChave(idCliente: String, chave: String): Optional<ChavePix>

}
