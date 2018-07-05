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
import arrow.data.invalid
import arrow.data.valid
import id.yoframework.core.json.get
import id.yoframework.core.json.validator.config.Length
import id.yoframework.core.json.validator.error.EmailError
import id.yoframework.core.json.validator.error.FormatError
import id.yoframework.core.json.validator.error.LengthError
import id.yoframework.core.json.validator.error.ValidationError
import io.vertx.core.json.JsonObject

fun JsonObject.format(key: String, format: Regex): Validated<ValidationError, String> {
    val value = this[key] ?: ""
    return if (format.matchEntire(value) != null) {
        value.valid()
    } else {
        FormatError(
            "$key doesn't match pattern [$format]",
            format.toString(),
            value
        ).invalid()
    }
}

fun JsonObject.email(key: String): Validated<ValidationError, String> {
    val emailRegex = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$".toRegex()
    return this.format(key, emailRegex).leftMap {
        EmailError("$key is not a valid email")
    }
}

fun JsonObject.length(key: String, config: Length): Validated<ValidationError, String> {
    val value = this[key] ?: ""
    val length = value.length
    val valid = when (config) {
        is Length.Exactly -> length == config.value
        is Length.Maximum -> length <= config.value
        is Length.Minimum -> length >= config.value
        is Length.Range -> length >= config.from && length <= config.to
    }
    return if (valid) value.valid() else LengthError("$key length is invalid", config).invalid()
}
