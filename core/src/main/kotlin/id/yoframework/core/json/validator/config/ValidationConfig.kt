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

package id.yoframework.core.json.validator.config

sealed class Length {
    class Exactly(val value: Int) : Length()
    class Maximum(val value: Int) : Length()
    class Minimum(val value: Int) : Length()
    class Range(val from: Int, val to: Int) : Length()
}

sealed class Numeric {
    class GreaterThan<out T : Number>(val value: T, val equals: Boolean = false) : Numeric()
    class LessThan<out T : Number>(val value: T, val equals: Boolean = false) : Numeric()
    class EqualTo<out T : Number>(val value: T) : Numeric()
    class DivisibleBy(val value: Int) : Numeric()
    data object Odd : Numeric()
    data object Even : Numeric()
}
