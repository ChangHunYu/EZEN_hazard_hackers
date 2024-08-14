package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChecklistControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChecklistRepository checklistRepository;

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

        ChecklistDto createdChecklist = response.jsonPath().getObject("",ChecklistDto.class);
        assertThat(createdChecklist.title()).isEqualTo(newTitle);
        assertThat(createdChecklist.userId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("체크리스트 조회 테스트")
    void getChecklist() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/checklists/" + testChecklist.getId())
                .then().log().all()
                .statusCode(200)
                .extract();

        ChecklistDto retrievedChecklist = response.jsonPath().getObject("",ChecklistDto.class);
        assertThat(retrievedChecklist.userId()).isEqualTo(testChecklist.getId());
        assertThat(retrievedChecklist.title()).isEqualTo(testChecklist.getTitle());
    }

    @Test
    @DisplayName("사용자별 체크리스트 목록 조회 테스트")
    void getChecklistsByUserId() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/checklists/user/" + testUser.getId())
                .then().log().all()
                .statusCode(200)
                .extract();

        List<ChecklistDto> checklists = response.jsonPath().getList(".", ChecklistDto.class);
        assertThat(checklists).hasSize(1);
    }

    @Test
    @DisplayName("체크리스트 업데이트 테스트")
    void updateChecklist() {
        // Given
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
                .body(updateDto)
                .when()
                .put("/api/checklists/{checklistId}", testChecklist.getId())
                .then().log().all()
                .statusCode(200)
                .extract();

        // Then
        ChecklistDto updatedChecklist = response.jsonPath().getObject("", ChecklistDto.class);
        assertThat(updatedChecklist.title()).isEqualTo(newTitle);
        assertThat(updatedChecklist.items()).hasSize(2);
        assertThat(updatedChecklist.items().get(0).description()).isEqualTo("New Item 1");
        assertThat(updatedChecklist.items().get(0).isChecked()).isFalse();
        assertThat(updatedChecklist.items().get(1).description()).isEqualTo("New Item 2");
        assertThat(updatedChecklist.items().get(1).isChecked()).isTrue();
    }

    @Test
    @DisplayName("체크리스트 삭제 테스트")
    void deleteChecklist() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/checklists/" + testChecklist.getId())
                .then().log().all()
                .statusCode(204);

        // 삭제 후 조회 시 404 에러 확인
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/checklists/" + testChecklist.getId())
                .then().log().all()
                .statusCode(500);
    }
}


