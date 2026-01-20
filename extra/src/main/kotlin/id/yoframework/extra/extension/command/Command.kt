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

package id.yoframework.extra.extension.command

import id.yoframework.core.extension.logger.logger
import id.yoframework.core.extension.resource.homeDir
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

object Command

/**
 * Execute command in form of List<String>.
 * Able to handle standard input source from File, InputStream and String
 * and able to handle standard output source to File and OutputStream
 * This function is suspendable.
 *
 * @param input Standard input for command, able to receive File, InputStream and String input.
 * @param output Standard output for command, able to send output to File and OutputStream.
 * @author Deny Prasetyo
 */

fun Command.execute(
    commands: List<String>,
    input: Any? = null,
    output: Any? = null,
    environment: Map<String, String> = emptyMap(),
    directory: String = homeDir(),
    config: (ProcessBuilder) -> Unit = {}
): Int {
    val log = logger("CommandExtension")

    log.debug("Command to Execute ${commands.joinToString(" ")}")

    val tmpDir: String = System.getProperty("java.io.tmpdir")

    val processBuilder = ProcessBuilder(commands)

    val env = processBuilder.environment()
    env.putAll(environment)

    processBuilder.directory(File(directory))

    config(processBuilder)

    when (input) {
        is File -> {
            log.debug("Accept File Input")
            processBuilder.redirectInput(input)
        }
        is InputStream -> {
            log.debug("Accept InputStream Input")
            val inputFile = File(tmpDir, UUID.randomUUID().toString())
            FileUtils.copyInputStreamToFile(input, inputFile)
            processBuilder.redirectInput(inputFile)
        }
        is String -> {
            log.debug("Accept String Input")
            val inputFile = File(tmpDir, UUID.randomUUID().toString())
            FileUtils.writeStringToFile(inputFile, input, "UTF-8")
            processBuilder.redirectInput(inputFile)
        }
    }

    return when (output) {
        is File -> {
            processBuilder.redirectOutput(output)
            processBuilder.start().waitFor()
        }
        is OutputStream -> {
            val inputFile = File(tmpDir, UUID.randomUUID().toString())
            processBuilder.redirectOutput(inputFile)
            val exitCode = processBuilder.start().waitFor()

            FileInputStream(inputFile).use { fis ->
                output.use {
                    IOUtils.copy(fis, output)
                }
            }

            exitCode
        }
        else -> {
            processBuilder.start().waitFor()
        }
    }
}
