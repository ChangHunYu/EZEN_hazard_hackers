package ezen.risk_buster.hazard_hackers.country;

import ezen.risk_buster.hazard_hackers.alert.Alert;
import ezen.risk_buster.hazard_hackers.alert.AlertRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class CountryTest {
    @LocalServerPort
    int port;

    @Autowired
    AlertRepository alertRepository;

    @Autowired
    ContinentRepository continentRepository;

    static Alert alert1;

    static Continent continent1;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        alert1 = alertRepository.save(
                Alert.builder()
                        .level(0L)
                        .message("message")
                        .description("")
                        .remark("")
                        .regionType("")
                        .build()
        );
        continent1 = continentRepository.save(
                Continent.builder()
                        .continent_eng_nm("asia")
                        .continent_nm("아시아")
                        .build()
        );
    }

    @Test
    @DisplayName("국가 생성 테스트")
    void createCountry() {
        CountryRequest request = new CountryRequest(
                continent1.getId(),
                alert1.getId(),
                "Repulic of Korea",
                "KR",
                "대한민국",
                "http://대한민국.국기.url",
                "http://대한민국.지도.url");
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/country")
                .then().log().all()
                .statusCode(200);
    }
}