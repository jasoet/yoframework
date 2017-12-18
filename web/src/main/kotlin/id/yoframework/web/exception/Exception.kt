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

package id.yoframework.web.exception

import id.yoframework.core.exception.DataInconsistentException
import id.yoframework.core.exception.NullObjectException
import io.vertx.ext.web.client.HttpResponse

data class NotAllowedException(override val message: String? = null, val ex: Throwable? = null) : RuntimeException(message, ex)
data class RegistrationException(override val message: String? = null, val ex: Throwable? = null) : RuntimeException(message, ex)
data class BadRequestException(override val message: String? = null, val ex: Throwable? = null) : RuntimeException(message, ex)
data class UnauthorizedException(override val message: String? = null, val ex: Throwable? = null) : RuntimeException(message, ex)
data class NotFoundException(override val message: String? = null, val ex: Throwable? = null) : RuntimeException(message, ex)
data class ValidationException(val errors: List<String>, val ex: Exception? = null) : RuntimeException(ex)
data class SecurityException(override val message: String? = null, val ex: Throwable? = null) : RuntimeException(message, ex)
data class InvalidCredentials(override val message: String? = null, val ex: Throwable? = null) : RuntimeException(message, ex)

data class RequestException(override val message: String? = "", private val response: HttpResponse<*>, private val ex: Exception? = null) : RuntimeException(message, ex) {
    val statusCode = response.statusCode()
    val stringBody = response.bodyAsString()
    val jsonBody = response.bodyAsJsonObject()
}

infix fun <T> T?.orNotFound(message: String): T {
    return this ?: throw  NullObjectException(message)
}

infix fun <T> T?.orBadRequest(message: String): T {
    return this ?: throw  BadRequestException(message)
}

infix fun <T> T?.orUnauthorized(message: String): T {
    return this ?: throw UnauthorizedException(message)
}

infix fun <T> T?.orForbidden(message: String): T {
    return this ?: throw InvalidCredentials(message)
}

infix fun <T> T?.orDataError(message: String): T {
    return this ?: throw DataInconsistentException(message)
}
