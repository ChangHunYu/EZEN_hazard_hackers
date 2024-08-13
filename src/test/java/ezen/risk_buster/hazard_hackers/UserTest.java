package ezen.risk_buster.hazard_hackers;

import ezen.risk_buster.hazard_hackers.user.LoginRequest;
import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import ezen.risk_buster.hazard_hackers.user.UserResponseDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("User 생성 테스트")
    void createUser() {
        //given - User 객체를 생성한다.
        User 유저1 = new User(1L, "young", "abc@gmail.com", "password1");

        //when - 생성한 User 객체를 저장한다.
        User 저장된_유저 = userRepository.save(유저1);
        em.clear();

        //then - 검증
        assertThat(저장된_유저.getUsername()).isEqualTo(유저1.getUsername());
        assertThat(저장된_유저.getEmail()).isEqualTo(유저1.getEmail());
        assertThat(저장된_유저.getPassword()).isEqualTo(유저1.getPassword());
    }

    @Test
    @DisplayName("로그인 테스트")
    void login() {
        // given
        User 유저1 = userRepository.save(new User(1L, "young", "abc@gmail.com", "password1"));
        LoginRequest loginRequest = new LoginRequest(유저1.getEmail(), 유저1.getPassword());

        //when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/users/login")
                .then().log().all()
                .statusCode(200);
    }
    
    @Test
    @DisplayName("User 조회 테스트")
    void findUserByUserId() {
        //given - User 객체를 생성 + 저장한다.
        User 유저1 = userRepository.save(new User(1L, "young", "abc@gmail.com", "password1"));

        //when - 저장된 User의 userId로 조회한다.
        User 찾은_User = userRepository.findByIdAndIsDeletedFalse(유저1.getId());

        //then - 찾은 User가 null이 아닌지 검증한다.
        //       찾은 User의 userId가 저장 시 입력한 userId와 일치하는지 검증한다.
        assertThat(찾은_User).isNotNull();
        assertThat(찾은_User.getId()).isEqualTo(유저1.getId());
    }

    @Test
    @DisplayName("프로필 수정 테스트")
    void update() {
        // given
        User 유저1 = userRepository.save(new User(1L, "young", "abc@gmail.com", "password1"));
        UserResponseDTO request = new UserResponseDTO(
                유저1.getId(),
                유저1.getUsername(),
                유저1.getEmail(),
                유저1.getPassword()
        );

        //when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/users/"+유저1.getId())
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("회원탈퇴 테스트")
    void delete() {
        User 유저1 = userRepository.save(new User(1L, "young", "abc@gmail.com", "password1"));
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .delete("/users/"+유저1.getId())
                .then().log().all()
                .statusCode(200).extract();

        ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/users/" + 유저1.getId())
                .then().log().all()
                .statusCode(500).extract();
    }
}
