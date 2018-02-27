package id.yoframework.core.json.validator

import arrow.data.Validated
import arrow.syntax.validated.invalid
import arrow.syntax.validated.valid
import id.yoframework.core.json.validator.config.Numeric
import id.yoframework.core.json.validator.error.NotExistError
import id.yoframework.core.json.validator.error.NumericError
import id.yoframework.core.json.validator.error.ValidationError
import io.vertx.core.json.JsonObject
import kotlin.reflect.KClass

fun <T : Number> T.compareTo(clazz: KClass<T>, value: T): Int {
    return when {
        clazz.isInstance(Byte::class) -> (this as Byte).compareTo(value as Byte)
        clazz.isInstance(Long::class) -> (this as Long).compareTo(value as Long)
        clazz.isInstance(Char::class) -> (this as Char).compareTo(value as Char)
        clazz.isInstance(Double::class) -> (this as Double).compareTo(value as Double)
        clazz.isInstance(Float::class) -> (this as Float).compareTo(value as Float)
        clazz.isInstance(Int::class) -> (this as Int).compareTo(value as Int)
        clazz.isInstance(Short::class) -> (this as Short).compareTo(value as Short)
        else -> throw IllegalArgumentException("${clazz.qualifiedName} is not supported!")
    }
}

fun JsonObject.odd(key: String): Validated<ValidationError, Int> {
    val value = this.saveGet(Int::class, key) ?: return NotExistError("$key is not exists ").invalid()

    return if (value.rem(2) != 0) {
        value.valid()
    } else {
        NumericError("$key is not odd", value, Numeric.Odd).invalid()
    }
}

fun JsonObject.even(key: String): Validated<ValidationError, Int> {
    val value = this.saveGet(Int::class, key) ?: return NotExistError("$key is not exists ").invalid()

    return if (value.rem(2) == 0) {
        value.valid()
    } else {
        NumericError("$key is not even", value, Numeric.Even).invalid()
    }
}

fun JsonObject.divisibleBy(key: String, divisor: Int): Validated<ValidationError, Int> {
    val value = this.saveGet(Int::class, key) ?: return NotExistError("$key is not exists ").invalid()

    return if (value.rem(divisor) == 0) {
        value.valid()
    } else {
        NumericError("$key is not divisible by $divisor", value, Numeric.DivisibleBy(divisor)).invalid()
    }
}

inline fun <reified T : Number> JsonObject.equalTo(key: String, target: T): Validated<ValidationError, T> {
    val value = this.saveGet(T::class, key) ?: return NotExistError("$key is not exists ").invalid()
    return if (value.compareTo(T::class, target) == 0) {
        value.valid()
    } else {
        NumericError("$key is not equalTo $target", value, Numeric.EqualTo(target)).invalid()
    }
}

inline fun <reified T : Number> JsonObject.greaterThan(key: String, target: T, equals: Boolean = false)
        : Validated<ValidationError, T> {
    val value = this.saveGet(T::class, key) ?: return NotExistError("$key is not exists ").invalid()
    val comparison = value.compareTo(T::class, target)
    return if (comparison > 0 || (equals && comparison == 0)) {
        value.valid()
    } else {
        val message = "greaterThan${if (equals) "OrEqualTo" else ""}"
        NumericError("$key is not $message $target", value, Numeric.GreaterThan(target, equals)).invalid()
    }
}

inline fun <reified T : Number> JsonObject.lessThan(key: String, target: T, equals: Boolean = false)
        : Validated<ValidationError, T> {
    val value = this.saveGet(T::class, key) ?: return NotExistError("$key is not exists ").invalid()
    val comparison = value.compareTo(T::class, target)
    return if (comparison > 0 || (equals && comparison == 0)) {
        value.valid()
    } else {
        val message = "lessThan${if (equals) "OrEqualTo" else ""}"
        NumericError("$key is not $message $target", value, Numeric.LessThan(target, equals)).invalid()
    }
}

