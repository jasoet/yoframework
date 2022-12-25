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
import io.ebean.Database
import io.ebean.Query
import io.ebean.Transaction
import kotlin.reflect.KClass

open class Repository<T : Model, in I : Any>(
    protected open val ebean: Database,
    private val clazz: KClass<T>
) {
    protected val query: Query<T>
        get() = ebean.createQuery(clazz.java)

    open fun count(): Int {
        return query.findCount()
    }

    open fun exists(id: I): Boolean {
        return findOne(id) != null
    }

    open fun findOne(id: I): T? {
        return ebean.find(clazz.java, id)
    }

    open fun findAll(): List<T> {
        return ebean.find(clazz.java).findList()
    }

    open fun save(o: T, transaction: Transaction? = null) {
        val ebeanTransaction = transaction ?: ebean.beginTransaction()
        return ebean.save(o, ebeanTransaction)
    }

    open fun saveAll(list: List<T>, transaction: Transaction? = null): Int {
        val ebeanTransaction = transaction ?: ebean.beginTransaction()
        return ebean.saveAll(list, ebeanTransaction)
    }

    open fun insert(o: T, transaction: Transaction? = null) {
        val ebeanTransaction = transaction ?: ebean.beginTransaction()
        return ebean.insert(o, ebeanTransaction)
    }

    open fun insertAll(list: List<T>, transaction: Transaction? = null) {
        val ebeanTransaction = transaction ?: ebean.beginTransaction()
        return ebean.insertAll(list, ebeanTransaction)
    }

    open fun update(code: I, o: T, transaction: Transaction? = null): Boolean {
        return if (exists(code)) {
            val ebeanTransaction = transaction ?: ebean.beginTransaction()
            ebean.update(o, ebeanTransaction)
            true
        } else {
            false
        }
    }

    open fun updateAll(transaction: Transaction? = null) {
        val ebeanTransaction = transaction ?: ebean.beginTransaction()
        return ebean.updateAll(findAll(), ebeanTransaction)
    }

    open fun updateAll(list: List<T>, transaction: Transaction? = null) {
        val ebeanTransaction = transaction ?: ebean.beginTransaction()
        return ebean.updateAll(list, ebeanTransaction)
    }

    open fun delete(o: T, transaction: Transaction? = null): Boolean {
        val ebeanTransaction = transaction ?: ebean.beginTransaction()
        return ebean.delete(o, ebeanTransaction)
    }

    open fun deleteAll(transaction: Transaction? = null): Int {
        val ebeanTransaction = transaction ?: ebean.beginTransaction()
        return ebean.deleteAll(findAll(), ebeanTransaction)
    }

    open fun deleteAll(list: List<T>, transaction: Transaction? = null): Int {
        val ebeanTransaction = transaction ?: ebean.beginTransaction()
        return ebean.deleteAll(list, ebeanTransaction)
    }

    open fun deleteById(id: I, transaction: Transaction? = null): Int {
        val ebeanTransaction = transaction ?: ebean.beginTransaction()
        return ebean.delete(clazz.java, id, ebeanTransaction)
    }

}
