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

package id.yoframework.web.module

import dagger.Module
import dagger.Provides
import id.yoframework.core.module.CoreModule
import io.vertx.core.Vertx
import io.vertx.ext.web.common.template.TemplateEngine
import io.vertx.ext.web.templ.pebble.PebbleTemplateEngine
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [CoreModule::class])
class PebbleModule {

    @Provides
    @Singleton
    @Named("pebbleTempl")
    fun providePebbleTemplate(vertx: Vertx): TemplateEngine {
        return PebbleTemplateEngine.create(vertx)
    }
}
