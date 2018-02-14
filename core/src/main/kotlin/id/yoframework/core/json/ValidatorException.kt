package id.yoframework.core.json

open class ValidationException(override val message: String? = null, open val ex: Throwable? = null) :
    RuntimeException(message, ex)

open class NullValueException(override val message: String? = null, override val ex: Throwable? = null) :
    ValidationException(message, ex)

data class ParentNotFoundException(override val message: String? = null, override val ex: Throwable? = null) :
    NullValueException(message, ex)

data class ClassIncompatibleException(override val message: String? = null, override val ex: Throwable? = null) :
    NullValueException(message, ex)

data class PatternException(
    override val message: String? = null,
    val pattern: String,
    val value: String,
    override val ex: Throwable? = null
) : ValidationException(message, ex)
