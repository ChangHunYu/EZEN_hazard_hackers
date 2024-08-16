package ezen.risk_buster.hazard_hackers.itinerary;

import ezen.risk_buster.hazard_hackers.checklist.Checklist;
import ezen.risk_buster.hazard_hackers.common.BaseEntity;
import ezen.risk_buster.hazard_hackers.country.Country;
import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserCountry;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Itinerary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_country_id", nullable = false)
    private UserCountry userCountry;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String description;

    @OneToOne(mappedBy = "itinerary", cascade = CascadeType.ALL)
    private Checklist checklist;

    //setter로 저장해서 리턴하면 수정할수있는 부분으로 혼동할수 있기 때문에 update함수를 만들어서 사용
    public void update(ItineraryRequest request) {
        this.title = request.title();
        this.startDate = request.startDate();
        this.endDate = request.endDate();
        this.description = request.description();
    }

    public void addCheklist(Checklist checklist) {
        this.checklist = checklist;
    }
}