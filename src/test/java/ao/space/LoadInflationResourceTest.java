package ao.space;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

import org.jboss.resteasy.reactive.RestResponse.StatusCode;


@QuarkusTest
public class LoadInflationResourceTest {

    @Test
    public void testImperativeGetInlfation() {
        given()
          .pathParam("id", 1)
          .when().get("/inflations/imperative/{id}")
          .then()
             .statusCode(StatusCode.OK)
             .body(containsString("Asia"));
    }

    @Test
    public void testImperativeGetInlfationNotFound() {
        given()
          .pathParam("id", Integer.MAX_VALUE)
          .when().get("/inflations/imperative/{id}")
          .then()
             .statusCode(StatusCode.NOT_FOUND);
    }

    @Test
    public void testReactiveGetInlfation() {
        given()
          .pathParam("id", 1)
          .when().get("/inflations/reactive/{id}")
          .then()
             .statusCode(StatusCode.OK)
             .body(containsString("Asia"));
    }

    @Test
    public void testReactiveGetInlfationNotFound() {
        given()
          .pathParam("id", Integer.MAX_VALUE)
          .when().get("/inflations/reactive/{id}")
          .then()
             .statusCode(StatusCode.NOT_FOUND);
    }

    @Test
    public void testVirtualThreadGetInlfation() {
        given()
          .pathParam("id", 1)
          .when().get("/inflations/virtual/{id}")
          .then()
             .statusCode(StatusCode.OK)
             .body(containsString("Asia"));
    }

    @Test
    public void testVirtualThreadGetInlfationNotFound() {
        given()
          .pathParam("id", Integer.MAX_VALUE)
          .when().get("/inflations/virtual/{id}")
          .then()
             .statusCode(StatusCode.NOT_FOUND);
    }
}
