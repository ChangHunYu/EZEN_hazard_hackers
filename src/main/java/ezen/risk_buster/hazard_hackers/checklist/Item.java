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

    @Column(nullable = false)
    private Boolean isChecked;

    public Item(Object o, Checklist checklist, String description, boolean b) {

    }

    public static Object builder() {
        return null;
    }
}