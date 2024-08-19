package ezen.risk_buster.hazard_hackers.itinerary;


import ezen.risk_buster.hazard_hackers.alert.Alert;
import ezen.risk_buster.hazard_hackers.alert.AlertRepository;
import ezen.risk_buster.hazard_hackers.common.auth.SecurityUtils;
import ezen.risk_buster.hazard_hackers.country.Continent;
import ezen.risk_buster.hazard_hackers.country.ContinentRepository;
import ezen.risk_buster.hazard_hackers.country.Country;
import ezen.risk_buster.hazard_hackers.country.CountryRepository;
import ezen.risk_buster.hazard_hackers.user.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    ItineraryService itineraryService;


    static User user;

    static UserCountry userCountry;

    static Country country;

    static Itinerary itinerary;


    static Alert alert;

    static Continent continent;

    //로그인이 필요함
    static String rawPassword = "password1";
    static String hasedPassword1;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void setUp(){
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

        itinerary = itineraryRepository.save(
                Itinerary.builder()
                        .user(user)
                        .userCountry(userCountry)
                        .title("testItinerary")
                        .description("testDescription")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(7))
                        .build()
        );
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
        ItineraryResponse savedItinerary = itineraryService.findById(user.getEmail(),userCountry.getId());
        assertThat(savedItinerary.id()).isEqualTo(userCountry.getId());
        assertThat(savedItinerary.id()).isEqualTo(country.getId());
    }



    @Test
    @DisplayName("일정 id로 조회테스트")
    void findById(){
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

        //일정 조회API 호출
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .get("/itinerary/" + itinerary.getId())
                .then().log().all()
                .statusCode(200).extract();
        ItineraryResponse response = extract.as(ItineraryResponse.class);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.title()).isEqualTo(itinerary.getTitle());

        // 관심 국가 설정 검증 추가
        Assertions.assertThat(response.userCountryEngName()).isNotNull();
        Assertions.assertThat(response.userCountryEngName()).isEqualTo(itinerary.getUserCountry().getCountry().getCountryEngName());
    }

    @Test
    @DisplayName("일정 목록조회")
    void findByAll(){
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
        Assertions.assertThat(list.get(0).title()).isEqualTo(itinerary.getTitle());
        Assertions.assertThat(list.get(0).userEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("일정 수정")
    void updateItinerary(){
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

        //일정 수정
        ItineraryRequest request = new ItineraryRequest(
                userCountry.getId(),
                //추가
                country.getId(),
                "title 수정",
                LocalDate.now(),
                LocalDate.now(),
                "description 수정");

        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .put("/itinerary/" + itinerary.getId())
                .then().log().all()
                .statusCode(200).extract();
        ItineraryResponse response = extract.jsonPath().getObject(
                "", ItineraryResponse.class);

        //수정된 일정 결과
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.title()).isEqualTo(request.title());
        Assertions.assertThat(response.description()).isEqualTo(request.description());
    }


    @Test
    @DisplayName("일정 삭제")
    void deleteItinerary(){
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

        ItineraryRequest request2 = new ItineraryRequest(
                userCountry.getId(),
                country.getId() ,
                "d",
                LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(7),
                "defd"
        );

        //삭제 전 일정 조회
        Itinerary itineraryBeforeDelete = itineraryRepository.findByIdAndIsDeletedFalse(itinerary.getId());

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .get("/itinerary/" + itinerary.getId())
                .then().log().all()
                .statusCode(200).extract();

        //일정 삭제
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .delete("/itinerary/"+itinerary.getId())
                .then().log().all()
                .statusCode(200).extract();

        //삭제 후 일정 재조회
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .when()
                .get("/itinerary/" + itinerary.getId())
                .then().log().all()
                .statusCode(500).extract();

        Assertions.assertThat(itineraryBeforeDelete).isNotNull();
        // soft-delete 적용으로 id로 찾아짐
        Itinerary itineraryAfterDelete = itineraryRepository.findById(itinerary.getId()).orElse(null);
        Assertions.assertThat(itineraryAfterDelete).isNotNull();
        // soft-delete 적용으로 Isdeleted = false 여서 못찾음
        Assertions.assertThat(itineraryRepository.findByIdAndIsDeletedFalse(itinerary.getId())).isNull();
    }
}