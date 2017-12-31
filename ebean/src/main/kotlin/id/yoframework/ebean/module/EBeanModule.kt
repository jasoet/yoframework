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

package id.yoframework.ebean.module

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dagger.Module
import dagger.Provides
import id.yoframework.core.extension.json.getExcept
import id.yoframework.core.module.CoreModule
import io.ebean.EbeanServer
import io.ebean.EbeanServerFactory
import io.ebean.config.ServerConfig
import io.vertx.core.json.JsonObject
import javax.inject.Named
import javax.inject.Singleton
import javax.sql.DataSource

@Module(includes = [CoreModule::class])
class EBeanModule {

    @Provides
    @Singleton
    @Named("databaseUser")
    fun username(config: JsonObject): String {
        val key = "DATABASE_USER"
        return config.getExcept(key)
    }

    @Provides
    @Singleton
    @Named("databasePassword")
    fun password(config: JsonObject): String {
        val key = "DATABASE_PASSWORD"
        return config.getExcept(key)
    }

    @Provides
    @Singleton
    @Named("databaseUrl")
    fun url(config: JsonObject): String {
        val key = "DATABASE_URL"
        return config.getExcept(key)
    }

    @Provides
    @Singleton
    @Named("databaseDriver")
    fun driver(config: JsonObject): String {
        val key = "DATABASE_DRIVER_CLASSNAME"
        return config.getExcept(key)
    }

    @Provides
    @Singleton
    fun dataSource(@Named("databaseUser") user: String,
                   @Named("databasePassword") password: String,
                   @Named("databaseUrl") url: String,
                   @Named("databaseDriver") driver: String): DataSource {

        val config = HikariConfig()
        config.poolName = "HikariPool"
        config.jdbcUrl = url
        config.username = user
        config.password = password
        config.driverClassName = driver

        /**
         * We will research for best configuration
        config.connectionTimeout = connectionTimeout
        config.minimumIdle = minimumIdle
        config.idleTimeout = idleTimeout
        config.maximumPoolSize = maximumPoolSize
        config.maxLifetime = idleTimeout
         **/

        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        return HikariDataSource(config)
    }

    @Singleton
    @Provides
    fun ebeanServer(dataSource: DataSource): EbeanServer {
        val config = ServerConfig().apply {
            name = "ebeands"
            setDataSource(dataSource)
        }

        return EbeanServerFactory.create(config)
    }
}

