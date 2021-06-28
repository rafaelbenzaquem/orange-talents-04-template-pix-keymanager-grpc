package br.com.zup.academy.benzaquem.pix.external.itau

import br.com.zup.academy.benzaquem.pix.common.TipoConta


class ContaItauResponse(
    val tipo: TipoConta,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    override fun toString(): String {
        return "ConsultaResponse(tipo=$tipo, instituicao=$instituicao, agencia='$agencia', numero='$numero', titular=$titular)"
    }
}

class TitularResponse(
    val id: String,
    val nome: String,
    val cpf: String
) {
    override fun toString(): String {
        return "TitularResponse(id='$id', nome='$nome', cpf='$cpf')"
    }
}

class InstituicaoResponse(
    val nome: String,
    val ispb: String
) {
    override fun toString(): String {
        return "InstituicaoResponse(nome='$nome', ispb='$ispb')"
    }
}
