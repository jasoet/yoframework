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

package id.yoframework.hystrix

import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixCommandProperties
import com.netflix.hystrix.HystrixThreadPoolKey
import id.yoframework.core.extension.system.applyIf
import com.netflix.hystrix.HystrixCommandProperties.Setter as CommandPropertiesSetter
import com.netflix.hystrix.HystrixObservableCommand.Setter as ObservableCommandSetter
import com.netflix.hystrix.HystrixThreadPoolProperties.Setter as ThreadPoolPropertiesSetter

fun commandGroupKey(key: String): HystrixCommandGroupKey {
    return HystrixCommandGroupKey.Factory.asKey(key)
}

fun commandKey(key: String): HystrixCommandKey {
    return HystrixCommandKey.Factory.asKey(key)
}

fun threadPoolKey(key: String): HystrixThreadPoolKey? {
    return HystrixThreadPoolKey.Factory.asKey(key)
}

fun buildObservableCommandConfig(
    groupKey: HystrixCommandGroupKey,
    commandKey: HystrixCommandKey? = null,
    commandPropertiesDefault: HystrixCommandProperties.Setter? = null
): ObservableCommandSetter {
    return ObservableCommandSetter
        .withGroupKey(groupKey)
        .applyIf(commandKey) {
            andCommandKey(it)
        }
        .applyIf(commandPropertiesDefault) {
            andCommandPropertiesDefaults(it)
        }
}

fun buildCommandConfig(
    groupKey: HystrixCommandGroupKey,
    commandKey: HystrixCommandKey? = null,
    threadPoolKey: HystrixThreadPoolKey? = null,
    commandProperties: CommandPropertiesSetter? = null,
    threadPoolProperties: ThreadPoolPropertiesSetter? = null
): HystrixCommand.Setter {
    return HystrixCommand.Setter
        .withGroupKey(groupKey)
        .applyIf(commandKey) {
            this.andCommandKey(it)
        }
        .applyIf(threadPoolKey) {
            andThreadPoolKey(it)
        }
        .applyIf(commandProperties) {
            andCommandPropertiesDefaults(it)
        }
        .applyIf(threadPoolProperties) {
            andThreadPoolPropertiesDefaults(it)
        }
}

fun buildCommandProperties(
    operation: CommandPropertiesSetter.() -> CommandPropertiesSetter
): CommandPropertiesSetter {
    return operation(CommandPropertiesSetter())
}

fun buildThreadPoolProperties(
    operation: ThreadPoolPropertiesSetter.() -> ThreadPoolPropertiesSetter
): ThreadPoolPropertiesSetter {
    return operation(ThreadPoolPropertiesSetter())
}
