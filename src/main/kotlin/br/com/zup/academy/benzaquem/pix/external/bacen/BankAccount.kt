package br.com.zup.academy.benzaquem.pix.external.bacen

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: TipoContaBacen
) {

}