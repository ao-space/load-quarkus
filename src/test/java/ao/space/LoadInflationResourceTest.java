package ao.space;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;


@QuarkusTest
public class LoadInflationResourceTest {

    @Test
    public void testImperativeGetInlfation() {
        given()
          .pathParam("id", 1)
          .when().get("/inflations/imperative/{id}")
          .then()
             .statusCode(200)
             .body(containsString("Asia"));
    }

    @Test
    public void testReactiveGetInlfation() {
        given()
          .pathParam("id", 1)
          .when().get("/inflations/reactive/{id}")
          .then()
             .statusCode(200)
             .body(containsString("Asia"));
    }

    @Test
    public void testVirtualThreadGetInlfation() {
        given()
          .pathParam("id", 1)
          .when().get("/inflations/virtual/{id}")
          .then()
             .statusCode(200)
             .body(containsString("Asia"));
    }
}
