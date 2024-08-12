package ezen.risk_buster.hazard_hackers.alert;

import ezen.risk_buster.hazard_hackers.common.BaseEntity;
import ezen.risk_buster.hazard_hackers.country.Country;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String message;

    private String description;

    private String dangMapDownloadUrl; // 위험 지도 + 정보

    private String regionType; // 위험 지역 ex) 전체

    private String remark; // 위험 지역 ex) 전 지역
}