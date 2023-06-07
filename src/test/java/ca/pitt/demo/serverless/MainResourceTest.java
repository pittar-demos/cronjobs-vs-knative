package ca.pitt.demo.serverless;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class MainResourceTest {

    @Test
    public void testInvokeEndpoint() {
        given()
          .when().get("/")
          .then()
             .statusCode(200)
             .body(is("Default POST message."));
    }

}