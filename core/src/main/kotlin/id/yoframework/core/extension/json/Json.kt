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

fun JsonObject?.toMap(): Map<String, Any?> {
    return this?.map ?: emptyMap()
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
fun <T : Any> JsonObject?.get(clazz: KClass<T>, key: String): T? {
    return when {
        clazz.isSubclassOf(String::class) -> this?.getString(key) as T?
        clazz.isSubclassOf(Int::class) -> this?.getInteger(key) as T?
        clazz.isSubclassOf(Double::class) -> this?.getDouble(key) as T?
        clazz.isSubclassOf(Boolean::class) -> this?.getBoolean(key) as T?
        clazz.isSubclassOf(JsonObject::class) -> this?.getJsonObject(key) as T?
        clazz.isSubclassOf(JsonArray::class) -> this?.getJsonArray(key) as T?
        clazz.isSubclassOf(ByteArray::class) -> this?.getBinary(key) as T?
        else -> throw IllegalArgumentException("${clazz.qualifiedName} Not Supported")
    }
}

inline operator fun <reified T : Any> JsonObject?.get(key: String): T? {
    return this.get(T::class, key)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> JsonObject.getExcept(clazz: KClass<T>, key: String, exceptionMessage: (String) -> String): T {
    return when {
        clazz.isSubclassOf(String::class) -> getString(key) as T?
        clazz.isSubclassOf(Int::class) -> getInteger(key) as T?
        clazz.isSubclassOf(Double::class) -> getDouble(key) as T?
        clazz.isSubclassOf(Boolean::class) -> getBoolean(key) as T?
        clazz.isSubclassOf(JsonObject::class) -> getJsonObject(key) as T?
        clazz.isSubclassOf(JsonArray::class) -> getJsonArray(key) as T?
        clazz.isSubclassOf(ByteArray::class) -> getBinary(key) as T?
        else -> throw IllegalArgumentException("${clazz.qualifiedName} Not Supported")
    } ?: throw IllegalArgumentException(exceptionMessage(key))
}

inline fun <reified T : Any> JsonObject.getExcept(
        key: String,
        noinline exceptionMessage: (String) -> String = { "$it is required!" }): T {
    return this.getExcept(T::class, key, exceptionMessage)
}
