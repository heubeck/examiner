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

import io.micrometer.core.annotation.Counted
import io.micrometer.core.annotation.Timed
import io.quarkus.runtime.Quarkus
import java.lang.Math.random
import java.util.Base64
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.resteasy.reactive.Cache
import org.jboss.resteasy.reactive.ResponseHeader
import org.jboss.resteasy.reactive.ResponseStatus

const val GET_BASE_PATH = "examine"

@Path("/")
class Routes(
    val logger: Logger,
    @ConfigProperty(name = "echo-value") val echoValue: String,
    @ConfigProperty(name = "server-error-rate", defaultValue = "0") val defaultServerErrorRate: Int,
    @ConfigProperty(name = "request-delay", defaultValue = "") val defaultRequestDelay: String,
    @ConfigProperty(name = "favicon-base64") val faviconBase64: String
) {

    private val favicon by lazy {
        Base64.getDecoder().decode(faviconBase64)
    }

    @GET
    @ResponseStatus(301)
    @ResponseHeader(name = "location", value = [GET_BASE_PATH])
    fun noop() = Unit

    @GET
    @Path("favicon.ico")
    @Produces("image/x-icon")
    @Cache(maxAge = Int.MAX_VALUE)
    fun favicon() = favicon

    @GET
    @Path(GET_BASE_PATH)
    @Timed
    @Counted
    suspend fun get(
        @QueryParam("status") status: String?,
        @QueryParam("delay") delay: String?,
        @QueryParam("load") load: String?,
        @QueryParam("allocation") allocation: String?
    ) = get("", status, delay.or(defaultRequestDelay), load, allocation)

    @GET
    @Path("$GET_BASE_PATH/{path: .*}")
    @Timed
    @Counted
    suspend fun get(
        @PathParam("path") path: String,
        @QueryParam("status") status: String?,
        @QueryParam("delay") delay: String?,
        @QueryParam("load") load: String?,
        @QueryParam("allocation") allocation: String?
    ): Response {
        Actor.act(delay.or(defaultRequestDelay), load, allocation)
        return Response
            .status(status(status))
            .entity(echoValue.trim())
            .build()
    }

    @POST
    @Path("{path: .*}")
    @Timed
    @Counted
    suspend fun post(
        @PathParam("path") path: String,
        @QueryParam("status") status: String?,
        @QueryParam("delay") delay: String?,
        @QueryParam("load") load: String?,
        @QueryParam("allocation") allocation: String?,
        body: String
    ): Response {
        Actor.act(delay.or(defaultRequestDelay), load, allocation)
        return Response
            .status(status(status))
            .build()
    }

    @DELETE
    @Path("poison-pill")
    suspend fun poisonPill(
        @QueryParam("delay") delay: String?,
        @QueryParam("exit") exit: String?
    ) {
        DelayParser(delay.or(defaultRequestDelay)).takeIf { it.isDelayed() }?.run {
            Waiter(getDelay()).act()
        }
        Quarkus.asyncExit(exit?.trim()?.toIntOrNull() ?: 0)
    }

    private fun <T> T?.or(defaultValue: T?): T? =
        this ?: defaultValue

    private fun status(status: String?): Int =
        // provided status has precedence
        status?.toIntOrNull()?.takeIf { it in 200..599 }
        // configured error rate as fallback
            ?: if (random() * 100 <= defaultServerErrorRate) {
                500
            } else {
                200
            }
}
