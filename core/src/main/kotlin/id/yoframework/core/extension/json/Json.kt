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
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

fun Any?.toJson(): JsonObject {
    if (this == null) return JsonObject()
    return JsonObject(Json.mapper.writeValueAsString(this))
}

inline fun <reified T : Any> JsonObject.mapTo(): T {
    return this.mapTo(T::class.java)
}

inline fun <reified T : Any> JsonObject?.toValue(): T? {
    return try {
        this?.mapTo(T::class.java)
    } catch (ie: IllegalArgumentException) {
        val log = logger("Json Extension")
        log.warn(ie.message, ie)
        null
    }
}

fun JsonObject?.getString(key: String): String? {
    return this?.getString(key)
}

fun JsonObject?.getString(key: String, default: String): String {
    return this?.getString(key, default) ?: default
}

fun JsonObject?.getBoolean(key: String): Boolean? {
    return this?.getBoolean(key)
}

fun JsonObject?.getBoolean(key: String, default: Boolean): Boolean {
    return this?.getBoolean(key, default) ?: default
}

fun JsonObject?.getInteger(key: String): Int? {
    return this?.getInteger(key)
}

fun JsonObject?.getInteger(key: String, default: Int): Int {
    return this?.getInteger(key, default) ?: default
}

fun JsonObject?.getLong(key: String): Long? {
    return this?.getLong(key)
}

fun JsonObject?.getLong(key: String, default: Long): Long {
    return this?.getLong(key, default) ?: default
}

fun JsonObject?.getByteArray(key: String): ByteArray? {
    return this?.getByteArray(key)
}

fun JsonObject?.getByteArray(key: String, default: ByteArray): ByteArray {
    return this?.getByteArray(key, default) ?: default
}

fun JsonObject?.getDouble(key: String): Double? {
    return this?.getDouble(key)
}

fun JsonObject?.getDouble(key: String, default: Double): Double {
    return this?.getDouble(key, default) ?: default
}

fun JsonObject?.getFloat(key: String): Float? {
    return this?.getFloat(key)
}

fun JsonObject?.getFloat(key: String, default: Float): Float {
    return this?.getFloat(key, default) ?: default
}

fun JsonObject?.getJsonObject(key: String): JsonObject? {
    return this?.getJsonObject(key)
}

fun JsonObject?.getJsonObject(key: String, default: JsonObject): JsonObject {
    return this?.getJsonObject(key, default) ?: default
}

fun JsonObject?.getJsonArray(key: String): JsonArray? {
    return this?.getJsonArray(key)
}

fun JsonObject?.getJsonArray(key: String, default: JsonArray): JsonArray {
    return this?.getJsonArray(key, default) ?: default
}

fun JsonObject?.getValue(key: String): Any? {
    return this?.getValue(key)
}

fun JsonObject?.getValue(key: String, default: Any): Any {
    return this?.getValue(key, default) ?: default
}

fun JsonObject?.toMap(): Map<String, Any?> {
    return this?.map ?: emptyMap()
}

fun JsonObject.getStringExcept(key: String, exceptionMessage: String): String {
    return this.getString(key) ?: throw IllegalArgumentException(exceptionMessage)
}

fun JsonObject.getIntegerExcept(key: String, exceptionMessage: String): Int {
    return this.getInteger(key) ?: throw IllegalArgumentException(exceptionMessage)
}

fun JsonObject.getDoubleExcept(key: String, exceptionMessage: String): Double {
    return this.getDouble(key) ?: throw IllegalArgumentException(exceptionMessage)
}

fun JsonObject.getBooleanExcept(key: String, exceptionMessage: String): Boolean {
    return this.getBoolean(key) ?: throw IllegalArgumentException(exceptionMessage)
}

fun JsonObject.getJsonObjectExcept(key: String, exceptionMessage: String): JsonObject {
    return this.getJsonObject(key) ?: throw IllegalArgumentException(exceptionMessage)
}

fun JsonObject.getJsonArrayExcept(key: String, exceptionMessage: String): JsonArray {
    return this.getJsonArray(key) ?: throw IllegalArgumentException(exceptionMessage)
}

fun <T : Any> JsonArray?.asList(clazz: KClass<T>): List<T> {
    if (this == null) return emptyList()

    @Suppress("UNCHECKED_CAST")
    val ops: (Any) -> T = when {
        clazz.isSubclassOf(String::class) -> { t -> t as T }
        clazz.isSubclassOf(Int::class) -> { t -> t as T }
        clazz.isSubclassOf(Double::class) -> { t -> t as T }
        clazz.isSubclassOf(Boolean::class) -> { t -> t as T }
        clazz.isSubclassOf(JsonObject::class) -> { t -> t as T }
        clazz.isSubclassOf(JsonArray::class) -> { t -> t as T }
        else -> { t -> (t as JsonObject).mapTo(clazz.java) }
    }
    return this.map { ops(it) }
}

inline fun <reified T : Any> JsonArray?.asList(): List<T> {
    return this.asList(T::class)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> JsonObject.getExcept(clazz: KClass<T>, key: String, exceptionMessage: (String) -> String): T {
    return when {
        clazz.isSubclassOf(String::class) -> this.getString(key) as T? ?: throw IllegalArgumentException(exceptionMessage(key))
        clazz.isSubclassOf(Int::class) -> this.getInteger(key) as T? ?: throw IllegalArgumentException(exceptionMessage(key))
        clazz.isSubclassOf(Double::class) -> this.getDouble(key) as T? ?: throw IllegalArgumentException(exceptionMessage(key))
        clazz.isSubclassOf(Boolean::class) -> this.getBoolean(key) as T? ?: throw IllegalArgumentException(exceptionMessage(key))
        clazz.isSubclassOf(JsonObject::class) -> this.getJsonObject(key) as T? ?: throw IllegalArgumentException(exceptionMessage(key))
        clazz.isSubclassOf(JsonArray::class) -> this.getJsonArray(key) as T? ?: throw IllegalArgumentException(exceptionMessage(key))
        clazz.isSubclassOf(ByteArray::class) -> this.getBinary(key) as T? ?: throw IllegalArgumentException(exceptionMessage(key))
        else -> throw IllegalArgumentException("${clazz.qualifiedName} Not Supported")
    }
}

inline fun <reified T : Any> JsonObject.getExcept(key: String, noinline exceptionMessage: (String) -> String = { "$it is required!" }): T {
    return this.getExcept(T::class, key, exceptionMessage)
}