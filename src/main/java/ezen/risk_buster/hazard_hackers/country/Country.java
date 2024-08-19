package ezen.risk_buster.hazard_hackers.country;

import ezen.risk_buster.hazard_hackers.alert.Alert;
import ezen.risk_buster.hazard_hackers.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Country extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<Alert> alertList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "continent_id", nullable = false)
    private Continent continent;

    @Column(nullable = false, unique = true)
    private String countryEngName; // 국가 영어 이름

    @Column(nullable = false, unique = true)
    private String countryIsoAlp2; // 국가 iso_alp2

    @Column(nullable = false, unique = true)
    private String countryName; // 국가 한글 이름

    private String flagDownloadUrl; // 국기

    private String mapDownloadUrl; // 지도
}