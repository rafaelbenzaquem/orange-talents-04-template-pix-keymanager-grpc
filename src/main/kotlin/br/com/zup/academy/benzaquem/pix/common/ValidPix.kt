package br.com.zup.academy.benzaquem.pix.common

import br.com.zup.academy.benzaquem.pix.chave.NovaChavePix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CNPJValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidPixValidator::class])
annotation class ValidPix(
    val message: String = "Não é um formado \${validatedValue.tipoChave} válido",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidPixValidator : ConstraintValidator<ValidPix, NovaChavePix> {
    override fun isValid(
        value: NovaChavePix,
        annotationMetadata: AnnotationValue<ValidPix>,
        context: ConstraintValidatorContext
    ): Boolean {

        var isValid = valida(value.tipoChave, value.chave)
        if (!isValid) {
            context.messageTemplate("Não é um formado de ${value.tipoChave} válido")
        }
        return isValid;
    }

    fun valida(tipoChave: TipoChave, chave: String?): Boolean {
        return when (tipoChave) {
            TipoChave.CPF -> CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }
            TipoChave.CNPJ -> CNPJValidator().run {
                initialize(null)
                isValid(chave, null)
            }
            TipoChave.EMAIL -> EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }
            TipoChave.PHONE -> chave!!
                .matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
            TipoChave.RANDOM -> chave.isNullOrBlank()
        }
    }
}
