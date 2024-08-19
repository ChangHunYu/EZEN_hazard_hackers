package ezen.risk_buster.hazard_hackers.itinerary;

import ezen.risk_buster.hazard_hackers.country.Country;
import ezen.risk_buster.hazard_hackers.country.CountryRepository;
import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserCountry;
import ezen.risk_buster.hazard_hackers.user.UserCountryRepostiory;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import ezen.risk_buster.hazard_hackers.checklist.Checklist;
import ezen.risk_buster.hazard_hackers.checklist.ChecklistService;
import ezen.risk_buster.hazard_hackers.user.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ItineraryService {

    private final ItineraryRepository itineraryRepository;
    private final UserRepository userRepository;
    private final UserCountryRepostiory userCountryRepostiory;
    private final CountryRepository countryRepository;
    private final ChecklistService checklistService;

    public ItineraryService(ItineraryRepository itineraryRepository, UserRepository userRepository, UserCountryRepostiory userCountryRepostiory, ChecklistService checklistService, CountryRepository countryRepository) {
        this.itineraryRepository = itineraryRepository;
        this.userRepository = userRepository;
        this.userCountryRepostiory = userCountryRepostiory;
        this.countryRepository = countryRepository;
        this.checklistService = checklistService;
    }



    //일정생성
    @Transactional
    public ItineraryResponse create(ItineraryRequest request, String userEmail) {

        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }

        // UserCountry가 존재하는지 확인
        UserCountry userCountry = userCountryRepostiory.findByUserAndCountry_Id(user, countryId).orElse(null);
        if (userCountry == null) {
            // UserCountry가 존재하지 않으면 새로 생성
            Country country = countryRepository.findByIdAndIsDeletedFalse(countryId);
            userCountry = UserCountry.builder()
                    .user(user)
                    .country(country)
                    .build();

            userCountry = userCountryRepostiory.save(userCountry);// 새 UserCountry 저장
        }

        Itinerary itinerary = Itinerary.builder()
                .user(user)
                .userCountry(userCountry)
                .title(request.title())
                .description(request.description())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
        String cheklistTitle = itinerary.getUserCountry() +
                " 여행 (" +
                itinerary.getStartDate() +
                " ~ " +
                itinerary.getEndDate() + " )";
        Checklist checklist = checklistService.createChecklist(user.getId(), cheklistTitle);
        itinerary.addCheklist(checklist);
        itinerary = itineraryRepository.save(itinerary);

        return new ItineraryResponse(
                itinerary.getId(),
                itinerary.getUser().getEmail(),
                itinerary.getUserCountry().getCountry().getCountryEngName(),
                itinerary.getTitle(),
                itinerary.getStartDate(),
                itinerary.getEndDate(),
                itinerary.getDescription(),
                itinerary.getChecklist().getId(),
                itinerary.getChecklist().getTitle()
        );
    }

    //일정 단일 조회
    public ItineraryResponse findById(String userEmail, Long id) {

        //유저 확인
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));

        //일정 검증
        Itinerary itinerary = itineraryRepository.findByIdAndIsDeletedFalse(id);

        if (itinerary == null) {
            throw new NoSuchElementException("id에 해당하는 일정이 없습니다");
        }

        // 찾은 일정의 유저의 id == 유저의 id
        if (!user.getId().equals(itinerary.getUser().getId())) {
            throw new IllegalArgumentException("본인 일정이 아닙니다");
        }

        return new ItineraryResponse(
                itinerary.getId(),
                itinerary.getUser().getEmail(),
                itinerary.getUserCountry().getCountry().getCountryEngName(),
                itinerary.getTitle(),
                itinerary.getStartDate(),
                itinerary.getEndDate(),
                itinerary.getDescription(),
                itinerary.getChecklist().getId(),
                itinerary.getChecklist().getTitle()
        );
    }

    //일정 목록 조회
    public List<ItineraryResponse> findAll(String userEmail) {
        //유저 확인
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new EntityNotFoundException("유저를 찾을 수 없습니다."+ userEmail));


        //목록 조회 검증
        List<Itinerary> itineraries = itineraryRepository.findAllByUserEmailAndUserIsDeletedFalseAndIsDeletedFalse(userEmail);


        return itineraries.stream()
                .map(i -> ItineraryResponse.builder()
                        .userEmail(i.getUser().getEmail())
                        .userCountryEngName(i.getUserCountry().getCountry().getCountryEngName())
                        .title(i.getTitle())
                        .description(i.getDescription())
                        .startDate(i.getStartDate())
                        .endDate(i.getEndDate())
                        .build())
                .toList();
    }


    //일정 수정
    @SneakyThrows
    @Transactional
    public ItineraryResponse update(Long id, ItineraryRequest request, String userEmail) {
        //유저 확인
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new EntityNotFoundException("유저를 찾을 수 없습니다."+ userEmail));

        //일정수정 가능한지 검증
        Itinerary itinerary = itineraryRepository.findByIdAndIsDeletedFalse(id);
        if (!user.getEmail().equals(userEmail)) {
            throw new AccessDeniedException("일정 수정할 권한이 없습니다.");
        }
        if (itinerary == null) {
            throw new EntityNotFoundException("Itinerary Not Found");
        }

        UserCountry userCountry = userCountryRepostiory.findById(request.userCountryId())
                .orElseThrow(()-> new EntityNotFoundException("UserCountry Not Found"));

        //일정 수정
        itinerary.update(request);

        //수정된걸 저장
        Itinerary updatedItinerary = itineraryRepository.save(itinerary);

        // ItineraryResponse 생성 및 반환
        return ItineraryResponse.builder()
                .id(updatedItinerary.getId())
                .userEmail(updatedItinerary.getUser().getEmail())
                .userCountryEngName(updatedItinerary.getUserCountry().getCountry().getCountryEngName())
                .title(updatedItinerary.getTitle())
                .startDate(updatedItinerary.getStartDate())
                .endDate(updatedItinerary.getEndDate())
                .description(updatedItinerary.getDescription())
                .build();
    }

    //일정 삭제
    @Transactional
    public void deleteItinerary(Long id, String userEmail) {
        //유저 확인
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new EntityNotFoundException("유저를 찾을 수 없습니다."+ userEmail));

        //유저가 일정을 생성한게 맞는지 일정 조회
        Itinerary deleteItinerary = itineraryRepository.findByIdAndIsDeletedFalse(id);

        if (!user.getEmail().equals(userEmail)) {
            throw new EntityNotFoundException("일정 삭제할 권한이 없습니다.");
        }
        if (deleteItinerary == null) {
            throw new EntityNotFoundException("일정이 없습니다.");
        }

        deleteItinerary.softDelete();
        itineraryRepository.save(deleteItinerary);
    }

}
