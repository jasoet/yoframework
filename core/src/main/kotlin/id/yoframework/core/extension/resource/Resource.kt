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

package id.yoframework.core.extension.resource

import id.yoframework.core.extension.logger.logger
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

fun String.resourceToBuffer(): Buffer {
    val inputStream = javaClass.getResourceAsStream(this)
    val byteArray = inputStream?.let {
        ByteArray(inputStream.available())
    }
    byteArray?.let {
        inputStream.use { i ->
            i.read(byteArray)
        }
    }
    return Buffer.buffer(byteArray)
}

fun String.pathToByteArray(): ByteArray {
    val inputStream = FileInputStream(this)
    return inputStream.use {
        IOUtils.toByteArray(it)
    }
}

fun String.readFileLine(): List<String> {
    return Files.readAllLines(Paths.get(this))
}

fun String.loadJsonObject(): JsonObject {
    val logger = logger(JsonObject::class)
    return try {
        val inputStream = InputStreamReader(javaClass.getResourceAsStream(this), StandardCharsets.UTF_8)
        val jsonString = inputStream.useLines { it.joinToString("") }
        logger.debug("Load config from $this")
        JsonObject(jsonString)
    } catch (e: IOException) {
        logger.debug("Config Cannot Loaded, Return Empty JsonObject.  Cause: ${e.message}")
        throw e
    }
}

fun tmpDir(): String {
    return System.getProperty("java.io.tmpdir")
}

fun homeDir(): String {
    return System.getProperty("user.home")
}

fun pathSeparator(): String {
    return File.pathSeparator
}

fun String.toBuffer(): Buffer {
    return Buffer.buffer(this)
}

fun randomPort(): Int {
    val socket = ServerSocket(0)
    val port = socket.localPort
    socket.close()
    return port
}
