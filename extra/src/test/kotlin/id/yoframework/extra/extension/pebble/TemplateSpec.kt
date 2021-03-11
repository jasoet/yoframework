package id.yoframework.extra.extension.pebble

import com.mitchellbosecke.pebble.PebbleEngine
import id.yoframework.core.extension.resource.tmpDir
import id.yoframework.core.extension.vertx.buildVertx
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.kotlin.coroutines.awaitResult
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotBeNullOrBlank
import org.apache.commons.lang3.RandomStringUtils
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.StringWriter

object TemplateSpec : Spek({
    lateinit var vertx: Vertx

    beforeGroup {
        vertx = buildVertx()
    }

    given("a pebble engine") {
        val engine = PebbleEngine.Builder().build()
        on("handling classpath template") {
            val templateLocation = "template/hello.peb"

            it("should able to load template") {
                val compiledTemplate = engine.getTemplate(templateLocation)
                compiledTemplate.shouldNotBeNull()
            }

            it("should able to evaluate template") {
                val compiledTemplate = engine.getTemplate(templateLocation)
                val result = StringWriter()
                val parameters = mapOf("title" to "String Title", "content" to "Content", "footer" to "this is footer")
                compiledTemplate.evaluate(result, parameters)
                result.toString().shouldNotBeNullOrBlank()
            }
        }

        on("handling file template") {
            val randomSuffix = RandomStringUtils.randomAlphabetic(12)
            val location = "${tmpDir()}/test_template-$randomSuffix.peb"
            val templateString =
                """
                   <html>
                   <head>
                        <title>{% block title %}Default Website{% endblock %}</title>
                   </head>
                   <body>
                     <div id="content">
                        {% block content %}{% endblock %}
                     </div>
                     <div id="footer">
                        {% block footer %}
                          Copyright 2014
                        {% endblock %}
                     </div>
                   </body>
                   </html>
                """.trimIndent()

            val templateLocation = runBlocking {
                val fileSystem = vertx.fileSystem()
                awaitResult<Void> { fileSystem.writeFile(location, Buffer.buffer(templateString), it) }
                location
            }

            it("should able to load template") {
                val compiledTemplate = engine.getTemplate(templateLocation)
                compiledTemplate.shouldNotBeNull()
            }

            it("should able to evaluate template") {
                val compiledTemplate = engine.getTemplate(templateLocation)
                val result = StringWriter()
                val parameters = mapOf("title" to "String Title", "content" to "Content", "footer" to "this is footer")
                compiledTemplate.evaluate(result, parameters)
                result.toString().shouldNotBeNullOrBlank()
            }
        }

        on("handling literal template") {
            val templateString =
                """
                   <html>
                   <head>
                        <title>{% block title %}Default Website{% endblock %}</title>
                   </head>
                   <body>
                     <div id="content">
                        {% block content %}{% endblock %}
                     </div>
                     <div id="footer">
                        {% block footer %}
                          Copyright 2014
                        {% endblock %}
                     </div>
                   </body>
                   </html>
                """.trimIndent()


            it("should able to load template") {
                val compiledTemplate = engine.getLiteralTemplate(templateString)
                compiledTemplate.shouldNotBeNull()
            }

            it("should able to evaluate template") {
                val compiledTemplate = engine.getLiteralTemplate(templateString)
                val result = StringWriter()
                val parameters = mapOf("title" to "String Title", "content" to "Content", "footer" to "this is footer")
                compiledTemplate.evaluate(result, parameters)
                result.toString().shouldNotBeNullOrBlank()
            }
        }
    }

    afterGroup {
        vertx.close()
    }
})
