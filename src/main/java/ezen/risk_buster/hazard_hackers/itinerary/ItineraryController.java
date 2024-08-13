package ezen.risk_buster.hazard_hackers.itinerary;

import ezen.risk_buster.hazard_hackers.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ItineraryController {
    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @PostMapping("/itineraryCreate")
    public ItineraryResponse create(@RequestBody ItineraryRequest itinerary){
        return itineraryService.createItineraty(itinerary);
    }

    //단일조회
    @GetMapping("/itinerary/{id}")
    public ItineraryResponse findByItinerary(@PathVariable Long id){
        return itineraryService.findByOne(id);
    }

    //목록조회
    @GetMapping("/itineraries")
    public List<ItineraryResponse> findAll(){
        return itineraryService.findAll();
    }

    //일정수정
    @PutMapping("/itineray/{id}")
    public ResponseEntity<ItineraryResponse> update(@PathVariable Long id,
                                                    @RequestBody ItineraryResponse request) {
        ItineraryResponse responseDTO = itineraryService.update(id, request);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    //일정삭제
    @DeleteMapping("/itineray/{id}")
    public ResponseEntity<String> delete (@PathVariable Long id){
        itineraryService.deleteItinerary(id);

        return new ResponseEntity<>("Deleted Success",HttpStatus.OK);
    }

}
