package ao.space;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class LoadGreetingResourceTest {

    @Test
    public void testImperativeGreetingEndpoint() {
        given()
          .when().get("/greeting/imperative")
          .then()
             .statusCode(200)
             .body(is("Hello from Imperative"));
    }

    @Test
    public void testReactiveGreetingEndpoint() {
        given()
          .when().get("/greeting/reactive")
          .then()
             .statusCode(200)
             .body(is("Hello from Reactive"));
    }
}