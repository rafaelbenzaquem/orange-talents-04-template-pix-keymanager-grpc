package br.com.zup.academy.benzaquem.pix.external.bacen

class Problem(
    val type: String,
    val status: Int,
    val title: String,
    val detail: String,
    val violations: List<Violation>?
) {
    class Violation(val field: String, val message: String) {
    }

    override fun toString(): String {
        return "Problem(type='$type', status=$status, title='$title', detail='$detail', violations=$violations)"
    }


}