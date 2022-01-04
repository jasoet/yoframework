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

package id.yoframework.web.extension

import arrow.core.Try
import arrow.core.getOrElse
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

fun RoutingContext.header(key: String): String? {
    return this.request().headers().get(key)
}

fun RoutingContext.param(key: String): String? {
    return this.request().getParam(key)
}

fun RoutingContext.jsonBody(): JsonObject? {
    return Try { bodyAsJson }.getOrElse { null }
}

fun RoutingContext.jsonArrayBody(): JsonArray? {
    return Try { bodyAsJsonArray }.getOrElse { null }
}

fun RoutingContext.principal(): JsonObject? {
    return this.user()?.principal()
}

//@Suppress("deprecation")
//fun RoutingContext.securityUser(): SecurityUser? {
//    return this.user() as? SecurityUser
//}

fun RoutingContext.getRemoteIpAddress(): String {
    return this.request()?.remoteAddress()?.host() ?: ""
}

fun RoutingContext.getBrowserInfo(): String = this.request()?.headers()?.get("User-Agent") ?: ""
