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
        return if (transaction == null) {
            ebean.save(o)
        } else {
            ebean.save(o, transaction)
        }
    }

    open fun saveAll(list: List<T>, transaction: Transaction? = null): Int {
        return if (transaction == null) {
            ebean.saveAll(list)
        } else {
            ebean.saveAll(list, transaction)
        }
    }

    open fun insert(o: T, transaction: Transaction? = null) {
        return if (transaction == null) {
            ebean.insert(o)
        } else {
            ebean.insert(o, transaction)
        }
    }

    open fun insertAll(list: List<T>, transaction: Transaction? = null) {
        return if (transaction == null) {
            ebean.insertAll(list)
        } else {
            ebean.insertAll(list, transaction)
        }
    }

    open fun update(code: I, o: T, transaction: Transaction? = null): Boolean {
        return if (exists(code)) {
            if (transaction == null) {
                ebean.update(o)
            } else {
                ebean.update(o, transaction)
            }
            true
        } else {
            false
        }
    }

    open fun updateAll(transaction: Transaction? = null) {
        return if (transaction == null) {
            ebean.updateAll(findAll())
        } else {
            ebean.updateAll(findAll(), transaction)
        }
    }

    open fun updateAll(list: List<T>, transaction: Transaction? = null) {
        return if (transaction == null) {
            ebean.updateAll(list)
        } else {
            ebean.updateAll(list, transaction)
        }
    }

    open fun delete(o: T, transaction: Transaction? = null): Boolean {
        return if (transaction == null) {
            ebean.delete(o)
        } else {
            ebean.delete(o, transaction)
        }
    }

    open fun deleteAll(transaction: Transaction? = null): Int {
        return if (transaction == null) {
            ebean.deleteAll(findAll())
        } else {
            ebean.deleteAll(findAll(), transaction)
        }
    }

    open fun deleteAll(list: List<T>, transaction: Transaction? = null): Int {
        return if (transaction == null) {
            ebean.deleteAll(list)
        } else {
            ebean.deleteAll(list, transaction)
        }
    }

    open fun deleteById(id: I, transaction: Transaction? = null): Int {
        return if (transaction == null) {
            ebean.delete(clazz.java, id)
        } else {
            ebean.delete(clazz.java, id, transaction)
        }
    }
}
