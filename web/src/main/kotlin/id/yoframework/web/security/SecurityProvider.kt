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

package id.yoframework.web.security

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import kotlinx.coroutines.experimental.runBlocking

interface SecurityProvider : AuthProvider {
    override fun authenticate(authInfo: JsonObject, resultHandler: Handler<AsyncResult<User>>) = runBlocking {
        try {
            val user = authenticate(authInfo)
            resultHandler.handle(Future.succeededFuture(user))
        } catch (e: Exception) {
            val exception = SecurityException(e)
            resultHandler.handle(Future.failedFuture(exception))
        }
    }

    suspend fun authenticate(authInfo: JsonObject): SecurityUser
}
