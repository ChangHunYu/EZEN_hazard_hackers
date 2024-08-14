package ezen.risk_buster.hazard_hackers;

import ezen.risk_buster.hazard_hackers.checklist.*;
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
import org.springframework.test.context.jdbc.Sql;


import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Sql("/truncate.sql")
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
    @DisplayName("아이템 조회 테스트")
    void getItem() {
//        given().log().all()
//                .contentType(ContentType.JSON)
//                .when()
//                .get("/api/items/{itemId}", item1.getId())
//                .then().log().all()
//                .statusCode(HttpStatus.OK.value())
//                .body("id", notNullValue())
//                .body("description", equalTo(item1.getDescription()))
//                .body("isChecked", equalTo(item1.getIsChecked()))
//                .body("checklistId", equalTo(checklist.getId().intValue()))
//                .extract().response();
        ExtractableResponse<Response> response = given().log().all()
                .contentType(ContentType.JSON)
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
    }

    @Test
    @DisplayName("체크리스트별 아이템 목록 조회 테스트")
    void getItemsByChecklistId() {
        ExtractableResponse<Response> response = given().log().all()
                .contentType(ContentType.JSON)
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
    @DisplayName("아이템 삭제 테스트")
    void deleteItem() {
        given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/items/{itemId}", item1.getId())
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // 삭제 후 해당 아이템 조회 시 404 에러 확인
        given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/items/{itemId}", item1.getId())
                .then().log().all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


    @Test
    void updateItemTest() {
        // item1의 ID를 사용하여 업데이트 요청을 보냅니다.
        Long itemId = item1.getId();

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/items/" + itemId)
                .then()
                .statusCode(200) // 성공적인 응답을 기대합니다.
                .body("id", equalTo(itemId.intValue()))
                .body("description", equalTo("새로운 설명"))
                .body("isChecked", equalTo(true))
                .extract()
                .response();

        // 응답을 검증합니다.
        System.out.println("Response: " + response.asString());
    }

}
