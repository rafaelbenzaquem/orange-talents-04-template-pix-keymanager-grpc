package br.com.zup.academy.benzaquem.pix.external

import br.com.zup.academy.benzaquem.pix.common.TipoConta


class ConsultaResponse(
    private val tipo: TipoConta,
    private val instituicao: InstituicaoResponse? = null,
    private val agencia: String? = null,
    private val numero: String? = null,
    private val titular: TitularResponse
) {
    override fun toString(): String {
        return "ConsultaResponse(tipo=$tipo, instituicao=$instituicao, agencia='$agencia', numero='$numero', titular=$titular)"
    }
}

class TitularResponse(
   private  val id: String,
   private  val nome: String? = null,
   private  val cpf: String? = null
) {
    override fun toString(): String {
        return "TitularResponse(id='$id', nome='$nome', cpf='$cpf')"
    }
}

class InstituicaoResponse(
   private  val nome: String,
   private  val ispb: String
) {
    override fun toString(): String {
        return "InstituicaoResponse(nome='$nome', ispb='$ispb')"
    }
}
