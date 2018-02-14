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

package id.yoframework.core.extension.json

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import id.yoframework.core.extension.logger.logger
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.time.Instant
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import io.vertx.kotlin.core.json.Json as KJson

fun KJson.enable() {
    Json.mapper.apply {
        registerKotlinModule()
        registerModule(ParameterNamesModule())
        registerModule(JavaTimeModule())
    }

    Json.prettyMapper.apply {
        registerKotlinModule()
        registerModule(ParameterNamesModule())
        registerModule(JavaTimeModule())
    }
}

fun KJson.encodePrettily(obj: Any): String {
    return Json.encodePrettily(obj)
}

fun KJson.encode(obj: Any): String {
    return Json.encode(obj)
}

/**
 * Convert [Any] to [JsonObject] using Vertx's default [Json.mapper].
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @return empty JsonObject if receiver is null
 * @see id.yoframework.core.module.CoreModule
 */
fun Any?.toJson(): JsonObject {
    if (this == null) return JsonObject()
    return JsonObject(Json.mapper.writeValueAsString(this))
}

/**
 * Convert [JsonObject] to [T] object, and throws [IllegalArgumentException] if failed.
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @see id.yoframework.core.module.CoreModule
 */
@Throws(IllegalArgumentException::class)
inline fun <reified T : Any> JsonObject.mapTo(): T {
    return this.mapTo(T::class.java)
}

/**
 * Convert [JsonObject] to [T] object, and return null if failed or receiver is null .
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @see id.yoframework.core.module.CoreModule
 */
fun <T : Any> JsonObject?.toValue(clazz: KClass<T>): T? {
    return try {
        this?.mapTo(clazz.java)
    } catch (ie: IllegalArgumentException) {
        val log = logger("Json Extension")
        log.warn(ie.message, ie)
        null
    }
}

/**
 * Reified version of [toValue]
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @see id.yoframework.core.extension.json.toValue
 */
inline fun <reified T : Any> JsonObject?.toValue(): T? {
    return this.toValue(T::class)
}

/**
 * Convert [JsonArray] to [List] object, and return empty [List] if receiver is null.
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @throws [IllegalArgumentException] if failed to convert.
 * @see id.yoframework.core.module.CoreModule
 */
@Throws(IllegalArgumentException::class)
fun <T : Any> JsonArray?.asList(clazz: KClass<T>): List<T> {
    if (this == null) return emptyList()

    @Suppress("UNCHECKED_CAST")
    val ops: (Any) -> T = when {
        clazz.isSubclassOf(String::class) -> { t -> t as T }
        clazz.isSubclassOf(Int::class) -> { t -> t as T }
        clazz.isSubclassOf(Long::class) -> { t -> t as T }
        clazz.isSubclassOf(Double::class) -> { t -> t as T }
        clazz.isSubclassOf(Float::class) -> { t -> t as T }
        clazz.isSubclassOf(Boolean::class) -> { t -> t as T }
        clazz.isSubclassOf(Instant::class) -> { t -> t as T }
        clazz.isSubclassOf(JsonObject::class) -> { t -> t as T }
        clazz.isSubclassOf(ByteArray::class) -> { t -> t as T }
        clazz.isSubclassOf(JsonArray::class) -> { t -> t as T }
        else -> { t -> (t as JsonObject).mapTo(clazz.java) }
    }
    return this.map { ops(it) }
}

/**
 * Reified version of [asList]
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @throws [IllegalArgumentException] if failed to convert.
 * @see id.yoframework.core.module.CoreModule
 */
inline fun <reified T : Any> JsonArray?.asList(): List<T> {
    return this.asList(T::class)
}

/**
 * Get property from [JsonObject] with [T] type. [List] object, and return empty [List] if receiver is null.
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @throws [IllegalArgumentException] if failed to convert.
 * @see id.yoframework.core.module.CoreModule
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> JsonObject?.get(clazz: KClass<T>, key: String): T? {
    return when {
        clazz.isSubclassOf(String::class) -> this?.getString(key) as T?
        clazz.isSubclassOf(Int::class) -> this?.getInteger(key) as T?
        clazz.isSubclassOf(Long::class) -> this?.getLong(key) as T?
        clazz.isSubclassOf(Double::class) -> this?.getDouble(key) as T?
        clazz.isSubclassOf(Float::class) -> this?.getFloat(key) as T?
        clazz.isSubclassOf(Boolean::class) -> this?.getBoolean(key) as T?
        clazz.isSubclassOf(Instant::class) -> this?.getInstant(key) as T?
        clazz.isSubclassOf(JsonObject::class) -> this?.getJsonObject(key) as T?
        clazz.isSubclassOf(JsonArray::class) -> this?.getJsonArray(key) as T?
        clazz.isSubclassOf(ByteArray::class) -> this?.getBinary(key) as T?
        else -> throw IllegalArgumentException("${clazz.qualifiedName} Not Supported")
    }
}

/**
 * Reified version of [get]
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @throws [IllegalArgumentException] if failed to convert.
 * @see id.yoframework.core.module.CoreModule
 */
inline operator fun <reified T : Any> JsonObject?.get(key: String): T? {
    return this.get(T::class, key)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> JsonObject.getExcept(clazz: KClass<T>, key: String, exceptionMessage: (String) -> String): T {
    return when {
        clazz.isSubclassOf(String::class) -> getString(key) as T?
        clazz.isSubclassOf(Int::class) -> getInteger(key) as T?
        clazz.isSubclassOf(Long::class) -> getLong(key) as T?
        clazz.isSubclassOf(Double::class) -> getDouble(key) as T?
        clazz.isSubclassOf(Float::class) -> getFloat(key) as T?
        clazz.isSubclassOf(Boolean::class) -> getBoolean(key) as T?
        clazz.isSubclassOf(Instant::class) -> getInstant(key) as T?
        clazz.isSubclassOf(JsonObject::class) -> getJsonObject(key) as T?
        clazz.isSubclassOf(JsonArray::class) -> getJsonArray(key) as T?
        clazz.isSubclassOf(ByteArray::class) -> getBinary(key) as T?
        else -> throw IllegalArgumentException("${clazz.qualifiedName} Not Supported")
    } ?: throw IllegalArgumentException(exceptionMessage(key))
}

inline fun <reified T : Any> JsonObject.getExcept(
    key: String,
    noinline exceptionMessage: (String) -> String = { "$it is required!" }
): T {
    return this.getExcept(T::class, key, exceptionMessage)
}
