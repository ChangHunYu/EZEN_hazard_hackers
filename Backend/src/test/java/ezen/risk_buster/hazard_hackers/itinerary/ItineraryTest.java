package ezen.risk_buster.hazard_hackers.itinerary;


import ezen.risk_buster.hazard_hackers.alert.Alert;
import ezen.risk_buster.hazard_hackers.alert.AlertRepository;
import ezen.risk_buster.hazard_hackers.checklist.Checklist;
import ezen.risk_buster.hazard_hackers.checklist.ChecklistDto;
import ezen.risk_buster.hazard_hackers.checklist.ChecklistRepository;
import ezen.risk_buster.hazard_hackers.common.auth.SecurityUtils;
import ezen.risk_buster.hazard_hackers.country.*;
import ezen.risk_buster.hazard_hackers.user.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItineraryTest {
    @LocalServerPort
    int port;

    @Autowired
    ItineraryRepository itineraryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserCountryRepostiory userCountryRepostiory;

    @Autowired
    AlertRepository alertRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    ContinentRepository continentRepository;

    @Autowired
    ChecklistRepository checklistRepository;

    @Autowired
    ItineraryService itineraryService;


    static User user;

    static UserCountry userCountry;

    static Country country;

    static Checklist checklist;

    static Alert alert;

    static Continent continent;

    //로그인이 필요함
    static String rawPassword = "password1";
    static String hasedPassword1;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void setUp() {
        hasedPassword1 = SecurityUtils.sha256Encrypt(rawPassword);
        RestAssured.port = port;
        user = userRepository.save(User.builder()
                .email("user1@gamil.com")
                .username("test")
                .password(hasedPassword1)
                .build());
        continent = continentRepository.save(Continent.builder()
                .continentEngNm("asia")
                .continentNm("아시아")
                .build());
        country = countryRepository.save(Country.builder()
                .mapDownloadUrl("url")
                .continent(continent)
                .countryEngName("Korea")
                .countryIsoAlp2("KR")
                .countryName("한국")
                .flagDownloadUrl("url")
                .mapDownloadUrl("url")
                .build());
        alert = alertRepository.save(Alert.builder()
                .country(country)
                .level(1L)
                .dangMapDownloadUrl("url")
                .description("alert")
                .remark("test")
                .message("alert")
                .regionType("test")
                .build());

        userCountry = userCountryRepostiory.save(UserCountry.builder()
                .country(country)
                .user(user)
                .build());
    }

    @Test
    @DisplayName("일정 생성 테스트")
    void createItinerary() {
        //로그인 후 토큰 발급
        ItineraryRequest request = new ItineraryRequest(
                userCountry.getId(),
                country.getId(),
                "d",
                LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(7),
                "defd"
        );
        //when
        ItineraryResponse response = itineraryService.create(request, user.getEmail());

        //then
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("d");
        assertThat(response.description()).isEqualTo("defd");

        //응답으로 받은 ID를 사용하여 지정된 여행 일정을 조회 (관심국가를 설정했을때 )
        ItineraryResponse savedItinerary = itineraryService.findById(user.getEmail(), response.id());
        assertThat(savedItinerary.id()).isEqualTo(response.id());
        assertThat(savedItinerary.userCountryEngName()).isEqualTo(response.userCountryEngName());
    }

    @Test
    @DisplayName("일정 id로 조회테스트")
    void findById() {
        //로그인 후 토큰 발급
        LoginRequest login = new LoginRequest(user.getEmail(), rawPassword);
        ExtractableResponse<Response> extract1 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200).extract();
        LoginResponse token = extract1.as(LoginResponse.class);

        ItineraryRequest itineraryRequest = new ItineraryRequest(
                userCountry.getId(),
                country.getId(),
                "일정",
                LocalDate.parse("2024-08-20"),
                LocalDate.parse("2024-09-09"),
                "설명"
        );
        ItineraryResponse itinerary = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .body(itineraryRequest)
                .when()
                .post("/itinerary")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getObject("", ItineraryResponse.class);

        //일정 조회API 호출
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .get("/itinerary/" + itinerary.id())
                .then().log().all()
                .statusCode(200).extract();
        ItineraryResponse response = extract.as(ItineraryResponse.class);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.title()).isEqualTo(itinerary.title());

    }

    @Test
    @DisplayName("일정 목록조회")
    void findByAll() {
        //로그인 후 토큰 발급
        LoginRequest login = new LoginRequest(user.getEmail(), rawPassword);
        ExtractableResponse<Response> extract1 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200).extract();
        LoginResponse token = extract1.as(LoginResponse.class);

        //일정 생성
        ItineraryRequest itineraryRequest = new ItineraryRequest(
                userCountry.getId(),
                country.getId(),
                "일정",
                LocalDate.parse("2024-08-20"),
                LocalDate.parse("2024-09-09"),
                "설명"
        );
        ItineraryResponse itinerary = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .body(itineraryRequest)
                .when()
                .post("/itinerary")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getObject("", ItineraryResponse.class);


        //목록검증
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .get("/itinerary")
                .then().log().all()
                .statusCode(200).extract();

        List<ItineraryResponse> list = extract.jsonPath().getList(
                "", ItineraryResponse.class);
        Assertions.assertThat(list.size()).isEqualTo(1);
        Assertions.assertThat(list.get(0).title()).isEqualTo(itinerary.title());
        Assertions.assertThat(list.get(0).userEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("일정 수정")
    void updateItinerary() {
        //로그인 후 토큰 발급
        LoginRequest login = new LoginRequest(user.getEmail(), rawPassword);
        ExtractableResponse<Response> extract1 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200).extract();
        LoginResponse token = extract1.as(LoginResponse.class);

        ItineraryRequest itineraryRequest = new ItineraryRequest(
                userCountry.getId(),
                country.getId(),
                "일정",
                LocalDate.parse("2024-08-20"),
                LocalDate.parse("2024-09-09"),
                "설명"
        );
        ItineraryResponse 일정 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .body(itineraryRequest)
                .when()
                .post("/itinerary")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getObject("", ItineraryResponse.class);

        //일정 수정
        ItineraryRequest 수정요청 = new ItineraryRequest(
                1L,
                1L,
                "title 수정",
                LocalDate.parse("2024-09-20"),
                LocalDate.parse("2024-10-09"),
                "description 수정");
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(수정요청)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .put("/itinerary/" + 일정.id())
                .then().log().all()
                .statusCode(200);

        // 수정 후 조회
        ItineraryResponse 조회한일정 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .get("/itinerary/" + 일정.id())
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getObject("", ItineraryResponse.class);

        //수정된 일정 결과
        Assertions.assertThat(조회한일정).isNotNull();
        Assertions.assertThat(조회한일정.title()).isEqualTo(수정요청.title());
        Assertions.assertThat(조회한일정.description()).isEqualTo(수정요청.description());
    }


    @Test
    @DisplayName("일정 삭제")
    void deleteItinerary() {
        //로그인
        LoginRequest login = new LoginRequest(user.getEmail(), rawPassword);
        ExtractableResponse<Response> extract1 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200).extract();
        LoginResponse token = extract1.as(LoginResponse.class);

        ItineraryRequest itineraryRequest = new ItineraryRequest(
                userCountry.getId(),
                country.getId(),
                "일정",
                LocalDate.parse("2024-08-20"),
                LocalDate.parse("2024-09-09"),
                "설명"
        );
        ItineraryResponse 일정 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .body(itineraryRequest)
                .when()
                .post("/itinerary")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getObject("", ItineraryResponse.class);

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .get("/itinerary/" + 일정.id())
                .then().log().all()
                .statusCode(200).extract();

        //일정 삭제
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .delete("/itinerary/" + 일정.id())
                .then().log().all()
                .statusCode(200);

        //삭제 후 일정 재조회
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .get("/itinerary/" + 일정.id())
                .then().log().all()
                .statusCode(500);
    }
}