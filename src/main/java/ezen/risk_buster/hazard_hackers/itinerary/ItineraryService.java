package ezen.risk_buster.hazard_hackers.itinerary;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItineraryService {

    private final ItineraryRepository itineraryRepository;

    public ItineraryService(ItineraryRepository itineraryRepository) {
        this.itineraryRepository = itineraryRepository;
    }

    //일정생성
    public Itinerary createItineraty(Itinerary itinerary) {
        return itineraryRepository.save(itinerary);
    }

    //일정 단일 조회
    public ItineraryResponse findByOne(Long id) {
        Itinerary itinerary = itineraryRepository.findById(id).orElse(null);

        if (itinerary == null) {
            throw new NoSuchElementException("id에 해당하는 일정이 없음");
        }
        return new ItineraryResponse(
                itinerary.getId(),
                itinerary.getTitle(),
                itinerary.getStartDate(),
                itinerary.getEndDate(),
                itinerary.getDescription()
        );
    }

    //일정 목록 조회
    public List<Itinerary> findAll() {
        return itineraryRepository.findAll();
    }




}
