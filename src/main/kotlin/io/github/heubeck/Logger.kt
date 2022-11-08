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
import io.vertx.core.http.HttpServerRequest
import java.nio.charset.StandardCharsets
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Context
import javax.ws.rs.ext.Provider
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jetbrains.kotlin.util.prefixIfNot
import org.jetbrains.kotlin.util.suffixIfNot


@Provider
class Logger(
    @ConfigProperty(name = "log-methods") methods: String,
    @ConfigProperty(name = "log-headers") headers: String,
    @Context val request: HttpServerRequest
) : ContainerRequestFilter {

    val headers = headers.split(',').map { Regex(it.trim()) }
    val methods = methods.split(',').map { it.trim() }
    val bodyLogMethods = listOf("POST", "PUT", "PATCH")

    override fun filter(ctx: ContainerRequestContext) {
        val method = ctx.method
        if (method in methods) {
            val source = request.remoteAddress()
            val path = ctx.uriInfo.requestUri
            val headerValues = ctx.headers
                .filterKeys { name -> headers.any { name.matches(it) } }
                .entries
                .joinToString(
                    separator = System.lineSeparator()
                ) { "> ${it.key}: ${it.value.joinToString()}" }
                .prefixIfNot(System.lineSeparator())
                .suffixIfNot(System.lineSeparator())

            fun logIt(body: String = "") {
                val colon = if (headerValues.isNotBlank() || body.isNotBlank()) ":" else ""
                Log.infof("|%n%s %s from %s%s%s%s", method, path, source, colon, headerValues, body)
            }

            if (method in bodyLogMethods) {
                request.body().onSuccess {
                    logIt(it.toString(StandardCharsets.UTF_8))
                }
            } else {
                logIt()
            }
        }
    }

}
