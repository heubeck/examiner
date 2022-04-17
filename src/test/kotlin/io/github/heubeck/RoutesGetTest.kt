package io.github.heubeck

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import kotlin.system.measureTimeMillis
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class RoutesGetTest {

    @Test
    fun `test different paths without further parameter`() {
        listOf("/", "/path", "/path/path", "/path/path/path").forEach {
            given()
                .`when`()
                .get(it)
                .then()
                .statusCode(200)
                .body(`is`(""))
        }
    }

    @Test
    fun `test different status codes on root path`() {
        listOf(200, 201, 204, 400, 401, 418, 500, 501, 503).forEach {
            given()
                .`when`()
                .queryParam("status", it)
                .get("/")
                .then()
                .statusCode(it)
                .body(`is`(""))
        }
    }

    @Test
    fun `test exact delays`() {
        listOf(0, 10, 100, 500, 1000, 1500).forEach {
            val duration = measureTimeMillis {
                given()
                    .`when`()
                    .queryParam("delay", it)
                    .get("/")
                    .then()
                    .body(`is`(""))
            }
            assert(duration > it) { "Respond did not last at least $it" }
        }
    }

    @Test
    fun `test range delays`() {
        listOf(0 to 100, 100 to 500, 500 to 1000, 1000 to 1500).forEach { (min, max) ->
            val dots = ".".repeat((1..10).random())
            val duration = measureTimeMillis {
                given()
                    .`when`()
                    .queryParam("delay", "$min$dots$max")
                    .get("/")
                    .then()
                    .body(`is`(""))
            }
            assert(duration >= min) { "Respond did not last at least $min" }
            assert(duration < max + 50) { "Respond did not respond before $max" }
        }
    }

    @Test
    fun `test invalid delays`() {
        listOf("asldfkj", "3414..", "..342", "23..234..23423", "asfd..fdas").forEach {
            val duration = measureTimeMillis {
                given()
                    .`when`()
                    .queryParam("delay", it)
                    .get("/")
                    .then()
                    .statusCode(200)
                    .body(`is`(""))
            }
            assert(duration < 50) { "Respond did last $duration" }
        }
    }

    @Test
    fun `test invalid status`() {
        listOf("asldfkj", "999", " ", "0", "one", "199").forEach {
            given()
                .`when`()
                .queryParam("status", it)
                .get("/")
                .then()
                .statusCode(200)
                .body(`is`(""))
        }
    }

    @Test
    fun `test load`() {
        (-150..150).forEach {
            given()
                .`when`()
                .queryParam("load", it)
                .queryParam("delay", "1")
                .get("/")
                .then()
                .statusCode(200)
                .body(`is`(""))
        }
    }
}
