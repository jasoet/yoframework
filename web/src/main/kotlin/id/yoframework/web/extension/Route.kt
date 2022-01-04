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

package id.yoframework.web.extension

import arrow.core.Try
import arrow.core.getOrElse
import id.yoframework.web.controller.Controller
import id.yoframework.web.default.DefaultErrorHandler
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.common.template.TemplateEngine
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun Route.first(): Route {
    return this.order(Int.MIN_VALUE)
}

fun Route.finalErrorHandler(errorHandler: ErrorHandler = DefaultErrorHandler) {
    this.last().failureHandler { routingContext: RoutingContext ->
        errorHandler.invoke(routingContext, routingContext.failure())
    }
}

fun Route.reroute(destination: String): Route {
    return this.handler { context ->
        context.reroute(destination)
    }
}

fun Route.serveStatic(): Route {
    return this.handler(StaticHandler.create())
}

fun Route.serveStatic(webRoot: String): Route {
    return this.handler(StaticHandler.create().apply {
        setWebRoot(webRoot)
    })
}

fun Route.asyncHandler(
    coroutineContext: CoroutineContext? = null,
    handler: suspend RoutingContext.() -> Unit
): Route {
    return this.handler { routingContext ->
        val context = coroutineContext ?: routingContext.vertx().dispatcher()
        CoroutineScope(context).launch {
            Try {
                handler(routingContext)
            }.getOrElse {
                routingContext.fail(it)
            }
        }
    }
}

fun Route.jsonAsyncHandler(
    coroutineContext: CoroutineContext? = null,
    handler: suspend RoutingContext.() -> Any
): Route {
    return this.asyncHandler(coroutineContext) { this.wireJson(handler(this)) }
}

fun Route.syncHandler(handler: RoutingContext.() -> Unit): Route {
    return this.handler { routingContext ->
        handler(routingContext)
    }
}

fun Route.jsonHandler(handler: RoutingContext.() -> Any): Route {
    return this.syncHandler { this.wireJson(handler(this)) }
}

fun Route.templateHandler(
    engine: TemplateEngine,
    templateName: String
): Route {
    return this.asyncHandler {
        this.text(render(engine, templateName))
    }
}

fun Router.subRoute(path: String, subController: Controller): Route {
    return this.mountSubRouter(path, subController.create())
}
