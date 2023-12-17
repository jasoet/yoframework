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
import kotlinx.coroutines.runBlocking
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

object ConfigSpec : Spek({
    given("a configuration") {
        lateinit var vertx: Vertx

        beforeGroup {
            vertx = buildVertx()
        }

        on("handling properties file") {
            runBlocking {
                val propertyConfig = propertiesConfig("config-test.properties")
                val config = vertx.retrieveConfig(propertyConfig)

                it("should retrieve correct value when given correct key") {
                    assertEquals(config.getExcept("TEST_KEY"), "Test Value From Properties File")
                    assertEquals(config.getExcept("TEST_INT"), 1234)
                }
                it("throw exception when given incorrect key") {
                    assertFailsWith(IllegalArgumentException::class) { config.getExcept("NOT_EXISTS_KEY") }
                }
            }
        }

        on("handling json file") {
            runBlocking {
                val jsonConfig = jsonConfig("config-test.json")
                val config = vertx.retrieveConfig(jsonConfig)

                it("should retrieve correct value when given correct key") {
                    assertEquals(config.getExcept("TEST_KEY"), "Test Value From JSON")
                    assertEquals(config.getExcept("TEST_INT"), 1234)
                    assertEquals(config.getExcept<JsonObject>("SUPER").getExcept("DUPER"), "Yes")
                    assertEquals(config.getExcept<JsonObject>("SUPER").getExcept("HORE"), 46)
                }

                it("should throw exception when given incorrect key") {
                    assertFailsWith(IllegalArgumentException::class) { config.getExcept("NOT_EXISTS_KEY") }
                }
            }
        }

        on("handling yaml file") {
            runBlocking {
                val yamlConfig = yamlConfig("config-test.yaml")
                val config = vertx.retrieveConfig(yamlConfig)

                it("should retrieve correct value when given correct key") {
                    assertEquals(config.getExcept("TEST_KEY"), "Test Value From YAML")
                    assertEquals(config.getExcept("TEST_INT"), 1234)
                    assertEquals(config.getExcept<JsonObject>("SUPER").getExcept("DUPER"), "Yeah")
                    assertEquals(config.getExcept<JsonObject>("SUPER").getExcept("HORE"), 46)
                }

                it("should throw exception when given incorrect key") {
                    assertFailsWith(IllegalArgumentException::class) { config.getExcept("NOT_EXISTS_KEY") }
                }
            }
        }

        afterGroup {
            vertx.close()
        }
    }
})