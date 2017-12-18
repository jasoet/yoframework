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

package id.yoframework.ebean.repository

import id.yoframework.core.model.Model
import io.ebean.EbeanServer
import io.ebean.Query
import io.ebean.Transaction
import kotlin.reflect.KClass

abstract class Repository<T : Model, in I : Any>(protected open val ebean: EbeanServer,
                                                 private val clazz: KClass<T>) {
    protected val query: Query<T>
        get() = ebean.createQuery(clazz.java)

    open fun exists(id: I): Boolean {
        return findOne(id) != null
    }

    open fun findOne(id: I): T? {
        return ebean.find(clazz.java, id)
    }

    open fun findAll(): List<T> {
        return ebean.find(clazz.java).findList()
    }

    open fun count(): Int {
        return query.findCount()
    }

    open fun deleteQuery(deleteQuery: Query<T>, transaction: Transaction? = null): Int {
        return ebean.delete(deleteQuery, transaction)
    }

    open fun delete(o: T, transaction: Transaction? = null): Boolean {
        return ebean.delete(o, transaction)
    }

    open fun deleteAll(transaction: Transaction? = null): Int {
        return ebean.deleteAll(findAll(), transaction)
    }

    open fun deleteById(id: I, transaction: Transaction? = null): Int {
        return ebean.delete(clazz.java, id, transaction)
    }

    open fun saveAll(list: List<T>, transaction: Transaction? = null): Int {
        return ebean.saveAll(list, transaction)
    }

    open fun save(o: T, transaction: Transaction? = null) {
        return ebean.save(o, transaction)
    }

    open fun update(code: I, o: T, transaction: Transaction? = null): Boolean {
        return if (exists(code)) {
            ebean.update(o, transaction)
            true
        } else {
            false
        }
    }

}