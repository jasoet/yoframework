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

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.Json as KJson

object Initializer {
    init {
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
