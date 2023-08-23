/*
 * Copyright (C)2018 - Deny Prasetyo <jasoet87@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.yoframework.core.json.validator

import arrow.data.Invalid
import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import id.yoframework.core.json.validator.config.Numeric
import id.yoframework.core.json.validator.error.NotExistError
import id.yoframework.core.json.validator.error.NumericError
import id.yoframework.core.json.validator.error.NumericErrors
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

fun JsonObject.odd(key: String): Validated<NumericError<Int>, Int> {
    val value = this.saveGet(Int::class, key) ?: return NumericError<Int>(
        "$key is not exists ",
        Numeric.Odd
    ).invalid()

    return if (value.rem(2) != 0) {
        value.valid()
    } else {
        NumericError("$key is not odd", Numeric.Odd, value).invalid()
    }
}

fun JsonObject.even(key: String): Validated<NumericError<Int>, Int> {
    val value = this.saveGet(Int::class, key) ?: return NumericError<Int>(
        "$key is not exists ",
        Numeric.Even
    ).invalid()

    return if (value.rem(2) == 0) {
        value.valid()
    } else {
        NumericError("$key is not even", Numeric.Even, value).invalid()
    }
}

fun JsonObject.divisibleBy(key: String, divisor: Int): Validated<ValidationError, Int> {
    val value = this.saveGet(Int::class, key) ?: return NumericError<Int>(
        "$key is not exists ",
        Numeric.DivisibleBy(divisor)
    ).invalid()

    return if (value.rem(divisor) == 0) {
        value.valid()
    } else {
        NumericError("$key is not divisible by $divisor", Numeric.DivisibleBy(divisor), value).invalid()
    }
}

inline fun <reified T : Number> JsonObject.equalTo(key: String, target: T): Validated<ValidationError, T> {
    val value = this.saveGet(T::class, key) ?: return NumericError<T>(
        "$key is not exists ",
        Numeric.EqualTo(target)
    ).invalid()

    return if (value.compareTo(T::class, target) == 0) {
        value.valid()
    } else {
        NumericError("$key is not equalTo $target", Numeric.EqualTo(target), value).invalid()
    }
}

inline fun <reified T : Number> JsonObject.greaterThan(
    key: String,
    target: T,
    equals: Boolean = false
): Validated<ValidationError, T> {
    val value = this.saveGet(T::class, key) ?: return NumericError<T>(
        "$key is not exists ",
        Numeric.GreaterThan(target, equals)
    ).invalid()

    val comparison = value.compareTo(T::class, target)
    return if (comparison > 0 || (equals && comparison == 0)) {
        value.valid()
    } else {
        val message = "greaterThan${if (equals) "OrEqualTo" else ""}"
        NumericError("$key is not $message $target", Numeric.GreaterThan(target, equals), value).invalid()
    }
}

inline fun <reified T : Number> JsonObject.lessThan(
    key: String,
    target: T,
    equals: Boolean = false
): Validated<ValidationError, T> {
    val value = this.saveGet(T::class, key) ?: return NumericError<T>(
        "$key is not exists ",
        Numeric.LessThan(target, equals)
    ).invalid()

    val comparison = value.compareTo(T::class, target)
    return if (comparison > 0 || (equals && comparison == 0)) {
        value.valid()
    } else {
        val message = "lessThan${if (equals) "OrEqualTo" else ""}"
        NumericError("$key is not $message $target", Numeric.LessThan(target, equals), value).invalid()
    }
}

inline fun <reified T : Number> JsonObject.numeric(
    key: String,
    vararg configs: Numeric
): Validated<ValidationError, T> {
    val value = this.saveGet(T::class, key) ?: return NotExistError("$key is not exists ").invalid()

    val errors = configs.toList()
        .map {
            when (it) {
                is Numeric.GreaterThan<*> -> this.greaterThan(key, it.value, it.equals)
                is Numeric.LessThan<*> -> this.lessThan(key, it.value, it.equals)
                is Numeric.EqualTo<*> -> this.equalTo(key, it.value)
                is Numeric.DivisibleBy -> this.divisibleBy(key, it.value)
                Numeric.Even -> this.even(key)
                Numeric.Odd -> this.odd(key)
            }
        }
        .filter { it.isInvalid }
        .map {
            @Suppress("UNCHECKED_CAST")
            (it as Invalid<NumericError<T>>).e
        }

    return if (errors.isNotEmpty()) {
        NumericErrors("$key is invalid", errors).invalid()
    } else {
        value.valid()
    }
}
