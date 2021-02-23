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

package id.yoframework.ebean.extension

import io.ebean.Database
import io.ebean.ExpressionList
import io.ebean.Query
import io.ebean.Transaction
import io.ebean.annotation.Platform
import io.ebean.dbmigration.DbMigration

inline fun <R : Any> Database.transaction(
    existingTransaction: Transaction? = null,
    operation: (Transaction) -> R
): R {
    return if (existingTransaction != null) {
        operation(existingTransaction)
    } else {
        val transaction = this.beginTransaction()
        try {
            val result = operation(transaction)
            this.commitTransaction()
            result
        } finally {
            this.endTransaction()
        }
    }
}

operator fun <T : Any> Query<T>.invoke(expression: Query<T>.() -> ExpressionList<T>): Query<T> {
    return expression(this).query()
}

fun Database.generateMigrationFile(platform: Platform, prefix: String): String? {
    val ebean = this
    val dbMigration = DbMigration.create().apply {
        setApplyPrefix("V")
        setServer(ebean)
        addPlatform(platform, prefix)
    }
    return dbMigration.generateMigration()
}
