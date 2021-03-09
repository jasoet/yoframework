package id.yoframework.extra.extension.pebble

import id.yoframework.core.extension.vertx.buildVertx
import io.vertx.core.Vertx
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotBeNullOrBlank
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object PebbleSpec : Spek({
    lateinit var vertx: Vertx

    beforeGroup {
        vertx = buildVertx()
    }

    given("Pebble Extension") {

        on("handling engine") {

            it("should produce and cache engine") {
                val strictEngine = Pebble.engine(true)
                val engine = Pebble.engine()

                strictEngine.shouldNotBeNull()
                engine.shouldNotBeNull()

                strictEngine.shouldBeEqualTo(Pebble.engine(true))
                engine.shouldBeEqualTo(Pebble.engine())
            }
        }

        on("handling template") {

            it("should able to compile string template") {
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

                val template = Pebble.compileStringTemplate(templateString)
                template.shouldNotBeNull()

                val templateStrict = Pebble.compileStringTemplate(templateString, true)
                templateStrict.shouldNotBeNull()
            }


            // temporarily disable this test before update core module version
//            it("should able to compile file template") {
//                val randomSuffix = RandomStringUtils.randomAlphabetic(12)
//                val location = "${tmpDir()}/test_template-$randomSuffix.peb"
//                val templateString =
//                    """
//                   <html>
//                   <head>
//                        <title>{% block title %}Default Website{% endblock %}</title>
//                   </head>
//                   <body>
//                     <div id="content">
//                        {% block content %}{% endblock %}
//                     </div>
//                     <div id="footer">
//                        {% block footer %}
//                          Copyright 2014
//                        {% endblock %}
//                     </div>
//                   </body>
//                   </html>
//                """.trimIndent()
//
//                val templateLocation = runBlocking {
//                    val fileSystem = vertx.fileSystem()
//                    fileSystem.writeFile(location, Buffer.buffer(templateString))
//                    location
//                }
//
//                val template = Pebble.compileTemplate(templateLocation)
//                template.shouldNotBeNull()
//
//                val templateStrict = Pebble.compileTemplate(templateLocation, true)
//                templateStrict.shouldNotBeNull()
//
//            }

            it("should able to compile classpath template") {
                val templateLocation = "template/hello.peb"
                val template = Pebble.compileTemplate(templateLocation)
                template.shouldNotBeNull()

                val templateStrict = Pebble.compileTemplate(templateLocation, true)
                templateStrict.shouldNotBeNull()
            }
        }

        on("evaluating template") {

            it("should able to produce html") {
                val templateLocation = "template/hello.peb"
                val template = Pebble.compileTemplate(templateLocation)
                template.shouldNotBeNull()

                val parameters = mapOf("title" to "String Title", "content" to "Content", "footer" to "this is footer")
                val html = template.evaluate(parameters)
                html.shouldNotBeNullOrBlank()
            }
        }
    }

    afterGroup {
        vertx.close()
    }
})
