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

package id.yoframework.core.module

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import dagger.Module
import dagger.Provides
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import javax.inject.Singleton

@Module
class ConfigModule(private val config: JsonObject) {

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

    @Provides
    @Singleton
    fun provideConfig(): JsonObject {
        return config
    }

}
