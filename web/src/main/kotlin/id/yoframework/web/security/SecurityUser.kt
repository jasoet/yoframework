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
import io.vertx.ext.auth.AbstractUser
import io.vertx.ext.auth.AuthProvider
import java.io.Serializable

class SecurityUser(private val model: SecurityModel, private val accesses: List<String>) : AbstractUser(), Serializable {
    override fun doIsPermitted(permission: String, resultHandler: Handler<AsyncResult<Boolean>>) {
        resultHandler.handle(Future.succeededFuture(doIsPermitted(permission)))
    }

    fun doIsPermitted(permission: String): Boolean {
        return accesses.contains(permission)
    }

    override fun setAuthProvider(authProvider: AuthProvider?) {}

    override fun principal(): JsonObject {
        return model.toJsonObject()
    }
}