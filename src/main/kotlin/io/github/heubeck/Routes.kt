package io.github.heubeck

import io.quarkus.logging.Log
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response
import kotlinx.coroutines.delay
import org.eclipse.microprofile.config.inject.ConfigProperty

@Path("/")
class Routes(
    @ConfigProperty(name = "echo-value") val echoValue: String
) {
    private val number = Regex("^\\d+$")
    private val range = Regex("^\\d+\\.+\\d+$")
    private val rangeDelimiter = Regex("\\.+")

    private suspend fun delay(delay: String?) {
        delay?.trim()?.apply {
            when {
                matches(number) -> {
                    delay(toLong())
                }
                matches(range) -> {
                    val (min, max) = split(rangeDelimiter).map { it.toLong() }
                    delay((min..max).random())
                }
            }
        }
    }

    private fun status(status: String?): Int =
        status?.toIntOrNull()?.takeIf { it in 200..599 } ?: 200

    @GET
    @Path("{path: .*}")
    suspend fun get(
        @PathParam("path") path: String,
        @QueryParam("status") status: String?,
        @QueryParam("delay") delay: String?
    ): Response {
        delay(delay)
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
        body: String
    ): Response {
        Log.info("POST '$path':\n$body")
        delay(delay)
        return Response
            .status(status(status))
            .build()
    }
}
