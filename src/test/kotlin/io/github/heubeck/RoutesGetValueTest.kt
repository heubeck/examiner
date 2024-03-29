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

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.QuarkusTestProfile
import io.quarkus.test.junit.TestProfile
import io.restassured.RestAssured.given
import kotlin.system.measureTimeMillis
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

class RoutesValueOverride : QuarkusTestProfile {
    override fun getConfigOverrides() =
        mutableMapOf(
            "echo-value" to "this is just a test",
            "server-error-rate" to "100",
            "request-delay" to "500..1000"
        )
}

@QuarkusTest
@TestProfile(RoutesValueOverride::class)
class RoutesGetValueTest {

    @Test
    fun `test configured testValue`() {
        val delay = measureTimeMillis {
            given()
                .`when`()
                .get("/$GET_BASE_PATH")
                .then()
                .statusCode(500)
                .body(`is`("this is just a test"))
        }
        assert(delay > 500) {"Configured request delay not respected"}
    }

}
