package ezen.risk_buster.hazard_hackers.country;

import ezen.risk_buster.hazard_hackers.alert.Alert;
import ezen.risk_buster.hazard_hackers.alert.AlertRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

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

    @Autowired
    CountryRepository countryRepository;

    static Alert alert1;
    static Alert alert2;

    static Continent continent1;

    static Country country1;
    static Country country2;
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
        alert2 = alertRepository.save(
                Alert.builder()
                        .level(1L)
                        .message("위험경보1단계")
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
        ;
        country1 = countryRepository.save(
                Country.builder()
                        .alert(alert1)
                        .continent(continent1)
                        .countryEngName("Japan")
                        .countryIsoAlp2("JP")
                        .countryName("일본")
                        .flagDownloadUrl("http://일본.국기.url")
                        .mapDownloadUrl("http://일본.지도.url")
                        .build()
        );
        country2 = countryRepository.save(
                Country.builder()
                        .alert(alert1)
                        .continent(continent1)
                        .countryEngName("China")
                        .countryIsoAlp2("CH")
                        .countryName("중국")
                        .flagDownloadUrl("http://중국.국기.url")
                        .mapDownloadUrl("http://중국.지도.url")
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

    @Test
    @DisplayName("국가 id로 조회 테스트")
    void findById() {
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/country/" + country1.getId())
                .then().log().all()
                .statusCode(200).extract();
        CountryResponse object = extract.jsonPath().getObject("", CountryResponse.class);
        Assertions.assertThat(object.countryName()).isEqualTo(country1.getCountryName());
    }

    @Test
    @DisplayName("국가 목록 조회 테스트")
    void findByAll() {
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/country")
                .then().log().all()
                .statusCode(200).extract();
        List<CountryResponse> list = extract.jsonPath().getList("", CountryResponse.class);
        Assertions.assertThat(list.size()).isEqualTo(2);
        Assertions.assertThat(list.get(0).countryName()).isEqualTo(country1.getCountryName());
        Assertions.assertThat(list.get(1).countryName()).isEqualTo(country2.getCountryName());
    }

    @Test
    @DisplayName("국가 수정 테스트")
    void updateCountry() {
        CountryRequest request = new CountryRequest(
                continent1.getId(),
                alert2.getId(),
                country1.getCountryEngName(),
                country1.getCountryIsoAlp2(),
                country1.getCountryName(),
                "http://일본.국기2.url",
                "http://일본.지도2.url");
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/country/"+country1.getId())
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("국가 삭제 테스트")
    void deleteCountry() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .delete("/country/"+country1.getId())
                .then().log().all()
                .statusCode(200).extract();

        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/country/" + country1.getId())
                .then().log().all()
                .statusCode(500).extract();
    }
}