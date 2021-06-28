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


@ValidPix
@Introspected
class NovaChavePix(
    @field:ValidUUID
    @field:NotBlank val clienteId:String,
    @field:Size(max = 77) val chave: String,
    @field:Enumerated(EnumType.STRING) val tipoChave: TipoChave,
    @field:Enumerated(EnumType.STRING) val tipoConta: TipoConta
) {}