package ezen.risk_buster.hazard_hackers;

import ezen.risk_buster.hazard_hackers.checklist.*;
import ezen.risk_buster.hazard_hackers.common.auth.JwtProvider;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;


import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemTest {

    @LocalServerPort
    int port;

    @Autowired
    ChecklistRepository checklistRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    JwtProvider jwtProvider;

    private Item item1;
    private Item item2;

    static Checklist checklist;
    static User user;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        user = userRepository.save(User.builder()
                .email("email@google.com")
                .password("password")
                .username("username")
                .build());
        checklist = checklistRepository.save(Checklist.builder()
                .user(user)
                .title("checkllist")
                .build());

        // 여기서 테스트 데이터를 데이터베이스에 저장하는 로직이 필요할 수 있습니다.
        item1 = itemRepository.save(
                Item.builder()
                        .isChecked(true)
                        .checklist(checklist)
                        .description("환전하기")
                        .build());
        item2 = itemRepository.save(
                Item.builder()
                        .isChecked(false)
                        .checklist(checklist)
                        .description("캐리어 구입")
                        .build());
    }

    @Test
    @DisplayName("아이템 생성 테스트")
    void createItem() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                false,
                checklist.getId(),
                "New Test Item");
        ExtractableResponse<Response> response = given().log().all()
                .contentType(ContentType.JSON)
                .body(itemRequestDto)
                .when()
                .post("/api/items")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        ItemResponseDto createdItem = response.jsonPath().getObject("", ItemResponseDto.class);
        assertThat(createdItem.description()).isEqualTo(itemRequestDto.description());
    }

    @Test
    @DisplayName("아이템 조회 테스트_성공")
    void getItem_Success() {
        String userEmail = user.getEmail();
        String token = jwtProvider.createToken(userEmail);
        ExtractableResponse<Response> response = given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token) // 사용자 인증 토큰 추가
                .when()
                .get("/api/items/{itemId}", item1.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        ItemResponseDto itemResponseDto = response.jsonPath()
                .getObject("", ItemResponseDto.class);
        assertThat(itemResponseDto.id()).isNotNull();
        assertThat(itemResponseDto.id()).isEqualTo(item1.getId());
        assertThat(itemResponseDto.description()).isEqualTo(item1.getDescription());
        assertThat(itemResponseDto.isChecked()).isEqualTo(item1.getIsChecked());
        assertThat(itemResponseDto.checklistId()).isEqualTo(checklist.getId());
    }

    @Test
    @DisplayName("아이템 조회 테스트_ 권한없음")
    void getItem_Unauthorized() {
        String unauthorizedUserEmail = "unauthorized@gmail.com";
        String token = jwtProvider.createToken(unauthorizedUserEmail);
        given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/items/{itemId}", item1.getId())
                .then().log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("아이템 조회 테스트 - 아이템 없음")
    void getItem_NotFound() {
        String token = jwtProvider.createToken("abc@gmail.com");
        given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/items/{itemId}", 9999L) // 존재하지 않는 아이템 ID
                .then().log().all()
                .statusCode(500);
    }


    @Test
    @DisplayName("체크리스트별 아이템 목록 조회 테스트")
    void getItemsByChecklistId() {
        String token = jwtProvider.createToken(user.getEmail());
        ExtractableResponse<Response> response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/api/items/checklist/{checklistId}", checklist.getId())
                        .then().log().all()
                        .statusCode(HttpStatus.OK.value())
                        .extract();

        List<ItemResponseDto> items = response.jsonPath()
                .getList("", ItemResponseDto.class);
        assertThat(items).hasSize(2);
        assertThat(items.get(0).description()).isEqualTo(item1.getDescription());
        assertThat(items.get(1).description()).isEqualTo(item2.getDescription());
    }

    @Test
    @DisplayName("체크리스트별 아이템 목록 조회 테스트_권한없음")
    void getItemsByChecklistId_Unauthorized() {
        String unauthorizedUserEmail = "unauthorized@gmail.com";
        String token = jwtProvider.createToken(unauthorizedUserEmail);
        given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/items/checklist/{checklistId}", checklist.getId())
                .then().log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("체크리스트별 아이템 목록 조회 테스트_체크리스트없음")
    void getItemByChecklistId_NotFound() {
        String token = jwtProvider.createToken(user.getEmail());
        given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/items/checklist/{checklistId}", 9999L)
                .then().log().all()
                .statusCode(500);
    }


    @Test
    @DisplayName("아이템 삭제 테스트_성공")
    void deleteItem_Success() {
        String token = jwtProvider.createToken(user.getEmail());
        given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/items/{itemId}", item1.getId())
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // 삭제 후 해당 아이템 조회 시 500 에러 확인
        given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/items/{itemId}", item1.getId())
                .then().log().all()
                .statusCode(500);

        assertThat(itemRepository.findById(item1.getId())).isEmpty();
    }

    @Test
    @DisplayName("아이템 삭제 테스트_권한없음")
    void deleteItem_Unauthorized() {
        String unauthorizedUserEmail = "unauthorized@gmail.com";
        String token = jwtProvider.createToken(unauthorizedUserEmail);
        given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/items/{itemId}", item1.getId())
                .then().log().all()
                .statusCode(500);

        assertThat(itemRepository.findById(item1.getId())).isPresent();
    }

    @Test
    @DisplayName("아이템 삭제 테스트_아이템없음")
    void deleteItem_NotFound() {
        String token = jwtProvider.createToken(user.getEmail());
        given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/items/{itemId}}", 9999L)
                .then().log().all()
                .statusCode(400);

    }


    @Test
    @DisplayName("아이템 업데이트 테스트_성공")
    void updateItemTest_Success() {
        String token = jwtProvider.createToken(user.getEmail());
        Long itemId = item1.getId();

        String newDescription = "새로운 설명";
        boolean newIschecked = true;

        ItemUpdateDto updateDto = new ItemUpdateDto(newDescription, newIschecked);

        Response response =
                given().log().all()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .body(updateDto)
                        .when()
                        .put("/api/items/" + itemId)
                        .then().log().all()
                        .statusCode(200)
                        .body("id", equalTo(itemId.intValue()))
                        .body("description", equalTo("새로운 설명"))
                        .body("isChecked", equalTo(true))
                        .extract()
                        .response();

        System.out.println("Response: " + response.asString());

        Item updatedItem = itemRepository.findById(itemId).orElseThrow();
        assertThat(updatedItem.getDescription()).isEqualTo(newDescription);
        assertThat(updatedItem.getIsChecked()).isEqualTo(newIschecked);
    }

    @Test
    @DisplayName("아이템 업데이트 테스트_권한없음")
    void updateItem_Unauthorized() {
        String unauthorizedUserEmail = "unauthorized@gmail.com";
        String token = jwtProvider.createToken(unauthorizedUserEmail);
        Long itemId = item1.getId();

        ItemUpdateDto updateDto = new ItemUpdateDto("UnauthorizedUpdate", true);

        given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(updateDto)
                .when()
                .put("/api/items/" + itemId)
                .then().log().all()
                .statusCode(500);

        Item unchangedItem = itemRepository.findById(itemId).orElseThrow();

        assertThat(unchangedItem.getDescription()).isEqualTo(item1.getDescription());
        assertThat(unchangedItem.getIsChecked()).isEqualTo(item1.getIsChecked());

    }

    @Test
    @DisplayName("아이템 업데이트 테스트_ 아이템없음")
    void updqteItem_NotFound(){
        String token = jwtProvider.createToken(user.getEmail());
        Long nonExistentItemId = 9999L;

        ItemUpdateDto updateDto = new ItemUpdateDto("Update Non-existent Item", true);

        given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(updateDto)
                .when()
                .put("/api/items/" +nonExistentItemId)
                .then().log().all()
                .statusCode(500);
    }
}
