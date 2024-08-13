package ezen.risk_buster.hazard_hackers.itinerary;


import ezen.risk_buster.hazard_hackers.alert.Alert;
import ezen.risk_buster.hazard_hackers.alert.AlertRepository;
import ezen.risk_buster.hazard_hackers.country.Continent;
import ezen.risk_buster.hazard_hackers.country.ContinentRepository;
import ezen.risk_buster.hazard_hackers.country.Country;
import ezen.risk_buster.hazard_hackers.country.CountryRepository;
import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserCountry;
import ezen.risk_buster.hazard_hackers.user.UserCountryRepostiory;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @BeforeEach
    void setUp(){
        RestAssured.port = port;
        user = userRepository.save(User.builder()
                .email("user1@gamil.com")
                        .username("test")
                        .password("password")
                .build());
        alert = alertRepository.save(Alert.builder()
                        .level(1L)
                        .dangMapDownloadUrl("url")
                        .description("")
                        .remark("")
                        .message("")
                        .regionType("")
                .build());
        continent = continentRepository.save(Continent.builder()
                .continent_eng_nm("")
                .continent_nm("")
                .build());
        country = countryRepository.save(Country.builder()
                        .alert(alert)
                        .mapDownloadUrl("")
                        .continent(continent)
                        .countryEngName("")
                        .countryIsoAlp2("")
                        .countryName("")
                        .flagDownloadUrl("")
                        .mapDownloadUrl("")
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
        ItineraryRequest request = new ItineraryRequest(
                user.getId(),
                userCountry.getId(),
                "d",
                LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(7),
                "defd"
        );

        ItineraryResponse response = itineraryService.createItineraty(request);
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("d");
        assertThat(response.description()).isEqualTo("defd");
    }

    @Test
    @DisplayName("일정 id로 조회테스트")
    void findById(){
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/itinerary/" + itinerary.getId())
                .then().log().all()
                .statusCode(200).extract();
        ItineraryResponse response = extract.as(ItineraryResponse.class);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.title()).isEqualTo(itinerary.getTitle());
    }

    @Test
    @DisplayName("일정 목록조회")
    void findByAll(){
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/itineraries")
                .then().log().all()
                .statusCode(200).extract();
        List<ItineraryResponse> list = extract.jsonPath().getList(
                "", ItineraryResponse.class);
        Assertions.assertThat(list.size()).isEqualTo(1);
        Assertions.assertThat(list.get(0).title()).isEqualTo(itinerary.getTitle());
 //       Assertions.assertThat(list.get(1).title()).isEqualTo(itinerary2.getTitle());
    }

    @Test
    @DisplayName("일정 수정")
    void updateItinerary(){
        ItineraryRequest request = new ItineraryRequest(
                continent.getId(),
                alert.getId(),
                itinerary.getTitle(),
                LocalDate.now(),
                LocalDate.now(),
                itinerary.getDescription());
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/itineray/"+itinerary.getId())
                .then().log().all()
                .statusCode(200);
    }
}