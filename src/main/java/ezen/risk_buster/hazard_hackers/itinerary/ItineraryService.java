package ezen.risk_buster.hazard_hackers.itinerary;

import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserCountry;
import ezen.risk_buster.hazard_hackers.user.UserCountryRepostiory;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItineraryService {

    private final ItineraryRepository itineraryRepository;
    private final UserRepository userRepository;
    private final UserCountryRepostiory userCountryRepostiory;

    public ItineraryService(ItineraryRepository itineraryRepository, UserRepository userRepository, UserCountryRepostiory userCountryRepostiory) {
        this.itineraryRepository = itineraryRepository;
        this.userRepository = userRepository;
        this.userCountryRepostiory = userCountryRepostiory;
    }



    //일정생성
    @Transactional
    public  ItineraryResponse create(ItineraryRequest request, String userEmail) {

        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }

        UserCountry userCountry = userCountryRepostiory.findById(request.userCountryId()).orElse(null);
        if (userCountry == null) {
            throw new IllegalArgumentException("userCountry not found");
        }

        Itinerary itinerary = itineraryRepository.save(
                Itinerary.builder()
                        .user(user)
                        .userCountry(userCountry)
                        .title(request.title())
                        .description(request.description())
                        .startDate(request.startDate())
                        .endDate(request.endDate())
                        .build()
        );

        return new ItineraryResponse(
                itinerary.getId(),
                itinerary.getUser().getEmail(),
                itinerary.getUserCountry().getCountry().getCountryName(),
                itinerary.getTitle(),
                itinerary.getStartDate(),
                itinerary.getEndDate(),
                itinerary.getDescription()
        );
    }

    //일정 단일 조회
    public ItineraryResponse findById(String userEmail,Long id) {

        //유저 확인
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new EntityNotFoundException("유저를 찾을 수 없습니다."+ userEmail));

        //일정 검증
        Itinerary itinerary = itineraryRepository.findByIdAndIsDeletedFalse(id);

        if (itinerary == null) {
            throw new NoSuchElementException("id에 해당하는 일정이 없음");
        }

        // 찾은 일정의 유저의 id == 유저의 id
        if(!user.getId().equals(itinerary.getUser().getId())) {
            throw new IllegalArgumentException("본인 일정이 아닙니다.");
        }

        return new ItineraryResponse(
                itinerary.getId(),
                itinerary.getUser().getEmail(),
                itinerary.getUserCountry().getCountry().getCountryName(),
                itinerary.getTitle(),
                itinerary.getStartDate(),
                itinerary.getEndDate(),
                itinerary.getDescription()
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
                        .userCountryName(i.getUserCountry().getCountry().getCountryName())
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
                .userCountryName(updatedItinerary.getUserCountry().getCountry().getCountryName())
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
