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
                .post(it)
                .then()
                .statusCode(200)
                .body(`is`(""))
        }
    }
}
