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

package id.yoframework.extra.extension.pebble

import arrow.core.Eval
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.template.PebbleTemplate
import java.io.StringWriter
import java.util.*

object Pebble {
    private val engine = Eval.later {
        PebbleEngine.Builder().build()
    }

    private val strictEngine = Eval.later {
        PebbleEngine.Builder().strictVariables(true).build()
    }

    fun engine(strictMode: Boolean = false): PebbleEngine {
        return if (strictMode) {
            strictEngine.value()
        } else {
            engine.value()
        }
    }
}

fun Pebble.compileStringTemplate(templateString: String, strictMode: Boolean = false): PebbleTemplate {
    val engine = engine(strictMode)
    return engine.getLiteralTemplate(templateString)
}

fun Pebble.compileTemplate(templateLocation: String, strictMode: Boolean = false): PebbleTemplate {
    val engine = engine(strictMode)
    return engine.getTemplate(templateLocation)
}

fun PebbleTemplate.evaluate(parameters: Map<String, Any>, locale: Locale? = null): String {
    val result = StringWriter()
    this.evaluate(result, parameters, locale)
    return result.toString()
}
