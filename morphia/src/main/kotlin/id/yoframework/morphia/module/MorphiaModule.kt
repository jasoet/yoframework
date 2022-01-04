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

package id.yoframework.morphia.module

import arrow.core.getOrElse
import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import dagger.Module
import dagger.Provides
import id.yoframework.core.extension.logger.logger
import id.yoframework.core.json.getExcept
import id.yoframework.core.json.getTry
import id.yoframework.core.module.CoreModule
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module(includes = [CoreModule::class])
class MorphiaModule {
    private val log = logger<MorphiaModule>()

    @Provides
    @Singleton
    @Named("morphiaDatabaseName")
    fun databaseName(config: JsonObject): String {
        val key = "MORPHIA_DATABASE"
        return config.getTry<String>(key).getOrElse { throw it }
    }

    @Provides
    @Singleton
    fun provideMorphiaClient(config: JsonObject): MongoClient {
        val host: String = config.getExcept("MORPHIA_HOST")
        val port: Int = config.getExcept("MORPHIA_PORT")
        val server = ServerAddress(host, port)

        val mongoUsername: String = config.getString("MORPHIA_USERNAME", "")
        val mongoPassword: String = config.getString("MORPHIA_PASSWORD", "")
        val databaseName: String = config.getExcept("MORPHIA_DATABASE")

        val client = if (mongoUsername.isBlank() && mongoPassword.isBlank()) {
            log.info("Initialize MongoClient with $host:$port without auth")
            MongoClient(server)
        } else {
            val credentials = MongoCredential.createScramSha1Credential(
                mongoUsername, databaseName, mongoPassword.toCharArray()
            )
            val credentialsList = listOf(credentials)
            log.info("Initialize MongoClient with $host:$port")
            MongoClient(server, credentialsList)
        }

        log.info("Trying to Connect MongoDB Database [$host:$port]...")
        val address = client.address

        if (address == null) {
            log.error("MongoClient could not connected to [$host:$port]. Try again later")
            client.close()
        }
        log.info("MongoDB was successfully connected to $address")
        return client
    }

    @Provides
    @Singleton
    fun provideMongoDatabase(mongoClient: MongoClient, config: JsonObject): MongoDatabase {
        val databaseName = config.getString("MORPHIA_DATABASE")
        log.info("Initialize Mongo Database with name $databaseName")
        return mongoClient.getDatabase(databaseName)
    }

    @Provides
    @Singleton
    @Named("morphiaThreadPool")
    @ObsoleteCoroutinesApi
    @DelicateCoroutinesApi
    fun morphiaThreadPool(config: JsonObject): CoroutineContext {
        val mongoThreadPoolSize = config.getInteger("MORPHIA_THREAD_POOL_SIZE", DEFAULT_THREAD_POOL)
        log.info("Initialize Mongo Database with thread pool size $mongoThreadPoolSize")
        return newFixedThreadPoolContext(mongoThreadPoolSize, name = "Mongo Thread Pool")
    }

    companion object {
        const val DEFAULT_THREAD_POOL = 6
    }
}
