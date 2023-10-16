package ao.space;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

import org.jboss.resteasy.reactive.RestResponse.StatusCode;


@QuarkusTest
public class LoadInflationResourceTest {

    @Test
    public void testImperativeGetInflation() {
        given()
          .pathParam("id", 1)
          .when().get("/inflation/imperative/{id}")
          .then()
             .statusCode(StatusCode.OK)
             .body(containsString("Asia"));
    }

    @Test
    public void testImperativeGetInflationNotFound() {
        given()
          .pathParam("id", Integer.MAX_VALUE)
          .when().get("/inflation/imperative/{id}")
          .then()
             .statusCode(StatusCode.NOT_FOUND);
    }

    @Test
    public void testReactiveGetInflation() {
        given()
          .pathParam("id", 1)
          .when().get("/inflation/reactive/{id}")
          .then()
             .statusCode(StatusCode.OK)
             .body(containsString("Asia"));
    }

    @Test
    public void testReactiveGetInflationNotFound() {
        given()
          .pathParam("id", Integer.MAX_VALUE)
          .when().get("/inflation/reactive/{id}")
          .then()
             .statusCode(StatusCode.NOT_FOUND);
    }

    @Test
    public void testVirtualThreadGetInflation() {
        given()
          .pathParam("id", 1)
          .when().get("/inflation/virtual/{id}")
          .then()
             .statusCode(StatusCode.OK)
             .body(containsString("Asia"));
    }

    @Test
    public void testVirtualThreadGetInflationNotFound() {
        given()
          .pathParam("id", Integer.MAX_VALUE)
          .when().get("/inflation/virtual/{id}")
          .then()
             .statusCode(StatusCode.NOT_FOUND);
    }
}
