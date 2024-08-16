package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.common.auth.JwtProvider;
import ezen.risk_buster.hazard_hackers.user.LoginRequest;
import ezen.risk_buster.hazard_hackers.user.LoginResponse;
import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChecklistControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChecklistRepository checklistRepository;

    @Autowired
    JwtProvider jwtProvider;  // JWT 토큰 생성을 위한 클래스

    @Autowired
    private EntityManager entityManager;


    static User testUser;
    static Checklist testChecklist;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testUser = userRepository.save(
                User.builder()
                        .username("testUser")
                        .email("test@example.com")
                        .password("123")
                        .build()
        );
        testChecklist = checklistRepository.save(
                Checklist.builder()
                        .user(testUser)
                        .title("Test Checklist")
                        .build()
        );
        testChecklist = checklistRepository.save(
                Checklist.builder()
                        .user(testUser)
                        .title("Test Checklist")
                        .items(new ArrayList<>())  // 빈 items 리스트 추가
                        .build()
        );
    }

    @Test
    @DisplayName("체크리스트 생성 테스트")
    void createChecklist() {
        String newTitle = "New Test Checklist";
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .queryParam("userId", testUser.getId())
                .queryParam("title", newTitle)
                .when()
                .post("/api/checklists")
                .then().log().all()
                .statusCode(200)
                .extract();

        ChecklistDto createdChecklist = response.jsonPath().getObject("", ChecklistDto.class);
        assertThat(createdChecklist.title()).isEqualTo(newTitle);
        assertThat(createdChecklist.userId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("JWT 토큰을 이용한 체크리스트 단일 조회 테스트")
    void getChecklistWithJwtToken() {
        // JWT 토큰 생성
        String token = jwtProvider.createToken(testUser.getEmail());

        // 체크리스트 조회 요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when()
                .get("/api/checklists/" + testChecklist.getId())
                .then().log().all()
                .statusCode(200)
                .extract();

        ChecklistDto responseDto = response.as(ChecklistDto.class);

        // 응답 검증
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.id()).isEqualTo(testChecklist.getId());
        assertThat(responseDto.title()).isEqualTo(testChecklist.getTitle());
        assertThat(responseDto.userId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("잘못된 JWT 토큰으로 체크리스트 조회 시 실패 테스트")
    void getChecklistWithInvalidJwtToken() {
        String invalidToken = "invalid.token.here";

        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                .when()
                .get("/api/checklists/" + testChecklist.getId())
                .then().log().all()
                .statusCode(500)
                .extract();
    }


    @Test
    @DisplayName("JWT 토큰 없이 체크리스트 조회 시 실패 테스트")
    void getChecklistWithoutJwtToken() {
        // 토큰 없이 체크리스트 조회 요청
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/checklists/" + testChecklist.getId())
                .then().log().all()
                .statusCode(500);// Unauthorized

    }

    @Test
    @DisplayName("JWT 토큰을 이용한 사용자의 모든 체크리스트 조회 테스트")
    void getChecklistsByUserIdWithJwtToken() {
        // 추가 체크리스트 생성
        Checklist anotherChecklist = checklistRepository.save(
                Checklist.builder()
                        .user(testUser)
                        .title("Another Test Checklist")
                        .items(new ArrayList<>())
                        .build()
        );

        // JWT 토큰 생성
        String token = jwtProvider.createToken(testUser.getEmail());

        // 사용자의 체크리스트 조회 요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when()
                .get("/api/checklists/user/" + testUser.getId())
                .then().log().all()
                .statusCode(200)
                .extract();

        List<ChecklistDto> responseDtos = response.jsonPath().getList(".", ChecklistDto.class);

        // 응답 검증
        assertThat(responseDtos).isNotNull();
        assertThat(responseDtos).hasSize(3);  // 세 개의 체크리스트가 있어야 함

        // 각 체크리스트 검증
        assertThat(responseDtos).allSatisfy(dto -> {
            assertThat(dto.userId()).isEqualTo(testUser.getId());
            assertThat(dto.items()).isEmpty();
            assertThat(dto.deleted()).isFalse();
        });

        // 체크리스트 제목 검증
        assertThat(responseDtos).extracting(ChecklistDto::title)
                .containsExactlyInAnyOrder("Test Checklist", "Test Checklist", "Another Test Checklist");

        // ID 검증
        assertThat(responseDtos).extracting(ChecklistDto::id)
                .containsExactlyInAnyOrder(1L, 2L, 3L);

        // 중복된 "Test Checklist" 제목 확인
        long testChecklistCount = responseDtos.stream()
                .filter(dto -> "Test Checklist".equals(dto.title()))
                .count();
        assertThat(testChecklistCount).isEqualTo(2);
    }


    @Test
    @DisplayName("JWT 토큰을 이용한 체크리스트 업데이트 테스트")
    void updateChecklist() {
        // Given
        String token = jwtProvider.createToken(testUser.getEmail());
        String newTitle = "Updated Test Checklist";
        List<Map<String, Object>> newItems = Arrays.asList(
                Map.of("description", "New Item 1", "isChecked", false),
                Map.of("description", "New Item 2", "isChecked", true)
        );
        Map<String, Object> updateDto = Map.of(
                "title", newTitle,
                "items", newItems
        );

        // When
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(updateDto)
                .when()
                .put("/api/checklists/{checklistId}", testChecklist.getId())
                .then().log().all()
                .extract();

        // Then
        assertThat(response.statusCode()).isEqualTo(200);
        ChecklistDto updatedChecklist = response.jsonPath().getObject("", ChecklistDto.class);
        assertThat(updatedChecklist.title()).isEqualTo(newTitle);
        assertThat(updatedChecklist.items()).hasSize(2);
        assertThat(updatedChecklist.items().get(0).description()).isEqualTo("New Item 1");
        assertThat(updatedChecklist.items().get(0).isChecked()).isFalse();
        assertThat(updatedChecklist.items().get(1).description()).isEqualTo("New Item 2");
        assertThat(updatedChecklist.items().get(1).isChecked()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 체크리스트 업데이트 시도 테스트")
    void updateNonExistentChecklist() {
        // Given
        String token = jwtProvider.createToken(testUser.getEmail());
        String newTitle = "Updated Non-Existent Checklist";
        List<Map<String, Object>> newItems = List.of(
                Map.of("description", "New Item", "isChecked", false)
        );
        Map<String, Object> updateDto = Map.of(
                "title", newTitle,
                "items", newItems
        );
        Long nonExistentChecklistId = 9999L;

        // When
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(updateDto)
                .when()
                .put("/api/checklists/{checklistId}", nonExistentChecklistId)
                .then().log().all()
                .extract();

        // Then
        assertThat(response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("권한 없는 사용자의 체크리스트 업데이트 시도 테스트")
    void updateChecklistWithoutPermission() {
        // Given
        User anotherUser = userRepository.save(
                User.builder()
                        .username("anotherUser")
                        .email("another@example.com")
                        .password("456")
                        .build()
        );
        String tokenOfAnotherUser = jwtProvider.createToken(anotherUser.getEmail());

        String newTitle = "Unauthorized Update";
        List<Map<String, Object>> newItems = List.of(
                Map.of("description", "Unauthorized Item", "isChecked", false)
        );
        Map<String, Object> updateDto = Map.of(
                "title", newTitle,
                "items", newItems
        );

        // When
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAnotherUser)
                .body(updateDto)
                .when()
                .put("/api/checklists/{checklistId}", testChecklist.getId())
                .then().log().all()
                .extract();

        // Then
        assertThat(response.statusCode()).isEqualTo(500);

        // 체크리스트가 변경되지 않았는지 확인
        Checklist unchangedChecklist = checklistRepository.findById(testChecklist.getId()).orElseThrow();
        assertThat(unchangedChecklist.getTitle()).isEqualTo("Test Checklist");
    }

//    @Test
//    @DisplayName("권한 없는 사용자의 체크리스트 업데이트 시도 테스트")
//    void updateChecklistWithoutPermission() {
//        // ... (기존 코드)
//
//        // 데이터베이스에서 체크리스트를 조회하여 변경되지 않았음을 확인
//        Checklist unchangedChecklist = entityManager.createQuery(
//                        "SELECT c FROM Checklist c LEFT JOIN FETCH c.items WHERE c.id = :id", Checklist.class)
//                .setParameter("id", testChecklist.getId())
//                .getSingleResult();
//
//        assertThat(unchangedChecklist.getTitle()).isEqualTo("Test Checklist");
//        assertThat(unchangedChecklist.getItems()).isEmpty();
//    }

    @Test
    @DisplayName("JWT 토큰을 이용한 체크리스트 삭제 테스트")
    void deleteChecklistWithJwtToken() {
        // JWT 토큰 생성
        String token = jwtProvider.createToken(testUser.getEmail());

        // 체크리스트 삭제 요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when()
                .delete("/api/checklists/" + testChecklist.getId())
                .then().log().all()
                .statusCode(204)  // No Content
                .extract();

        // 응답 검증
        assertThat(response.statusCode()).isEqualTo(204);

        // 데이터베이스에서 체크리스트가 삭제되었는지 확인
        assertThat(checklistRepository.findById(testChecklist.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 체크리스트 삭제 시도 테스트")
    void deleteNonExistentChecklist() {
        // JWT 토큰 생성
        String token = jwtProvider.createToken(testUser.getEmail());

        // 존재하지 않는 체크리스트 ID
        Long nonExistentId = 9999L;

        // 존재하지 않는 체크리스트 삭제 요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when()
                .delete("/api/checklists/" + nonExistentId)
                .then().log().all()
                .extract();

        // 응답 검증
        assertThat(response.statusCode()).isEqualTo(204);  // Not Found
    }


//    @Test
//    @DispLayName("프로필 조회 테스트")
//    void getcurrentser {
//
//        User저장된_유저= userRepositony.save（유저1);
//        em. clear();
//
//        LoginRequest login = new LoginRequest (유저1. getEmail, rawPassword);
//        ExtractableResponse<Response> extract = RestAssured
//                .given().log().all()
//                .contentType (ContentType.JSON)
//                .body(login)
//                .when()
//                .post("/users/login")
//                .then.log().all()
//                .statusCode (200). extract();
//        LoginResponse token = extract.as (LoginResponse.class);
//
//        ExtractableResponse<Response> extract1 = RestAssured
//                .given().log().all
//                .contentType (ContentType.JSON)
//                .header (HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
//                .body (login)
//                .when ()
//                .get("/users/me")
//                .statusCode (200) .extractO:
//        UserResponseDTO responseDTo = extract1.as(UserResponseDTO.class);
//        Assertions.assertThat(responseDTO).isNotNulLO:
//        Assertions.assertThat(responseDTO.name).isEqualTo(유저1.getUsernameO);
//        Assertions.assertThat (responseDTO.email).isEqualTo(유저1.getEmait);
//    }
}


