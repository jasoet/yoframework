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

import arrow.data.Validated
import arrow.syntax.validated.invalid
import arrow.syntax.validated.valid
import id.yoframework.core.json.get
import id.yoframework.core.json.getNested
import io.vertx.core.json.JsonObject

inline fun <reified T : Any> JsonObject.notNull(key: String): Validated<ValidationError, T> {
    val value = this.getNested(T::class, key)
    return value?.valid() ?: NotExistError("$key cannot be null").invalid()
}

fun JsonObject.regex(key: String, pattern: Regex): Validated<ValidationError, String> {
    val value = this[key] ?: ""
    return if (pattern.matchEntire(value) != null) {
        value.valid()
    } else {
        RegexError("$key doesn't match pattern [$pattern]", pattern.toString(), value).invalid()
    }
}

private fun <E> Map.Entry<String, Validated<E, *>>.isValid(): Boolean {
    return this.value.isValid
}

private fun <E> Map.Entry<String, Validated<E, *>>.toValidPair(): Pair<String, *> {
    return this.key to (this.value as Validated.Valid<*>).a
}

fun <E : ValidationError, R> validate(
    vararg validator: Pair<String, Validated<E, *>>,
    f: (Map<String, *>) -> R
): Validated<List<E>, R> {
    val validated = mapOf(*validator)
    return when {
        validated.all { it.isValid() } -> {
            Validated.Valid(f(validated.map { it.toValidPair() }.toMap()))
        }
        else -> {
            val errorList = validated
                .filter { !it.isValid() }
                .values
                .map { (it as Validated.Invalid<E>).e }

            Validated.Invalid(errorList)
        }
    }
}


