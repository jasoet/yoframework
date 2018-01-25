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

import id.yoframework.core.extension.logger.logger
import id.yoframework.web.controller.Controller
import id.yoframework.web.extension.startHttpServer
import io.vertx.kotlin.coroutines.CoroutineVerticle


open class WebVerticle(
    private val controller: Controller,
    private val serverName: String = "HttpServer",
    private val portConfigName: String = "HTTP_PORT"
) : CoroutineVerticle() {
    private val log = logger(WebVerticle::class)

    open fun resolvePort(): Int {
        return config.getInteger(portConfigName)
    }

    override suspend fun start() {
        val router = controller.create()
        try {
            val port = resolvePort()
            log.info("Starting $serverName on port $port")
            val httpServer = vertx.startHttpServer(router, port)
            log.info("$serverName started in port ${httpServer.actualPort()}")
        } catch (e: Exception) {
            log.error("Failed to start $serverName. [${e.message}]", e)
        }
    }
}