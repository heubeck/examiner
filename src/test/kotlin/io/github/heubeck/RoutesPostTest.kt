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

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import java.util.UUID
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class RoutesPostTest {

    @Test
    fun `test different paths without further parameter`() {
        listOf("/", "/path", "/path/path/", "/path/path/path").forEach {
            given()
                .`when`()
                .body(UUID.randomUUID().toString())
                .header("X-Forwarded-For", "me")
                .post(it)
                .then()
                .statusCode(200)
                .body(`is`(""))
        }
    }
}
