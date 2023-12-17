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

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitResult

suspend fun Vertx.startHttpServer(
    router: Router,
    port: Int,
    httpServerOptions: HttpServerOptions = HttpServerOptions()
): HttpServer {
    val httpServer = this.createHttpServer(httpServerOptions)
        .requestHandler(router)
    return awaitResult { httpServer.listen(port, it) }
}

typealias ErrorHandler = (RoutingContext, Throwable) -> Unit
