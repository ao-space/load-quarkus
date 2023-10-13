package ao.space;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class LoadStaticFilesRouteTest {

    @Test
    public void testImperativeGreetingEndpoint() {
        given()
          .when().get("/static/README.md")
          .then()
             .statusCode(200)
             .body(containsString("load-quarkus"));
    }
    
}
