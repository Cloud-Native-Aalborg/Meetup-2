package org.mejlholm;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class WisdomResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/wisdom/random")
          .then()
             .statusCode(200);
    }

}