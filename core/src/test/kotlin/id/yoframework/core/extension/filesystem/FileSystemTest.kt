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

package id.yoframework.core.extension.filesystem

import id.yoframework.core.extension.resource.tmpDir
import id.yoframework.core.extension.string.randomAlpha
import id.yoframework.core.extension.vertx.buildVertx
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.file.FileSystem
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.spy
import org.mockito.junit.MockitoJUnit
import java.io.File
import kotlin.test.assertEquals


class FileSystemTest {
    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

    private lateinit var vertx: Vertx

    @Captor
    private lateinit var handlerCaptor: ArgumentCaptor<Handler<AsyncResult<Buffer>>>

    @Before
    fun setUp() {
        vertx = buildVertx()
    }

    @After
    fun tearDown() {
        vertx.close()
    }

    @Test
    fun `file system should able to write and load file`() = runBlocking {
        val fileSystem: FileSystem = spy(vertx.fileSystem())
        val fileName = "${tmpDir()}${File.separator}${randomAlpha(5)}"
        val content = "Content To Test"
        val bufferContent = Buffer.buffer(content)
        fileSystem.writeFile(fileName, bufferContent)
        val bufferFromFile = fileSystem.readFile(fileName)
        assertEquals(bufferContent.length(), bufferFromFile.length())
        assertEquals(content, bufferFromFile.toString())
    }
}