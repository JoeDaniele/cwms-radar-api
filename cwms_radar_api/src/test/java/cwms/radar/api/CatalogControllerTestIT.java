package cwms.radar.api;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

import fixtures.RadarApiSetupCallback;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.matcher.RestAssuredMatchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Tag("integration")
@ExtendWith(RadarApiSetupCallback.class)
public class CatalogControllerTestIT {


    @BeforeAll
    public static void before_all(){

    }

    @Test
    public void test_no_aliased_results_returned() {
        given().accept("application/json;version=2")
        .queryParam("office", "SPK")
        .get("/catalog/TIMESERIES").then().assertThat()
        .statusCode(is(200))
        .body("$",hasKey("total"))
        .body("total",is(4))
        .body("entries.size()",is(4));
    }
}
