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

import id.yoframework.core.extension.logger.logger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.time.Instant
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

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

private val supportedPrimitiveTypes = listOf(
    String::class,
    Int::class,
    Long::class,
    Double::class,
    Float::class,
    Boolean::class
)

private val primitiveMapping: (KClass<*>, JsonObject, String) -> Any? = { clazz, json, key ->
    when {
        clazz.isSubclassOf(String::class) -> json.getString(key)
        clazz.isSubclassOf(Int::class) -> json.getInteger(key)
        clazz.isSubclassOf(Long::class) -> json.getLong(key)
        clazz.isSubclassOf(Double::class) -> json.getDouble(key)
        clazz.isSubclassOf(Float::class) -> json.getFloat(key)
        clazz.isSubclassOf(Boolean::class) -> json.getBoolean(key)
        else -> null
    }
}

private val supportedTypes = listOf(
    Instant::class,
    JsonObject::class,
    ByteArray::class,
    JsonArray::class
)

private val mapping: (KClass<*>, JsonObject, String) -> Any? = { clazz, json, key ->
    when {
        clazz.isSubclassOf(Instant::class) -> json.getInstant(key)
        clazz.isSubclassOf(JsonObject::class) -> json.getJsonObject(key)
        clazz.isSubclassOf(JsonArray::class) -> json.getJsonArray(key)
        clazz.isSubclassOf(ByteArray::class) -> json.getBinary(key)
        else -> null
    }
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
        (supportedPrimitiveTypes + supportedTypes).any { clazz.isSubclassOf(it) } -> { t -> t as T }
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
        this == null -> null
        supportedPrimitiveTypes.any { clazz.isSubclassOf(it) } -> primitiveMapping(clazz, this, key) as T?
        supportedTypes.any { clazz.isSubclassOf(it) } -> mapping(clazz, this, key) as T?
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
        (supportedPrimitiveTypes + supportedTypes).any { clazz.isSubclassOf(it) } -> this.get(clazz, key)
        else -> throw IllegalArgumentException("${clazz.qualifiedName} Not Supported")
    } ?: throw IllegalArgumentException(exceptionMessage(key))
}

inline fun <reified T : Any> JsonObject.getExcept(
    key: String,
    noinline exceptionMessage: (String) -> String = { "$it is required!" }
): T {
    return this.getExcept(T::class, key, exceptionMessage)
}
