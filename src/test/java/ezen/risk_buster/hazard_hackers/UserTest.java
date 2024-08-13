package ezen.risk_buster.hazard_hackers;

import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import ezen.risk_buster.hazard_hackers.user.UserResponseDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
}
