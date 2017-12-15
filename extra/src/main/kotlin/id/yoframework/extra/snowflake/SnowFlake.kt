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

package id.yoframework.extra.snowflake

import com.google.common.cache.CacheBuilder
import id.yoframework.core.extension.time.toLocalDateTime
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private class SnowFlake(private val machineId: Int) {
    companion object {
        private val ATOMIC_INCREMENT = AtomicInteger(0)
        val EPOCH = 1420045200000L
        val MAX_MACHINE_ID = 64
        val ALPHA_NUMERIC_BASE = 36
        val TIME_STAMP_SHIFT = 22
        val MACHINE_ID_SHIFT = 16
    }

    init {
        if (machineId >= MAX_MACHINE_ID || machineId < 0) {
            throw IllegalArgumentException("Machine Number must between 0 - ${MAX_MACHINE_ID - 1}")
        }
    }

    fun nextId(): Long {
        synchronized(this) {
            val currentTs = System.currentTimeMillis()
            val ts = currentTs - EPOCH
            val maxIncrement = 16384
            val max = maxIncrement - 2

            if (ATOMIC_INCREMENT.get() >= max) {
                ATOMIC_INCREMENT.set(0)
            }
            val i = ATOMIC_INCREMENT.incrementAndGet()
            return (ts shl TIME_STAMP_SHIFT) or (this.machineId shl MACHINE_ID_SHIFT).toLong() or i.toLong()
        }
    }
}

data class SnowFlakeId(
        val timestamp: LocalDateTime,
        val machineId: Int,
        val increment: Int
)

private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(2, TimeUnit.MINUTES)
        .build<Int, SnowFlake>()

fun nextId(machineId: Int = 42): Long {
    val cached = cache.getIfPresent(machineId)
    val snowFlake = if (cached == null) {
        val newSnowFlake = SnowFlake(machineId)
        cache.put(machineId, newSnowFlake)
        newSnowFlake
    } else {
        cached
    }
    return snowFlake.nextId()
}

fun nextAlpha(machineId: Int = 42): String {
    val id = nextId(machineId)
    return java.lang.Long.toString(id, SnowFlake.ALPHA_NUMERIC_BASE)
}

fun parse(id: Long): SnowFlakeId {
    val ts = (id shr SnowFlake.TIME_STAMP_SHIFT) + SnowFlake.EPOCH
    val max = SnowFlake.MAX_MACHINE_ID - 1L
    val machineId = (id shr SnowFlake.MACHINE_ID_SHIFT) and max
    val i = id and max
    return SnowFlakeId(ts.toLocalDateTime(), machineId.toInt(), i.toInt())
}

fun parse(alpha: String): SnowFlakeId {
    val id = java.lang.Long.parseLong(alpha.toLowerCase(), SnowFlake.ALPHA_NUMERIC_BASE)
    return parse(id)
}
