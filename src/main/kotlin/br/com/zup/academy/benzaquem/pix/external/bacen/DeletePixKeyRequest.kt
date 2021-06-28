package br.com.zup.academy.benzaquem.pix.external.bacen

data class DeletePixKeyRequest(
    val key: String,
    val participant: String
) {
}
