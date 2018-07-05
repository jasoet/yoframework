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

package id.yoframework.web.default

import id.yoframework.core.exception.DataInconsistentException
import id.yoframework.core.exception.NullObjectException
import id.yoframework.core.extension.logger.logger
import id.yoframework.web.exception.BadRequestException
import id.yoframework.web.exception.InvalidCredentials
import id.yoframework.web.exception.NotAllowedException
import id.yoframework.web.exception.NotFoundException
import id.yoframework.web.exception.RegistrationException
import id.yoframework.web.exception.UnauthorizedException
import id.yoframework.web.exception.ValidationException
import id.yoframework.web.extension.ErrorHandler
import id.yoframework.web.extension.header
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import java.io.FileNotFoundException


object DefaultErrorHandler : ErrorHandler {
    private const val DEFAULT_ERROR_CODE = 500

    private val log = logger(DefaultErrorHandler::class)
    private fun mapException(context: RoutingContext, e: Throwable): Int {
        return when (e) {
            is FileNotFoundException,
            is NullObjectException,
            is NotFoundException -> HttpResponseStatus.NOT_FOUND.code()

            is SecurityException,
            is UnauthorizedException -> HttpResponseStatus.UNAUTHORIZED.code()

            is RegistrationException,
            is ValidationException,
            is BadRequestException -> HttpResponseStatus.BAD_REQUEST.code()

            is NotAllowedException -> HttpResponseStatus.METHOD_NOT_ALLOWED.code()
            is InvalidCredentials -> HttpResponseStatus.FORBIDDEN.code()
            else ->
                if (context.statusCode() > 0) {
                    context.statusCode()
                } else {
                    DEFAULT_ERROR_CODE
                }
        }
    }

    override fun invoke(context: RoutingContext, e: Throwable) {
        val code = mapException(context, e)

        if (code.toString().startsWith("5")) {
            log.error(e.message)
        }

        val acceptHeader = context.header("Accept") ?: ""
        val contentTypeHeader = context.header("Content-Type") ?: ""

        if (acceptHeader.contains("/json") || contentTypeHeader.contains("/json")) {
            val result = if (e is ValidationException) {
                mapOf(
                    "message" to "Validation Failure",
                    "errors" to e.errors
                )
            } else {
                mapOf(
                    "message" to (e.message ?: ""),
                    "errors" to (e.message ?: "")
                )
            }
            context.response().setStatusCode(code).end(Json.encode(result))
        } else {
            context.response().setStatusCode(code).end(e.message ?: "")
        }
    }
}
