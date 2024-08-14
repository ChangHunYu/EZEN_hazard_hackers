package ezen.risk_buster.hazard_hackers.checklist;

import ezen.risk_buster.hazard_hackers.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    @Column(nullable = false)
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isChecked = false;

}