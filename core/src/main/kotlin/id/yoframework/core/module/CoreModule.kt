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

import dagger.Module
import dagger.Provides
import id.yoframework.core.json.enable
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.file.FileSystem
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.Json
import javax.inject.Singleton
import javax.validation.Validation
import javax.validation.Validator

@Module
class CoreModule(
    private val config: JsonObject,
    private val vertx: Vertx
) {

    init {
        Json.enable()
    }

    @Provides
    @Singleton
    fun provideConfig(): JsonObject {
        return config
    }

    @Singleton
    @Provides
    fun provideEventBus(): EventBus {
        return vertx.eventBus()
    }

    @Provides
    @Singleton
    fun provideVertx(): Vertx {
        return vertx
    }

    @Provides
    @Singleton
    fun provideFileSystem(): FileSystem {
        return vertx.fileSystem()
    }

    @Provides
    @Singleton
    fun provideValidator(): Validator {
        val validatorFactory = Validation.buildDefaultValidatorFactory()
        return validatorFactory.validator
    }

}
