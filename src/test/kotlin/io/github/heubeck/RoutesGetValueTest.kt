package io.github.heubeck

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.QuarkusTestProfile
import io.quarkus.test.junit.TestProfile
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

class RoutesValueOverride : QuarkusTestProfile {
    override fun getConfigOverrides() = mutableMapOf("echo-value" to "this is just a test")
}

@QuarkusTest
@TestProfile(RoutesValueOverride::class)
class RoutesGetValueTest {

    @Test
    fun `test configured testValue`() {
        given()
            .`when`()
            .get("/")
            .then()
            .body(`is`("this is just a test"))
    }

}
