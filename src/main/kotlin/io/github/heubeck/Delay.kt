// Copyright 2024 Florian Heubeck
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.github.heubeck

private val number = Regex("^\\d+$")
private val range = Regex("^\\d+\\.+\\d+$")
private val rangeDelimiter = Regex("\\.+")

class DelayParser(delay: String?) {
    private val delay = delay?.trim()?.run {
        when {
            matches(number) -> toLong()
            matches(range) -> split(rangeDelimiter).map { it.toLong() }.let { (min, max) ->
                (min..max).random()
            }
            else -> null
        }
    }

    fun isDelayed() = delay != null
    fun getDelay() = checkNotNull(delay)
}
