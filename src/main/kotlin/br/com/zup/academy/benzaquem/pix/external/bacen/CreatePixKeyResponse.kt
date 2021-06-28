package br.com.zup.academy.benzaquem.pix.external.bacen

import br.com.zup.academy.benzaquem.pix.chave.ChavePix
import br.com.zup.academy.benzaquem.pix.common.TipoChave
import br.com.zup.academy.benzaquem.pix.external.itau.ContaItauResponse
import java.time.LocalDateTime
import java.util.*


class CreatePixKeyResponse(
    val keyType: TipoChave,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {

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
        ),
        createdAt = LocalDateTime.now()
    ) {
    }

    override fun toString(): String {
        return "CreatePixKeyResponse(keyType=$keyType, key='$key', bankAccount=$bankAccount, owner=$owner, createdAt=$createdAt)"
    }
}

fun CreatePixKeyResponse.criarChavePix(idClienteItau: String): ChavePix {
    return ChavePix(
        id = UUID.randomUUID().toString(),
        clienteId = idClienteItau,
        chave = this.key,
        ispb = bankAccount.participant,
        tipoChave = TipoChave.valueOf(keyType.name),
        tipoConta = TipoContaBacen.toTipoConta(bankAccount.accountType)
    )
}
