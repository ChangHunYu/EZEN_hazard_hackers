package ezen.risk_buster.hazard_hackers.user;

import ezen.risk_buster.hazard_hackers.alert.Alert;
import ezen.risk_buster.hazard_hackers.alert.AlertRepository;
import ezen.risk_buster.hazard_hackers.common.auth.JwtProvider;
import ezen.risk_buster.hazard_hackers.common.auth.SecurityUtils;
import ezen.risk_buster.hazard_hackers.country.*;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserCountryControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    UserCountryRepostiory userCountryRepostiory;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AlertRepository alertRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    ContinentRepository continentRepository;

    @Autowired
    JwtProvider jwtProvider;

    @PersistenceContext
    EntityManager em;

    static User user1;
    static Continent continent1;
    static Country country1;
    static Country country2;
    static Alert alert1;
    static Alert alert2;
    static UserCountry userCountry1;
    static UserCountry userCountry2;

    static String rawPassword1 = "abc123!";
    static String rawPassword2 = "!cba321";
    static String hashedPassword1;
    static String hashedPassword2;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        hashedPassword1 = SecurityUtils.sha256Encrypt(rawPassword1);
        hashedPassword2 = SecurityUtils.sha256Encrypt(rawPassword2);
        user1 = userRepository.save(
                User.builder()
                        .username("Tae Hee Lee")
                        .email("abc@gmail.com")
                        .password(hashedPassword1)
                        .build()
        );
        continent1 = continentRepository.save(
                Continent.builder()
                        .continentEngNm("asia")
                        .continentNm("아시아")
                        .build()
        );
        country1 = countryRepository.save(
                Country.builder()
                        .alertList(List.of())
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
                        .alertList(List.of())
                        .continent(continent1)
                        .countryEngName("China")
                        .countryIsoAlp2("CN")
                        .countryName("중국")
                        .flagDownloadUrl("http://중국.국기.url")
                        .mapDownloadUrl("http://중국.지도.url")
                        .build()
        );
        alert1 = alertRepository.save(
                Alert.builder()
                        .level(1L)
                        .country(country1)
                        .message("여행 경보 1단계")
                        .description("여행 경보가 1단계로 발령되었습니다.")
                        .regionType("해당 국가의 수도권")
                        .remark("야간에 외출은 가급적 삼가해주시기 바랍니다.")
                        .dangMapDownloadUrl("http://경보.지도1.url")
                        .writtenDate(LocalDate.now())
                        .build()
        );
        alert2 = alertRepository.save(
                Alert.builder()
                        .level(2L)
                        .country(country2)
                        .message("여행 경보 2단계")
                        .description("여행 경보가 2단계로 발령되었습니다.")
                        .regionType("해당 국가의 수도권")
                        .remark("야간에 외출은 가급적 삼가해주시기 바랍니다.")
                        .dangMapDownloadUrl("http://경보.지도2.url")
                        .writtenDate(LocalDate.now())
                        .build()
        );
        userCountry1 = userCountryRepostiory.save(
                UserCountry.builder()
                        .user(user1)
                        .country(country1)
                        .build()
        );
        userCountry2 = userCountryRepostiory.save(
                UserCountry.builder()
                        .user(user1)
                        .country(country2)
                        .build()
        );

    }

    @Test
    @DisplayName("로그인한 유저의 관심 국가 생성 테스트")
    void create() {

        String token = jwtProvider.createToken(user1.getEmail());

        UserCountryRequestDto request = new UserCountryRequestDto(
                userCountry1.getUser().getEmail(),
                userCountry1.getCountry().getId()
        );

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(request)
                .when()
                .post("/UserCountries")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("로그인한 유저의 관심 국가 목록 조회 테스트")
    void findAll() {

        String token = jwtProvider.createToken(user1.getEmail());

        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when()
                .get("/UserCountries")
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();

        List<UserCountryResponseDto> list = extract.jsonPath().getList("", UserCountryResponseDto.class);
        Assertions.assertThat(list.size()).isEqualTo(2);
        Assertions.assertThat(list.get(0).countryId()).isEqualTo(country1.getId());
        Assertions.assertThat(list.get(1).countryId()).isEqualTo(country2.getId());
    }

    @Test
    @DisplayName("로그인한 유저의 관심 국가 상세 조회 테스트")
    void findOne() {

        String token = jwtProvider.createToken(user1.getEmail());

        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when()
                .get("/UserCountries/" + userCountry1.getUser().getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();

        UserCountryResponseDto object = extract.jsonPath().getObject("", UserCountryResponseDto.class);
        Assertions.assertThat(object.countryId()).isEqualTo(userCountry1.getCountry().getId());
    }

    @Test
    @DisplayName("로그인한 유저의 관심 국가 수정 테스트")
    void update() {

        String token = jwtProvider.createToken(user1.getEmail());

        UserCountryRequestDto request = new UserCountryRequestDto(
                userCountry1.getUser().getEmail(),
                userCountry1.getCountry().getId()
        );

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(request)
                .when()
                .put("/UserCountries/" + userCountry1.getUser().getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("로그인한 유저의 관심 국가 삭제 테스트")
    void delete() {

        String token = jwtProvider.createToken(user1.getEmail());

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when()
                .delete("/UserCountries/" + userCountry1.getUser().getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();

        ExtractableResponse<Response> extract = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/UserCountries/" + userCountry1.getUser().getId())
                .then().log().all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).extract();
    }
}