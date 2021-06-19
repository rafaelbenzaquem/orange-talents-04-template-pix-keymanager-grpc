package br.com.zup.academy.benzaquem.pix.common

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@ReportAsSingleViolation
@Constraint(validatedBy = [ValidUUIDValidator::class])
@Retention(RUNTIME)
@Target(FIELD, CONSTRUCTOR, PROPERTY, VALUE_PARAMETER)
annotation class ValidUUID(
    val message: String = "Não é um formado UUID válido",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidUUIDValidator : ConstraintValidator<ValidUUID, String> {
    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<ValidUUID>,
        context: ConstraintValidatorContext
    ): Boolean {
        if (value.isNullOrBlank()) {
            return true
        }
        return value.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$".toRegex())
    }
}
