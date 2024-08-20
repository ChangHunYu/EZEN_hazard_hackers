package ezen.risk_buster.hazard_hackers.checklist;

import java.util.Arrays;
import java.util.List;

public enum CheckListType {
    TRAVEL("여행 준비 체크리스트", Arrays.asList(
            "여권",
            "항공권",
            "숙소 예약 확인서",
            "여행자 보험",
            "현금 및 신용카드",
            "충전기 및 어댑터",
            "필수 의약품",
            "세면도구",
            "갈아입을 옷",
            "카메라",
            "비상 연락처"
    ));

    private final String title;
    private final List<String> items;

    CheckListType(String title, List<String> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getItems() {
        return items;
    }
}