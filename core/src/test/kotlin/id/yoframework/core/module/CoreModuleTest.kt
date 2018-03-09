/*
 * Copyright (C) 2018 - Deny Prasetyo <jasoet87@gmail.com>
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

import id.yoframework.core.extension.config.propertiesConfig
import id.yoframework.core.extension.config.retrieveConfig
import id.yoframework.core.extension.vertx.buildVertx
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class CoreModuleTest {

    @Test
    fun `core module should produce correct object`() = runBlocking<Unit> {
        val vertx = buildVertx()
        val propertyConfig = propertiesConfig("config-test.properties")
        val config = vertx.retrieveConfig(propertyConfig)
        val component = DaggerCoreAppComponent
            .builder()
            .coreModule(CoreModule(config, vertx))
            .build()

        assertEquals(component.config(), config)
        assertEquals(component.vertx(), vertx)
        assertEquals(component.eventBus(), vertx.eventBus())
        assertEquals(component.fileSystem(), vertx.fileSystem())
        assertNotNull(component.validator())

        vertx.close()
    }

}