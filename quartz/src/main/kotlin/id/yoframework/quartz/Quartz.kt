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

package id.yoframework.quartz

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.quartz.*
import kotlin.coroutines.CoroutineContext


fun job(coroutineContext: CoroutineContext, executable: suspend (JobExecutionContext) -> Unit): Job {
    return Job { jobContext ->
        GlobalScope.launch(coroutineContext) {
            executable(jobContext)
        }
    }
}

inline fun <reified T : Job> job(vararg data: Pair<String, Any>): JobBuilder {
    val jobDataMap = data.fold(JobDataMap()) { map, (key, value) ->
        map[key] = value
        map
    }
    return JobBuilder.newJob(T::class.java).setJobData(jobDataMap)
}

fun cronTrigger(cronExpression: String): TriggerBuilder<CronTrigger> {
    return TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
}

fun cronTrigger(scheduleBuilder: CronScheduleBuilder): TriggerBuilder<CronTrigger> {
    return TriggerBuilder.newTrigger().withSchedule(scheduleBuilder)
}

fun simpleTrigger(scheduleBuilder: SimpleScheduleBuilder): TriggerBuilder<SimpleTrigger> {
    return TriggerBuilder.newTrigger().withSchedule(scheduleBuilder)
}

fun calendarIntervalTrigger(scheduleBuilder: CalendarIntervalScheduleBuilder): TriggerBuilder<CalendarIntervalTrigger> {
    return TriggerBuilder.newTrigger().withSchedule(scheduleBuilder)
}

inline fun <reified T : Any> JobExecutionContext?.getData(
    key: String,
    default: T? = null,
    missing: (String) -> Unit
): T {
    val value = this?.jobDetail?.jobDataMap?.get(key) as T?
    return when {
        value != null -> value
        default != null -> default
        else -> {
            missing(key)
            throw IllegalStateException("$key object is missing from job data")
        }
    }
}
