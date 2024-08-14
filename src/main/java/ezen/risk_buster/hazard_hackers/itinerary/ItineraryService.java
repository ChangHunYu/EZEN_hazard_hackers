package ezen.risk_buster.hazard_hackers.itinerary;

import ezen.risk_buster.hazard_hackers.user.User;
import ezen.risk_buster.hazard_hackers.user.UserCountry;
import ezen.risk_buster.hazard_hackers.user.UserCountryRepostiory;
import ezen.risk_buster.hazard_hackers.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
    public  ItineraryResponse create(ItineraryRequest request) {
        User user = userRepository.findById(request.userId()).orElse(null);
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
    public ItineraryResponse findById(Long id) {
        Itinerary itinerary = itineraryRepository.findById(id).orElse(null);

        if (itinerary == null) {
            throw new NoSuchElementException("id에 해당하는 일정이 없음");
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
    public List<ItineraryResponse> findAll() {
        List<Itinerary> itineraries = itineraryRepository.findAll();
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
    @Transactional
    public ItineraryResponse update(Long id, ItineraryResponse request) {
        Itinerary itinerary = itineraryRepository.findByIdAndIsDeletedFalse(id);
        if (itinerary == null) {
            throw new EntityNotFoundException("Itinerary Not Found");
        }

        Itinerary savedItinerary = itineraryRepository.save(itinerary);

        return ItineraryResponse.builder()
                .id(itinerary.getId())
                .userEmail(itinerary.getUser().getEmail())
                .userCountryName(request.userCountryName())
                .title(itinerary.getTitle())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .description(itinerary.getDescription())
                .build();
    }

    //일정 삭제
    @Transactional
    public void deleteItinerary(Long id) {
        Itinerary deleteItinerary = itineraryRepository.findById(id)
                .orElse(null);
        if (deleteItinerary == null) {
            throw new EntityNotFoundException("Itinerary Not Found");
        }

        deleteItinerary.softDelete();
    }


}
