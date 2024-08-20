package ezen.risk_buster.hazard_hackers.alert;

import ezen.risk_buster.hazard_hackers.common.BaseEntity;
import ezen.risk_buster.hazard_hackers.country.Country;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Alert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long level;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(nullable = false)
    private String message;

    @Column(length = 4000)
    private String description;

    private String dangMapDownloadUrl; // 위험 지도 + 정보

    private String regionType; // 위험 지역 ex) 전체

    private String remark; // 위험 지역 ex) 전 지역

    private LocalDate writtenDate;

    public void updateMessage(String message) {
        this.message = message;
    }
}