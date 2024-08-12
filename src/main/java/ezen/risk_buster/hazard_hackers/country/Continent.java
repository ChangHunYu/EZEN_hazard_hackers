package ezen.risk_buster.hazard_hackers.country;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Continent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String continent_eng_nm; // 대륙 영문 이름

    @Column(nullable = false, unique = true)
    private String continent_nm; // 대륙 한글 이름
}