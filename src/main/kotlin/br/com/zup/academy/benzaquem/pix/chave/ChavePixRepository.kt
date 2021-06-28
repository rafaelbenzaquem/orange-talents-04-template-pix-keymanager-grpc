package br.com.zup.academy.benzaquem.pix.chave

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, String> {

    fun existsByChave(chave: String?): Boolean

    @Query(value = "select c from ChavePix as c where c.id =:id and c.clienteId =:clienteId")
    fun findByIdAndClienteId(id: String, clienteId: String): Optional<ChavePix>

}
