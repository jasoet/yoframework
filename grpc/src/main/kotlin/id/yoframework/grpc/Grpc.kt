/*
 * Copyright (C) 2018 - Deny Prasetyo <jasoet87@gmail.com>
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

package id.yoframework.grpc

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.net.SocketAddress
import io.vertx.grpc.client.GrpcClient
import io.vertx.grpc.server.GrpcServer
import io.vertx.grpc.server.Service
import io.vertx.grpcio.client.GrpcIoClientChannel
import io.vertx.kotlin.coroutines.coAwait

suspend fun Vertx.buildGrpcServer(
    options: HttpServerOptions = HttpServerOptions(),
    vararg services: Service,
): HttpServer {
    val grpcServer = GrpcServer.server(this)
        .let {
            services.toList()
                .fold(it) { builder, service ->
                    builder.addService(service)
                }
        }
    return this
        .createHttpServer(options)
        .requestHandler(grpcServer)
}

suspend fun HttpServer.startServer(port: Int) {
    this
        .listen(port)
        .coAwait()
}

suspend fun HttpServer.shutdownServer() {
    this.shutdown()
}

fun Vertx.buildGrpcChannel(
    host: String,
    port: Int,
): GrpcIoClientChannel {
    val client = GrpcClient.client(this)
    return GrpcIoClientChannel(client, SocketAddress.inetSocketAddress(port, host))
}

