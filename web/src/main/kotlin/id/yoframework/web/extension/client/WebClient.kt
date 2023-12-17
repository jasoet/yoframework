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

package id.yoframework.web.extension.client

import io.vertx.core.MultiMap
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.awaitResult

suspend fun WebClient.get(absoluteURI: String, header: Map<String, String>): HttpResponse<Buffer> {
    return awaitResult {
        this.getAbs(absoluteURI).apply { headers().addAll(header) }.send(it)
    }
}

suspend fun WebClient.post(
    absoluteURI: String,
    body: JsonObject,
    header: Map<String, String> = emptyMap()
): HttpResponse<Buffer> {
    return awaitResult {
        this.postAbs(absoluteURI).apply { headers().addAll(header) }.sendJsonObject(body, it)
    }
}

suspend fun WebClient.postForm(
    absoluteURI: String,
    header: Map<String, String> = emptyMap(),
    formData: Map<String, String>
): HttpResponse<Buffer> {
    val payload = formData.toList().fold(MultiMap.caseInsensitiveMultiMap()) { form, (key, value) ->
        form.set(key, value)
    }
    return awaitResult {
        this.postAbs(absoluteURI).apply { headers().addAll(header) }.sendForm(payload, it)
    }
}

suspend fun <T : Any> HttpRequest<T>.sendJson(body: Any, header: Map<String, String> = emptyMap()): HttpResponse<T> {
    return awaitResult {
        headers().addAll(header)
        method(HttpMethod.POST)
        sendJson(body, it)
    }
}

suspend fun <T : Any> HttpRequest<T>.sendJsonObject(
    body: JsonObject,
    header: Map<String, String> = emptyMap()
): HttpResponse<T> {
    return awaitResult {
        headers().addAll(header)
        method(HttpMethod.POST)
        sendJsonObject(body, it)
    }
}

inline fun <reified T : Any> HttpResponse<Buffer>.toValue(): T {
    return this.bodyAsJson(T::class.java)
}

fun HttpResponse<Buffer>.jsonBody(): JsonObject? {
    return this.bodyAsJsonObject()
}

fun HttpResponse<Buffer>.jsonArrayBody(): JsonArray? {
    return this.bodyAsJsonArray()
}
