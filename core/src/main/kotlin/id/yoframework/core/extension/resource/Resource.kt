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
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.net.ServerSocket
import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


@Suppress("UNCHECKED_CAST")
fun <T : Any> env(clazz: KClass<T>, key: String, defaultValue: T? = null): T {
    val value: String? = System.getenv(key)
    return if (value != null) {
        when {
            clazz.isSubclassOf(String::class) -> value as T
            clazz.isSubclassOf(Int::class) -> value.toInt() as T
            clazz.isSubclassOf(Double::class) -> value.toDouble() as T
            clazz.isSubclassOf(Boolean::class) -> value.toBoolean() as T
            else -> throw IllegalArgumentException("${clazz.qualifiedName} Not Supported")
        }
    } else defaultValue ?: throw IllegalArgumentException("Illegal: $key not found and default value is null!")
}

inline fun <reified T : Any> env(key: String, defaultValue: T? = null): T {
    return env(T::class, key, defaultValue)
}

inline fun <reified T : Any> applyEnv(key: String, defaultValue: T? = null, operation: (T) -> Unit) {
    val logger = logger("Resource Extension")
    try {
        val value = env(key, defaultValue)
        operation(value)
    } catch (e: Exception) {
        logger.info("Exception occurred ${e.message}, Operation ignored!")
    }
}

fun String.resourceToBuffer(): Buffer {
    val inputStream = javaClass.getResourceAsStream(this)
    val byteArray = ByteArray(inputStream.available())

    inputStream.use {
        it.read(byteArray)
    }
    return Buffer.buffer(byteArray)
}

fun String.loadJsonObject(): JsonObject {
    val logger = logger(JsonObject::class)
    return try {
        val inputStream = InputStreamReader(javaClass.getResourceAsStream(this), StandardCharsets.UTF_8)
        val jsonString = inputStream.useLines { it.joinToString("") }
        logger.debug("Load config from $this")
        JsonObject(jsonString)
    } catch (e: Exception) {
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

fun randomPort(): Int {
    val socket = ServerSocket(0)
    val port = socket.localPort
    socket.close()
    return port
}