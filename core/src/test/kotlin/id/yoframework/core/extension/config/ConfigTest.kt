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

package id.yoframework.core.extension.config

import id.yoframework.core.extension.vertx.buildVertx
import id.yoframework.core.json.getExcept
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConfigTest {

    private lateinit var vertx: Vertx

    @Before
    fun setUp() {
        vertx = buildVertx()
    }

    @After
    fun tearDown() {
        vertx.close()
    }

    @Test
    @Suppress("DEPRECATION")
    fun `propertiesConfig should able to Load config from properties file`() = runBlocking<Unit> {
        val propertyConfig = propertiesConfig("config-test.properties")
        val config = vertx.retrieveConfig(propertyConfig)

        assertEquals(config.getExcept("TEST_KEY"), "Test Value From Properties File")
        assertEquals(config.getExcept<Int>("TEST_INT"), 1234)
        assertFailsWith(IllegalArgumentException::class, { config.getExcept("NOT_EXISTS_KEY") })

    }

    @Test
    @Suppress("DEPRECATION")
    fun `jsonConfig should able to Load config from json file`() = runBlocking<Unit> {
        val jsonConfig = jsonConfig("config-test.json")
        val config = vertx.retrieveConfig(jsonConfig)

        assertEquals(config.getExcept("TEST_KEY"), "Test Value From JSON")
        assertEquals(config.getExcept("TEST_INT"), 1234)
        assertEquals(config.getExcept<JsonObject>("SUPER").getExcept("DUPER"), "Yes")
        assertEquals(config.getExcept<JsonObject>("SUPER").getExcept("HORE"), 46)
        assertFailsWith(IllegalArgumentException::class, { config.getExcept("NOT_EXISTS_KEY") })
    }

    @Test
    @Suppress("DEPRECATION")
    fun `yamlConfig should able to Load config from yaml file`() = runBlocking<Unit> {
        val yamlConfig = yamlConfig("config-test.yaml")
        val config = vertx.retrieveConfig(yamlConfig)

        assertEquals(config.getExcept("TEST_KEY"), "Test Value From YAML")
        assertEquals(config.getExcept("TEST_INT"), 1234)
        assertEquals(config.getExcept<JsonObject>("SUPER").getExcept("DUPER"), "Yeah")
        assertEquals(config.getExcept<JsonObject>("SUPER").getExcept("HORE"), 46)
        assertFailsWith(IllegalArgumentException::class, { config.getExcept("NOT_EXISTS_KEY") })
    }
}