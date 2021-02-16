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

package id.yoframework.web.verticle

import arrow.core.Try
import arrow.core.getOrElse
import id.yoframework.core.extension.logger.logger
import id.yoframework.web.controller.Controller
import id.yoframework.web.extension.startHttpServer
import io.vertx.kotlin.core.http.HttpServerOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle

open class WebVerticle(
    private val controller: Controller,
    private val serverName: String = "HttpServer",
    private val portConfigName: String = "HTTP_PORT",
    private val compressionSupportConfig: String = "HTTP_COMPRESSION_SUPPORTED",
    private val compressionLevelConfig: String = "HTTP_COMPRESSION_LEVEL",
) : CoroutineVerticle() {
    private val log = logger(WebVerticle::class)

    open fun resolvePort(): Int {
        return config.getInteger(portConfigName)
    }
    open fun resolveCompressionSupport(): Boolean {
        return config.getBoolean(compressionSupportConfig)
    }
    open fun resolveCompressionLevel(): Int {
        return config.getInteger(compressionLevelConfig)
    }

    override suspend fun start() {
        val router = controller.create()
        Try {
            val port = resolvePort()
            log.info("Starting $serverName on port $port")

            val compressionSupported = resolveCompressionSupport()
            log.info("HTTP Compression Supported set to $compressionSupported")

            val compressionLevel = resolveCompressionLevel()
            log.info("HTTP Compression Level set to $compressionLevel")

            val serverOptions = HttpServerOptions().apply {
                isCompressionSupported = compressionSupported
                setCompressionLevel(compressionLevel)
            }

            val httpServer = vertx.startHttpServer(router, port, serverOptions)
            log.info("$serverName started in port ${httpServer.actualPort()}")
        }.getOrElse {
            log.error("Failed to start $serverName. [${it.message}]", it)
        }
    }
}
