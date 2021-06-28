package br.com.zup.academy.benzaquem.pix.external.bacen

data class Owner(
    val type: TipoOwnerBacen,
    val name: String,
    val taxIdNumber: String
) {

}

enum class TipoOwnerBacen {
    NATURAL_PERSON, LEGAL_PERSON
}
