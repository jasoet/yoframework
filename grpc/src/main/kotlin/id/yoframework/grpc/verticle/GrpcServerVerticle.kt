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

package id.yoframework.grpc.verticle

import id.yoframework.core.extension.logger.logger
import id.yoframework.grpc.buildGrpcServer
import id.yoframework.grpc.shutdownServer
import id.yoframework.grpc.startServer
import io.grpc.BindableService
import io.vertx.grpc.VertxServer
import io.vertx.grpc.VertxServerBuilder
import io.vertx.kotlin.coroutines.CoroutineVerticle


open class GrpcServerVerticle(
    private val host: String,
    private val port: Int,
    private vararg val services: BindableService,
    private val configuration: (VertxServerBuilder) -> VertxServerBuilder = { it }
) : CoroutineVerticle() {
    private val log = logger<GrpcServerVerticle>()
    private lateinit var server: VertxServer

    override suspend fun start() {
        try {
            log.debug("Initialize Grpc Server on $host:$port with ${services.size} Service(s).")
            log.debug("===== Included Services =====")
            services.forEach {
                log.debug("${it::class.qualifiedName}")
            }
            server = vertx.buildGrpcServer(host, port, *services, configuration = configuration)
            log.debug("Starting Grpc Server")
            server.startServer()
            log.debug("Grpc Server Started on $host:$port")
        } catch (e: Exception) {
            log.error("Grpc Server Failed to Start ${e.message}", e)
            throw e
        }
    }

    override suspend fun stop() {
        log.debug("Shutting down Grpc Server")
        server.shutdownServer()
    }
}