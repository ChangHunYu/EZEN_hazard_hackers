package ezen.risk_buster.hazard_hackers.alert;

import ezen.risk_buster.hazard_hackers.country.Continent;
import ezen.risk_buster.hazard_hackers.country.ContinentRepository;
import ezen.risk_buster.hazard_hackers.country.Country;
import ezen.risk_buster.hazard_hackers.country.CountryRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;


@ActiveProfiles("test")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlertControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private AlertRepository alertRepository;

    @PersistenceContext
    EntityManager em;

    static Alert alert1;
    static Alert alert2;
    static Country country1;
    static Continent continent1;

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ContinentRepository continentRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        continent1 = continentRepository.save(
                Continent.builder()
                        .continentEngNm("asia")
                        .continentNm("아시아")
                        .build()
        );
        country1 = countryRepository.save(
                Country.builder()
                        .continent(continent1)
                        .countryEngName("Japan")
                        .countryIsoAlp2("JP")
                        .countryName("일본")
                        .flagDownloadUrl("http://일본.국기.url")
                        .mapDownloadUrl("http://일본.지도.url")
                        .build()
        );

        alert1 = alertRepository.save(
                Alert.builder()
                        .country(country1)
                        .level(1L)
                        .message("여행 경보 1단계")
                        .description("여행 경보가 1단계로 발령되었습니다.")
                        .regionType("해당 국가의 수도권")
                        .remark("야간에 외출은 가급적 삼가해주시기 바랍니다.")
                        .dangMapDownloadUrl("http://경보.지도1.url")
                        .build()
        );

        alert2 = alertRepository.save(
                Alert.builder()
                        .country(country1)
                        .level(2L)
                        .message("여행 경보 2단계")
                        .description("여행 경보가 2단계로 발령되었습니다.")
                        .regionType("국가 전체")
                        .remark("최대한 야외 활동을 자제해주시기 바랍니다.")
                        .dangMapDownloadUrl("http://경보.지도2.url")
                        .build()
        );
    }

    @Test
    @DisplayName("경보 생성 테스트")
    void create() {

        AlertRequestDto request = new AlertRequestDto(
                alert1.getCountry().getCountryEngName(),
                alert1.getLevel(),
                alert1.getMessage(),
                alert1.getDescription(),
                alert1.getRegionType(),
                alert1.getRemark(),
                alert1.getDangMapDownloadUrl(),
                LocalDate.now()
        );

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/alerts")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("경보 목록 조회 테스트")
    void findAll() {

        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/alerts")
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();

        List<AlertResponseDto> list = extract.jsonPath().getList("", AlertResponseDto.class);
        Assertions.assertThat(list.size()).isEqualTo(2);
        Assertions.assertThat(list.get(0).message()).isEqualTo(alert1.getMessage());
        Assertions.assertThat(list.get(1).message()).isEqualTo(alert2.getMessage());
    }

    @Test
    @DisplayName("경보 상세 조회 테스트")
    void findById() {

        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/alerts/" + alert1.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();

        AlertResponseDto object = extract.jsonPath().getObject("", AlertResponseDto.class);
        Assertions.assertThat(object.message()).isEqualTo(alert1.getMessage());
    }

    @Test
    @DisplayName("경보 수정 테스트")
    void update() {

        AlertRequestDto request = new AlertRequestDto(
                country1.getCountryEngName(),
                alert2.getLevel(),
                alert2.getMessage(),
                alert2.getDescription(),
                alert2.getRegionType(),
                alert2.getRemark(),
                alert2.getDangMapDownloadUrl(),
                LocalDate.now()
        );

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/alerts/" + alert2.getId())
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("경보 삭제 테스트")
    void delete() {

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .delete("/alerts/" + alert1.getId())
                .then()
                .statusCode(HttpStatus.OK.value()).extract();

        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/alerts/" + alert1.getId())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).extract();
    }
}