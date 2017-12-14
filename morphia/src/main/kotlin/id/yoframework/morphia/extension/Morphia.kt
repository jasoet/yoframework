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

package id.yoframework.morphia.extension

import com.mongodb.MongoClient
import com.mongodb.WriteResult
import id.yoframework.core.model.Model
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Key
import org.mongodb.morphia.Morphia
import org.mongodb.morphia.query.Query
import org.mongodb.morphia.query.UpdateOperations

fun MongoClient.buildMorphiaDatastore(databaseName: String, packages: Set<String>): Datastore {
    val morphia = Morphia().apply {
        packages.fold(this) { morphia, pkg ->
            morphia.mapPackage(pkg)
        }
    }

    return morphia
            .createDatastore(this, databaseName)
            .apply {
                ensureIndexes()
            }
}

fun <T : Model> Query<T>.filter(vararg filters: Pair<String, Any?>): Query<T> {
    return filters.fold(this) { query, (key, value) ->
        query.field(key).equal(value)
    }
}

fun <T : Model> Query<T>.filterIgnoreCase(vararg filters: Pair<String, Any?>): Query<T> {
    return filters.fold(this) { query, (key, value) ->
        query.field(key).equalIgnoreCase(value)
    }
}

fun <T : Model> Query<T>.filterRegex(vararg filters: Pair<String, String>): Query<T> {
    return filters.fold(this) { query, (key, value) ->
        query.filter(key, value.toRegex())
    }
}

fun <T : Model> Query<T>.filterIn(vararg filters: Pair<String, Iterable<*>>): Query<T> {
    return filters.fold(this) { query, (key, value) ->
        query.field(key).`in`(value)
    }
}

inline operator fun <T : Model> Query<T>.invoke(filters: Query<T>.() -> Query<T>): Query<T> {
    return filters(this)
}

fun <T : Model> Query<T>.include(vararg fields: String): Query<T> {
    return fields.fold(this) { query, field ->
        query.project(field, true)
    }
}

fun <T : Model> Query<T>.orders(vararg fields: String): Query<T> {
    return fields.filter { it.isNotBlank() }.fold(this) { query, field ->
        query.order(field)
    }
}

inline operator fun <T : Model> UpdateOperations<T>.invoke(ops: UpdateOperations<T>.() -> UpdateOperations<T>): UpdateOperations<T> {
    return ops(this)
}

inline fun <T : Model> UpdateOperations<T>.and(ops: UpdateOperations<T>.() -> UpdateOperations<T>): UpdateOperations<T> {
    return ops(this)
}

fun <T : Model> UpdateOperations<T>.unset(vararg unsets: String): UpdateOperations<T> {
    return unsets.fold(this) { updateOps, field ->
        updateOps.unset(field)
    }
}

operator fun <T : Model> UpdateOperations<T>.invoke(vararg sets: Pair<String, Any?>): UpdateOperations<T> {
    return sets.fold(this) { updateOps, (key, value) ->
        updateOps.setIfNotNull(key, value)
    }
}

inline fun <reified T : Any> Datastore.createQuery(): Query<T> {
    return this.createQuery(T::class.java)
}

inline fun <reified T : Any> Datastore.createUpdateOperations(): UpdateOperations<T> {
    return this.createUpdateOperations(T::class.java)
}

inline fun <reified T : Any, V : Any> Datastore.deleteById(id: V): WriteResult {
    return this.delete(T::class.java, id)
}

inline fun <reified T : Any, V : Any> Datastore.deleteByIds(ids: Iterable<V>): WriteResult {
    return this.delete(T::class.java, ids)
}

inline fun <reified T : Any> Datastore.count(): Long {
    return this.getCount(T::class.java)
}

inline fun <reified T : Any> Datastore.count(query: Query<T>): Long {
    return this.getCount(query)
}

inline fun <reified T : Any> Datastore.getById(id: Any?): T? {
    return this.get(T::class.java, id)
}

inline fun <reified T : Any> Datastore.getByIds(id: Iterable<Any>): Query<T> {
    return this.get(T::class.java, id)
}

inline fun <reified T : Any> Datastore.getByKey(id: Key<T>): T? {
    return this.getByKey(T::class.java, id)
}

inline fun <reified T : Any> Datastore.getByKeyList(ids: Iterable<Key<T>>): List<T> {
    return this.getByKeys(T::class.java, ids) ?: emptyList()
}

fun <T : Any> Datastore.refresh(id: T): T? {
    return this.get(id)
}

inline fun <reified T : Any> Datastore.find(): Query<T> {
    return this.find(T::class.java)
}

inline fun <reified T : Any> Datastore.updateOperation(): UpdateOperations<T> {
    return this.createUpdateOperations(T::class.java)
}

fun <T : Any> UpdateOperations<T>.setIfNotNull(field: String, value: Any?): UpdateOperations<T> {
    return if (value !== null) {
        this.set(field, value)
    } else {
        this
    }
}

