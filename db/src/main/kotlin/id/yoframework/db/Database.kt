/*
 * Copyright (C) 2018 - Deny Prasetyo <jasoet87@gmail.com>
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

package id.yoframework.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import javax.sql.DataSource

fun DataSource.assertConnectionOpen() {
    if (this.connection.isClosed) {
        throw AssertionError("Connection is Closed!")
    }
}

fun DataSource.executeMigration(location: String) {
    val dataSource = this
    val flyway = Flyway.configure().dataSource(dataSource)
        .locations(location)
        .load()
    flyway.migrate()
}

fun createHikariPoolDataSource(
    name: String,
    url: String,
    username: String,
    password: String,
    driver: String,
    config: HikariConfig.() -> Unit = {}
): HikariDataSource {

    val hikariConfig = HikariConfig()

    hikariConfig.poolName = name
    hikariConfig.jdbcUrl = url

    hikariConfig.username = username
    hikariConfig.password = password
    hikariConfig.driverClassName = driver

    hikariConfig.addDataSourceProperty("cachePrepStmts", "true")
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250")
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

    config(hikariConfig)

    return HikariDataSource(hikariConfig)
}