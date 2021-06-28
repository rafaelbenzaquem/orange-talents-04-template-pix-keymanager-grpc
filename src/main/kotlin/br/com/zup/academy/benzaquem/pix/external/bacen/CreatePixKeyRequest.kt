package br.com.zup.academy.benzaquem.pix.external.bacen

import br.com.zup.academy.benzaquem.pix.chave.NovaChavePix
import br.com.zup.academy.benzaquem.pix.common.TipoChave
import br.com.zup.academy.benzaquem.pix.external.itau.ContaItauResponse

data class CreatePixKeyRequest(
    val keyType: TipoChave,
    val key: String?,
    val bankAccount: BankAccount,
    val owner: Owner
) {
    constructor(contaItau: ContaItauResponse, novaChavePix: NovaChavePix) : this(
        novaChavePix.tipoChave, novaChavePix.chave,
        BankAccount(
            contaItau.instituicao.ispb,
            contaItau.agencia,
            contaItau.numero,
            TipoContaBacen.parseTipoContaBacen(contaItau.tipo)
        ),
        Owner(
            TipoOwnerBacen.NATURAL_PERSON,
            contaItau.titular.nome,
            contaItau.titular.cpf
        )
    ) {
    }

    constructor(contaItau: ContaItauResponse, keyType: TipoChave, key: String) : this(
        keyType, key,
        BankAccount(
            contaItau.instituicao.ispb,
            contaItau.agencia,
            contaItau.numero,
            TipoContaBacen.parseTipoContaBacen(contaItau.tipo)
        ),
        Owner(
            TipoOwnerBacen.NATURAL_PERSON,
            contaItau.titular.nome,
            contaItau.titular.cpf
        )
    ) {
    }
}

