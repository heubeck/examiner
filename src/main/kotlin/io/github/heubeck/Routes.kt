package io.github.heubeck

import io.quarkus.logging.Log
import java.lang.Math.random
import java.util.concurrent.atomic.AtomicLong
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response
import kotlin.math.atan
import kotlin.math.tan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.microprofile.config.inject.ConfigProperty

@Path("/")
class Routes(
    @ConfigProperty(name = "echo-value") val echoValue: String
) {

    @GET
    @Path("{path: .*}")
    suspend fun get(
        @PathParam("path") path: String,
        @QueryParam("status") status: String?,
        @QueryParam("delay") delay: String?,
        @QueryParam("load") load: String?
    ): Response {
        Actor.act(delay, load)
        return Response
            .status(status(status))
            .entity(echoValue.trim())
            .build()
    }

    @POST
    @Path("{path: .*}")
    suspend fun post(
        @PathParam("path") path: String,
        @QueryParam("status") status: String?,
        @QueryParam("delay") delay: String?,
        @QueryParam("load") load: String?,
        body: String
    ): Response {
        Log.info("POST '$path':\n$body")
        Actor.act(delay, load)
        return Response
            .status(status(status))
            .build()
    }

    private fun status(status: String?): Int =
        status?.toIntOrNull()?.takeIf { it in 200..599 } ?: 200
}

sealed interface Actor {
    suspend fun act()

    companion object {
        suspend fun act(delay: String?, load: String?) {
            val delay = DelayParser(delay)
            val loadWeight = load?.trim()?.toLongOrNull()?.takeIf { it > 0 }
            when {
                loadWeight != null && delay.isDelayed() -> LoadProducer(delay.getDelay(), loadWeight)
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

class Waiter(private val durationMs: Long) : Actor {
    override suspend fun act() {
        delay(durationMs)
    }
}

object Noop : Actor {
    override suspend fun act() = Unit
}

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
