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

import io.grpc.BindableService
import io.grpc.ManagedChannel
import io.vertx.core.Vertx
import io.vertx.grpc.VertxChannelBuilder
import io.vertx.grpc.VertxServer
import io.vertx.grpc.VertxServerBuilder
import io.vertx.kotlin.coroutines.awaitResult

fun Vertx.buildGrpcServer(
    host: String,
    port: Int,
    vararg services: BindableService,
    configuration: (VertxServerBuilder) -> VertxServerBuilder = { it }
): VertxServer {

    return VertxServerBuilder
        .forAddress(this, host, port)
        .let {
            services.toList()
                .fold(it) { builder, service ->
                    builder.addService(service)
                }
        }
        .let {
            configuration(it)
        }
        .build()
}

suspend fun VertxServer.startServer() {
    awaitResult<Void> { this.start(it) }
}

suspend fun VertxServer.shutdownServer() {
    awaitResult<Void> { this.shutdown(it) }
}

fun Vertx.buildGrpcChannel(
    host: String,
    port: Int,
    configuration: (VertxChannelBuilder) -> VertxChannelBuilder = { it }
): ManagedChannel {
    return VertxChannelBuilder.forAddress(this, host, port)
        .let {
            configuration(it)
        }
        .build()
}

