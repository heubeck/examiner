// Copyright 2022 Florian Heubeck
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

import io.quarkus.logging.Log
import java.lang.Math.random
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.atan
import kotlin.math.tan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface Actor {
    suspend fun act()

    companion object {
        suspend fun act(delay: String?, load: String?, allocation: String?) {
            val delay = DelayParser(delay)
            val loadWeight = load?.trim()?.toLongOrNull()?.takeIf { it > 0 }
            val memWeight = allocation?.trim()?.toLongOrNull()?.takeIf { it > 0 }
            when {
                loadWeight != null && delay.isDelayed() -> LoadProducer(delay.getDelay(), loadWeight)
                memWeight != null && delay.isDelayed() -> MemoryAllocator(delay.getDelay(), memWeight)
                delay.isDelayed() -> Waiter(delay.getDelay())
                else -> Noop
            }.act()
        }
    }
}

class LoadProducer(private val durationMs: Long, private val weight: Long) : Actor {

    private tailrec fun compute(weight: Long, endAt: Long, runner: Double = 0.0): Double =
        if (weight > 0 && System.currentTimeMillis() < endAt) {
            compute(weight - 1, endAt, atan(tan(atan(tan(atan(tan(atan(tan(random())))))))))
        } else {
            runner
        }

    override suspend fun act() {
        val endAt = System.currentTimeMillis() + durationMs
        val normalizedWeight = if (weight > 100) 100 else weight
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val throttle = AtomicLong()
            while (System.currentTimeMillis() < endAt) {
                if (weight < 100 && throttle.incrementAndGet() % normalizedWeight == 0L) {
                    delay(1)
                }
                launch {
                    Log.trace(compute(normalizedWeight, endAt))
                }
            }
        }
    }
}

class MemoryAllocator(private val durationMs: Long, private val weight: Long) : Actor {

    override suspend fun act() {
        val endAt = System.currentTimeMillis() + durationMs
        val normalizedWeight = if (weight > 100) 100 else weight
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val sb = StringBuilder()
            val throttle = AtomicLong()
            while (System.currentTimeMillis() < endAt) {
                if (weight < 100 && throttle.incrementAndGet() % normalizedWeight == 0L) {
                    delay(1)
                }
                sb.append(UUID.randomUUID().toString())
            }
            Log.trace(sb.toString())
        }
    }
}

class Waiter(private val durationMs: Long) : Actor {
    override suspend fun act() {
        delay(durationMs)
    }
}

object Noop : Actor {
    override suspend fun act() = Unit
}
