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
 * WITHOUT WARRANTIES OR CONDITIONS OF Morphia.ktANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.yoframework.morphia.repository

import com.mongodb.WriteResult
import id.yoframework.core.model.Model
import id.yoframework.morphia.extension.invoke
import id.yoframework.morphia.extension.orders
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Key
import org.mongodb.morphia.aggregation.AggregationPipeline
import org.mongodb.morphia.query.FindOptions
import org.mongodb.morphia.query.Query
import org.mongodb.morphia.query.UpdateOperations
import org.mongodb.morphia.query.UpdateResults
import kotlin.reflect.KClass


abstract class Repository<T : Model, in ID : Any>(private val datastore: Datastore,
                                                  private val idField: String,
                                                  private val entityClass: KClass<T>) {

    val query: Query<T>
        get() {
            return datastore.createQuery(entityClass.java)
        }

    val updateOps: UpdateOperations<T>
        get() {
            return datastore.createUpdateOperations(entityClass.java)
        }

    val aggregate: AggregationPipeline
        get() {
            return datastore.createAggregation(entityClass.java)
        }

    abstract fun defaultUpdateOps(model: T): UpdateOperations<T>

    suspend open fun save(model: T): Key<T> {
        return datastore.save(model)
    }

    suspend open fun save(iterableModel: Iterable<T>): Iterable<Key<T>> {
        return datastore.save(iterableModel)
    }

    suspend open fun findAll(vararg orders: String, findOptions: FindOptions = FindOptions()): List<T> {
        return query.orders(*orders).asList(findOptions)
    }

    suspend open fun findByKey(id: Key<T>): T? {
        return datastore.getByKey(entityClass.java, id)
    }

    suspend open fun findByKeys(id: Iterable<Key<T>>): List<T> {
        return datastore.getByKeys(entityClass.java, id)
    }

    suspend open fun createQueryById(id: ID): Query<T> {
        return query.field(idField).equal(id)
    }

    suspend open fun findById(id: ID): T? {
        return createQueryById(id).get()
    }

    suspend open fun createQueryByIds(ids: Iterable<*>): Query<T> {
        return query.field(idField).`in`(ids)
    }

    suspend open fun findAllByIds(ids: Iterable<*>, findOptions: FindOptions = FindOptions()): List<T> {
        return createQueryByIds(ids).asList(findOptions)
    }

    suspend open fun createQueryByFields(vararg fieldList: Pair<String, Any?>): Query<T> {
        return fieldList.fold(query) { q, (field, value) ->
            q.filter(field, value)
        }
    }

    suspend open fun findByFields(vararg fieldList: Pair<String, Any?>, findOptions: FindOptions = FindOptions()): T? {
        return createQueryByFields(*fieldList).get(findOptions)
    }

    suspend open fun findAllByFields(vararg fieldList: Pair<String, Any?>, findOptions: FindOptions = FindOptions()): List<T> {
        return createQueryByFields(*fieldList).asList(findOptions)
    }

    suspend open fun count(): Long {
        return query.count()
    }

    suspend open fun countByFields(vararg fieldList: Pair<String, Any?>): Long {
        return createQueryByFields(fieldList = *fieldList).count()
    }

    suspend open fun update(id: ID, updateOperation: UpdateOperations<T>): UpdateResults {
        val query = datastore.createQuery(entityClass.java).field(idField).equal(id)
        return datastore.update(query, updateOperation)
    }

    suspend open fun update(query: Query<T>, updateOperation: UpdateOperations<T>): UpdateResults {
        return datastore.update(query, updateOperation)
    }

    suspend open fun update(id: ID, model: T): UpdateResults {
        val idQuery = query {
            field(idField).equal(id)
        }
        return update(idQuery, defaultUpdateOps(model))
    }

    suspend open fun updateByMap(vararg updateQuery: Pair<String, Any?>, updateOperation: Map<String, Any?>): UpdateResults {
        return updateByFields(*updateQuery, updateOperation = updateOperation.toList())
    }

    suspend open fun updateByFields(vararg updateQuery: Pair<String, Any?>, updateOperation: List<Pair<String, Any?>>): UpdateResults {
        val queries = updateQuery.fold(query) { q, (field, value) ->
            q.filter(field, value)
        }

        val updateOperations = updateOperation.toList().fold(updateOps) { u, (field, value) ->
            u(field to value)
        }

        return update(queries, updateOperations)
    }

    suspend open fun delete(model: T): WriteResult {
        return datastore.delete(model)
    }

    suspend open fun deleteById(id: ID): WriteResult {
        return datastore.delete(entityClass.java, id)
    }

    suspend open fun deleteByIds(ids: Iterable<ID>): WriteResult {
        return datastore.delete(entityClass.java, ids)
    }

    suspend open fun deleteByFields(vararg deleteFields: Pair<String, Any?>): WriteResult {
        val queries = deleteFields.fold(query) { q, (field, value) ->
            q.filter(field, value)
        }
        return datastore.delete(queries)
    }

}