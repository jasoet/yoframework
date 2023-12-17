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

package id.yoframework.core.extension.logger

import net.logstash.logback.marker.Markers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

inline fun <reified T : Any> logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

fun logger(clz: KClass<*>): Logger {
    return LoggerFactory.getLogger(clz.qualifiedName)
}

fun logger(name: String): Logger {
    return LoggerFactory.getLogger(name)
}

sealed class LogType

class INFO(val message: String, val throwable: Throwable? = null) : LogType()
class WARN(val message: String, val throwable: Throwable? = null) : LogType()
class ERROR(val message: String, val throwable: Throwable? = null) : LogType()

fun Logger.log(log: LogType, defaultParam: Map<String, Any> = emptyMap(), vararg params: Pair<String, Any>) {
    val allParams = defaultParam + params.toList().toMap()
    when (log) {
        is INFO -> {
            if (log.throwable != null) {
                this.info(Markers.appendEntries(allParams), log.message, log.throwable)
            } else {
                this.info(Markers.appendEntries(allParams), log.message)
            }
        }
        is WARN -> {
            if (log.throwable != null) {
                this.warn(Markers.appendEntries(allParams), log.message, log.throwable)
            } else {
                this.warn(Markers.appendEntries(allParams), log.message)
            }
        }
        is ERROR -> {
            if (log.throwable != null) {
                this.error(Markers.appendEntries(allParams), log.message, log.throwable)
            } else {
                this.error(Markers.appendEntries(allParams), log.message)
            }
        }
    }
}
