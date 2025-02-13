package br.com.zup.academy.benzaquem.pix.chave

import br.com.zup.academy.benzaquem.pix.common.TipoChave
import br.com.zup.academy.benzaquem.pix.common.TipoConta
import br.com.zup.academy.benzaquem.pix.common.ValidPix
import br.com.zup.academy.benzaquem.pix.common.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


@Entity
@Introspected
class ChavePix(
    @field:ValidUUID
    @field:NotBlank @field:Id val id: String,
    @field:ValidUUID
    @field:NotBlank val clienteId: String,
    @field:NotBlank @field:Size(max = 77) val chave: String,
    @field:NotBlank val ispb: String,
    @field:Enumerated(EnumType.STRING) val tipoChave: TipoChave,
    @field:Enumerated(EnumType.STRING) val tipoConta: TipoConta
) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChavePix

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}