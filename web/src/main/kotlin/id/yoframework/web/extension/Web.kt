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

import id.yoframework.core.extension.vertx.createHttpServer
import id.yoframework.web.controller.Controller
import id.yoframework.web.default.DefaultErrorHandler
import id.yoframework.web.security.SecurityUser
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.templ.TemplateEngine
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext


fun Route.first(): Route {
    return this.order(-100)
}

inline fun handler(crossinline handle: (RoutingContext) -> Unit): Handler<RoutingContext> {
    return Handler { handle(it) }
}


@Suppress("NOTHING_TO_INLINE")
inline fun Route.reroute(destination: String): Route {
    return this.handler { context ->
        context.reroute(destination)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Route.serveStatic(): Route {
    return this.handler(StaticHandler.create())
}


@Suppress("NOTHING_TO_INLINE")
inline fun Route.serveStatic(webRoot: String): Route {
    return this.handler(StaticHandler.create().apply {
        setWebRoot(webRoot)
    })
}

/**
 * Extension to the HTTP response to output JSON objects.
 */
fun HttpServerResponse.endWithJson(obj: Any) {
    this.putHeader("Content-Type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(obj))
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.header(key: String): String? {
    return this.request().headers().get(key)
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.param(key: String): String? {
    return this.request().getParam(key)
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.json(obj: Any) {
    val response = this.response()
    response.putHeader("Content-Type", "application/json; charset=utf-8")
            .putHeader("Access-Control-Allow-Origin", "*")
            .end(Json.encode(obj))
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.text(obj: Any, contentType: String = "text/plain") {
    val response = this.response()
    response.putHeader("Content-Type", contentType)
            .end(Json.encode(obj))
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.json(headers: Map<String, String> = emptyMap(), message: Any) {
    this.response().apply {
        headers.entries.fold(this) { response, entries ->
            response.putHeader(entries.key, entries.value)
        }
        putHeader("Content-Type", "application/json; charset=utf-8")
        end(Json.encode(message))
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.jsonBody(): JsonObject? {
    return try {
        bodyAsJson
    } catch (e: Exception) {
        null
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.jsonArrayBody(): JsonArray? {
    return try {
        bodyAsJsonArray
    } catch (e: Exception) {
        null
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.OK(message: String = "", headers: Map<String, String> = emptyMap()) {
    this.response().let {
        it.statusCode = HttpResponseStatus.OK.code()
        headers.entries.fold(it) { response, entries ->
            response.putHeader(entries.key, entries.value)
        }
        it.end(message)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.prettyJson(obj: Any) {
    val response = this.response()
    response.putHeader("Content-Type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(obj))
}


fun RoutingContext.principal(): JsonObject? {
    return this.user()?.principal()
}

fun RoutingContext.securityUser(): SecurityUser? {
    return this.user() as? SecurityUser
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

fun RoutingContext.getRemoteIpAddress(): String {
    return this.request()?.remoteAddress()?.host() ?: ""
}

fun RoutingContext.getBrowserInfo(): String = this.request()?.headers()?.get("User-Agent") ?: ""

fun Route.asyncHandler(coroutineContext: CoroutineContext? = null, errorHandler: ErrorHandler = DefaultErrorHandler, handler: suspend RoutingContext.() -> Unit): Route {
    return this.handler { routingContext ->
        val context = coroutineContext ?: routingContext.vertx().dispatcher()
        async(context) {
            try {
                handler(routingContext)
            } catch (e: Exception) {
                errorHandler(routingContext, e)
            }
        }
    }
}

fun Route.jsonHandler(coroutineContext: CoroutineContext? = null, errorHandler: ErrorHandler = DefaultErrorHandler, handler: suspend RoutingContext.() -> Any): Route {
    return this.handler { routingContext ->
        val context = coroutineContext ?: routingContext.vertx().dispatcher()
        async(context) {
            try {
                routingContext.json(handler(routingContext))
            } catch (e: Exception) {
                errorHandler(routingContext, e)
            }
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
fun Route.templateHandler(coroutineContext: CoroutineContext? = null, errorHandler: ErrorHandler = DefaultErrorHandler, engine: TemplateEngine, templateName: String): Route {
    return this.asyncHandler(coroutineContext, errorHandler) {
        this.text(render(engine, templateName))
    }
}

suspend fun RoutingContext.render(engine: TemplateEngine, templateName: String): Buffer {
    return awaitResult { engine.render(this, "", templateName, it) }
}

fun Router.subRoute(path: String, subController: Controller): Router {
    return this.mountSubRouter(path, subController.create())
}

suspend fun Vertx.startHttpServer(router: Router, port: Int): HttpServer {
    return this.createHttpServer(port) { router.accept(it) }
}

typealias ErrorHandler = (RoutingContext, Throwable) -> Unit

