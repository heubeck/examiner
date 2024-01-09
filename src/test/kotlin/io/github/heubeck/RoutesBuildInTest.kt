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
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test

@QuarkusTest
class RoutesBuildInTest {

    @Test
    fun `test all build in paths are functional`() {
        listOf("/metrics", "/.well-known/live", "/.well-known/ready").forEach {
            given()
                .`when`()
                .get(it)
                .then()
                .statusCode(200)
                .body(notNullValue())
        }
    }
}
