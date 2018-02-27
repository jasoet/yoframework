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

package id.yoframework.core.json.validator.error

import id.yoframework.core.json.validator.config.Length
import id.yoframework.core.json.validator.config.Numeric

open class ValidationError(open val message: String)

data class NotExistError(override val message: String) : ValidationError(message)

data class FormatError(
    override val message: String, val pattern: String, val value: String
) : ValidationError(message)

data class LengthError(override val message: String, val config: Length) : ValidationError(message)

data class EmailError(override val message: String) : ValidationError(message)

data class NumericError<out T : Number>(override val message: String, val config: Numeric, val value: T? = null) :
    ValidationError(message)

data class NumericErrors<out T : Number>(
    override val message: String,
    val errors: List<NumericError<T>>,
    val value: T? = null
) :
    ValidationError(message) {
    val messages: List<String> = errors.map { it.message }
}
