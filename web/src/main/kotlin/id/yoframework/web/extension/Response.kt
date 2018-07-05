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

import id.yoframework.core.json.encode
import id.yoframework.core.json.encodePrettily
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.TemplateEngine
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.coroutines.awaitResult

fun RoutingContext.json(obj: Any) {
    val response = this.response()
    response.putHeader("Content-Type", "application/json; charset=utf-8")
        .putHeader("Access-Control-Allow-Origin", "*")
        .end(Json.encode(obj))
}

fun RoutingContext.text(obj: Any, contentType: String = "text/plain") {
    val response = this.response()
    response.putHeader("Content-Type", contentType)
        .end(obj.toString())
}

fun RoutingContext.json(headers: Map<String, String> = emptyMap(), message: Any) {
    this.response().apply {
        headers.entries.fold(this) { response, entries ->
            response.putHeader(entries.key, entries.value)
        }
        putHeader("Content-Type", "application/json; charset=utf-8")
        end(Json.encode(message))
    }
}

fun RoutingContext.ok(message: String = "", headers: Map<String, String> = emptyMap()) {
    this.response().let {
        it.statusCode = HttpResponseStatus.OK.code()
        headers.entries.fold(it) { response, entries ->
            response.putHeader(entries.key, entries.value)
        }
        it.end(message)
    }
}

fun RoutingContext.prettyJson(obj: Any) {
    val response = this.response()
    response.putHeader("Content-Type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(obj))
}

suspend fun RoutingContext.sendFile(fileName: String): Void? {
    return this.sendFile(fileName, 0)
}

suspend fun RoutingContext.sendFile(fileName: String, offset: Long): Void? {
    return this.sendFile(fileName, offset, Long.MAX_VALUE)
}

suspend fun RoutingContext.sendFile(fileName: String, offset: Long, length: Long): Void? {
    val response = this.response()
    return awaitResult { response.sendFile(fileName, offset, length, it) }
}

suspend fun RoutingContext.render(engine: TemplateEngine, templateName: String): Buffer {
    return awaitResult { engine.render(this, "", templateName, it) }
}
