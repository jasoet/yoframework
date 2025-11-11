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

package id.yoframework.core.extension.vertx

import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.ThreadingModel
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.VertxException
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun Vertx.deployVerticle(verticle: Verticle, config: JsonObject, worker: Boolean = false): String {
    val option = DeploymentOptions().apply {
        this.config = config
        if (worker) {
            this.setThreadingModel(ThreadingModel.WORKER)
        } else {
            this.setThreadingModel(ThreadingModel.EVENT_LOOP)
        }
    }
    return this.deployVerticle(verticle, option).coAwait()
}

suspend fun buildClusteredVertx(options: VertxOptions): Vertx {
    return Vertx.builder().with(options).buildClustered().coAwait()
}

fun buildVertx(options: VertxOptions): Vertx {
    return Vertx.builder().with(options).build()
}

fun buildVertx(): Vertx {
    return buildVertx(VertxOptions())
}

/**
 * Converts this deferred value to the instance of Promise.
 * The deferred value is cancelled when the resulting promise is cancelled or otherwise completed.
 */
@ExperimentalCoroutinesApi
fun <T> Deferred<T>.toPromise(promise: Promise<T>) {
    invokeOnCompletion {
        try {
            promise.complete(getCompleted())
        } catch (t: IllegalStateException) {
            promise.fail(VertxException(t))
        }
    }
}

@ExperimentalCoroutinesApi
suspend fun <T> Promise<T>.executeAsync(op: suspend () -> T) {
    val deferred = coroutineScope {
        async(start = CoroutineStart.LAZY) {
            op()
        }
    }
    deferred.toPromise(this)
    deferred.start()
}
