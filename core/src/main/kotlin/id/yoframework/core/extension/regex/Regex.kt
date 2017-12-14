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

package id.yoframework.core.extension.regex

import java.util.regex.Matcher
import java.util.regex.Pattern


data class Match(val start: Int, val end: Int, val text: String, val group: List<String> = emptyList())

fun List<Regex>.regexPatterns(): String {
    return this.map { it.pattern }.reduce { i, s -> "$i - $s" }
}

fun List<Regex>.matches(input: String): Boolean {
    return this.any { it.matches(input) }
}

fun List<Regex>.containMatchIn(input: String): Boolean {
    return this.any { it.containsMatchIn(input) }
}

fun List<Regex>.find(input: String, startIndex: Int = 0): List<MatchResult> {
    return this.map { it.find(input, startIndex) }.filter { it != null }.map { it!! }
}

fun List<Regex>.findAll(input: String, startIndex: Int = 0): List<MatchResult> {
    return this.flatMap { it.findAll(input, startIndex).toList() }
}

fun String.r(option: RegexOption = RegexOption.IGNORE_CASE): Regex {
    return Regex(this, option)
}

/**
 * Matching string with pattern
 *
 * @param pattern String
 *
 * @return boolean
 */
fun String.matchWith(pattern: String): Boolean {
    return this.matchTo(pattern).find()
}

/**
 * Matching string with pattern
 *
 * @param pattern String
 *
 * @return Matcher
 */
fun String.matchTo(pattern: String): Matcher {
    if (pattern.isEmpty()) {
        throw IllegalArgumentException("Regex pattern is empty")
    }
    val p: Pattern = Pattern.compile(pattern)

    return p.matcher(this)
}

fun String.matchFirst(pattern: String): Match? {
    val matcher = this.matchTo(pattern)
    return if (matcher.find()) {
        val groupCount = matcher.groupCount()
        val groups =
                if (groupCount < 1) {
                    emptyList<String>()
                } else {
                    (1..groupCount).map {
                        matcher.group(it)
                    }
                }
        Match(matcher.start(), matcher.end(), matcher.group(), groups)
    } else {
        null
    }
}

fun String.matchAll(pattern: String): List<Match> {
    val matcher = this.matchTo(pattern)
    val result: MutableList<Match> = arrayListOf()
    while (matcher.find()) {
        val groupCount = matcher.groupCount()
        val groups =
                if (groupCount < 1) {
                    emptyList<String>()
                } else {
                    (1..groupCount).map {
                        matcher.group(it)
                    }
                }
        result.add(Match(matcher.start(), matcher.end(), matcher.group(), groups))
    }
    return result
}