package ezen.risk_buster.hazard_hackers.country;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContinentTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Create Continent")
    void create() {
        ContinentRequest request = new ContinentRequest(
                "Asia",
                "아주"
        );
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/continent")
                .then().log().all()
                .statusCode(200).extract();
        ContinentResponse response = extract.as(ContinentResponse.class);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.continentNm()).isEqualTo(request.continentNm());
        Assertions.assertThat(response.continentEngNm()).isEqualTo(request.continentEngNm());
    }
}


