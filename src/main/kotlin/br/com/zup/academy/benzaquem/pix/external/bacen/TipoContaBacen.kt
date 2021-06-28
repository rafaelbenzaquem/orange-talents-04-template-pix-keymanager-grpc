package br.com.zup.academy.benzaquem.pix.external.bacen

import br.com.zup.academy.benzaquem.pix.common.TipoConta
import java.lang.IllegalArgumentException


enum class TipoContaBacen {
    CACC,
    SVGS;

    companion object {}

}

fun TipoContaBacen.Companion.toTipoConta(tipoContaBacen: TipoContaBacen): TipoConta {
    return when (tipoContaBacen) {
        TipoContaBacen.CACC -> TipoConta.CONTA_CORRENTE
        TipoContaBacen.SVGS -> TipoConta.CONTA_POUPANCA
    }
}

fun TipoContaBacen.Companion.parseTipoContaBacen(tipoConta: TipoConta): TipoContaBacen {
    return when (tipoConta) {
        TipoConta.CONTA_CORRENTE -> TipoContaBacen.CACC
        TipoConta.CONTA_POUPANCA -> TipoContaBacen.SVGS
    }
}

fun TipoContaBacen.Companion.parseTipoContaBacen(tipoConta: String): TipoContaBacen {
    return when (tipoConta) {
        "CONTA_CORRENTE" -> TipoContaBacen.CACC
        "CONTA_POUPANCA" -> TipoContaBacen.SVGS
        else -> throw IllegalArgumentException("$tipoConta é um valor diferente de 'CONTA_CORRENTE' ou 'CONTA_POUPANCA'")
    }
}