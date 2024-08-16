package ezen.risk_buster.hazard_hackers;

import ezen.risk_buster.hazard_hackers.common.auth.JwtProvider;
import ezen.risk_buster.hazard_hackers.common.auth.SecurityUtils;
import ezen.risk_buster.hazard_hackers.user.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest {

    @LocalServerPort
    int port;

    static String rawPassword = "password1";
    static String hasedPassword1;
    static User 유저1;
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        hasedPassword1 = SecurityUtils.sha256Encrypt("password1");
        유저1 = new User("young", "abc@gmail.com", hasedPassword1);
    }

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("회원가입")
    void createUser() {
        //given - User 객체를 생성한다.
        User 저장된_유저 = userRepository.save(유저1);
        em.clear();

        //when & then - 검증
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new SignupRequest(유저1.getUsername(), 유저1.getEmail(), 유저1.getPassword()))
                .when()
                .post("/users/signup")
                .then().log().all()
                .statusCode(200);

        Assertions.assertThat(저장된_유저).isNotNull();
    }

    @Test
    @DisplayName("로그인 테스트")
    void login() {
        // given - 유저 생성 및 저장
        User 저장된_유저 = userRepository.save(유저1);
        em.clear();

        //when & then - 로그인 후 토큰 발급
        LoginRequest login = new LoginRequest(저장된_유저.getEmail(), rawPassword);
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200).extract();

        Assertions.assertThat(login).isNotNull();
        Assertions.assertThat(login.userEmail()).isEqualTo(저장된_유저.getEmail());
        Assertions.assertThat(hasedPassword1).isEqualTo(저장된_유저.getPassword());
    }

    @Test
    @DisplayName("프로필 조회 테스트")
    void getCurrentUser() {
        //given
        // User 저장
        User 저장된_유저 = userRepository.save(유저1);
        em.clear();

        // 로그인 후 토큰 발급
        LoginRequest login = new LoginRequest(유저1.getEmail(), rawPassword);
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200).extract();
        LoginResponse token = extract.as(LoginResponse.class);

        //when
        // 프로필 조회
        ExtractableResponse<Response> extract1 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .body(login)
                .when()
                .get("/users/me")
                .then().log().all()
                .statusCode(200).extract();
        UserResponseDTO responseDTO = extract1.as(UserResponseDTO.class);
        Assertions.assertThat(responseDTO).isNotNull();
        Assertions.assertThat(responseDTO.name()).isEqualTo(유저1.getUsername());
        Assertions.assertThat(responseDTO.email()).isEqualTo(유저1.getEmail());
    }

    @Test
    @DisplayName("프로필 수정 테스트")
    void update() {
        //given - User 생성 및 저장
        User 저장된_유저 = userRepository.save(유저1);
        em.clear();

        // 로그인 후 토큰 발급
        LoginRequest login = new LoginRequest(저장된_유저.getEmail(), rawPassword);
        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200).extract();
        LoginResponse loginResponse = extract.as(LoginResponse.class);
        String token = loginResponse.accessToken();

        //수정할 데이터
        String newName = "updatedYoung";
        String newEmail = "newemail@gmail.com";
        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO(newName ,newEmail);
        //프로필 수정 요청
        ExtractableResponse<Response> updateResponse = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestDTO)
                .when()
                .put("/users/"+저장된_유저.getId())
                .then().log().all()
                .statusCode(200)
                .extract();

        //데이터베이스에 업데이트 되었는지 확인
        em.clear();
        User updatedUser = userRepository.findByEmail(newEmail)
                .orElseThrow(() -> new AssertionError("Updated user not found"));
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(newName);
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("비밀번호 변경")
    void updatePassword() {
        //given - 회원가입
        String userEmail = "abc123@gmail.com";
        String 기존_비밀번호 = "abc123!";
        String 새_비밀번호 = "efg456@";
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new SignupRequest(userEmail, "young", 기존_비밀번호))
                .when()
                .post("/users/signup")
                .then().log().all()
                .statusCode(200);

        //로그인 후 토큰 발급
        String accessToken = 로그인(userEmail, 기존_비밀번호)
                .jsonPath()
                .getString("accessToken");

        //비밀번호 변경
        RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(new ChangePasswordRequest(
                        기존_비밀번호,
                        새_비밀번호
                ))
                .when()
                .patch("/users/me")
                .then().log().all()
                .statusCode(200);

        //then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest(userEmail, 기존_비밀번호))
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(500);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest(userEmail, 새_비밀번호))
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200);

//        //검증
//        ExtractableResponse<Response> 기존_비밀번호_응답 = 로그인(userEmail, 기존_비밀번호);
//        Assertions.assertThat(기존_비밀번호_응답.statusCode()).isEqualTo(500);
//
//        ExtractableResponse<Response> 새_비밀번호_응답 = 로그인(userEmail, 새_비밀번호);
//        Assertions.assertThat(새_비밀번호_응답.statusCode()).isEqualTo(200);
    }

    //static 로그인 구현
    private static ExtractableResponse<Response> 로그인(String userEmail, String 비밀번호){
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest(
                        userEmail,
                        비밀번호
                ))
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200)
                .extract();
    }

    @Test
    @DisplayName("회원탈퇴")
    void delete() {
        //given - User 생성 및 저장
        User 저장된_유저 = userRepository.save(유저1);
        em.clear();

        // 로그인
        LoginRequest login = new LoginRequest(저장된_유저.getEmail(), rawPassword);
        ExtractableResponse<Response> extract1 = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200).extract();

        //user 삭제
            ExtractableResponse<Response> extract2 = RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/users/"+저장된_유저.getId())
                    .then().log().all()
                    .statusCode(500).extract();
    }
}
