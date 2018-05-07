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

package id.yoframework.core.json

import arrow.core.Try
import arrow.core.getOrElse
import id.yoframework.core.extension.logger.logger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.get
import java.time.Instant
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

private val log = logger("Json Extension")
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
 * Get direct parent from path [path]
 * return pair of [JsonObject] and [String] remaining path or null if path is invalid.
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @see id.yoframework.core.module.CoreModule
 */
private fun JsonObject.getDirectParent(path: String): Pair<JsonObject, String>? {
    val paths = path.split(".")
    return when {
        paths.size == 1 -> this to path
        paths.size > 1 -> {
            val parents = paths.subList(0, paths.size - 1)
            val jsonParent = try {
                parents.fold(this as JsonObject?) { item, parent ->
                    item?.getJsonObject(parent)
                }
            } catch (e: ClassCastException) {
                log.warn("${e.message} occurred when get direct parent", e)
                null
            }

            jsonParent?.let { it to paths.last() }
        }
        else -> null
    }
}

/**
 * Get property [key] from [JsonObject] with [T] type, support nested path. return null if property not found.
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @throws [ClassCastException] if failed to convert.
 * @see id.yoframework.core.module.CoreModule
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> JsonObject?.getNested(clazz: KClass<T>, key: String): T? {
    val (parent, lastPath) = this?.getDirectParent(key) ?: return null
    return when {
        supportedPrimitiveTypes.any { clazz.isSubclassOf(it) } -> primitiveMapping(clazz, parent, lastPath) as T?
        supportedTypes.any { clazz.isSubclassOf(it) } -> mapping(clazz, parent, lastPath) as T?
        else -> parent.get<T>(lastPath)
    }
}

/**
 * Get property [key] from [JsonObject] with [T] type, support nested path. return null if property not found.
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @throws [ClassCastException] if failed to convert.
 * @see id.yoframework.core.module.CoreModule
 */
inline fun <reified T : Any> JsonObject?.getNested(key: String): T? {
    return this.getNested(T::class, key)
}

/**
 * Get property [key] from [JsonObject] with [T] type, support nested path. return null if property not found.
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @throws [ClassCastException] if failed to convert.
 * @see id.yoframework.core.module.CoreModule
 */
inline operator fun <reified T : Any> JsonObject?.get(key: String): T? {
    return this.getNested(T::class, key)
}

/**
 * Get property [key] from [JsonObject] with [T] type, support nested path. return monad [Try]
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @throws [ClassCastException] if failed to convert.
 * @see id.yoframework.core.module.CoreModule
 */
inline fun <reified T : Any> JsonObject.getTry(
    key: String, exceptionMessage: (String) -> String = { "$it is required!" }
): Try<T> {
    return Try {
        this.getNested<T>(key) ?: throw IllegalArgumentException(exceptionMessage(key))
    }
}

/**
 * Get property [key] from [JsonObject] with [T] type, support nested path. return exception if property not found.
 * Require [com.fasterxml.jackson.module.kotlin.KotlinModule] installed on Json.mapper.
 *
 * @throws [ClassCastException] if failed to convert.
 * @see id.yoframework.core.module.CoreModule
 */
inline fun <reified T : Any> JsonObject.getExcept(
    key: String, exceptionMessage: (String) -> String = { "$it is required!" }
): T {
    return this.getTry<T>(key, exceptionMessage).getOrElse { throw  it }
}

